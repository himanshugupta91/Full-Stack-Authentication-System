# 🚀 Full-Stack Authentication: Backend Implementation Blueprint (Deep Dive)

This document breaks down the enterprise-grade construction of the Authentication Backend into actionable step-by-step tasks. Follow each phase chronologically to build out the architecture from scratch.

---

## 🏗️ Phase 1: Infrastructure & Configuration Layer
*Purpose: Bootstrapping the application, integrating external databases (PostgreSQL for persistence, Redis for caching), and securely managing environment secrets.*

### Task 1: Initialize the Application
* **Step A:** Go to `start.spring.io`. Select Java 21, Spring Boot 3.x, Maven.
* **Step B:** Add Dependencies: Web, Data JPA, PostgreSQL Driver, Spring Security, Validation, Lombok, Mail, Redis, OAuth2 Client.
* **Step C:** Generate, download, and open the project in your IDE.

### Task 2: External Services Setup (`docker-compose.yml`)
* **Step A:** Create `docker-compose.yml` in the root directory.
* **Step B:** Define the `postgres` service using image `postgres:16`. Map ports `"5432:5432"`. Inject environment variables: `POSTGRES_DB=auth_db`, `POSTGRES_USER=auth_user`, and `POSTGRES_PASSWORD=${DB_PASSWORD}`.
* **Step C:** Define the `redis` service using image `redis:7`. Map ports `"6379:6379"`.
* **Step D:** Run `docker-compose up -d` in your terminal to start the databases.

### Task 3: Environment Variables Loader (`.env`)
* **Step A:** Create a `.env` file in the `backend/` directory.
* **Step B:** Define secrets: `DB_PASSWORD=your_password`, `JWT_SECRET=your_base64_secret_key`, `GOOGLE_CLIENT_ID=your_id`, `GOOGLE_SECRET=your_secret`.
* **Step C:** Ensure `.env` is added to your `.gitignore` file immediately.

### Task 4: Spring Boot Properties (`application.yml`)
* **Step A:** Rename `application.properties` to `application.yml`.
* **Step B:** Configure Datasource: Set `url: jdbc:postgresql://localhost:5432/auth_db`, `username: auth_user`, `password: ${DB_PASSWORD}`.
* **Step C:** Configure JPA: Set `spring.jpa.hibernate.ddl-auto: update` to let Hibernate generate your tables.
* **Step D:** Configure Redis: Set `spring.data.redis.host: localhost` and `port: 6379`.
* **Step E:** Configure custom properties: Add a `jwt.secret: ${JWT_SECRET}` key.

---

## 💾 Phase 2: Domain Layer (Entities & Persistence)
*Purpose: Defining the shape of your data and how it translates into PostgreSQL tables.*

### Task 1: Core Enumerations (`Role.java` & `AuthProvider.java`)
* **Step A:** Create `com.auth.entity.Role`. Define constants: `ROLE_USER`, `ROLE_ADMIN`.
* **Step B:** Create `com.auth.entity.AuthProvider`. Define constants: `LOCAL`, `GOOGLE`, `GITHUB`, `APPLE`, `LINKEDIN`.

### Task 2: The Master User Model (`User.java`)
* **Step A:** Create `com.auth.entity.User`. Annotate with `@Entity`, `@Table(name="users")`, and `@Data` (Lombok).
* **Step B:** Define primary key: `@Id @GeneratedValue UUID id`.
* **Step C:** Define unique column: `@Column(unique = true, nullable = false) String email`.
* **Step D:** Define basic fields: `String password`, `boolean enabled = false`.
* **Step E:** Map Enums: Use `@Enumerated(EnumType.STRING)` on `Role role` and `AuthProvider provider`.

### Task 3: The Database Gateway (`UserRepository.java`)
* **Step A:** Create interface `com.auth.repository.UserRepository` extending `JpaRepository<User, UUID>`.
* **Step B:** Declare method: `Optional<User> findByEmail(String email);`.
* **Step C:** Declare method: `boolean existsByEmail(String email);`.

---

## 📦 Phase 3: Data Transfer Objects (DTOs) & Exception Handling
*Purpose: Securing the API inputs and outputs and standardizing error formats.*

### Task 1: Incoming Request Payloads (`dto/request`)
* **Step A:** Create record `RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min=6) String password)`.
* **Step B:** Create record `LoginRequest(String email, String password)`.
* **Step C:** Create record `VerifyOtpRequest(@NotBlank String email, @NotBlank String otp)`.

### Task 2: Outgoing Response Payloads (`dto/response`)
* **Step A:** Create record `UserResponse`. Include *only* safe fields: `UUID id`, `String email`, `Role role`. (Never include the password).

### Task 3: Global Exception Handler (`GlobalExceptionHandler.java`)
* **Step A:** Create `com.auth.exception.GlobalExceptionHandler` annotated with `@RestControllerAdvice`.
* **Step B:** Create a method annotated with `@ExceptionHandler(MethodArgumentNotValidException.class)`. Loop through the field errors and return a `Map<String, String>` of validation messages. Return HTTP 400.
* **Step C:** Create a method for `BadCredentialsException.class`. Return a generic message: "Invalid email or password."

---

## 🔒 Phase 4: Foundational Security Configuration
*Purpose: Disabling default Session behaviors and opening CORS pathways.*

### Task 1: Cross-Origin Control (`CorsConfig.java`)
* **Step A:** Create `com.auth.config.CorsConfig`. Define a `@Bean CorsConfigurationSource`.
* **Step B:** Set `.setAllowedOrigins(List.of("http://localhost:5173"))` (Your Vite frontend).
* **Step C:** Crucial: Execute `.setAllowCredentials(true)` to allow the browser to accept `HttpOnly` cookies.

### Task 2: The Main Security Filter Chain (`SecurityConfig.java`)
* **Step A:** Create `com.auth.config.SecurityConfig` annotated with `@EnableWebSecurity`.
* **Step B:** Define a `@Bean PasswordEncoder` returning `new BCryptPasswordEncoder()`.
* **Step C:** Define a `@Bean SecurityFilterChain(HttpSecurity http)`.
* **Step D:** Disable CSRF: `http.csrf(csrf -> csrf.disable())`.
* **Step E:** Go Stateless: `http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`.
* **Step F:** Configure Routes: `.requestMatchers("/api/auth/**", "/oauth2/**").permitAll()`. Then `.requestMatchers("/api/admin/**").hasRole("ADMIN")`. Finally, `.anyRequest().authenticated()`.

---

## 🔑 Phase 5: JSON Web Token Architecture (JWT) Deep Dive
*Purpose: Minting and validating the cryptographic tokens.*

### Task 1: Token Cryptography Service (`JwtService.java`)
* **Step A (Secret Key):** Inject your `${jwt.secret}` variable from `application.yml` using the `@Value` annotation.
* **Step B (Key Generator):** Create a private method `getSigningKey()`. Inside, decode your secret Base-64 string into bytes using `Decoders.BASE64.decode(secret)`. Return a cryptographic HMAC key using `Keys.hmacShaKeyFor(keyBytes)`.
* **Step C (Access Token Creation):** Write `generateAccessToken(UserDetails user)`. Use `Jwts.builder()` to create the token payload. Set `.subject(user.getUsername())`, `.issuedAt(now)`, `.expiration(now + 15 mins)`, sign it using `.signWith(getSigningKey())`, and `.compact()` it.
* **Step D (Refresh Token Creation):** Copy the Access Token method, but change the expiration math to equal 7 days.
* **Step E (Token claim Extraction):** Write `extractUsername(String token)`. Use `Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody()`. Return `.getSubject()`.
* **Step F (Token Validation):** Write `isTokenValid(String token, UserDetails userDetails)`. Ensure `extractUsername(token).equals(userDetails.getUsername())` AND expiration date is after `new Date()`.

### Task 2: User Authentication Gateway (`JwtAuthenticationFilter.java`)
* **Step A:** Create class extending `OncePerRequestFilter`. Annotate `@Component`. Inject `JwtService` and `UserDetailsService`.
* **Step B (Header Extraction):** Override `doFilterInternal()`. Extract the "Authorization" header. If it doesn't start with "Bearer ", call `filterChain.doFilter` and `return;`.
* **Step C:** Cut the `"Bearer "` prefix off to get the raw JWT string.
* **Step D (Context Verification):** If `JwtService` extracts a username, and `SecurityContextHolder` is currently empty: load the user from the DB. If `JwtService.isTokenValid()` returns true, create a `UsernamePasswordAuthenticationToken`, give it the user's authorities, and set it into the `SecurityContextHolder`. Let the filter chain continue.

---

## 📧 Phase 6: OTP Generation & Redis Integration Deep Dive
*Purpose: Halting fake accounts by forcing users to verify their physical email address.*

### Task 1: The OTP Cashier (`OtpService.java`)
* **Step A:** Annotate with `@Service`. Inject `StringRedisTemplate`.
* **Step B (Generation):** Write `generateAndSaveOtp(String email)`. Generate a 6-digit string using `new Random()`. Store it in Redis: `redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));`. Return the OTP.
* **Step C (Validation):** Write `validateOtp(String email, String userInputOtp)`. Fetch the value from Redis. If it matches, immediately run `redisTemplate.delete("OTP:" + email);` (to stop double verification bounds) and return `true`.

### Task 2: Physical Email Dispatcher (`EmailService.java`)
* **Step A:** Annotate with `@Service`. Inject `JavaMailSender`. (Ensure `spring.mail` fields are in `application.yml`).
* **Step B:** Write `sendOtpEmail(String recipientEmail, String rawOtp)`. Instantiate a `SimpleMailMessage`. Set `.setTo()`, `.setSubject()`, and `.setText("Here is your validation code: " + rawOtp)`. Execute `mailSender.send()`.

---

## ⚙️ Phase 7: The Master Authentication Service Deep Dive
*Purpose: Combining the entities and tokens securely.*

### Task 1: Building the logic (`AuthServiceImpl.java`)
* **Step A:** Create `com.auth.service.impl.AuthServiceImpl` implementing `AuthService`. Inject Repositories, Providers, and Support Services.
* **Step B (Register Flow):** Write `register(RegisterRequest req)`. Throw Exception if `userRepository.existsByEmail()`. Create new `User`. Hash the password: `user.setPassword(passwordEncoder.encode(req.password()))`. Set `enabled = false` and `provider = LOCAL`. Save user. Finally, call `OtpService` to generate the code and `EmailService` to send it.
* **Step C (Verification Flow):** Write `verifyUser(VerifyOtpRequest req)`. Check `OtpService.validateOtp()`. Extract user from repository and update `enabled = true`. Save.
* **Step D (Login Flow):** Write `login(LoginRequest req)`. 
  1. Trigger `authenticationManager.authenticate(...)`.
  2. If successful, fetch the `User`. Ensure the DB shows `enabled = true`.
  3. Mint the Access & Refresh JWTs via `JwtService`. Pack them into an internal DTO or Map to send back to the Controller.

---

## 🛂 Phase 8: Exposing the REST API Controllers Deep Dive
*Purpose: Mapping the business logic to web URLs securely using HTTP standards.*

### Task 1: The Authentication Gateway (`AuthController.java`)
* **Step A:** Create `com.auth.controller.AuthController`. Annotate with `@RestController` and `@RequestMapping("/api/auth")`.
* **Step B (Register):** Write `@PostMapping("/register")`. Accept `@Valid RegisterRequest @RequestBody`. Call service. Return success message.
* **Step C (Login):** Write `@PostMapping("/login")`. Accept `LoginRequest`.
  1. Call service to authenticate and retrieve `accessToken` and `refreshToken` strings.
  2. Instantiate `Cookie cookie = new Cookie("refresh_token", refreshToken)`.
  3. Security lock: `cookie.setHttpOnly(true)` (Blocks frontend JS from reading it via XSS).
  4. Routing lock: `cookie.setPath("/api/auth/refresh")` (Browser only sends this cookie on refresh calls).
  5. Append it: `response.addCookie(cookie)`.
  6. Return the `accessToken` inside a standard JSON `ResponseEntity`.
* **Step D (Refresh):** Write `@PostMapping("/refresh")`. Extract the cookie using `@CookieValue("refresh_token") String refreshToken`. Validate it. Call `JwtService.generateAccessToken(user)`. Return the new Access Token in JSON.
* **Step E (Logout):** Write `@PostMapping("/logout")`. Instantiate the exact same `refresh_token` cookie, but execute `.setMaxAge(0)` to command the browser to delete it from memory.

---

## 🛡️ Phase 9: Active Rate Limiting Deep Dive
*Purpose: Stopping Brute-Force password bots.*

### Task 1: The Redis Blocker (`RateLimitingService.java`)
* **Step A:** Create `RateLimitingService`. Inject `StringRedisTemplate`.
* **Step B:** Write `recordFailedAttempt(String email)`. Query Redis to increment `"ATTEMPTS:" + email`. If attempts reach 5, write a new key `"LOCK:" + email` to Redis with a `Duration.ofMinutes(30)`. Delete the `ATTEMPTS` counter.
* **Step C:** Write `isAccountLocked(String email)`. Simply check if the `"LOCK:"` key exists via Redis `hasKey`. If true, throw a `LockedException`.
* **Step D:** Write `clearAttempts(String email)`. Call this on successful login to delete the attempt counter.

### Task 2: Service Integration
* **Step A:** Go back into `AuthServiceImpl.login()`.
* **Step B:** At the very top, run `RateLimitingService.isAccountLocked(email)`.
* **Step C:** Wrap `authenticationManager.authenticate` in a try-catch. In the catch block (where BadCredentialsException occurs), trigger `recordFailedAttempt()`. In the success block, trigger `clearAttempts()`.

---

## 🌐 Phase 10: Seamless OAuth2 Integration Deep Dive
*Purpose: Enabling Click-to-Login functionality.*

### Task 1: Client Configurations
* **Step A:** Update `application.yml`. Add your `google` and `github` client IDs under `spring.security.oauth2.client.registration`.
* **Step B:** Define scope (`email`, `profile`). Spring Security natively generates the `/oauth2/authorization/google` redirect endpoints for you.

### Task 2: Custom JSON Parser & Mapping (`CustomOAuth2UserService.java`)
* **Step A:** Create `com.auth.security.oauth2.CustomOAuth2UserService` extending `DefaultOAuth2UserService`.
* **Step B:** Override `loadUser()`. Get attributes via `super.loadUser(userRequest).getAttributes()`.
* **Step C:** Create an interface `OAuth2UserInfo` with a `getEmail()` method. Build implementations for Google (extracts `email` key) vs GitHub (extracts `user/email` structure).
* **Step D:** Check Postgres for that email. If missing, save a new `User` with a dummy password (`BCryptPasswordEncoder()`), set `enabled=true`, provider `GOOGLE`. 
* **Step E:** Return a Spring `DefaultOAuth2User` containing the user's ID/Authorities so the Security Context accepts them.

### Task 3: Redirection Control (`OAuth2AuthenticationSuccessHandler.java`)
* **Step A:** Implement `AuthenticationSuccessHandler`. Override `onAuthenticationSuccess`.
* **Step B:** Extract the user email from the `Authentication` object.
* **Step C:** Generate your internal Access & Refresh tokens via `JwtService`.
* **Step D:** Build the `HttpOnly` cookie exactly like the Login Controller and append it.
* **Step E:** Run `response.sendRedirect("http://localhost:5173/dashboard")` to command the user's browser to leave the Spring Boot server and go back to your React app.

---

## 👑 Phase 11: Admin Module & Output Responses Deep Dive
*Purpose: Establishing RBAC endpoints.*

### Task 1: Protected Admin Controller (`AdminController.java`)
* **Step A:** Annotate the entire class with `@PreAuthorize("hasRole('ADMIN')")`.
* **Step B:** Write `@GetMapping("/users")`. Accept pagination: `@RequestParam(defaultValue = "0") int page`.
* **Step C:** Query `UserRepository.findAll(PageRequest.of(page, 20))`. 
* **Step D:** Map the raw `User` list into the safe `UserResponse` DTO manually or using MapStruct before returning it to the frontend.

---
---

# 🛣️ API-Driven Implementation Checklist

*Once the foundational layers are built from the blueprint above, you can verify your progress by reviewing these specific API Endpoints from end-to-end. This shows how all the pieces connect together for each HTTP request.*

## 📌 API: `POST /api/auth/register`
*Purpose: Accept a user's email and password, save them as disabled, and dispatch an OTP verification email.*

### Task 1: Payloads & Handlers
* **Step A:** Create `RegisterRequest` DTO with `@Email` and `@NotBlank` annotations.
* **Step B:** Create `GlobalExceptionHandler` with `@RestControllerAdvice` to catch validation errors and return clean HTTP 400 JSON mapping.

### Task 2: OTP & Email Architecture
* **Step A:** Create `OtpService` using `StringRedisTemplate`. Write `generateAndSaveOtp()` to generate a 6-digit code and save it to Redis with a 5-minute TTL.
* **Step B:** Create `EmailService` using `JavaMailSender`. Write `sendOtpEmail()` to format and dispatch the code to the user's inbox.

### Task 3: Business Logic & Controller
* **Step A:** Create `AuthService.register()`. Assert email doesn't exist. Hash password using `BCrypt`. Save `User` (enabled=false). Call `OtpService` and `EmailService`.
* **Step B:** Create `AuthController`. Add `@PostMapping("/register")` that accepts `@Valid RegisterRequest`, delegates to `AuthService`, and returns a HTTP 200 "Success" message.

---

## 📌 API: `POST /api/auth/verify-otp`
*Purpose: Consume the 6-digit code, validate it against Redis, and activate the user's account.*

### Task 1: Execution Logic
* **Step A:** Create `VerifyOtpRequest` DTO containing `email` and `otp` strings.
* **Step B:** Add `validateOtp(email, otp)` to `OtpService`. Query Redis and delete the key immediately if it matches to prevent reuse.
* **Step C:** Add `verifyUser()` to `AuthService`. Call `OtpService.validateOtp`. If true, fetch `User` from DB, set `enabled=true`, and save.
* **Step D:** Add `@PostMapping("/verify-otp")` to `AuthController` to expose the endpoint.

---

## 📌 API: `POST /api/auth/login`
*Purpose: Authenticate credentials, enforce rate limits, and securely dispatch JWT tokens.*

### Task 1: Rate Limiting Defense
* **Step A:** Create `RateLimitingService` using Redis. Write `recordFailedAttempt()` (increments counter, locks account for 30 mins at 5 fails). Write `isAccountLocked()` and `clearAttempts()`.

### Task 2: Business Logic
* **Step A:** Create `LoginRequest` DTO containing `email` and `password`.
* **Step B:** Add `login()` to `AuthService`. 
  1. Check `RateLimitingService.isAccountLocked()`.
  2. Use Spring's `AuthenticationManager` to attempt login. (Wrap in try/catch to trigger `recordFailedAttempt()`).
  3. On success, verify `enabled=true`. Generate Access and Refresh tokens via `JwtService`. Return them.

### Task 3: Secure Cookie Controller
* **Step A:** Add `@PostMapping("/login")` to `AuthController`.
* **Step B:** Retrieve tokens from service. Place `accessToken` in JSON body.
* **Step C:** Create a Java `Cookie` for the `refreshToken`. Set `setHttpOnly(true)` and `setPath("/api/auth/refresh")`. Add cookie to the HTTP response.

---

## 📌 API: `POST /api/auth/refresh` & `POST /api/auth/logout`
*Purpose: Maintain user sessions securely without requiring re-login.*

### Task 1: Token Refresh Flow
* **Step A:** Add `@PostMapping("/refresh")` to `AuthController`.
* **Step B:** Read the cookie using `@CookieValue("refresh_token") String token`.
* **Step C:** Pass the token to `JwtService` for validation. Load the `User`.
* **Step D:** Generate a new `accessToken` and return it in the JSON response.

### Task 2: Logout Flow
* **Step A:** Add `@PostMapping("/logout")` to `AuthController`.
* **Step B:** Create a dummy `Cookie` with the exact same name, path, and HttpOnly settings.
* **Step C:** Execute `.setMaxAge(0)` on the cookie and append it to the response to instruct the browser to delete the refresh token.

---

## 📌 API: OAuth2 `/oauth2/authorization/{provider}`
*Purpose: Spring-managed endpoints to support "Continue with Google/GitHub".*

### Task 1: Configuration & Handlers
* **Step A:** Add credentials to `application.yml` under `spring.security.oauth2.client.registration`.
* **Step B:** Create `CustomOAuth2UserService` to normalize Google/GitHub attributes into a standard email field. Save non-existent emails into Postgres with a dummy password and `enabled=true`.
* **Step C:** Create `OAuth2AuthenticationSuccessHandler`. Generate JWTs, attach the `HttpOnly` refresh cookie, and execute `response.sendRedirect("http://localhost:5173")`.
* **Step D:** Register the UserService and SuccessHandler in your `SecurityConfig` filter chain under `.oauth2Login()`.

---

## 📌 API: `GET /api/admin/users`
*Purpose: Establishing Role-Based Access Control (RBAC) data fetching.*

### Task 1: Protected Data Retrieval
* **Step A:** Create `UserResponse` DTO containing `id`, `email`, `role` (No Password).
* **Step B:** Create `AdminController`. Annotate the whole class with `@PreAuthorize("hasRole('ADMIN')")`.
* **Step C:** Add `@GetMapping("/users")`. Accept pagination params `@RequestParam int page`.
* **Step D:** Use `UserRepository.findAll(PageRequest.of(page, 20))` and map the resulting generic Users safely into `UserResponse` objects before returning.
