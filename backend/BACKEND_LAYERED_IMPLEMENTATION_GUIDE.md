# Backend Layered Implementation Guide (Line-by-Line Deep Dive)

This guide is intentionally exhaustive and generated directly from the backend codebase.
It is designed to help you rebuild the system file-by-file and line-by-line with implementation notes.

## How To Use This Guide
1. Follow the files in order.
2. Copy each line into your project file as shown.
3. Read the inline explanation for why the line exists.
4. For secret values, use your own environment-specific values.

## Build Order
1. Build tooling and app properties.
2. Build entities, repositories, DTOs, and mappers.
3. Build security and configuration.
4. Build service interfaces and implementations.
5. Build controllers and exception handling.
6. Run tests and verify behavior.

---

## File 01: `backend/pom.xml`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 150

### Line-by-Line Walkthrough
- L0001: `<?xml version="1.0" encoding="UTF-8"?>` - Implements part of the file's concrete application logic.
- L0002: `<project xmlns="http://maven.apache.org/POM/4.0.0"` - Implements part of the file's concrete application logic.
- L0003: `         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"` - Implements part of the file's concrete application logic.
- L0004: `         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">` - Implements part of the file's concrete application logic.
- L0005: `    <modelVersion>4.0.0</modelVersion>` - Implements part of the file's concrete application logic.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `    <parent>` - Implements part of the file's concrete application logic.
- L0008: `        <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0009: `        <artifactId>spring-boot-starter-parent</artifactId>` - Implements part of the file's concrete application logic.
- L0010: `        <version>3.5.10</version>` - Implements part of the file's concrete application logic.
- L0011: `        <relativePath/>` - Implements part of the file's concrete application logic.
- L0012: `    </parent>` - Implements part of the file's concrete application logic.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `    <groupId>com.auth</groupId>` - Implements part of the file's concrete application logic.
- L0015: `    <artifactId>auth-backend</artifactId>` - Implements part of the file's concrete application logic.
- L0016: `    <version>1.0.0</version>` - Implements part of the file's concrete application logic.
- L0017: `    <name>auth-backend</name>` - Implements part of the file's concrete application logic.
- L0018: `    <description>Spring Boot Authentication Backend with JWT</description>` - Security-related logic for tokens, OAuth, or authentication state.
- L0019: `` - Blank line used to separate logical blocks for readability.
- L0020: `    <properties>` - Implements part of the file's concrete application logic.
- L0021: `        <java.version>21</java.version>` - Implements part of the file's concrete application logic.
- L0022: `        <jjwt.version>0.12.3</jjwt.version>` - Security-related logic for tokens, OAuth, or authentication state.
- L0023: `        <mapstruct.version>1.5.5.Final</mapstruct.version>` - Implements part of the file's concrete application logic.
- L0024: `    </properties>` - Implements part of the file's concrete application logic.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    <dependencies>` - Implements part of the file's concrete application logic.
- L0027: `        <!-- Spring Boot Starters -->` - Implements part of the file's concrete application logic.
- L0028: `        <dependency>` - Implements part of the file's concrete application logic.
- L0029: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0030: `            <artifactId>spring-boot-starter-web</artifactId>` - Implements part of the file's concrete application logic.
- L0031: `        </dependency>` - Implements part of the file's concrete application logic.
- L0032: `        <dependency>` - Implements part of the file's concrete application logic.
- L0033: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0034: `            <artifactId>spring-boot-starter-security</artifactId>` - Implements part of the file's concrete application logic.
- L0035: `        </dependency>` - Implements part of the file's concrete application logic.
- L0036: `        <dependency>` - Implements part of the file's concrete application logic.
- L0037: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0038: `            <artifactId>spring-boot-starter-data-jpa</artifactId>` - Implements part of the file's concrete application logic.
- L0039: `        </dependency>` - Implements part of the file's concrete application logic.
- L0040: `        <dependency>` - Implements part of the file's concrete application logic.
- L0041: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0042: `            <artifactId>spring-boot-starter-mail</artifactId>` - Implements part of the file's concrete application logic.
- L0043: `        </dependency>` - Implements part of the file's concrete application logic.
- L0044: `        <dependency>` - Implements part of the file's concrete application logic.
- L0045: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0046: `            <artifactId>spring-boot-starter-validation</artifactId>` - Implements part of the file's concrete application logic.
- L0047: `        </dependency>` - Implements part of the file's concrete application logic.
- L0048: `        <dependency>` - Implements part of the file's concrete application logic.
- L0049: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0050: `            <artifactId>spring-boot-starter-oauth2-client</artifactId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0051: `        </dependency>` - Implements part of the file's concrete application logic.
- L0052: `        <dependency>` - Implements part of the file's concrete application logic.
- L0053: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0054: `            <artifactId>spring-boot-starter-data-redis</artifactId>` - Implements part of the file's concrete application logic.
- L0055: `        </dependency>` - Implements part of the file's concrete application logic.
- L0056: `        <dependency>` - Implements part of the file's concrete application logic.
- L0057: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0058: `            <artifactId>spring-boot-devtools</artifactId>` - Implements part of the file's concrete application logic.
- L0059: `            <scope>runtime</scope>` - Implements part of the file's concrete application logic.
- L0060: `            <optional>true</optional>` - Implements part of the file's concrete application logic.
- L0061: `        </dependency>` - Implements part of the file's concrete application logic.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `        <!-- PostgreSQL Driver -->` - Implements part of the file's concrete application logic.
- L0064: `        <dependency>` - Implements part of the file's concrete application logic.
- L0065: `            <groupId>org.postgresql</groupId>` - Implements part of the file's concrete application logic.
- L0066: `            <artifactId>postgresql</artifactId>` - Implements part of the file's concrete application logic.
- L0067: `            <scope>runtime</scope>` - Implements part of the file's concrete application logic.
- L0068: `        </dependency>` - Implements part of the file's concrete application logic.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `        <!-- JWT -->` - Security-related logic for tokens, OAuth, or authentication state.
- L0071: `        <dependency>` - Implements part of the file's concrete application logic.
- L0072: `            <groupId>io.jsonwebtoken</groupId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0073: `            <artifactId>jjwt-api</artifactId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0074: `            <version>${jjwt.version}</version>` - Security-related logic for tokens, OAuth, or authentication state.
- L0075: `        </dependency>` - Implements part of the file's concrete application logic.
- L0076: `        <dependency>` - Implements part of the file's concrete application logic.
- L0077: `            <groupId>io.jsonwebtoken</groupId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0078: `            <artifactId>jjwt-impl</artifactId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0079: `            <version>${jjwt.version}</version>` - Security-related logic for tokens, OAuth, or authentication state.
- L0080: `            <scope>runtime</scope>` - Implements part of the file's concrete application logic.
- L0081: `        </dependency>` - Implements part of the file's concrete application logic.
- L0082: `        <dependency>` - Implements part of the file's concrete application logic.
- L0083: `            <groupId>io.jsonwebtoken</groupId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0084: `            <artifactId>jjwt-jackson</artifactId>` - Security-related logic for tokens, OAuth, or authentication state.
- L0085: `            <version>${jjwt.version}</version>` - Security-related logic for tokens, OAuth, or authentication state.
- L0086: `            <scope>runtime</scope>` - Implements part of the file's concrete application logic.
- L0087: `        </dependency>` - Implements part of the file's concrete application logic.
- L0088: `` - Blank line used to separate logical blocks for readability.
- L0089: `        <!-- Lombok - version 1.18.34+ required for Java 25 -->` - Implements part of the file's concrete application logic.
- L0090: `        <dependency>` - Implements part of the file's concrete application logic.
- L0091: `            <groupId>org.projectlombok</groupId>` - Implements part of the file's concrete application logic.
- L0092: `            <artifactId>lombok</artifactId>` - Implements part of the file's concrete application logic.
- L0093: `            <version>1.18.40</version>` - Implements part of the file's concrete application logic.
- L0094: `        </dependency>` - Implements part of the file's concrete application logic.
- L0095: `` - Blank line used to separate logical blocks for readability.
- L0096: `        <!-- MapStruct -->` - Implements part of the file's concrete application logic.
- L0097: `        <dependency>` - Implements part of the file's concrete application logic.
- L0098: `            <groupId>org.mapstruct</groupId>` - Implements part of the file's concrete application logic.
- L0099: `            <artifactId>mapstruct</artifactId>` - Implements part of the file's concrete application logic.
- L0100: `            <version>${mapstruct.version}</version>` - Implements part of the file's concrete application logic.
- L0101: `        </dependency>` - Implements part of the file's concrete application logic.
- L0102: `` - Blank line used to separate logical blocks for readability.
- L0103: `        <!-- Test -->` - Implements part of the file's concrete application logic.
- L0104: `        <dependency>` - Implements part of the file's concrete application logic.
- L0105: `            <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0106: `            <artifactId>spring-boot-starter-test</artifactId>` - Implements part of the file's concrete application logic.
- L0107: `            <scope>test</scope>` - Implements part of the file's concrete application logic.
- L0108: `        </dependency>` - Implements part of the file's concrete application logic.
- L0109: `        <dependency>` - Implements part of the file's concrete application logic.
- L0110: `            <groupId>org.springframework.security</groupId>` - Implements part of the file's concrete application logic.
- L0111: `            <artifactId>spring-security-test</artifactId>` - Implements part of the file's concrete application logic.
- L0112: `            <scope>test</scope>` - Implements part of the file's concrete application logic.
- L0113: `        </dependency>` - Implements part of the file's concrete application logic.
- L0114: `    </dependencies>` - Implements part of the file's concrete application logic.
- L0115: `` - Blank line used to separate logical blocks for readability.
- L0116: `    <build>` - Implements part of the file's concrete application logic.
- L0117: `        <plugins>` - Implements part of the file's concrete application logic.
- L0118: `            <plugin>` - Implements part of the file's concrete application logic.
- L0119: `                <groupId>org.apache.maven.plugins</groupId>` - Implements part of the file's concrete application logic.
- L0120: `                <artifactId>maven-compiler-plugin</artifactId>` - Implements part of the file's concrete application logic.
- L0121: `                <configuration>` - Implements part of the file's concrete application logic.
- L0122: `                    <annotationProcessorPaths>` - Implements part of the file's concrete application logic.
- L0123: `                        <path>` - Implements part of the file's concrete application logic.
- L0124: `                            <groupId>org.projectlombok</groupId>` - Implements part of the file's concrete application logic.
- L0125: `                            <artifactId>lombok</artifactId>` - Implements part of the file's concrete application logic.
- L0126: `                            <version>1.18.40</version>` - Implements part of the file's concrete application logic.
- L0127: `                        </path>` - Implements part of the file's concrete application logic.
- L0128: `                        <path>` - Implements part of the file's concrete application logic.
- L0129: `                            <groupId>org.mapstruct</groupId>` - Implements part of the file's concrete application logic.
- L0130: `                            <artifactId>mapstruct-processor</artifactId>` - Implements part of the file's concrete application logic.
- L0131: `                            <version>${mapstruct.version}</version>` - Implements part of the file's concrete application logic.
- L0132: `                        </path>` - Implements part of the file's concrete application logic.
- L0133: `                    </annotationProcessorPaths>` - Implements part of the file's concrete application logic.
- L0134: `                </configuration>` - Implements part of the file's concrete application logic.
- L0135: `            </plugin>` - Implements part of the file's concrete application logic.
- L0136: `            <plugin>` - Implements part of the file's concrete application logic.
- L0137: `                <groupId>org.springframework.boot</groupId>` - Implements part of the file's concrete application logic.
- L0138: `                <artifactId>spring-boot-maven-plugin</artifactId>` - Implements part of the file's concrete application logic.
- L0139: `                <configuration>` - Implements part of the file's concrete application logic.
- L0140: `                    <excludes>` - Implements part of the file's concrete application logic.
- L0141: `                        <exclude>` - Implements part of the file's concrete application logic.
- L0142: `                            <groupId>org.projectlombok</groupId>` - Implements part of the file's concrete application logic.
- L0143: `                            <artifactId>lombok</artifactId>` - Implements part of the file's concrete application logic.
- L0144: `                        </exclude>` - Implements part of the file's concrete application logic.
- L0145: `                    </excludes>` - Implements part of the file's concrete application logic.
- L0146: `                </configuration>` - Implements part of the file's concrete application logic.
- L0147: `            </plugin>` - Implements part of the file's concrete application logic.
- L0148: `        </plugins>` - Implements part of the file's concrete application logic.
- L0149: `    </build>` - Implements part of the file's concrete application logic.
- L0150: `</project>` - Implements part of the file's concrete application logic.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 02: `backend/src/main/resources/application.properties`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 131

### Line-by-Line Walkthrough
- L0001: `# ================================` - Implements part of the file's concrete application logic.
- L0002: `# Server` - Implements part of the file's concrete application logic.
- L0003: `# ================================` - Implements part of the file's concrete application logic.
- L0004: `server.port=8080` - Configuration property line that controls environment-specific behavior.
- L0005: `server.servlet.session.cookie.same-site=lax` - Configuration property line that controls environment-specific behavior.
- L0006: `server.servlet.session.cookie.secure=false` - Configuration property line that controls environment-specific behavior.
- L0007: `server.servlet.session.tracking-modes=cookie` - Configuration property line that controls environment-specific behavior.
- L0008: `` - Blank line used to separate logical blocks for readability.
- L0009: `# ================================` - Implements part of the file's concrete application logic.
- L0010: `# Database` - Implements part of the file's concrete application logic.
- L0011: `# ================================` - Implements part of the file's concrete application logic.
- L0012: `spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db` - Configuration property line that controls environment-specific behavior.
- L0013: `spring.datasource.username=auth_user` - Configuration property line that controls environment-specific behavior.
- L0014: `spring.datasource.password=<REDACTED_SET_IN_ENV>` - Credential or recovery logic for authentication safety.
- L0015: `spring.jpa.hibernate.ddl-auto=create-drop` - Configuration property line that controls environment-specific behavior.
- L0016: `spring.jpa.show-sql=false` - Configuration property line that controls environment-specific behavior.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `# ================================` - Implements part of the file's concrete application logic.
- L0019: `# Redis` - Implements part of the file's concrete application logic.
- L0020: `# ================================` - Implements part of the file's concrete application logic.
- L0021: `spring.data.redis.host=localhost` - Configuration property line that controls environment-specific behavior.
- L0022: `spring.data.redis.port=6379` - Configuration property line that controls environment-specific behavior.
- L0023: `spring.data.redis.timeout=2s` - Configuration property line that controls environment-specific behavior.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `# ================================` - Implements part of the file's concrete application logic.
- L0026: `# Mail` - Implements part of the file's concrete application logic.
- L0027: `# ================================` - Implements part of the file's concrete application logic.
- L0028: `spring.mail.host=smtp.gmail.com` - Configuration property line that controls environment-specific behavior.
- L0029: `spring.mail.port=587` - Configuration property line that controls environment-specific behavior.
- L0030: `spring.mail.username=hg1480144@gmail.com` - Configuration property line that controls environment-specific behavior.
- L0031: `spring.mail.password=<REDACTED_SET_IN_ENV>` - Credential or recovery logic for authentication safety.
- L0032: `spring.mail.properties.mail.smtp.auth=true` - Configuration property line that controls environment-specific behavior.
- L0033: `spring.mail.properties.mail.smtp.starttls.enable=true` - Configuration property line that controls environment-specific behavior.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `# ================================` - Implements part of the file's concrete application logic.
- L0036: `# JWT Access + Refresh Token` - Security-related logic for tokens, OAuth, or authentication state.
- L0037: `# ================================` - Implements part of the file's concrete application logic.
- L0038: `# Generate with: openssl rand -base64 64` - Implements part of the file's concrete application logic.
- L0039: `jwt.secret=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0040: `jwt.expiration=900000` - Security-related logic for tokens, OAuth, or authentication state.
- L0041: `jwt.refresh.expiration=604800000` - Security-related logic for tokens, OAuth, or authentication state.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `# ================================` - Implements part of the file's concrete application logic.
- L0044: `# Frontend and Cookie` - Implements part of the file's concrete application logic.
- L0045: `# ================================` - Implements part of the file's concrete application logic.
- L0046: `app.frontend-url=http://localhost:5173` - Configuration property line that controls environment-specific behavior.
- L0047: `app.frontend-reset-password-url=http://localhost:5173/reset-password` - Credential or recovery logic for authentication safety.
- L0048: `app.backend-url=http://localhost:8080` - Configuration property line that controls environment-specific behavior.
- L0049: `auth.refresh-token.cookie-name=refreshToken` - Security-related logic for tokens, OAuth, or authentication state.
- L0050: `auth.refresh-token.cookie-path=/api/auth` - Security-related logic for tokens, OAuth, or authentication state.
- L0051: `auth.refresh-token.cookie-secure=false` - Security-related logic for tokens, OAuth, or authentication state.
- L0052: `auth.refresh-token.cookie-same-site=Lax` - Security-related logic for tokens, OAuth, or authentication state.
- L0053: `security.token-hash-pepper=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0054: `` - Blank line used to separate logical blocks for readability.
- L0055: `# ================================` - Implements part of the file's concrete application logic.
- L0056: `# Abuse Protection` - Implements part of the file's concrete application logic.
- L0057: `# ================================` - Implements part of the file's concrete application logic.
- L0058: `auth.protection.enabled=true` - Configuration property line that controls environment-specific behavior.
- L0059: `` - Blank line used to separate logical blocks for readability.
- L0060: `auth.rate-limit.login.ip.limit=5` - Configuration property line that controls environment-specific behavior.
- L0061: `auth.rate-limit.login.ip.window-seconds=60` - Configuration property line that controls environment-specific behavior.
- L0062: `auth.rate-limit.login.email.limit=10` - Configuration property line that controls environment-specific behavior.
- L0063: `auth.rate-limit.login.email.window-seconds=900` - Configuration property line that controls environment-specific behavior.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `auth.rate-limit.otp-verify.ip.limit=20` - Credential or recovery logic for authentication safety.
- L0066: `auth.rate-limit.otp-verify.ip.window-seconds=600` - Credential or recovery logic for authentication safety.
- L0067: `auth.rate-limit.otp-verify.email.limit=5` - Credential or recovery logic for authentication safety.
- L0068: `auth.rate-limit.otp-verify.email.window-seconds=600` - Credential or recovery logic for authentication safety.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `auth.rate-limit.resend-otp.email.cooldown-seconds=60` - Credential or recovery logic for authentication safety.
- L0071: `auth.rate-limit.resend-otp.email.limit=3` - Credential or recovery logic for authentication safety.
- L0072: `auth.rate-limit.resend-otp.email.window-seconds=900` - Credential or recovery logic for authentication safety.
- L0073: `auth.rate-limit.resend-otp.ip.limit=20` - Credential or recovery logic for authentication safety.
- L0074: `auth.rate-limit.resend-otp.ip.window-seconds=900` - Credential or recovery logic for authentication safety.
- L0075: `` - Blank line used to separate logical blocks for readability.
- L0076: `auth.rate-limit.reset-password.email.limit=3` - Credential or recovery logic for authentication safety.
- L0077: `auth.rate-limit.reset-password.email.window-seconds=1800` - Credential or recovery logic for authentication safety.
- L0078: `auth.rate-limit.reset-password.ip.limit=10` - Credential or recovery logic for authentication safety.
- L0079: `auth.rate-limit.reset-password.ip.window-seconds=1800` - Credential or recovery logic for authentication safety.
- L0080: `` - Blank line used to separate logical blocks for readability.
- L0081: `auth.bruteforce.login.max-attempts=10` - Configuration property line that controls environment-specific behavior.
- L0082: `auth.bruteforce.login.lock-minutes=15` - Configuration property line that controls environment-specific behavior.
- L0083: `auth.bruteforce.otp.max-attempts=5` - Credential or recovery logic for authentication safety.
- L0084: `auth.bruteforce.otp.lock-minutes=10` - Credential or recovery logic for authentication safety.
- L0085: `` - Blank line used to separate logical blocks for readability.
- L0086: `# ================================` - Implements part of the file's concrete application logic.
- L0087: `# OAuth2 Providers` - Security-related logic for tokens, OAuth, or authentication state.
- L0088: `# ================================` - Implements part of the file's concrete application logic.
- L0089: `# Google` - Implements part of the file's concrete application logic.
- L0090: `spring.security.oauth2.client.registration.google.client-id=532475816213-v6tmim6t8v2m5tmme96c1rosqbm37i72.apps.googleusercontent.com` - Security-related logic for tokens, OAuth, or authentication state.
- L0091: `spring.security.oauth2.client.registration.google.client-secret=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0092: `spring.security.oauth2.client.registration.google.scope=openid,profile,email` - Security-related logic for tokens, OAuth, or authentication state.
- L0093: `spring.security.oauth2.client.registration.google.redirect-uri=${app.backend-url}/login/oauth2/code/{registrationId}` - Security-related logic for tokens, OAuth, or authentication state.
- L0094: `` - Blank line used to separate logical blocks for readability.
- L0095: `# GitHub` - Implements part of the file's concrete application logic.
- L0096: `spring.security.oauth2.client.registration.github.client-id=Ov23li8emqU9VkaYPIKU` - Security-related logic for tokens, OAuth, or authentication state.
- L0097: `spring.security.oauth2.client.registration.github.client-secret=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0098: `spring.security.oauth2.client.registration.github.scope=read:user,user:email` - Security-related logic for tokens, OAuth, or authentication state.
- L0099: `spring.security.oauth2.client.registration.github.redirect-uri=${app.backend-url}/login/oauth2/code/{registrationId}` - Security-related logic for tokens, OAuth, or authentication state.
- L0100: `` - Blank line used to separate logical blocks for readability.
- L0101: `# Apple` - Implements part of the file's concrete application logic.
- L0102: `spring.security.oauth2.client.registration.apple.client-id=your-apple-services-id` - Service interaction applies business logic or orchestration.
- L0103: `spring.security.oauth2.client.registration.apple.client-secret=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0104: `spring.security.oauth2.client.registration.apple.client-name=Apple` - Security-related logic for tokens, OAuth, or authentication state.
- L0105: `spring.security.oauth2.client.registration.apple.client-authentication-method=client_secret_post` - Security-related logic for tokens, OAuth, or authentication state.
- L0106: `spring.security.oauth2.client.registration.apple.authorization-grant-type=authorization_code` - Security-related logic for tokens, OAuth, or authentication state.
- L0107: `spring.security.oauth2.client.registration.apple.redirect-uri=${app.backend-url}/login/oauth2/code/{registrationId}` - Security-related logic for tokens, OAuth, or authentication state.
- L0108: `spring.security.oauth2.client.registration.apple.scope=openid,name,email` - Security-related logic for tokens, OAuth, or authentication state.
- L0109: `spring.security.oauth2.client.provider.apple.authorization-uri=https://appleid.apple.com/auth/authorize` - Security-related logic for tokens, OAuth, or authentication state.
- L0110: `spring.security.oauth2.client.provider.apple.token-uri=https://appleid.apple.com/auth/token` - Security-related logic for tokens, OAuth, or authentication state.
- L0111: `spring.security.oauth2.client.provider.apple.jwk-set-uri=https://appleid.apple.com/auth/keys` - Security-related logic for tokens, OAuth, or authentication state.
- L0112: `spring.security.oauth2.client.provider.apple.user-name-attribute=sub` - Security-related logic for tokens, OAuth, or authentication state.
- L0113: `` - Blank line used to separate logical blocks for readability.
- L0114: `# LinkedIn (OIDC)` - Implements part of the file's concrete application logic.
- L0115: `spring.security.oauth2.client.registration.linkedin.client-id=865bv2keay2hbv` - Security-related logic for tokens, OAuth, or authentication state.
- L0116: `spring.security.oauth2.client.registration.linkedin.client-secret=<REDACTED_SET_IN_ENV>` - Security-related logic for tokens, OAuth, or authentication state.
- L0117: `spring.security.oauth2.client.registration.linkedin.client-name=LinkedIn` - Security-related logic for tokens, OAuth, or authentication state.
- L0118: `spring.security.oauth2.client.registration.linkedin.authorization-grant-type=authorization_code` - Security-related logic for tokens, OAuth, or authentication state.
- L0119: `spring.security.oauth2.client.registration.linkedin.client-authentication-method=client_secret_post` - Security-related logic for tokens, OAuth, or authentication state.
- L0120: `spring.security.oauth2.client.registration.linkedin.redirect-uri=http://localhost:8080/login/oauth2/code/linkedin` - Security-related logic for tokens, OAuth, or authentication state.
- L0121: `spring.security.oauth2.client.registration.linkedin.scope=openid,profile,email` - Security-related logic for tokens, OAuth, or authentication state.
- L0122: `spring.security.oauth2.client.registration.linkedin.provider=linkedin` - Security-related logic for tokens, OAuth, or authentication state.
- L0123: `# Use LinkedIn OIDC discovery metadata to avoid endpoint mismatches.` - Implements part of the file's concrete application logic.
- L0124: `spring.security.oauth2.client.provider.linkedin.issuer-uri=https://www.linkedin.com/oauth` - Security-related logic for tokens, OAuth, or authentication state.
- L0125: `spring.security.oauth2.client.provider.linkedin.authorization-uri=https://www.linkedin.com/oauth/v2/authorization` - Security-related logic for tokens, OAuth, or authentication state.
- L0126: `spring.security.oauth2.client.provider.linkedin.token-uri=https://www.linkedin.com/oauth/v2/accessToken` - Security-related logic for tokens, OAuth, or authentication state.
- L0127: `spring.security.oauth2.client.provider.linkedin.user-info-uri=https://api.linkedin.com/v2/userinfo` - Security-related logic for tokens, OAuth, or authentication state.
- L0128: `spring.security.oauth2.client.provider.linkedin.jwk-set-uri=https://www.linkedin.com/oauth/openid/jwks` - Security-related logic for tokens, OAuth, or authentication state.
- L0129: `spring.security.oauth2.client.provider.linkedin.user-name-attribute=sub` - Security-related logic for tokens, OAuth, or authentication state.
- L0130: `` - Blank line used to separate logical blocks for readability.
- L0131: `otp.expiration.minutes=5` - Credential or recovery logic for authentication safety.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 03: `backend/src/main/java/com/auth/AuthApplication.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 21

### Line-by-Line Walkthrough
- L0001: `package com.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.boot.SpringApplication;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.boot.autoconfigure.SpringBootApplication;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0007: ` * Main application class for the Authentication Backend.` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * This Spring Boot application provides:` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` * - JWT-based authentication` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` * - OTP email verification` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` * - Password reset functionality` - JavaDoc/comment line documenting intent and behavior.
- L0012: ` * - Role-based access control` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0014: `@SpringBootApplication` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `public class AuthApplication {` - Defines the core type and responsibility boundary for this file.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    /** Starts the Spring Boot authentication application. */` - JavaDoc/comment line documenting intent and behavior.
- L0018: `    public static void main(String[] args) {` - Declares a method signature, contract, or constructor entry point.
- L0019: `        SpringApplication.run(AuthApplication.class, args);` - Implements part of the file's concrete application logic.
- L0020: `    }` - Closes the current scope block.
- L0021: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 04: `backend/src/main/java/com/auth/config/CorsConfig.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 47

### Line-by-Line Walkthrough
- L0001: `package com.auth.config;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.context.annotation.Bean;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.context.annotation.Configuration;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.web.cors.CorsConfiguration;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.web.cors.CorsConfigurationSource;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.web.cors.UrlBasedCorsConfigurationSource;` - Imports a type required by this file to compile and run.
- L0009: `` - Blank line used to separate logical blocks for readability.
- L0010: `import java.util.Arrays;` - Imports a type required by this file to compile and run.
- L0011: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0012: `import java.util.stream.Collectors;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0015: ` * CORS configuration to allow requests from React frontend.` - JavaDoc/comment line documenting intent and behavior.
- L0016: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `@Configuration` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `public class CorsConfig {` - Defines the core type and responsibility boundary for this file.
- L0019: `` - Blank line used to separate logical blocks for readability.
- L0020: `    private static final long DEFAULT_MAX_AGE_SECONDS = 3600L;` - Implements part of the file's concrete application logic.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `    @Value("${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `    private String allowedOrigins;` - Implements part of the file's concrete application logic.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    /** Registers allowed origins, methods, and headers for browser-to-backend requests. */` - JavaDoc/comment line documenting intent and behavior.
- L0026: `    @Bean` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `    public CorsConfigurationSource corsConfigurationSource() {` - Declares a method signature, contract, or constructor entry point.
- L0028: `        CorsConfiguration configuration = new CorsConfiguration();` - Implements part of the file's concrete application logic.
- L0029: `        configuration.setAllowedOrigins(parseAllowedOrigins(allowedOrigins));` - Implements part of the file's concrete application logic.
- L0030: `        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));` - Implements part of the file's concrete application logic.
- L0031: `        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));` - Implements part of the file's concrete application logic.
- L0032: `        configuration.setExposedHeaders(List.of("Authorization"));` - Implements part of the file's concrete application logic.
- L0033: `        configuration.setAllowCredentials(true);` - Implements part of the file's concrete application logic.
- L0034: `        configuration.setMaxAge(DEFAULT_MAX_AGE_SECONDS);` - Implements part of the file's concrete application logic.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();` - Implements part of the file's concrete application logic.
- L0037: `        source.registerCorsConfiguration("/**", configuration);` - Implements part of the file's concrete application logic.
- L0038: `        return source;` - Returns data to caller after applying current method logic.
- L0039: `    }` - Closes the current scope block.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    private List<String> parseAllowedOrigins(String originList) {` - Declares a method signature, contract, or constructor entry point.
- L0042: `        return Arrays.stream(originList.split(","))` - Returns data to caller after applying current method logic.
- L0043: `                .map(String::trim)` - Implements part of the file's concrete application logic.
- L0044: `                .filter(origin -> !origin.isBlank())` - Implements part of the file's concrete application logic.
- L0045: `                .collect(Collectors.toList());` - Implements part of the file's concrete application logic.
- L0046: `    }` - Closes the current scope block.
- L0047: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 05: `backend/src/main/java/com/auth/config/DataInitializer.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 79

### Line-by-Line Walkthrough
- L0001: `package com.auth.config;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.repository.RoleRepository;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.repository.UserRepository;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0008: `import lombok.extern.slf4j.Slf4j;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.boot.CommandLineRunner;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `import java.util.HashSet;` - Imports a type required by this file to compile and run.
- L0015: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0016: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0019: ` * Data initializer to create default roles and admin user on startup.` - JavaDoc/comment line documenting intent and behavior.
- L0020: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0021: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `@Slf4j` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `public class DataInitializer implements CommandLineRunner {` - Defines the core type and responsibility boundary for this file.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    private static final String DEFAULT_ADMIN_NAME = "Admin";` - Implements part of the file's concrete application logic.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    private final RoleRepository roleRepository;` - Repository usage handles persistence access to database records.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    private final UserRepository userRepository;` - Repository usage handles persistence access to database records.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `    private final PasswordEncoder passwordEncoder;` - Credential or recovery logic for authentication safety.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `    @Value("${app.seed.admin.name:" + DEFAULT_ADMIN_NAME + "}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0035: `    private String seedAdminName;` - Implements part of the file's concrete application logic.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `    @Value("${app.seed.admin.email:admin@admin.com}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0038: `    private String seedAdminEmail;` - Implements part of the file's concrete application logic.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    @Value("${app.seed.admin.password:admin123}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0041: `    private String seedAdminPassword;` - Credential or recovery logic for authentication safety.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `    /** Seeds default roles and a local admin account if they are missing at startup. */` - JavaDoc/comment line documenting intent and behavior.
- L0044: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    public void run(String... args) {` - Declares a method signature, contract, or constructor entry point.
- L0046: `        Role userRole = findOrCreateRole(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0047: `        Role adminRole = findOrCreateRole(Role.RoleName.ROLE_ADMIN);` - Authorization rule, authority mapping, or role handling line.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `        if (!userRepository.existsByEmail(seedAdminEmail)) {` - Conditional branch enforcing a business rule or guard path.
- L0050: `            User admin = new User();` - Implements part of the file's concrete application logic.
- L0051: `            admin.setName(seedAdminName);` - Implements part of the file's concrete application logic.
- L0052: `            admin.setEmail(seedAdminEmail);` - Implements part of the file's concrete application logic.
- L0053: `            admin.setPassword(passwordEncoder.encode(seedAdminPassword));` - Credential or recovery logic for authentication safety.
- L0054: `            admin.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `            Set<Role> adminRoles = new HashSet<>();` - Authorization rule, authority mapping, or role handling line.
- L0057: `            adminRoles.add(adminRole);` - Authorization rule, authority mapping, or role handling line.
- L0058: `            adminRoles.add(userRole);` - Authorization rule, authority mapping, or role handling line.
- L0059: `            admin.setRoles(adminRoles);` - Authorization rule, authority mapping, or role handling line.
- L0060: `` - Blank line used to separate logical blocks for readability.
- L0061: `            userRepository.save(admin);` - Repository usage handles persistence access to database records.
- L0062: `            log.info("Default admin user created: {}", seedAdminEmail);` - Structured log statement for traceability and diagnostics.
- L0063: `        }` - Closes the current scope block.
- L0064: `    }` - Closes the current scope block.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `    private Role findOrCreateRole(Role.RoleName roleName) {` - Declares a method signature, contract, or constructor entry point.
- L0067: `        Optional<Role> roleOpt = roleRepository.findByName(roleName);` - Repository usage handles persistence access to database records.
- L0068: `        if (roleOpt.isPresent()) {` - Conditional branch enforcing a business rule or guard path.
- L0069: `            return roleOpt.get();` - Returns data to caller after applying current method logic.
- L0070: `        }` - Closes the current scope block.
- L0071: `        return createRole(roleName);` - Returns data to caller after applying current method logic.
- L0072: `    }` - Closes the current scope block.
- L0073: `` - Blank line used to separate logical blocks for readability.
- L0074: `    private Role createRole(Role.RoleName roleName) {` - Declares a method signature, contract, or constructor entry point.
- L0075: `        Role role = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0076: `        role.setName(roleName);` - Authorization rule, authority mapping, or role handling line.
- L0077: `        return roleRepository.save(role);` - Returns data to caller after applying current method logic.
- L0078: `    }` - Closes the current scope block.
- L0079: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 06: `backend/src/main/java/com/auth/config/PasswordConfig.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 16

### Line-by-Line Walkthrough
- L0001: `package com.auth.config;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.context.annotation.Bean;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.context.annotation.Configuration;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `@Configuration` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0009: `public class PasswordConfig {` - Defines the core type and responsibility boundary for this file.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `    /** Exposes BCrypt password encoder used for hashing user passwords. */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `    @Bean` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `    public PasswordEncoder passwordEncoder() {` - Declares a method signature, contract, or constructor entry point.
- L0014: `        return new BCryptPasswordEncoder();` - Returns data to caller after applying current method logic.
- L0015: `    }` - Closes the current scope block.
- L0016: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 07: `backend/src/main/java/com/auth/config/SecurityConfig.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 81

### Line-by-Line Walkthrough
- L0001: `package com.auth.config;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.security.CustomUserDetailsService;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.security.JwtAuthFilter;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.security.LinkedInAuthorizationRequestResolver;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.security.OAuth2AuthenticationFailureHandler;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.security.OAuth2AuthenticationSuccessHandler;` - Imports a type required by this file to compile and run.
- L0008: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.context.annotation.Bean;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.context.annotation.Configuration;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.security.authentication.AuthenticationManager;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.security.authentication.dao.DaoAuthenticationProvider;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;` - Imports a type required by this file to compile and run.
- L0015: `import org.springframework.security.config.annotation.web.builders.HttpSecurity;` - Imports a type required by this file to compile and run.
- L0016: `import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;` - Imports a type required by this file to compile and run.
- L0017: `import org.springframework.security.config.http.SessionCreationPolicy;` - Imports a type required by this file to compile and run.
- L0018: `import org.springframework.security.config.Customizer;` - Imports a type required by this file to compile and run.
- L0019: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0020: `import org.springframework.security.web.SecurityFilterChain;` - Imports a type required by this file to compile and run.
- L0021: `import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `@Configuration` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `@EnableWebSecurity` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `@EnableMethodSecurity` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0026: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `public class SecurityConfig {` - Defines the core type and responsibility boundary for this file.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    private static final String AUTHORITY_ADMIN = "ROLE_ADMIN";` - Authorization rule, authority mapping, or role handling line.
- L0030: `    private static final String AUTHORITY_USER = "ROLE_USER";` - Authorization rule, authority mapping, or role handling line.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `    private final JwtAuthFilter jwtAuthFilter;` - Security-related logic for tokens, OAuth, or authentication state.
- L0033: `    private final OAuth2AuthenticationSuccessHandler successHandler;` - Security-related logic for tokens, OAuth, or authentication state.
- L0034: `    private final OAuth2AuthenticationFailureHandler failureHandler;` - Security-related logic for tokens, OAuth, or authentication state.
- L0035: `    private final LinkedInAuthorizationRequestResolver linkedInAuthorizationRequestResolver;` - Implements part of the file's concrete application logic.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `    /** Configures stateless security, endpoint authorization, OAuth2, and JWT filter order. */` - JavaDoc/comment line documenting intent and behavior.
- L0038: `    @Bean` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0039: `    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {` - Declares a method signature, contract, or constructor entry point.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `        http` - Implements part of the file's concrete application logic.
- L0042: `                .cors(Customizer.withDefaults())` - Implements part of the file's concrete application logic.
- L0043: `                .csrf(csrf -> csrf.disable())` - Implements part of the file's concrete application logic.
- L0044: `                .sessionManagement(session ->` - Implements part of the file's concrete application logic.
- L0045: `                        // OAuth2 login requires temporary session storage for state/nonce validation.` - Inline comment for maintainability and context.
- L0046: `                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))` - Implements part of the file's concrete application logic.
- L0047: `                .authorizeHttpRequests(auth -> auth` - Implements part of the file's concrete application logic.
- L0048: `                        .requestMatchers("/api/auth/**").permitAll()` - Implements part of the file's concrete application logic.
- L0049: `                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()` - Security-related logic for tokens, OAuth, or authentication state.
- L0050: `                        .requestMatchers("/api/admin/**").hasAuthority(AUTHORITY_ADMIN)` - Authorization rule, authority mapping, or role handling line.
- L0051: `                        .requestMatchers("/api/user/**").hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN)` - Authorization rule, authority mapping, or role handling line.
- L0052: `                        .anyRequest().authenticated()` - Implements part of the file's concrete application logic.
- L0053: `                )` - Implements part of the file's concrete application logic.
- L0054: `                .oauth2Login(oauth -> oauth` - Security-related logic for tokens, OAuth, or authentication state.
- L0055: `                        .authorizationEndpoint(endpoint ->` - Implements part of the file's concrete application logic.
- L0056: `                                endpoint.authorizationRequestResolver(linkedInAuthorizationRequestResolver))` - Implements part of the file's concrete application logic.
- L0057: `                        .successHandler(successHandler)` - Implements part of the file's concrete application logic.
- L0058: `                        .failureHandler(failureHandler)` - Implements part of the file's concrete application logic.
- L0059: `                )` - Implements part of the file's concrete application logic.
- L0060: `                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);` - Security-related logic for tokens, OAuth, or authentication state.
- L0061: `` - Blank line used to separate logical blocks for readability.
- L0062: `        return http.build();` - Returns data to caller after applying current method logic.
- L0063: `    }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `    /** Exposes AuthenticationManager from Spring's AuthenticationConfiguration. */` - JavaDoc/comment line documenting intent and behavior.
- L0066: `    @Bean` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0067: `    public AuthenticationManager authenticationManager(` - Declares a method signature, contract, or constructor entry point.
- L0068: `            AuthenticationConfiguration config) throws Exception {` - Opens a new scope block for type, method, or control flow.
- L0069: `        return config.getAuthenticationManager();` - Returns data to caller after applying current method logic.
- L0070: `    }` - Closes the current scope block.
- L0071: `` - Blank line used to separate logical blocks for readability.
- L0072: `    /** Builds DAO authentication provider with explicit user details service and encoder. */` - JavaDoc/comment line documenting intent and behavior.
- L0073: `    @Bean` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0074: `    public DaoAuthenticationProvider authenticationProvider(` - Declares a method signature, contract, or constructor entry point.
- L0075: `            CustomUserDetailsService userDetailsService,` - Service interaction applies business logic or orchestration.
- L0076: `            PasswordEncoder passwordEncoder) {` - Opens a new scope block for type, method, or control flow.
- L0077: `        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);` - Service interaction applies business logic or orchestration.
- L0078: `        provider.setPasswordEncoder(passwordEncoder);` - Credential or recovery logic for authentication safety.
- L0079: `        return provider;` - Returns data to caller after applying current method logic.
- L0080: `    }` - Closes the current scope block.
- L0081: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 08: `backend/src/main/java/com/auth/controller/AdminController.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 54

### Line-by-Line Walkthrough
- L0001: `package com.auth.controller;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AdminDashboardDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.AdminService;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.data.domain.Page;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.http.ResponseEntity;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.access.prepost.PreAuthorize;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.security.core.Authentication;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.web.bind.annotation.*;` - Imports a type required by this file to compile and run.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` * REST controller for admin dashboard.` - JavaDoc/comment line documenting intent and behavior.
- L0015: ` * Protected endpoints accessible only to ADMIN users.` - JavaDoc/comment line documenting intent and behavior.
- L0016: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `@RestController` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `@RequestMapping("/api/admin")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `@PreAuthorize("hasRole('ADMIN')")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `public class AdminController {` - Defines the core type and responsibility boundary for this file.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    private static final String DEFAULT_PAGE = "0";` - Implements part of the file's concrete application logic.
- L0024: `    private static final String DEFAULT_PAGE_SIZE = "20";` - Implements part of the file's concrete application logic.
- L0025: `    private static final String DEFAULT_SORT_FIELD = "createdAt";` - Implements part of the file's concrete application logic.
- L0026: `    private static final String DEFAULT_SORT_DIRECTION = "desc";` - Implements part of the file's concrete application logic.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    private final AdminService adminService;` - Service interaction applies business logic or orchestration.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0031: `     * Get admin dashboard data.` - JavaDoc/comment line documenting intent and behavior.
- L0032: `     * GET /api/admin/dashboard` - JavaDoc/comment line documenting intent and behavior.
- L0033: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0034: `    @GetMapping("/dashboard")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0035: `    public ResponseEntity<AdminDashboardDto> getDashboard(Authentication authentication) {` - Declares a method signature, contract, or constructor entry point.
- L0036: `        return ResponseEntity.ok(adminService.getDashboard(authentication.getName()));` - Returns data to caller after applying current method logic.
- L0037: `    }` - Closes the current scope block.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0040: `     * Get users list with pagination/filtering/search.` - JavaDoc/comment line documenting intent and behavior.
- L0041: `     * GET /api/admin/users` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    @GetMapping("/users")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    public ResponseEntity<Page<UserDto>> getAllUsers(` - Declares a method signature, contract, or constructor entry point.
- L0045: `            @RequestParam(defaultValue = DEFAULT_PAGE) int page,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0047: `            @RequestParam(required = false) String search,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0048: `            @RequestParam(required = false) Boolean enabled,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0049: `            @RequestParam(required = false) String role,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0050: `            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortBy,` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0051: `            @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION) String sortDir) {` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0052: `        return ResponseEntity.ok(adminService.getUsers(page, size, search, enabled, role, sortBy, sortDir));` - Returns data to caller after applying current method logic.
- L0053: `    }` - Closes the current scope block.
- L0054: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 09: `backend/src/main/java/com/auth/controller/AuthController.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 168

### Line-by-Line Walkthrough
- L0001: `package com.auth.controller;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.security.RefreshTokenCookieService;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.AuthResponse;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.dto.LoginRequest;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.dto.OtpVerifyRequest;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.dto.RegisterRequest;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.dto.ResetPasswordRequest;` - Imports a type required by this file to compile and run.
- L0010: `import com.auth.dto.TokenRefreshRequest;` - Imports a type required by this file to compile and run.
- L0011: `import com.auth.dto.UpdatePasswordRequest;` - Imports a type required by this file to compile and run.
- L0012: `import com.auth.service.auth.AuthTokenService;` - Imports a type required by this file to compile and run.
- L0013: `import com.auth.service.AuthService;` - Imports a type required by this file to compile and run.
- L0014: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0015: `import jakarta.servlet.http.Cookie;` - Imports a type required by this file to compile and run.
- L0016: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0017: `import jakarta.servlet.http.HttpServletResponse;` - Imports a type required by this file to compile and run.
- L0018: `import jakarta.validation.Valid;` - Imports a type required by this file to compile and run.
- L0019: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0020: `import org.springframework.http.HttpHeaders;` - Imports a type required by this file to compile and run.
- L0021: `import org.springframework.http.ResponseEntity;` - Imports a type required by this file to compile and run.
- L0022: `import org.springframework.web.bind.annotation.*;` - Imports a type required by this file to compile and run.
- L0023: `` - Blank line used to separate logical blocks for readability.
- L0024: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0025: ` * REST controller for authentication endpoints.` - JavaDoc/comment line documenting intent and behavior.
- L0026: ` * Handles registration, login, OTP verification, and password reset.` - JavaDoc/comment line documenting intent and behavior.
- L0027: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0028: `@RestController` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `@RequestMapping("/api/auth")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0030: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `public class AuthController {` - Defines the core type and responsibility boundary for this file.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `    private static final String LOGOUT_SUCCESS_MESSAGE = "Logged out successfully.";` - Implements part of the file's concrete application logic.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `    private final AuthService authService;` - Service interaction applies business logic or orchestration.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `    private final AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    private final RefreshTokenCookieService refreshTokenCookieService;` - Service interaction applies business logic or orchestration.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     * Register a new user.` - JavaDoc/comment line documenting intent and behavior.
- L0043: `     * POST /api/auth/register` - JavaDoc/comment line documenting intent and behavior.
- L0044: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0045: `    @PostMapping("/register")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0047: `        MessageResponse response = authService.register(request);` - Service interaction applies business logic or orchestration.
- L0048: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0049: `    }` - Closes the current scope block.
- L0050: `` - Blank line used to separate logical blocks for readability.
- L0051: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0052: `     * Verify email with OTP.` - JavaDoc/comment line documenting intent and behavior.
- L0053: `     * POST /api/auth/verify-otp` - JavaDoc/comment line documenting intent and behavior.
- L0054: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0055: `    @PostMapping("/verify-otp")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0056: `    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0057: `        MessageResponse response = authService.verifyOtp(request);` - Service interaction applies business logic or orchestration.
- L0058: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0059: `    }` - Closes the current scope block.
- L0060: `` - Blank line used to separate logical blocks for readability.
- L0061: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0062: `     * Login user and return JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0063: `     * POST /api/auth/login` - JavaDoc/comment line documenting intent and behavior.
- L0064: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0065: `    @PostMapping("/login")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0066: `    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse httpResponse) {` - Declares a method signature, contract, or constructor entry point.
- L0067: `        AuthTokens authTokens = authService.login(request);` - Service interaction applies business logic or orchestration.
- L0068: `        setRefreshTokenCookie(httpResponse, authTokens.refreshToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0069: `        return ResponseEntity.ok(authTokens.response());` - Returns data to caller after applying current method logic.
- L0070: `    }` - Closes the current scope block.
- L0071: `` - Blank line used to separate logical blocks for readability.
- L0072: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0073: `     * Refresh access token using refresh token from secure cookie.` - JavaDoc/comment line documenting intent and behavior.
- L0074: `     * POST /api/auth/refresh` - JavaDoc/comment line documenting intent and behavior.
- L0075: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0076: `    @PostMapping("/refresh")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0077: `    public ResponseEntity<AuthResponse> refreshToken(` - Declares a method signature, contract, or constructor entry point.
- L0078: `            HttpServletRequest httpRequest,` - Implements part of the file's concrete application logic.
- L0079: `            HttpServletResponse httpResponse,` - Implements part of the file's concrete application logic.
- L0080: `            @RequestBody(required = false) TokenRefreshRequest request) {` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0081: `        String refreshToken = resolveRefreshToken(httpRequest, request);` - Security-related logic for tokens, OAuth, or authentication state.
- L0082: `` - Blank line used to separate logical blocks for readability.
- L0083: `        AuthTokens authTokens = authTokenService.refreshTokens(refreshToken);` - Service interaction applies business logic or orchestration.
- L0084: `        setRefreshTokenCookie(httpResponse, authTokens.refreshToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0085: `        return ResponseEntity.ok(authTokens.response());` - Returns data to caller after applying current method logic.
- L0086: `    }` - Closes the current scope block.
- L0087: `` - Blank line used to separate logical blocks for readability.
- L0088: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0089: `     * Logout and invalidate refresh token.` - JavaDoc/comment line documenting intent and behavior.
- L0090: `     * POST /api/auth/logout` - JavaDoc/comment line documenting intent and behavior.
- L0091: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0092: `    @PostMapping("/logout")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0093: `    public ResponseEntity<MessageResponse> logout(` - Declares a method signature, contract, or constructor entry point.
- L0094: `            HttpServletRequest httpRequest,` - Implements part of the file's concrete application logic.
- L0095: `            HttpServletResponse httpResponse,` - Implements part of the file's concrete application logic.
- L0096: `            @RequestBody(required = false) TokenRefreshRequest request) {` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0097: `        String refreshToken = resolveRefreshToken(httpRequest, request);` - Security-related logic for tokens, OAuth, or authentication state.
- L0098: `        authTokenService.revokeRefreshToken(refreshToken);` - Service interaction applies business logic or orchestration.
- L0099: `` - Blank line used to separate logical blocks for readability.
- L0100: `        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.clearRefreshTokenCookie());` - Service interaction applies business logic or orchestration.
- L0101: `        return ResponseEntity.ok(new MessageResponse(LOGOUT_SUCCESS_MESSAGE, true));` - Returns data to caller after applying current method logic.
- L0102: `    }` - Closes the current scope block.
- L0103: `` - Blank line used to separate logical blocks for readability.
- L0104: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0105: `     * Request password reset email.` - JavaDoc/comment line documenting intent and behavior.
- L0106: `     * POST /api/auth/reset-password` - JavaDoc/comment line documenting intent and behavior.
- L0107: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0108: `    @PostMapping("/reset-password")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0109: `    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0110: `        MessageResponse response = authService.resetPassword(request);` - Service interaction applies business logic or orchestration.
- L0111: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0112: `    }` - Closes the current scope block.
- L0113: `` - Blank line used to separate logical blocks for readability.
- L0114: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0115: `     * Update password with reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0116: `     * POST /api/auth/update-password` - JavaDoc/comment line documenting intent and behavior.
- L0117: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0118: `    @PostMapping("/update-password")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0119: `    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0120: `        MessageResponse response = authService.updatePassword(request);` - Service interaction applies business logic or orchestration.
- L0121: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0122: `    }` - Closes the current scope block.
- L0123: `` - Blank line used to separate logical blocks for readability.
- L0124: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0125: `     * Resend OTP for email verification.` - JavaDoc/comment line documenting intent and behavior.
- L0126: `     * POST /api/auth/resend-otp` - JavaDoc/comment line documenting intent and behavior.
- L0127: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0128: `    @PostMapping("/resend-otp")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0129: `    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email) {` - Declares a method signature, contract, or constructor entry point.
- L0130: `        MessageResponse response = authService.resendOtp(email);` - Service interaction applies business logic or orchestration.
- L0131: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0132: `    }` - Closes the current scope block.
- L0133: `` - Blank line used to separate logical blocks for readability.
- L0134: `    /** Resolves refresh token from request body first, then from configured cookie. */` - JavaDoc/comment line documenting intent and behavior.
- L0135: `    private String resolveRefreshToken(HttpServletRequest request, TokenRefreshRequest body) {` - Declares a method signature, contract, or constructor entry point.
- L0136: `        if (body != null && hasText(body.getRefreshToken())) {` - Conditional branch enforcing a business rule or guard path.
- L0137: `            return body.getRefreshToken();` - Returns data to caller after applying current method logic.
- L0138: `        }` - Closes the current scope block.
- L0139: `        return extractTokenFromCookies(request);` - Returns data to caller after applying current method logic.
- L0140: `    }` - Closes the current scope block.
- L0141: `` - Blank line used to separate logical blocks for readability.
- L0142: `    private String extractTokenFromCookies(HttpServletRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0143: `        Cookie[] cookies = request.getCookies();` - Implements part of the file's concrete application logic.
- L0144: `        if (cookies == null) {` - Conditional branch enforcing a business rule or guard path.
- L0145: `            return null;` - Returns data to caller after applying current method logic.
- L0146: `        }` - Closes the current scope block.
- L0147: `` - Blank line used to separate logical blocks for readability.
- L0148: `        String refreshCookieName = refreshTokenCookieService.getCookieName();` - Service interaction applies business logic or orchestration.
- L0149: `        for (Cookie cookie : cookies) {` - Iterates through values to process collections or repeated logic.
- L0150: `            if (refreshCookieName.equals(cookie.getName())) {` - Conditional branch enforcing a business rule or guard path.
- L0151: `                String cookieValue = cookie.getValue();` - Implements part of the file's concrete application logic.
- L0152: `                if (hasText(cookieValue)) {` - Conditional branch enforcing a business rule or guard path.
- L0153: `                    return cookieValue;` - Returns data to caller after applying current method logic.
- L0154: `                }` - Closes the current scope block.
- L0155: `            }` - Closes the current scope block.
- L0156: `        }` - Closes the current scope block.
- L0157: `` - Blank line used to separate logical blocks for readability.
- L0158: `        return null;` - Returns data to caller after applying current method logic.
- L0159: `    }` - Closes the current scope block.
- L0160: `` - Blank line used to separate logical blocks for readability.
- L0161: `    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {` - Declares a method signature, contract, or constructor entry point.
- L0162: `        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookieService.buildRefreshTokenCookie(refreshToken));` - Service interaction applies business logic or orchestration.
- L0163: `    }` - Closes the current scope block.
- L0164: `` - Blank line used to separate logical blocks for readability.
- L0165: `    private boolean hasText(String value) {` - Declares a method signature, contract, or constructor entry point.
- L0166: `        return value != null && !value.isBlank();` - Returns data to caller after applying current method logic.
- L0167: `    }` - Closes the current scope block.
- L0168: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 10: `backend/src/main/java/com/auth/controller/UserController.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 61

### Line-by-Line Walkthrough
- L0001: `package com.auth.controller;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.ChangePasswordRequest;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.UserDashboardDto;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.service.AuthService;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.service.UserPortalService;` - Imports a type required by this file to compile and run.
- L0009: `import jakarta.validation.Valid;` - Imports a type required by this file to compile and run.
- L0010: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.http.ResponseEntity;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.security.access.prepost.PreAuthorize;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.security.core.Authentication;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.web.bind.annotation.*;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0017: ` * REST controller for user dashboard.` - JavaDoc/comment line documenting intent and behavior.
- L0018: ` * Protected endpoints accessible only to authenticated users.` - JavaDoc/comment line documenting intent and behavior.
- L0019: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0020: `@RestController` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `@RequestMapping("/api/user")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `public class UserController {` - Defines the core type and responsibility boundary for this file.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    private final AuthService authService;` - Service interaction applies business logic or orchestration.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    private final UserPortalService userPortalService;` - Service interaction applies business logic or orchestration.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0030: `     * Get user dashboard data.` - JavaDoc/comment line documenting intent and behavior.
- L0031: `     * GET /api/user/dashboard` - JavaDoc/comment line documenting intent and behavior.
- L0032: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0033: `    @GetMapping("/dashboard")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0035: `    public ResponseEntity<UserDashboardDto> getDashboard(Authentication authentication) {` - Declares a method signature, contract, or constructor entry point.
- L0036: `        return ResponseEntity.ok(userPortalService.getDashboard(authentication.getName()));` - Returns data to caller after applying current method logic.
- L0037: `    }` - Closes the current scope block.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0040: `     * Get user profile.` - JavaDoc/comment line documenting intent and behavior.
- L0041: `     * GET /api/user/profile` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    @GetMapping("/profile")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    public ResponseEntity<UserDto> getProfile(Authentication authentication) {` - Declares a method signature, contract, or constructor entry point.
- L0046: `        return ResponseEntity.ok(userPortalService.getProfile(authentication.getName()));` - Returns data to caller after applying current method logic.
- L0047: `    }` - Closes the current scope block.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0050: `     * Change password.` - JavaDoc/comment line documenting intent and behavior.
- L0051: `     * POST /api/user/change-password` - JavaDoc/comment line documenting intent and behavior.
- L0052: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0053: `    @PostMapping("/change-password")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0054: `    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0055: `    public ResponseEntity<MessageResponse> changePassword(` - Declares a method signature, contract, or constructor entry point.
- L0056: `            Authentication authentication,` - Implements part of the file's concrete application logic.
- L0057: `            @Valid @RequestBody ChangePasswordRequest request) {` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0058: `        MessageResponse response = authService.changePassword(authentication.getName(), request);` - Service interaction applies business logic or orchestration.
- L0059: `        return ResponseEntity.ok(response);` - Returns data to caller after applying current method logic.
- L0060: `    }` - Closes the current scope block.
- L0061: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 11: `backend/src/main/java/com/auth/dto/AdminDashboardDto.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 16

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0008: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0009: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0010: `public class AdminDashboardDto {` - Defines the core type and responsibility boundary for this file.
- L0011: `    private String message;` - Implements part of the file's concrete application logic.
- L0012: `    private String admin;` - Implements part of the file's concrete application logic.
- L0013: `    private long totalUsers;` - Implements part of the file's concrete application logic.
- L0014: `    private long activeUsers;` - Implements part of the file's concrete application logic.
- L0015: `    private String timestamp;` - Implements part of the file's concrete application logic.
- L0016: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 12: `backend/src/main/java/com/auth/dto/AuthResponse.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 25

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0008: `` - Blank line used to separate logical blocks for readability.
- L0009: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` * DTO for authentication response containing access token metadata and user details.` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `public class AuthResponse {` - Defines the core type and responsibility boundary for this file.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    private String accessToken;` - Security-related logic for tokens, OAuth, or authentication state.
- L0018: `    private String tokenType;` - Security-related logic for tokens, OAuth, or authentication state.
- L0019: `    private long accessTokenExpiresInMs;` - Security-related logic for tokens, OAuth, or authentication state.
- L0020: `    private long refreshTokenExpiresInMs;` - Security-related logic for tokens, OAuth, or authentication state.
- L0021: `    private Long id;` - Implements part of the file's concrete application logic.
- L0022: `    private String name;` - Implements part of the file's concrete application logic.
- L0023: `    private String email;` - Implements part of the file's concrete application logic.
- L0024: `    private List<String> roles;` - Authorization rule, authority mapping, or role handling line.
- L0025: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 13: `backend/src/main/java/com/auth/dto/AuthTokens.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 7

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0004: ` * Internal login/refresh result containing API response payload and raw refresh token.` - JavaDoc/comment line documenting intent and behavior.
- L0005: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0006: `public record AuthTokens(AuthResponse response, String refreshToken) {` - Declares a method signature, contract, or constructor entry point.
- L0007: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 14: `backend/src/main/java/com/auth/dto/ChangePasswordRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 16

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.Size;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0008: `public class ChangePasswordRequest {` - Defines the core type and responsibility boundary for this file.
- L0009: `` - Blank line used to separate logical blocks for readability.
- L0010: `    @NotBlank(message = "Current password is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `    private String currentPassword;` - Credential or recovery logic for authentication safety.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    @NotBlank(message = "New password is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `    @Size(min = 6, message = "New password must be at least 6 characters")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `    private String newPassword;` - Credential or recovery logic for authentication safety.
- L0016: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 15: `backend/src/main/java/com/auth/dto/LoginRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 19

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.Email;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * DTO for user login request.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class LoginRequest {` - Defines the core type and responsibility boundary for this file.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    @NotBlank(message = "Email is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `    @Email(message = "Please provide a valid email")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `    private String email;` - Implements part of the file's concrete application logic.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    @NotBlank(message = "Password is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    private String password;` - Credential or recovery logic for authentication safety.
- L0019: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 16: `backend/src/main/java/com/auth/dto/MessageResponse.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 23

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * Generic message response DTO.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0012: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `public class MessageResponse {` - Defines the core type and responsibility boundary for this file.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `    private String message;` - Implements part of the file's concrete application logic.
- L0016: `    private boolean success;` - Implements part of the file's concrete application logic.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `    /** Creates a successful response with only a message payload. */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `    public MessageResponse(String message) {` - Declares a method signature, contract, or constructor entry point.
- L0020: `        this.message = message;` - Implements part of the file's concrete application logic.
- L0021: `        this.success = true;` - Implements part of the file's concrete application logic.
- L0022: `    }` - Closes the current scope block.
- L0023: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 17: `backend/src/main/java/com/auth/dto/OtpVerifyRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 19

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.Email;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * DTO for OTP verification request.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class OtpVerifyRequest {` - Defines the core type and responsibility boundary for this file.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    @NotBlank(message = "Email is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `    @Email(message = "Please provide a valid email")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `    private String email;` - Implements part of the file's concrete application logic.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    @NotBlank(message = "OTP is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    private String otp;` - Credential or recovery logic for authentication safety.
- L0019: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 18: `backend/src/main/java/com/auth/dto/RegisterRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 24

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.Email;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0005: `import jakarta.validation.constraints.Size;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` * DTO for user registration request.` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0011: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0012: `public class RegisterRequest {` - Defines the core type and responsibility boundary for this file.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `    @NotBlank(message = "Name is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `    private String name;` - Implements part of the file's concrete application logic.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    @NotBlank(message = "Email is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    @Email(message = "Please provide a valid email")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `    private String email;` - Implements part of the file's concrete application logic.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    @NotBlank(message = "Password is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `    @Size(min = 6, message = "Password must be at least 6 characters")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `    private String password;` - Credential or recovery logic for authentication safety.
- L0024: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 19: `backend/src/main/java/com/auth/dto/ResetPasswordRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 16

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.Email;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * DTO for password reset request.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class ResetPasswordRequest {` - Defines the core type and responsibility boundary for this file.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    @NotBlank(message = "Email is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `    @Email(message = "Please provide a valid email")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `    private String email;` - Implements part of the file's concrete application logic.
- L0016: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 20: `backend/src/main/java/com/auth/dto/TokenRefreshRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 12

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0006: ` * Optional request body for refresh token endpoint.` - JavaDoc/comment line documenting intent and behavior.
- L0007: ` * Cookie is preferred; this exists for non-browser clients.` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0009: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0010: `public class TokenRefreshRequest {` - Defines the core type and responsibility boundary for this file.
- L0011: `    private String refreshToken;` - Security-related logic for tokens, OAuth, or authentication state.
- L0012: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 21: `backend/src/main/java/com/auth/dto/UpdatePasswordRequest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 19

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.validation.constraints.NotBlank;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.validation.constraints.Size;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * DTO for updating password with reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class UpdatePasswordRequest {` - Defines the core type and responsibility boundary for this file.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    @NotBlank(message = "Token is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `    private String token;` - Security-related logic for tokens, OAuth, or authentication state.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `    @NotBlank(message = "Password is required")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `    @Size(min = 6, message = "Password must be at least 6 characters")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    private String newPassword;` - Credential or recovery logic for authentication safety.
- L0019: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 22: `backend/src/main/java/com/auth/dto/UserDashboardDto.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 17

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0006: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0009: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0010: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class UserDashboardDto {` - Defines the core type and responsibility boundary for this file.
- L0012: `    private String message;` - Implements part of the file's concrete application logic.
- L0013: `    private String user;` - Implements part of the file's concrete application logic.
- L0014: `    private String email;` - Implements part of the file's concrete application logic.
- L0015: `    private List<String> roles;` - Authorization rule, authority mapping, or role handling line.
- L0016: `    private String timestamp;` - Implements part of the file's concrete application logic.
- L0017: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 23: `backend/src/main/java/com/auth/dto/UserDto.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 14

### Line-by-Line Walkthrough
- L0001: `package com.auth.dto;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0004: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0007: `public class UserDto {` - Defines the core type and responsibility boundary for this file.
- L0008: `    private Long id;` - Implements part of the file's concrete application logic.
- L0009: `    private String name;` - Implements part of the file's concrete application logic.
- L0010: `    private String email;` - Implements part of the file's concrete application logic.
- L0011: `    private Set<String> roles;` - Authorization rule, authority mapping, or role handling line.
- L0012: `    private boolean enabled;` - Implements part of the file's concrete application logic.
- L0013: `    private java.time.LocalDateTime createdAt;` - Implements part of the file's concrete application logic.
- L0014: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 24: `backend/src/main/java/com/auth/entity/Role.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 31

### Line-by-Line Walkthrough
- L0001: `package com.auth.entity;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.persistence.*;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` * Role entity representing user roles in the system.` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` * Supports role-based access control (RBAC).` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `@Entity` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `@Table(name = "roles")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0016: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `public class Role {` - Defines the core type and responsibility boundary for this file.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    @Id` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `    @GeneratedValue(strategy = GenerationType.IDENTITY)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `    private Long id;` - Implements part of the file's concrete application logic.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    @Enumerated(EnumType.STRING)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `    @Column(length = 20, unique = true, nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    private RoleName name;` - Authorization rule, authority mapping, or role handling line.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    public enum RoleName {` - Defines enum constants used as constrained values.
- L0028: `        ROLE_USER,` - Authorization rule, authority mapping, or role handling line.
- L0029: `        ROLE_ADMIN` - Authorization rule, authority mapping, or role handling line.
- L0030: `    }` - Closes the current scope block.
- L0031: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 25: `backend/src/main/java/com/auth/entity/User.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 94

### Line-by-Line Walkthrough
- L0001: `package com.auth.entity;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.persistence.*;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.AllArgsConstructor;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.Data;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.NoArgsConstructor;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0009: `import java.util.HashSet;` - Imports a type required by this file to compile and run.
- L0010: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` * User entity representing application users.` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` * Contains authentication details, verification status, and roles.` - JavaDoc/comment line documenting intent and behavior.
- L0015: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0016: `@Entity` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `@Table(name = "users")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `@Data` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `@NoArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `@AllArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `public class User {` - Defines the core type and responsibility boundary for this file.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    @Id` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `    @GeneratedValue(strategy = GenerationType.IDENTITY)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    private Long id;` - Implements part of the file's concrete application logic.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    @Column(nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `    private String name;` - Implements part of the file's concrete application logic.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    @Column(nullable = false, unique = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `    private String email;` - Implements part of the file's concrete application logic.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `    @Column(nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `    private String password;` - Credential or recovery logic for authentication safety.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    @Column(nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0037: `    private boolean enabled = false;` - Implements part of the file's concrete application logic.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    @Column(name = "verification_otp")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0040: `    private String verificationOtp;` - Credential or recovery logic for authentication safety.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    @Column(name = "otp_expiry")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0043: `    private LocalDateTime otpExpiry;` - Credential or recovery logic for authentication safety.
- L0044: `` - Blank line used to separate logical blocks for readability.
- L0045: `    @Column(name = "reset_token")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `    private String resetToken;` - Security-related logic for tokens, OAuth, or authentication state.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `    @Column(name = "reset_token_expiry")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0049: `    private LocalDateTime resetTokenExpiry;` - Security-related logic for tokens, OAuth, or authentication state.
- L0050: `` - Blank line used to separate logical blocks for readability.
- L0051: `    @Column(name = "refresh_token", length = 512)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0052: `    private String refreshToken;` - Security-related logic for tokens, OAuth, or authentication state.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `    @Column(name = "refresh_token_expiry")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0055: `    private LocalDateTime refreshTokenExpiry;` - Security-related logic for tokens, OAuth, or authentication state.
- L0056: `` - Blank line used to separate logical blocks for readability.
- L0057: `    @Column(name = "failed_login_attempts", nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0058: `    private int failedLoginAttempts = 0;` - Implements part of the file's concrete application logic.
- L0059: `` - Blank line used to separate logical blocks for readability.
- L0060: `    @Column(name = "account_locked_until")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0061: `    private LocalDateTime accountLockedUntil;` - Implements part of the file's concrete application logic.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `    @Column(name = "failed_otp_attempts", nullable = false)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0064: `    private int failedOtpAttempts = 0;` - Credential or recovery logic for authentication safety.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `    @Column(name = "otp_locked_until")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0067: `    private LocalDateTime otpLockedUntil;` - Credential or recovery logic for authentication safety.
- L0068: `` - Blank line used to separate logical blocks for readability.
- L0069: `    @Column(name = "auth_provider")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0070: `    private String authProvider;` - Implements part of the file's concrete application logic.
- L0071: `` - Blank line used to separate logical blocks for readability.
- L0072: `    @Column(name = "created_at")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0073: `    private LocalDateTime createdAt;` - Implements part of the file's concrete application logic.
- L0074: `` - Blank line used to separate logical blocks for readability.
- L0075: `    @Column(name = "updated_at")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0076: `    private LocalDateTime updatedAt;` - Implements part of the file's concrete application logic.
- L0077: `` - Blank line used to separate logical blocks for readability.
- L0078: `    @ManyToMany(fetch = FetchType.EAGER)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0079: `    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0080: `    private Set<Role> roles = new HashSet<>();` - Declares a method signature, contract, or constructor entry point.
- L0081: `` - Blank line used to separate logical blocks for readability.
- L0082: `    /** Initializes create/update timestamps before first persistence. */` - JavaDoc/comment line documenting intent and behavior.
- L0083: `    @PrePersist` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0084: `    protected void onCreate() {` - Declares a method signature, contract, or constructor entry point.
- L0085: `        createdAt = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0086: `        updatedAt = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0087: `    }` - Closes the current scope block.
- L0088: `` - Blank line used to separate logical blocks for readability.
- L0089: `    /** Updates the modification timestamp before each entity update. */` - JavaDoc/comment line documenting intent and behavior.
- L0090: `    @PreUpdate` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0091: `    protected void onUpdate() {` - Declares a method signature, contract, or constructor entry point.
- L0092: `        updatedAt = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0093: `    }` - Closes the current scope block.
- L0094: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 26: `backend/src/main/java/com/auth/exception/AccountLockedException.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 17

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.Getter;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0006: ` * Raised when a user account is temporarily locked due to repeated failed attempts.` - JavaDoc/comment line documenting intent and behavior.
- L0007: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0008: `@Getter` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0009: `public class AccountLockedException extends RuntimeException {` - Defines the core type and responsibility boundary for this file.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `    private final long retryAfterSeconds;` - Implements part of the file's concrete application logic.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    public AccountLockedException(String message, long retryAfterSeconds) {` - Declares a method signature, contract, or constructor entry point.
- L0014: `        super(message);` - Implements part of the file's concrete application logic.
- L0015: `        this.retryAfterSeconds = retryAfterSeconds;` - Implements part of the file's concrete application logic.
- L0016: `    }` - Closes the current scope block.
- L0017: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 27: `backend/src/main/java/com/auth/exception/GlobalExceptionHandler.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 134

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.extern.slf4j.Slf4j;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.http.HttpHeaders;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.http.HttpStatus;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.http.ResponseEntity;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.authentication.BadCredentialsException;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.validation.FieldError;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.web.bind.MethodArgumentNotValidException;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.web.bind.annotation.ControllerAdvice;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.web.bind.annotation.ExceptionHandler;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0017: ` * Global exception handler for REST controllers.` - JavaDoc/comment line documenting intent and behavior.
- L0018: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `@ControllerAdvice` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `@Slf4j` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `public class GlobalExceptionHandler {` - Defines the core type and responsibility boundary for this file.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0024: `     * Handle validation errors (e.g. @Valid annotations).` - JavaDoc/comment line documenting intent and behavior.
- L0025: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0026: `    @ExceptionHandler(MethodArgumentNotValidException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `    public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0028: `        String errorMessage = buildValidationErrorMessage(ex.getBindingResult().getFieldErrors());` - Implements part of the file's concrete application logic.
- L0029: `        return ResponseEntity.badRequest().body(new MessageResponse(errorMessage, false));` - Returns data to caller after applying current method logic.
- L0030: `    }` - Closes the current scope block.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0033: `     * Handle bad credentials (login failure).` - JavaDoc/comment line documenting intent and behavior.
- L0034: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0035: `    @ExceptionHandler(BadCredentialsException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0036: `    public ResponseEntity<MessageResponse> handleBadCredentialsException(BadCredentialsException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0037: `        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)` - Returns data to caller after applying current method logic.
- L0038: `                .body(new MessageResponse("Invalid email or password!", false));` - Credential or recovery logic for authentication safety.
- L0039: `    }` - Closes the current scope block.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     * Handle UserAlreadyExistsException.` - JavaDoc/comment line documenting intent and behavior.
- L0043: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0044: `    @ExceptionHandler(UserAlreadyExistsException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    public ResponseEntity<MessageResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0046: `        return ResponseEntity.status(HttpStatus.CONFLICT)` - Returns data to caller after applying current method logic.
- L0047: `                .body(new MessageResponse(ex.getMessage(), false));` - Implements part of the file's concrete application logic.
- L0048: `    }` - Closes the current scope block.
- L0049: `` - Blank line used to separate logical blocks for readability.
- L0050: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0051: `     * Handle ResourceNotFoundException.` - JavaDoc/comment line documenting intent and behavior.
- L0052: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0053: `    @ExceptionHandler(ResourceNotFoundException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0054: `    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0055: `        return ResponseEntity.status(HttpStatus.NOT_FOUND)` - Returns data to caller after applying current method logic.
- L0056: `                .body(new MessageResponse(ex.getMessage(), false));` - Implements part of the file's concrete application logic.
- L0057: `    }` - Closes the current scope block.
- L0058: `` - Blank line used to separate logical blocks for readability.
- L0059: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0060: `     * Handle TokenValidationException.` - JavaDoc/comment line documenting intent and behavior.
- L0061: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0062: `    @ExceptionHandler(TokenValidationException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0063: `    public ResponseEntity<MessageResponse> handleTokenValidationException(TokenValidationException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0064: `        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)` - Returns data to caller after applying current method logic.
- L0065: `                .body(new MessageResponse(ex.getMessage(), false));` - Implements part of the file's concrete application logic.
- L0066: `    }` - Closes the current scope block.
- L0067: `` - Blank line used to separate logical blocks for readability.
- L0068: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0069: `     * Handle temporary account lockout errors.` - JavaDoc/comment line documenting intent and behavior.
- L0070: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0071: `    @ExceptionHandler(AccountLockedException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0072: `    public ResponseEntity<MessageResponse> handleAccountLockedException(AccountLockedException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0073: `        HttpHeaders headers = new HttpHeaders();` - Implements part of the file's concrete application logic.
- L0074: `        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));` - Implements part of the file's concrete application logic.
- L0075: `        return ResponseEntity.status(HttpStatus.LOCKED)` - Returns data to caller after applying current method logic.
- L0076: `                .headers(headers)` - Implements part of the file's concrete application logic.
- L0077: `                .body(new MessageResponse(ex.getMessage(), false));` - Implements part of the file's concrete application logic.
- L0078: `    }` - Closes the current scope block.
- L0079: `` - Blank line used to separate logical blocks for readability.
- L0080: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0081: `     * Handle request-rate limit violations.` - JavaDoc/comment line documenting intent and behavior.
- L0082: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0083: `    @ExceptionHandler(RateLimitExceededException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0084: `    public ResponseEntity<MessageResponse> handleRateLimitExceededException(RateLimitExceededException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0085: `        HttpHeaders headers = new HttpHeaders();` - Implements part of the file's concrete application logic.
- L0086: `        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfterSeconds())));` - Implements part of the file's concrete application logic.
- L0087: `        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)` - Returns data to caller after applying current method logic.
- L0088: `                .headers(headers)` - Implements part of the file's concrete application logic.
- L0089: `                .body(new MessageResponse(ex.getMessage(), false));` - Implements part of the file's concrete application logic.
- L0090: `    }` - Closes the current scope block.
- L0091: `` - Blank line used to separate logical blocks for readability.
- L0092: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0093: `     * Handle client-side validation/argument errors raised from service layer.` - JavaDoc/comment line documenting intent and behavior.
- L0094: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0095: `    @ExceptionHandler(IllegalArgumentException.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0096: `    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {` - Declares a method signature, contract, or constructor entry point.
- L0097: `        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage(), false));` - Returns data to caller after applying current method logic.
- L0098: `    }` - Closes the current scope block.
- L0099: `` - Blank line used to separate logical blocks for readability.
- L0100: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0101: `     * Handle generic exceptions.` - JavaDoc/comment line documenting intent and behavior.
- L0102: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0103: `    @ExceptionHandler(Exception.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0104: `    public ResponseEntity<MessageResponse> handleException(Exception ex) {` - Declares a method signature, contract, or constructor entry point.
- L0105: `        log.error("Unhandled exception in API layer", ex);` - Structured log statement for traceability and diagnostics.
- L0106: `        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)` - Returns data to caller after applying current method logic.
- L0107: `                .body(new MessageResponse("An unexpected error occurred. Please try again later.", false));` - Implements part of the file's concrete application logic.
- L0108: `    }` - Closes the current scope block.
- L0109: `` - Blank line used to separate logical blocks for readability.
- L0110: `    private String buildValidationErrorMessage(List<FieldError> fieldErrors) {` - Declares a method signature, contract, or constructor entry point.
- L0111: `        StringBuilder messageBuilder = new StringBuilder();` - Implements part of the file's concrete application logic.
- L0112: `        boolean firstMessageAdded = false;` - Implements part of the file's concrete application logic.
- L0113: `` - Blank line used to separate logical blocks for readability.
- L0114: `        for (FieldError fieldError : fieldErrors) {` - Iterates through values to process collections or repeated logic.
- L0115: `            String defaultMessage = fieldError.getDefaultMessage();` - Implements part of the file's concrete application logic.
- L0116: `            if (defaultMessage == null || defaultMessage.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0117: `                continue;` - Implements part of the file's concrete application logic.
- L0118: `            }` - Closes the current scope block.
- L0119: `` - Blank line used to separate logical blocks for readability.
- L0120: `            if (firstMessageAdded) {` - Conditional branch enforcing a business rule or guard path.
- L0121: `                messageBuilder.append(", ");` - Implements part of the file's concrete application logic.
- L0122: `            }` - Closes the current scope block.
- L0123: `` - Blank line used to separate logical blocks for readability.
- L0124: `            messageBuilder.append(defaultMessage);` - Implements part of the file's concrete application logic.
- L0125: `            firstMessageAdded = true;` - Implements part of the file's concrete application logic.
- L0126: `        }` - Closes the current scope block.
- L0127: `` - Blank line used to separate logical blocks for readability.
- L0128: `        if (!firstMessageAdded) {` - Conditional branch enforcing a business rule or guard path.
- L0129: `            return "Validation failed.";` - Returns data to caller after applying current method logic.
- L0130: `        }` - Closes the current scope block.
- L0131: `` - Blank line used to separate logical blocks for readability.
- L0132: `        return messageBuilder.toString();` - Returns data to caller after applying current method logic.
- L0133: `    }` - Closes the current scope block.
- L0134: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 28: `backend/src/main/java/com/auth/exception/RateLimitExceededException.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 17

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.Getter;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0006: ` * Thrown when request frequency exceeds configured rate limits.` - JavaDoc/comment line documenting intent and behavior.
- L0007: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0008: `@Getter` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0009: `public class RateLimitExceededException extends RuntimeException {` - Defines the core type and responsibility boundary for this file.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `    private final long retryAfterSeconds;` - Implements part of the file's concrete application logic.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    public RateLimitExceededException(String message, long retryAfterSeconds) {` - Declares a method signature, contract, or constructor entry point.
- L0014: `        super(message);` - Implements part of the file's concrete application logic.
- L0015: `        this.retryAfterSeconds = retryAfterSeconds;` - Implements part of the file's concrete application logic.
- L0016: `    }` - Closes the current scope block.
- L0017: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 29: `backend/src/main/java/com/auth/exception/ResourceNotFoundException.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 12

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.http.HttpStatus;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.web.bind.annotation.ResponseStatus;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `@ResponseStatus(HttpStatus.NOT_FOUND)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0007: `public class ResourceNotFoundException extends RuntimeException {` - Defines the core type and responsibility boundary for this file.
- L0008: `    /** Creates a not-found exception with a caller-provided message. */` - JavaDoc/comment line documenting intent and behavior.
- L0009: `    public ResourceNotFoundException(String message) {` - Declares a method signature, contract, or constructor entry point.
- L0010: `        super(message);` - Implements part of the file's concrete application logic.
- L0011: `    }` - Closes the current scope block.
- L0012: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 30: `backend/src/main/java/com/auth/exception/TokenValidationException.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 12

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.http.HttpStatus;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.web.bind.annotation.ResponseStatus;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `@ResponseStatus(HttpStatus.UNAUTHORIZED)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0007: `public class TokenValidationException extends RuntimeException {` - Defines the core type and responsibility boundary for this file.
- L0008: `    /** Creates an unauthorized exception with a caller-provided message. */` - JavaDoc/comment line documenting intent and behavior.
- L0009: `    public TokenValidationException(String message) {` - Declares a method signature, contract, or constructor entry point.
- L0010: `        super(message);` - Implements part of the file's concrete application logic.
- L0011: `    }` - Closes the current scope block.
- L0012: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 31: `backend/src/main/java/com/auth/exception/UserAlreadyExistsException.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 12

### Line-by-Line Walkthrough
- L0001: `package com.auth.exception;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.http.HttpStatus;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.web.bind.annotation.ResponseStatus;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `@ResponseStatus(HttpStatus.CONFLICT)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0007: `public class UserAlreadyExistsException extends RuntimeException {` - Defines the core type and responsibility boundary for this file.
- L0008: `    /** Creates a conflict exception with a caller-provided message. */` - JavaDoc/comment line documenting intent and behavior.
- L0009: `    public UserAlreadyExistsException(String message) {` - Declares a method signature, contract, or constructor entry point.
- L0010: `        super(message);` - Implements part of the file's concrete application logic.
- L0011: `    }` - Closes the current scope block.
- L0012: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 32: `backend/src/main/java/com/auth/mapper/UserMapper.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 52

### Line-by-Line Walkthrough
- L0001: `package com.auth.mapper;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.RegisterRequest;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDashboardDto;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0008: `import org.mapstruct.Mapper;` - Imports a type required by this file to compile and run.
- L0009: `import org.mapstruct.Mapping;` - Imports a type required by this file to compile and run.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `@Mapper(componentModel = "spring")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `public interface UserMapper {` - Defines the core type and responsibility boundary for this file.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `    /** Maps registration payload to a new User entity while ignoring managed fields. */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `    @Mapping(target = "password", ignore = true) // Encoded manually` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    @Mapping(target = "roles", ignore = true) // Set manually` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `    @Mapping(target = "verificationOtp", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `    @Mapping(target = "otpExpiry", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `    @Mapping(target = "resetToken", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `    @Mapping(target = "resetTokenExpiry", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `    @Mapping(target = "refreshToken", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `    @Mapping(target = "refreshTokenExpiry", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    @Mapping(target = "failedLoginAttempts", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0026: `    @Mapping(target = "accountLockedUntil", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `    @Mapping(target = "failedOtpAttempts", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `    @Mapping(target = "otpLockedUntil", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `    @Mapping(target = "authProvider", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0030: `    @Mapping(target = "enabled", constant = "false")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `    @Mapping(target = "id", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0032: `    @Mapping(target = "createdAt", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0033: `    @Mapping(target = "updatedAt", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `    User toEntity(RegisterRequest request);` - Implements part of the file's concrete application logic.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    /** Maps User entity to API-safe user DTO. */` - JavaDoc/comment line documenting intent and behavior.
- L0037: `    UserDto toDto(User user);` - Implements part of the file's concrete application logic.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    /** Maps a list of users to their DTO representation. */` - JavaDoc/comment line documenting intent and behavior.
- L0040: `    List<UserDto> toDtoList(List<User> users);` - Implements part of the file's concrete application logic.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    /** Maps User entity to dashboard payload with custom user field mapping. */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    @Mapping(source = "name", target = "user")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    @Mapping(target = "message", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    @Mapping(target = "timestamp", ignore = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `    UserDashboardDto toUserDashboardDto(User user);` - Implements part of the file's concrete application logic.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `    /** Converts Role entity to its role-name string for DTO serialization. */` - JavaDoc/comment line documenting intent and behavior.
- L0049: `    default String map(Role role) {` - Opens a new scope block for type, method, or control flow.
- L0050: `        return role.getName().name();` - Returns data to caller after applying current method logic.
- L0051: `    }` - Closes the current scope block.
- L0052: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 33: `backend/src/main/java/com/auth/repository/RoleRepository.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 17

### Line-by-Line Walkthrough
- L0001: `package com.auth.repository;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.data.jpa.repository.JpaRepository;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.stereotype.Repository;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0008: `` - Blank line used to separate logical blocks for readability.
- L0009: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` * Repository for Role entity operations.` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `@Repository` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `public interface RoleRepository extends JpaRepository<Role, Long> {` - Defines the core type and responsibility boundary for this file.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `    /** Finds a role by enum-backed role name. */` - JavaDoc/comment line documenting intent and behavior.
- L0016: `    Optional<Role> findByName(Role.RoleName name);` - Authorization rule, authority mapping, or role handling line.
- L0017: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 34: `backend/src/main/java/com/auth/repository/UserRepository.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 30

### Line-by-Line Walkthrough
- L0001: `package com.auth.repository;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.data.jpa.repository.JpaRepository;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.data.jpa.repository.JpaSpecificationExecutor;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.stereotype.Repository;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0009: `` - Blank line used to separate logical blocks for readability.
- L0010: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` * Repository for User entity operations.` - JavaDoc/comment line documenting intent and behavior.
- L0012: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0013: `@Repository` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0014: `public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {` - Defines the core type and responsibility boundary for this file.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `    /** Finds a user by unique email address. */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `    Optional<User> findByEmail(String email);` - Data access call to load or persist domain objects.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    /** Checks whether a user exists for the given email. */` - JavaDoc/comment line documenting intent and behavior.
- L0020: `    boolean existsByEmail(String email);` - Data access call to load or persist domain objects.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `    /** Finds a user by active password-reset token hash. */` - JavaDoc/comment line documenting intent and behavior.
- L0023: `    Optional<User> findByResetToken(String resetToken);` - Security-related logic for tokens, OAuth, or authentication state.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    /** Finds a user by current refresh token hash. */` - JavaDoc/comment line documenting intent and behavior.
- L0026: `    Optional<User> findByRefreshToken(String refreshToken);` - Security-related logic for tokens, OAuth, or authentication state.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    /** Counts users with enabled=true for admin metrics. */` - JavaDoc/comment line documenting intent and behavior.
- L0029: `    long countByEnabledTrue();` - Implements part of the file's concrete application logic.
- L0030: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 35: `backend/src/main/java/com/auth/security/CustomUserDetailsService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 74

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0005: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.security.core.authority.SimpleGrantedAuthority;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.security.core.userdetails.UserDetails;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.core.userdetails.UserDetailsService;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.core.userdetails.UsernameNotFoundException;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.transaction.annotation.Transactional;` - Imports a type required by this file to compile and run.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0014: `import java.util.Locale;` - Imports a type required by this file to compile and run.
- L0015: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0016: `import java.util.stream.Collectors;` - Imports a type required by this file to compile and run.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0019: ` * Custom UserDetailsService implementation for Spring Security.` - JavaDoc/comment line documenting intent and behavior.
- L0020: ` * Loads user details from database for authentication.` - JavaDoc/comment line documenting intent and behavior.
- L0021: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0022: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `@Transactional(readOnly = true)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `public class CustomUserDetailsService implements UserDetailsService {` - Defines the core type and responsibility boundary for this file.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0030: `     * Loads a user by email and returns Spring Security-compatible user details.` - JavaDoc/comment line documenting intent and behavior.
- L0031: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0032: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0033: `    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {` - Declares a method signature, contract, or constructor entry point.
- L0034: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0035: `        User user = findUserOrThrow(normalizedEmail);` - Implements part of the file's concrete application logic.
- L0036: `        return buildSecurityUser(user);` - Returns data to caller after applying current method logic.
- L0037: `    }` - Closes the current scope block.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    /** Normalizes email input used for user lookup. */` - JavaDoc/comment line documenting intent and behavior.
- L0040: `    protected String normalizeEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0041: `        if (email == null) {` - Conditional branch enforcing a business rule or guard path.
- L0042: `            throw new UsernameNotFoundException("Email must not be null.");` - Raises an exception for invalid state or request path.
- L0043: `        }` - Closes the current scope block.
- L0044: `        return email.trim().toLowerCase(Locale.ROOT);` - Returns data to caller after applying current method logic.
- L0045: `    }` - Closes the current scope block.
- L0046: `` - Blank line used to separate logical blocks for readability.
- L0047: `    /** Finds an application user by email or throws UsernameNotFoundException. */` - JavaDoc/comment line documenting intent and behavior.
- L0048: `    protected User findUserOrThrow(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0049: `        Optional<User> userOpt = userService.findByEmail(email);` - Service interaction applies business logic or orchestration.
- L0050: `        if (userOpt.isEmpty()) {` - Conditional branch enforcing a business rule or guard path.
- L0051: `            throw new UsernameNotFoundException("User not found with email: " + email);` - Raises an exception for invalid state or request path.
- L0052: `        }` - Closes the current scope block.
- L0053: `        return userOpt.get();` - Returns data to caller after applying current method logic.
- L0054: `    }` - Closes the current scope block.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `    /** Converts application roles into Spring Security granted authorities. */` - JavaDoc/comment line documenting intent and behavior.
- L0057: `    protected List<SimpleGrantedAuthority> buildAuthorities(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0058: `        return user.getRoles().stream()` - Returns data to caller after applying current method logic.
- L0059: `                .map(role -> new SimpleGrantedAuthority(role.getName().name()))` - Authorization rule, authority mapping, or role handling line.
- L0060: `                .collect(Collectors.toList());` - Implements part of the file's concrete application logic.
- L0061: `    }` - Closes the current scope block.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `    /** Converts the application user model into Spring Security UserDetails. */` - JavaDoc/comment line documenting intent and behavior.
- L0064: `    protected UserDetails buildSecurityUser(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0065: `        return new org.springframework.security.core.userdetails.User(` - Returns data to caller after applying current method logic.
- L0066: `                user.getEmail(),` - Implements part of the file's concrete application logic.
- L0067: `                user.getPassword(),` - Credential or recovery logic for authentication safety.
- L0068: `                user.isEnabled(),` - Implements part of the file's concrete application logic.
- L0069: `                true,` - Implements part of the file's concrete application logic.
- L0070: `                true,` - Implements part of the file's concrete application logic.
- L0071: `                true,` - Implements part of the file's concrete application logic.
- L0072: `                buildAuthorities(user));` - Authorization rule, authority mapping, or role handling line.
- L0073: `    }` - Closes the current scope block.
- L0074: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 36: `backend/src/main/java/com/auth/security/JwtAuthFilter.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 84

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.servlet.FilterChain;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.servlet.ServletException;` - Imports a type required by this file to compile and run.
- L0005: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0006: `import jakarta.servlet.http.HttpServletResponse;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.core.context.SecurityContextHolder;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.security.core.userdetails.UserDetails;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.util.StringUtils;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.web.filter.OncePerRequestFilter;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `import java.io.IOException;` - Imports a type required by this file to compile and run.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0019: ` * JWT Authentication Filter that intercepts requests and validates JWT tokens.` - JavaDoc/comment line documenting intent and behavior.
- L0020: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0021: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `public class JwtAuthFilter extends OncePerRequestFilter {` - Defines the core type and responsibility boundary for this file.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    private static final String AUTHORIZATION_HEADER = "Authorization";` - Implements part of the file's concrete application logic.
- L0026: `    private static final String BEARER_PREFIX = "Bearer ";` - Implements part of the file's concrete application logic.
- L0027: `    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();` - Declares a method signature, contract, or constructor entry point.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    private final JwtUtil jwtUtil;` - Security-related logic for tokens, OAuth, or authentication state.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    private final CustomUserDetailsService userDetailsService;` - Service interaction applies business logic or orchestration.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0034: `     * Validates incoming bearer token and sets authenticated user context when` - JavaDoc/comment line documenting intent and behavior.
- L0035: `     * token is valid.` - JavaDoc/comment line documenting intent and behavior.
- L0036: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0037: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0038: `    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,` - Declares a method signature, contract, or constructor entry point.
- L0039: `            FilterChain filterChain) throws ServletException, IOException {` - Opens a new scope block for type, method, or control flow.
- L0040: `        try {` - Exception handling block for controlled failure behavior.
- L0041: `            String jwt = parseJwt(request);` - Security-related logic for tokens, OAuth, or authentication state.
- L0042: `            if (jwt != null && jwtUtil.validateToken(jwt)) {` - Conditional branch enforcing a business rule or guard path.
- L0043: `                authenticateRequest(request, jwt);` - Security-related logic for tokens, OAuth, or authentication state.
- L0044: `            }` - Closes the current scope block.
- L0045: `        } catch (Exception exception) {` - Opens a new scope block for type, method, or control flow.
- L0046: `            logger.error("Cannot set user authentication", exception);` - Implements part of the file's concrete application logic.
- L0047: `        }` - Closes the current scope block.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `        filterChain.doFilter(request, response);` - Implements part of the file's concrete application logic.
- L0050: `    }` - Closes the current scope block.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0053: `     * Extract JWT token from Authorization header.` - JavaDoc/comment line documenting intent and behavior.
- L0054: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0055: `    private String parseJwt(HttpServletRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0056: `        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);` - Implements part of the file's concrete application logic.
- L0057: `` - Blank line used to separate logical blocks for readability.
- L0058: `        boolean hasHeader = StringUtils.hasText(headerAuth);` - Implements part of the file's concrete application logic.
- L0059: `        if (hasHeader) {` - Conditional branch enforcing a business rule or guard path.
- L0060: `            boolean isBearer = headerAuth.startsWith(BEARER_PREFIX);` - Implements part of the file's concrete application logic.
- L0061: `            if (isBearer) {` - Conditional branch enforcing a business rule or guard path.
- L0062: `                return headerAuth.substring(BEARER_PREFIX_LENGTH);` - Returns data to caller after applying current method logic.
- L0063: `            }` - Closes the current scope block.
- L0064: `        }` - Closes the current scope block.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `        return null;` - Returns data to caller after applying current method logic.
- L0067: `    }` - Closes the current scope block.
- L0068: `` - Blank line used to separate logical blocks for readability.
- L0069: `    private void authenticateRequest(HttpServletRequest request, String jwt) {` - Declares a method signature, contract, or constructor entry point.
- L0070: `        String email = jwtUtil.getEmailFromToken(jwt);` - Security-related logic for tokens, OAuth, or authentication state.
- L0071: `        UserDetails userDetails = userDetailsService.loadUserByUsername(email);` - Service interaction applies business logic or orchestration.
- L0072: `` - Blank line used to separate logical blocks for readability.
- L0073: `        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(` - Security-related logic for tokens, OAuth, or authentication state.
- L0074: `                userDetails,` - Implements part of the file's concrete application logic.
- L0075: `                null,` - Implements part of the file's concrete application logic.
- L0076: `                userDetails.getAuthorities());` - Authorization rule, authority mapping, or role handling line.
- L0077: `        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));` - Implements part of the file's concrete application logic.
- L0078: `        SecurityContextHolder.getContext().setAuthentication(authentication);` - Implements part of the file's concrete application logic.
- L0079: `` - Blank line used to separate logical blocks for readability.
- L0080: `        if (logger.isDebugEnabled()) {` - Conditional branch enforcing a business rule or guard path.
- L0081: `            logger.debug("Authenticated user: " + email + " with authorities: " + userDetails.getAuthorities());` - Authorization rule, authority mapping, or role handling line.
- L0082: `        }` - Closes the current scope block.
- L0083: `    }` - Closes the current scope block.
- L0084: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 37: `backend/src/main/java/com/auth/security/JwtUtil.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 110

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import io.jsonwebtoken.*;` - Imports a type required by this file to compile and run.
- L0004: `import io.jsonwebtoken.io.Decoders;` - Imports a type required by this file to compile and run.
- L0005: `import io.jsonwebtoken.security.Keys;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.Getter;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.core.Authentication;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.core.userdetails.UserDetails;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `import javax.crypto.SecretKey;` - Imports a type required by this file to compile and run.
- L0013: `import java.nio.charset.StandardCharsets;` - Imports a type required by this file to compile and run.
- L0014: `import java.util.Date;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0017: ` * Utility class for JWT token generation and validation.` - JavaDoc/comment line documenting intent and behavior.
- L0018: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `public class JwtUtil {` - Defines the core type and responsibility boundary for this file.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `    private static final String ACCESS_TOKEN_TYPE = "access";` - Security-related logic for tokens, OAuth, or authentication state.
- L0023: `    private static final String TOKEN_TYPE_CLAIM = "tokenType";` - Security-related logic for tokens, OAuth, or authentication state.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    @Value("${jwt.secret}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0026: `    private String jwtSecret;` - Security-related logic for tokens, OAuth, or authentication state.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    @Getter` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `    @Value("${jwt.expiration}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0030: `    private long accessTokenExpiration;` - Security-related logic for tokens, OAuth, or authentication state.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0033: `     * Generate JWT token from authentication object.` - JavaDoc/comment line documenting intent and behavior.
- L0034: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0035: `    public String generateToken(Authentication authentication) {` - Declares a method signature, contract, or constructor entry point.
- L0036: `        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();` - Implements part of the file's concrete application logic.
- L0037: `        return generateTokenFromEmail(userPrincipal.getUsername());` - Returns data to caller after applying current method logic.
- L0038: `    }` - Closes the current scope block.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0041: `     * Generate JWT token from email.` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    public String generateTokenFromEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0044: `        return Jwts.builder()` - Returns data to caller after applying current method logic.
- L0045: `                .subject(email)` - Implements part of the file's concrete application logic.
- L0046: `                .issuedAt(new Date())` - Implements part of the file's concrete application logic.
- L0047: `                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)` - Security-related logic for tokens, OAuth, or authentication state.
- L0048: `                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))` - Security-related logic for tokens, OAuth, or authentication state.
- L0049: `                .signWith(getSigningKey())` - Implements part of the file's concrete application logic.
- L0050: `                .compact();` - Implements part of the file's concrete application logic.
- L0051: `    }` - Closes the current scope block.
- L0052: `` - Blank line used to separate logical blocks for readability.
- L0053: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0054: `     * Extract email from JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0055: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0056: `    public String getEmailFromToken(String token) {` - Declares a method signature, contract, or constructor entry point.
- L0057: `        return Jwts.parser()` - Returns data to caller after applying current method logic.
- L0058: `                .verifyWith(getSigningKey())` - Implements part of the file's concrete application logic.
- L0059: `                .build()` - Implements part of the file's concrete application logic.
- L0060: `                .parseSignedClaims(token)` - Security-related logic for tokens, OAuth, or authentication state.
- L0061: `                .getPayload()` - Implements part of the file's concrete application logic.
- L0062: `                .getSubject();` - Implements part of the file's concrete application logic.
- L0063: `    }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0066: `     * Validate JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0067: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0068: `    public boolean validateToken(String token) {` - Declares a method signature, contract, or constructor entry point.
- L0069: `        try {` - Exception handling block for controlled failure behavior.
- L0070: `            Jwts.parser()` - Security-related logic for tokens, OAuth, or authentication state.
- L0071: `                    .verifyWith(getSigningKey())` - Implements part of the file's concrete application logic.
- L0072: `                    .build()` - Implements part of the file's concrete application logic.
- L0073: `                    .parseSignedClaims(token);` - Security-related logic for tokens, OAuth, or authentication state.
- L0074: `            return true;` - Returns data to caller after applying current method logic.
- L0075: `        } catch (JwtException | IllegalArgumentException e) {` - Opens a new scope block for type, method, or control flow.
- L0076: `            return false;` - Returns data to caller after applying current method logic.
- L0077: `        }` - Closes the current scope block.
- L0078: `    }` - Closes the current scope block.
- L0079: `` - Blank line used to separate logical blocks for readability.
- L0080: `    /** Builds the JWT signing key from configured secret using Base64, Base64URL, or raw text fallback. */` - JavaDoc/comment line documenting intent and behavior.
- L0081: `    private SecretKey getSigningKey() {` - Declares a method signature, contract, or constructor entry point.
- L0082: `        byte[] keyBytes = decodeSecret(jwtSecret);` - Security-related logic for tokens, OAuth, or authentication state.
- L0083: `        if (keyBytes.length < 32) {` - Conditional branch enforcing a business rule or guard path.
- L0084: `            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256 signing.");` - Raises an exception for invalid state or request path.
- L0085: `        }` - Closes the current scope block.
- L0086: `        return Keys.hmacShaKeyFor(keyBytes);` - Returns data to caller after applying current method logic.
- L0087: `    }` - Closes the current scope block.
- L0088: `` - Blank line used to separate logical blocks for readability.
- L0089: `    /** Decodes secret value from Base64/Base64URL and falls back to UTF-8 bytes for plain text secrets. */` - JavaDoc/comment line documenting intent and behavior.
- L0090: `    private byte[] decodeSecret(String secret) {` - Declares a method signature, contract, or constructor entry point.
- L0091: `        if (secret == null || secret.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0092: `            throw new IllegalStateException("jwt.secret is missing or blank.");` - Raises an exception for invalid state or request path.
- L0093: `        }` - Closes the current scope block.
- L0094: `` - Blank line used to separate logical blocks for readability.
- L0095: `        String normalizedSecret = secret.trim();` - Implements part of the file's concrete application logic.
- L0096: `` - Blank line used to separate logical blocks for readability.
- L0097: `        try {` - Exception handling block for controlled failure behavior.
- L0098: `            return Decoders.BASE64.decode(normalizedSecret);` - Returns data to caller after applying current method logic.
- L0099: `        } catch (RuntimeException ignored) {` - Opens a new scope block for type, method, or control flow.
- L0100: `            // Fallback to Base64URL or plain text for local/dev setups.` - Inline comment for maintainability and context.
- L0101: `        }` - Closes the current scope block.
- L0102: `` - Blank line used to separate logical blocks for readability.
- L0103: `        try {` - Exception handling block for controlled failure behavior.
- L0104: `            return Decoders.BASE64URL.decode(normalizedSecret);` - Returns data to caller after applying current method logic.
- L0105: `        } catch (RuntimeException ignored) {` - Opens a new scope block for type, method, or control flow.
- L0106: `            return normalizedSecret.getBytes(StandardCharsets.UTF_8);` - Returns data to caller after applying current method logic.
- L0107: `        }` - Closes the current scope block.
- L0108: `    }` - Closes the current scope block.
- L0109: `` - Blank line used to separate logical blocks for readability.
- L0110: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 38: `backend/src/main/java/com/auth/security/LinkedInAuthorizationRequestResolver.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 70

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0012: ` * Customizes OAuth2 authorization requests for LinkedIn compatibility.` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0014: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `public class LinkedInAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {` - Defines the core type and responsibility boundary for this file.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    private static final String AUTHORIZATION_BASE_URI = "/oauth2/authorization";` - Security-related logic for tokens, OAuth, or authentication state.
- L0018: `    private static final String LINKEDIN_REGISTRATION_ID = "linkedin";` - Implements part of the file's concrete application logic.
- L0019: `    private static final String LINKEDIN_PATH_SUFFIX = "/" + LINKEDIN_REGISTRATION_ID;` - Implements part of the file's concrete application logic.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    private final DefaultOAuth2AuthorizationRequestResolver delegate;` - Security-related logic for tokens, OAuth, or authentication state.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    public LinkedInAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {` - Declares a method signature, contract, or constructor entry point.
- L0024: `        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(` - Security-related logic for tokens, OAuth, or authentication state.
- L0025: `                clientRegistrationRepository,` - Repository usage handles persistence access to database records.
- L0026: `                AUTHORIZATION_BASE_URI);` - Implements part of the file's concrete application logic.
- L0027: `    }` - Closes the current scope block.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    /** Resolves provider authorization request from incoming servlet request. */` - JavaDoc/comment line documenting intent and behavior.
- L0030: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0032: `        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);` - Security-related logic for tokens, OAuth, or authentication state.
- L0033: `        return customizeForLinkedIn(isLinkedInRequest(request), authorizationRequest);` - Returns data to caller after applying current method logic.
- L0034: `    }` - Closes the current scope block.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    /** Resolves provider authorization request for an explicit client registration id. */` - JavaDoc/comment line documenting intent and behavior.
- L0037: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0038: `    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {` - Declares a method signature, contract, or constructor entry point.
- L0039: `        OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);` - Security-related logic for tokens, OAuth, or authentication state.
- L0040: `        return customizeForLinkedIn(LINKEDIN_REGISTRATION_ID.equals(clientRegistrationId), authorizationRequest);` - Returns data to caller after applying current method logic.
- L0041: `    }` - Closes the current scope block.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `    private OAuth2AuthorizationRequest customizeForLinkedIn(boolean isLinkedInRequest,` - Declares a method signature, contract, or constructor entry point.
- L0044: `            OAuth2AuthorizationRequest authorizationRequest) {` - Opens a new scope block for type, method, or control flow.
- L0045: `        if (!isLinkedInRequest || authorizationRequest == null) {` - Conditional branch enforcing a business rule or guard path.
- L0046: `            return authorizationRequest;` - Returns data to caller after applying current method logic.
- L0047: `        }` - Closes the current scope block.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `        return removeNonce(authorizationRequest);` - Returns data to caller after applying current method logic.
- L0050: `    }` - Closes the current scope block.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    private boolean isLinkedInRequest(HttpServletRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0053: `        String requestUri = request.getRequestURI();` - Implements part of the file's concrete application logic.
- L0054: `        return requestUri != null && requestUri.endsWith(LINKEDIN_PATH_SUFFIX);` - Returns data to caller after applying current method logic.
- L0055: `    }` - Closes the current scope block.
- L0056: `` - Blank line used to separate logical blocks for readability.
- L0057: `    /** Removes nonce parameter/attribute for LinkedIn to prevent nonce validation mismatches. */` - JavaDoc/comment line documenting intent and behavior.
- L0058: `    private OAuth2AuthorizationRequest removeNonce(OAuth2AuthorizationRequest authorizationRequest) {` - Declares a method signature, contract, or constructor entry point.
- L0059: `        boolean hasNonce = authorizationRequest.getAdditionalParameters().containsKey(OidcParameterNames.NONCE)` - Implements part of the file's concrete application logic.
- L0060: `                || authorizationRequest.getAttributes().containsKey(OidcParameterNames.NONCE);` - Implements part of the file's concrete application logic.
- L0061: `        if (!hasNonce) {` - Conditional branch enforcing a business rule or guard path.
- L0062: `            return authorizationRequest;` - Returns data to caller after applying current method logic.
- L0063: `        }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `        return OAuth2AuthorizationRequest.from(authorizationRequest)` - Returns data to caller after applying current method logic.
- L0066: `                .additionalParameters(params -> params.remove(OidcParameterNames.NONCE))` - Implements part of the file's concrete application logic.
- L0067: `                .attributes(attrs -> attrs.remove(OidcParameterNames.NONCE))` - Implements part of the file's concrete application logic.
- L0068: `                .build();` - Implements part of the file's concrete application logic.
- L0069: `    }` - Closes the current scope block.
- L0070: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 39: `backend/src/main/java/com/auth/security/OAuth2AuthenticationFailureHandler.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 84

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import jakarta.annotation.PostConstruct;` - Imports a type required by this file to compile and run.
- L0004: `import jakarta.servlet.ServletException;` - Imports a type required by this file to compile and run.
- L0005: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0006: `import jakarta.servlet.http.HttpServletResponse;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.extern.slf4j.Slf4j;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.core.AuthenticationException;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.web.util.UriComponentsBuilder;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `import java.io.IOException;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0017: ` * Redirects frontend with OAuth2 login error details.` - JavaDoc/comment line documenting intent and behavior.
- L0018: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0020: `@Slf4j` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0021: `public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {` - Defines the core type and responsibility boundary for this file.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    private static final String LOGIN_PATH = "/login";` - Implements part of the file's concrete application logic.
- L0024: `    private static final String OAUTH_ERROR_QUERY_PARAM = "oauthError";` - Security-related logic for tokens, OAuth, or authentication state.
- L0025: `    private static final String FALLBACK_ERROR_MESSAGE = "OAuth login failed.";` - Security-related logic for tokens, OAuth, or authentication state.
- L0026: `    private static final int MAX_ERROR_MESSAGE_LENGTH = 240;` - Implements part of the file's concrete application logic.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    @Value("${app.frontend-url:http://localhost:5173}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `    private String frontendUrl;` - Implements part of the file's concrete application logic.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    private String frontendLoginUrl;` - Implements part of the file's concrete application logic.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `    @PostConstruct` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `    void initializeRedirectTarget() {` - Opens a new scope block for type, method, or control flow.
- L0035: `        frontendLoginUrl = UriComponentsBuilder` - Implements part of the file's concrete application logic.
- L0036: `                .fromUriString(frontendUrl)` - Implements part of the file's concrete application logic.
- L0037: `                .path(LOGIN_PATH)` - Implements part of the file's concrete application logic.
- L0038: `                .build(true)` - Implements part of the file's concrete application logic.
- L0039: `                .toUriString();` - Implements part of the file's concrete application logic.
- L0040: `    }` - Closes the current scope block.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    /** Redirects failed OAuth2 attempts back to frontend login with the error message. */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    public void onAuthenticationFailure(HttpServletRequest request,` - Declares a method signature, contract, or constructor entry point.
- L0045: `            HttpServletResponse response,` - Implements part of the file's concrete application logic.
- L0046: `            AuthenticationException exception) throws IOException, ServletException {` - Opens a new scope block for type, method, or control flow.
- L0047: `        String errorMessage = resolveErrorMessage(exception);` - Implements part of the file's concrete application logic.
- L0048: `        String provider = request.getParameter("registrationId");` - Implements part of the file's concrete application logic.
- L0049: `        Throwable rootCause = resolveRootCause(exception);` - Implements part of the file's concrete application logic.
- L0050: `        log.debug("OAuth2 authentication failed. uri={}, providerHint={}, message={}, rootCause={}",` - Security-related logic for tokens, OAuth, or authentication state.
- L0051: `                request.getRequestURI(),` - Implements part of the file's concrete application logic.
- L0052: `                provider,` - Implements part of the file's concrete application logic.
- L0053: `                errorMessage,` - Implements part of the file's concrete application logic.
- L0054: `                rootCause.getMessage());` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `        String targetUrl = UriComponentsBuilder` - Implements part of the file's concrete application logic.
- L0057: `                .fromUriString(frontendLoginUrl)` - Implements part of the file's concrete application logic.
- L0058: `                .queryParam(OAUTH_ERROR_QUERY_PARAM, errorMessage)` - Security-related logic for tokens, OAuth, or authentication state.
- L0059: `                .build()` - Implements part of the file's concrete application logic.
- L0060: `                .encode()` - Implements part of the file's concrete application logic.
- L0061: `                .toUriString();` - Implements part of the file's concrete application logic.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `        getRedirectStrategy().sendRedirect(request, response, targetUrl);` - Implements part of the file's concrete application logic.
- L0064: `    }` - Closes the current scope block.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `    private String resolveErrorMessage(AuthenticationException exception) {` - Declares a method signature, contract, or constructor entry point.
- L0067: `        String message = exception.getMessage();` - Implements part of the file's concrete application logic.
- L0068: `        if (message == null || message.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0069: `            return FALLBACK_ERROR_MESSAGE;` - Returns data to caller after applying current method logic.
- L0070: `        }` - Closes the current scope block.
- L0071: `        if (message.length() <= MAX_ERROR_MESSAGE_LENGTH) {` - Conditional branch enforcing a business rule or guard path.
- L0072: `            return message;` - Returns data to caller after applying current method logic.
- L0073: `        }` - Closes the current scope block.
- L0074: `        return message.substring(0, MAX_ERROR_MESSAGE_LENGTH);` - Returns data to caller after applying current method logic.
- L0075: `    }` - Closes the current scope block.
- L0076: `` - Blank line used to separate logical blocks for readability.
- L0077: `    private Throwable resolveRootCause(AuthenticationException exception) {` - Declares a method signature, contract, or constructor entry point.
- L0078: `        Throwable cause = exception.getCause();` - Implements part of the file's concrete application logic.
- L0079: `        if (cause == null) {` - Conditional branch enforcing a business rule or guard path.
- L0080: `            return exception;` - Returns data to caller after applying current method logic.
- L0081: `        }` - Closes the current scope block.
- L0082: `        return cause;` - Returns data to caller after applying current method logic.
- L0083: `    }` - Closes the current scope block.
- L0084: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 40: `backend/src/main/java/com/auth/security/OAuth2AuthenticationSuccessHandler.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 69

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.auth.AuthTokenService;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.service.auth.OAuth2UserProvisioningService;` - Imports a type required by this file to compile and run.
- L0007: `import jakarta.annotation.PostConstruct;` - Imports a type required by this file to compile and run.
- L0008: `import jakarta.servlet.ServletException;` - Imports a type required by this file to compile and run.
- L0009: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0010: `import jakarta.servlet.http.HttpServletResponse;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0012: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.http.HttpHeaders;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.security.core.Authentication;` - Imports a type required by this file to compile and run.
- L0015: `import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;` - Imports a type required by this file to compile and run.
- L0016: `import org.springframework.security.oauth2.core.user.OAuth2User;` - Imports a type required by this file to compile and run.
- L0017: `import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;` - Imports a type required by this file to compile and run.
- L0018: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0019: `import org.springframework.web.util.UriComponentsBuilder;` - Imports a type required by this file to compile and run.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `import java.io.IOException;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0024: ` * Handles OAuth2 success by creating/finding a local user and issuing application tokens.` - JavaDoc/comment line documenting intent and behavior.
- L0025: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0026: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {` - Defines the core type and responsibility boundary for this file.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    private static final String OAUTH2_CALLBACK_PATH = "/oauth2/callback";` - Security-related logic for tokens, OAuth, or authentication state.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `    private final OAuth2UserProvisioningService oAuth2UserProvisioningService;` - Service interaction applies business logic or orchestration.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `    private final AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    private final RefreshTokenCookieService refreshTokenCookieService;` - Service interaction applies business logic or orchestration.
- L0037: `` - Blank line used to separate logical blocks for readability.
- L0038: `    @Value("${app.frontend-url:http://localhost:5173}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0039: `    private String frontendUrl;` - Implements part of the file's concrete application logic.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    private String oauthCallbackUrl;` - Security-related logic for tokens, OAuth, or authentication state.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `    @PostConstruct` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    void initializeRedirectTarget() {` - Opens a new scope block for type, method, or control flow.
- L0045: `        oauthCallbackUrl = UriComponentsBuilder` - Security-related logic for tokens, OAuth, or authentication state.
- L0046: `                .fromUriString(frontendUrl)` - Implements part of the file's concrete application logic.
- L0047: `                .path(OAUTH2_CALLBACK_PATH)` - Security-related logic for tokens, OAuth, or authentication state.
- L0048: `                .build(true)` - Implements part of the file's concrete application logic.
- L0049: `                .toUriString();` - Implements part of the file's concrete application logic.
- L0050: `    }` - Closes the current scope block.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    /** Provisions local user data and redirects to frontend callback with a fresh access token. */` - JavaDoc/comment line documenting intent and behavior.
- L0053: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0054: `    public void onAuthenticationSuccess(HttpServletRequest request,` - Declares a method signature, contract, or constructor entry point.
- L0055: `            HttpServletResponse response,` - Implements part of the file's concrete application logic.
- L0056: `            Authentication authentication) throws IOException, ServletException {` - Opens a new scope block for type, method, or control flow.
- L0057: `        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;` - Security-related logic for tokens, OAuth, or authentication state.
- L0058: `        OAuth2User oauth2User = oauthToken.getPrincipal();` - Security-related logic for tokens, OAuth, or authentication state.
- L0059: `` - Blank line used to separate logical blocks for readability.
- L0060: `        User user = oAuth2UserProvisioningService.loadOrCreateUser(oauthToken, oauth2User);` - Service interaction applies business logic or orchestration.
- L0061: `        AuthTokens tokenResult = authTokenService.issueTokens(user);` - Service interaction applies business logic or orchestration.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `        response.addHeader(HttpHeaders.SET_COOKIE,` - Implements part of the file's concrete application logic.
- L0064: `                refreshTokenCookieService.buildRefreshTokenCookie(tokenResult.refreshToken()));` - Service interaction applies business logic or orchestration.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `        clearAuthenticationAttributes(request);` - Implements part of the file's concrete application logic.
- L0067: `        getRedirectStrategy().sendRedirect(request, response, oauthCallbackUrl);` - Security-related logic for tokens, OAuth, or authentication state.
- L0068: `    }` - Closes the current scope block.
- L0069: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 41: `backend/src/main/java/com/auth/security/RefreshTokenCookieService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 58

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.http.ResponseCookie;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.stereotype.Component;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * Builds HTTP-only cookie headers for refresh token handling.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `@Component` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0011: `public class RefreshTokenCookieService {` - Defines the core type and responsibility boundary for this file.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `    private static final long MILLISECONDS_PER_SECOND = 1000L;` - Implements part of the file's concrete application logic.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `    @Value("${auth.refresh-token.cookie-name:refreshToken}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0016: `    private String cookieName;` - Implements part of the file's concrete application logic.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `    @Value("${auth.refresh-token.cookie-path:/api/auth}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `    private String cookiePath;` - Implements part of the file's concrete application logic.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    @Value("${auth.refresh-token.cookie-secure:false}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `    private boolean cookieSecure;` - Implements part of the file's concrete application logic.
- L0023: `` - Blank line used to separate logical blocks for readability.
- L0024: `    @Value("${auth.refresh-token.cookie-same-site:Lax}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    private String cookieSameSite;` - Implements part of the file's concrete application logic.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    @Value("${jwt.refresh.expiration}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `    private long refreshTokenExpirationMs;` - Security-related logic for tokens, OAuth, or authentication state.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    /** Builds the HTTP-only refresh-token cookie header value. */` - JavaDoc/comment line documenting intent and behavior.
- L0031: `    public String buildRefreshTokenCookie(String refreshToken) {` - Declares a method signature, contract, or constructor entry point.
- L0032: `        return ResponseCookie.from(cookieName, refreshToken)` - Returns data to caller after applying current method logic.
- L0033: `                .httpOnly(true)` - Implements part of the file's concrete application logic.
- L0034: `                .secure(cookieSecure)` - Implements part of the file's concrete application logic.
- L0035: `                .path(cookiePath)` - Implements part of the file's concrete application logic.
- L0036: `                .sameSite(cookieSameSite)` - Implements part of the file's concrete application logic.
- L0037: `                .maxAge(refreshTokenExpirationMs / MILLISECONDS_PER_SECOND)` - Security-related logic for tokens, OAuth, or authentication state.
- L0038: `                .build()` - Implements part of the file's concrete application logic.
- L0039: `                .toString();` - Implements part of the file's concrete application logic.
- L0040: `    }` - Closes the current scope block.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    /** Builds an expired refresh-token cookie header value to clear browser state. */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    public String clearRefreshTokenCookie() {` - Declares a method signature, contract, or constructor entry point.
- L0044: `        return ResponseCookie.from(cookieName, "")` - Returns data to caller after applying current method logic.
- L0045: `                .httpOnly(true)` - Implements part of the file's concrete application logic.
- L0046: `                .secure(cookieSecure)` - Implements part of the file's concrete application logic.
- L0047: `                .path(cookiePath)` - Implements part of the file's concrete application logic.
- L0048: `                .sameSite(cookieSameSite)` - Implements part of the file's concrete application logic.
- L0049: `                .maxAge(0)` - Implements part of the file's concrete application logic.
- L0050: `                .build()` - Implements part of the file's concrete application logic.
- L0051: `                .toString();` - Implements part of the file's concrete application logic.
- L0052: `    }` - Closes the current scope block.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `    /** Returns the configured refresh-token cookie name for request lookup. */` - JavaDoc/comment line documenting intent and behavior.
- L0055: `    public String getCookieName() {` - Declares a method signature, contract, or constructor entry point.
- L0056: `        return cookieName;` - Returns data to caller after applying current method logic.
- L0057: `    }` - Closes the current scope block.
- L0058: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 42: `backend/src/main/java/com/auth/service/AdminService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 24

### Line-by-Line Walkthrough
- L0001: `package com.auth.service;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AdminDashboardDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.data.domain.Page;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * Business logic contract for admin dashboard and user management views.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `public interface AdminService {` - Defines the core type and responsibility boundary for this file.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `    /** Builds admin dashboard metrics payload for the authenticated admin email. */` - JavaDoc/comment line documenting intent and behavior.
- L0013: `    AdminDashboardDto getDashboard(String adminEmail);` - Implements part of the file's concrete application logic.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `    /** Returns paginated/filterable/sortable users for admin listing screens. */` - JavaDoc/comment line documenting intent and behavior.
- L0016: `    Page<UserDto> getUsers(` - Implements part of the file's concrete application logic.
- L0017: `            int page,` - Implements part of the file's concrete application logic.
- L0018: `            int size,` - Implements part of the file's concrete application logic.
- L0019: `            String search,` - Implements part of the file's concrete application logic.
- L0020: `            Boolean enabled,` - Implements part of the file's concrete application logic.
- L0021: `            String role,` - Authorization rule, authority mapping, or role handling line.
- L0022: `            String sortBy,` - Implements part of the file's concrete application logic.
- L0023: `            String sortDir);` - Implements part of the file's concrete application logic.
- L0024: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 43: `backend/src/main/java/com/auth/service/AuthService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 50

### Line-by-Line Walkthrough
- L0001: `package com.auth.service;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.LoginRequest;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.OtpVerifyRequest;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.dto.RegisterRequest;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.dto.ResetPasswordRequest;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.dto.UpdatePasswordRequest;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0010: `import com.auth.dto.ChangePasswordRequest;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` * Service interface for authentication operations.` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0015: `public interface AuthService {` - Defines the core type and responsibility boundary for this file.
- L0016: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0017: `     * Register a new user.` - JavaDoc/comment line documenting intent and behavior.
- L0018: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `    MessageResponse register(RegisterRequest request);` - Implements part of the file's concrete application logic.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0022: `     * Verify email with OTP.` - JavaDoc/comment line documenting intent and behavior.
- L0023: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0024: `    MessageResponse verifyOtp(OtpVerifyRequest request);` - Credential or recovery logic for authentication safety.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0027: `     * Login user and return JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0028: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0029: `    AuthTokens login(LoginRequest request);` - Security-related logic for tokens, OAuth, or authentication state.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0032: `     * Request password reset email.` - JavaDoc/comment line documenting intent and behavior.
- L0033: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0034: `    MessageResponse resetPassword(ResetPasswordRequest request);` - Credential or recovery logic for authentication safety.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0037: `     * Update password with reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0038: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0039: `    MessageResponse updatePassword(UpdatePasswordRequest request);` - Credential or recovery logic for authentication safety.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     * Resend OTP for email verification.` - JavaDoc/comment line documenting intent and behavior.
- L0043: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0044: `    MessageResponse resendOtp(String email);` - Credential or recovery logic for authentication safety.
- L0045: `` - Blank line used to separate logical blocks for readability.
- L0046: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0047: `     * Change password for authenticated user.` - JavaDoc/comment line documenting intent and behavior.
- L0048: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0049: `    MessageResponse changePassword(String email, ChangePasswordRequest request);` - Credential or recovery logic for authentication safety.
- L0050: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 44: `backend/src/main/java/com/auth/service/RoleService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 26

### Line-by-Line Walkthrough
- L0001: `package com.auth.service;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * Service interface for Role operations.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `public interface RoleService {` - Defines the core type and responsibility boundary for this file.
- L0011: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0012: `     * Find a role by name, or create it if it doesn't exist.` - JavaDoc/comment line documenting intent and behavior.
- L0013: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0014: `     * @param roleName Role name enum` - JavaDoc/comment line documenting intent and behavior.
- L0015: `     * @return Found or created Role` - JavaDoc/comment line documenting intent and behavior.
- L0016: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `    Role findOrCreateRole(Role.RoleName roleName);` - Authorization rule, authority mapping, or role handling line.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0020: `     * Find a role by name.` - JavaDoc/comment line documenting intent and behavior.
- L0021: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0022: `     * @param roleName Role name enum` - JavaDoc/comment line documenting intent and behavior.
- L0023: `     * @return Optional Role` - JavaDoc/comment line documenting intent and behavior.
- L0024: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0025: `    Optional<Role> findByName(Role.RoleName roleName);` - Authorization rule, authority mapping, or role handling line.
- L0026: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 45: `backend/src/main/java/com/auth/service/UserPortalService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 16

### Line-by-Line Walkthrough
- L0001: `package com.auth.service;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.UserDashboardDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0007: ` * Business logic contract for authenticated user portal views.` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0009: `public interface UserPortalService {` - Defines the core type and responsibility boundary for this file.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `    /** Builds user dashboard payload for the provided authenticated email. */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `    UserDashboardDto getDashboard(String email);` - Implements part of the file's concrete application logic.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `    /** Returns profile payload for the provided authenticated email. */` - JavaDoc/comment line documenting intent and behavior.
- L0015: `    UserDto getProfile(String email);` - Implements part of the file's concrete application logic.
- L0016: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 46: `backend/src/main/java/com/auth/service/UserService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 58

### Line-by-Line Walkthrough
- L0001: `package com.auth.service;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0006: `` - Blank line used to separate logical blocks for readability.
- L0007: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0008: ` * Service interface for User operations.` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0010: `public interface UserService {` - Defines the core type and responsibility boundary for this file.
- L0011: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0012: `     * Get user by email.` - JavaDoc/comment line documenting intent and behavior.
- L0013: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0014: `     * @param email User email` - JavaDoc/comment line documenting intent and behavior.
- L0015: `     * @return User object` - JavaDoc/comment line documenting intent and behavior.
- L0016: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `    User getUserByEmail(String email);` - Implements part of the file's concrete application logic.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0020: `     * Find user by email (returns Optional).` - JavaDoc/comment line documenting intent and behavior.
- L0021: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0022: `     * @param email User email` - JavaDoc/comment line documenting intent and behavior.
- L0023: `     * @return Optional User` - JavaDoc/comment line documenting intent and behavior.
- L0024: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0025: `    Optional<User> findByEmail(String email);` - Data access call to load or persist domain objects.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0028: `     * Check if user exists by email.` - JavaDoc/comment line documenting intent and behavior.
- L0029: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0030: `     * @param email User email` - JavaDoc/comment line documenting intent and behavior.
- L0031: `     * @return true if exists` - JavaDoc/comment line documenting intent and behavior.
- L0032: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0033: `    boolean existsByEmail(String email);` - Data access call to load or persist domain objects.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0036: `     * Save user entity.` - JavaDoc/comment line documenting intent and behavior.
- L0037: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0038: `     * @param user User to save` - JavaDoc/comment line documenting intent and behavior.
- L0039: `     * @return Saved user` - JavaDoc/comment line documenting intent and behavior.
- L0040: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0041: `    User save(User user);` - Data access call to load or persist domain objects.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0044: `     * Find user by reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0045: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0046: `     * @param token Reset token` - JavaDoc/comment line documenting intent and behavior.
- L0047: `     * @return Optional User` - JavaDoc/comment line documenting intent and behavior.
- L0048: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0049: `    Optional<User> findByResetToken(String token);` - Security-related logic for tokens, OAuth, or authentication state.
- L0050: `` - Blank line used to separate logical blocks for readability.
- L0051: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0052: `     * Find user by refresh token.` - JavaDoc/comment line documenting intent and behavior.
- L0053: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0054: `     * @param token Refresh token` - JavaDoc/comment line documenting intent and behavior.
- L0055: `     * @return Optional User` - JavaDoc/comment line documenting intent and behavior.
- L0056: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0057: `    Optional<User> findByRefreshToken(String token);` - Security-related logic for tokens, OAuth, or authentication state.
- L0058: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 47: `backend/src/main/java/com/auth/service/auth/AuthAbuseProtectionService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 348

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.exception.AccountLockedException;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.exception.RateLimitExceededException;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.service.support.RateLimitService;` - Imports a type required by this file to compile and run.
- L0008: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0009: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.web.context.request.RequestContextHolder;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.web.context.request.ServletRequestAttributes;` - Imports a type required by this file to compile and run.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `import java.time.Duration;` - Imports a type required by this file to compile and run.
- L0016: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0017: `import java.util.Locale;` - Imports a type required by this file to compile and run.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0020: ` * Coordinates Redis rate limiting and user-level brute-force lockouts.` - JavaDoc/comment line documenting intent and behavior.
- L0021: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0022: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `public class AuthAbuseProtectionService {` - Defines the core type and responsibility boundary for this file.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    private static final String UNKNOWN_IP = "unknown";` - Implements part of the file's concrete application logic.
- L0027: `    private static final String UNKNOWN_EMAIL = "unknown-email";` - Implements part of the file's concrete application logic.
- L0028: `    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";` - Implements part of the file's concrete application logic.
- L0029: `    private static final String HEADER_X_REAL_IP = "X-Real-IP";` - Implements part of the file's concrete application logic.
- L0030: `    private static final String FORWARDED_FOR_SEPARATOR = ",";` - Implements part of the file's concrete application logic.
- L0031: `    private static final String RATE_LIMIT_LOGIN_IP_KEY_PREFIX = "auth:login:ip:";` - Implements part of the file's concrete application logic.
- L0032: `    private static final String RATE_LIMIT_LOGIN_EMAIL_KEY_PREFIX = "auth:login:email:";` - Implements part of the file's concrete application logic.
- L0033: `    private static final String RATE_LIMIT_OTP_VERIFY_IP_KEY_PREFIX = "auth:otp-verify:ip:";` - Credential or recovery logic for authentication safety.
- L0034: `    private static final String RATE_LIMIT_OTP_VERIFY_EMAIL_KEY_PREFIX = "auth:otp-verify:email:";` - Credential or recovery logic for authentication safety.
- L0035: `    private static final String RATE_LIMIT_RESEND_OTP_EMAIL_COOLDOWN_KEY_PREFIX = "auth:resend-otp:email-cooldown:";` - Credential or recovery logic for authentication safety.
- L0036: `    private static final String RATE_LIMIT_RESEND_OTP_EMAIL_KEY_PREFIX = "auth:resend-otp:email:";` - Credential or recovery logic for authentication safety.
- L0037: `    private static final String RATE_LIMIT_RESEND_OTP_IP_KEY_PREFIX = "auth:resend-otp:ip:";` - Credential or recovery logic for authentication safety.
- L0038: `    private static final String RATE_LIMIT_RESET_PASSWORD_EMAIL_KEY_PREFIX = "auth:reset-password:email:";` - Credential or recovery logic for authentication safety.
- L0039: `    private static final String RATE_LIMIT_RESET_PASSWORD_IP_KEY_PREFIX = "auth:reset-password:ip:";` - Credential or recovery logic for authentication safety.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    private final RateLimitService rateLimitService;` - Service interaction applies business logic or orchestration.
- L0042: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0043: `` - Blank line used to separate logical blocks for readability.
- L0044: `    @Value("${auth.protection.enabled:true}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    private boolean protectionEnabled;` - Implements part of the file's concrete application logic.
- L0046: `` - Blank line used to separate logical blocks for readability.
- L0047: `    @Value("${auth.rate-limit.login.ip.limit:5}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0048: `    private long loginIpLimit;` - Implements part of the file's concrete application logic.
- L0049: `    @Value("${auth.rate-limit.login.ip.window-seconds:60}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0050: `    private long loginIpWindowSeconds;` - Implements part of the file's concrete application logic.
- L0051: `    @Value("${auth.rate-limit.login.email.limit:10}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0052: `    private long loginEmailLimit;` - Implements part of the file's concrete application logic.
- L0053: `    @Value("${auth.rate-limit.login.email.window-seconds:900}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0054: `    private long loginEmailWindowSeconds;` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `    @Value("${auth.rate-limit.otp-verify.ip.limit:20}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0057: `    private long otpVerifyIpLimit;` - Credential or recovery logic for authentication safety.
- L0058: `    @Value("${auth.rate-limit.otp-verify.ip.window-seconds:600}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0059: `    private long otpVerifyIpWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0060: `    @Value("${auth.rate-limit.otp-verify.email.limit:5}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0061: `    private long otpVerifyEmailLimit;` - Credential or recovery logic for authentication safety.
- L0062: `    @Value("${auth.rate-limit.otp-verify.email.window-seconds:600}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0063: `    private long otpVerifyEmailWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `    @Value("${auth.rate-limit.resend-otp.email.cooldown-seconds:60}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0066: `    private long resendOtpCooldownSeconds;` - Credential or recovery logic for authentication safety.
- L0067: `    @Value("${auth.rate-limit.resend-otp.email.limit:3}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0068: `    private long resendOtpEmailLimit;` - Credential or recovery logic for authentication safety.
- L0069: `    @Value("${auth.rate-limit.resend-otp.email.window-seconds:900}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0070: `    private long resendOtpEmailWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0071: `    @Value("${auth.rate-limit.resend-otp.ip.limit:20}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0072: `    private long resendOtpIpLimit;` - Credential or recovery logic for authentication safety.
- L0073: `    @Value("${auth.rate-limit.resend-otp.ip.window-seconds:900}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0074: `    private long resendOtpIpWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0075: `` - Blank line used to separate logical blocks for readability.
- L0076: `    @Value("${auth.rate-limit.reset-password.email.limit:3}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0077: `    private long resetPasswordEmailLimit;` - Credential or recovery logic for authentication safety.
- L0078: `    @Value("${auth.rate-limit.reset-password.email.window-seconds:1800}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0079: `    private long resetPasswordEmailWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0080: `    @Value("${auth.rate-limit.reset-password.ip.limit:10}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0081: `    private long resetPasswordIpLimit;` - Credential or recovery logic for authentication safety.
- L0082: `    @Value("${auth.rate-limit.reset-password.ip.window-seconds:1800}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0083: `    private long resetPasswordIpWindowSeconds;` - Credential or recovery logic for authentication safety.
- L0084: `` - Blank line used to separate logical blocks for readability.
- L0085: `    @Value("${auth.bruteforce.login.max-attempts:10}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0086: `    private int loginMaxAttempts;` - Implements part of the file's concrete application logic.
- L0087: `    @Value("${auth.bruteforce.login.lock-minutes:15}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0088: `    private long loginLockMinutes;` - Implements part of the file's concrete application logic.
- L0089: `` - Blank line used to separate logical blocks for readability.
- L0090: `    @Value("${auth.bruteforce.otp.max-attempts:5}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0091: `    private int otpMaxAttempts;` - Credential or recovery logic for authentication safety.
- L0092: `    @Value("${auth.bruteforce.otp.lock-minutes:10}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0093: `    private long otpLockMinutes;` - Credential or recovery logic for authentication safety.
- L0094: `` - Blank line used to separate logical blocks for readability.
- L0095: `    /** Checks login endpoint rate limits and active account lock state. */` - JavaDoc/comment line documenting intent and behavior.
- L0096: `    public void guardLoginAttempt(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0097: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0098: `            return;` - Implements part of the file's concrete application logic.
- L0099: `        }` - Closes the current scope block.
- L0100: `` - Blank line used to separate logical blocks for readability.
- L0101: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0102: `        String clientIp = resolveClientIp();` - Implements part of the file's concrete application logic.
- L0103: `` - Blank line used to separate logical blocks for readability.
- L0104: `        enforce(buildRateLimitKey(RATE_LIMIT_LOGIN_IP_KEY_PREFIX, clientIp), loginIpLimit, loginIpWindowSeconds,` - Implements part of the file's concrete application logic.
- L0105: `                "Too many login attempts from this IP. Please retry later.");` - Implements part of the file's concrete application logic.
- L0106: `        enforce(buildRateLimitKey(RATE_LIMIT_LOGIN_EMAIL_KEY_PREFIX, normalizedEmail), loginEmailLimit,` - Implements part of the file's concrete application logic.
- L0107: `                loginEmailWindowSeconds,` - Implements part of the file's concrete application logic.
- L0108: `                "Too many login attempts for this account. Please retry later.");` - Implements part of the file's concrete application logic.
- L0109: `` - Blank line used to separate logical blocks for readability.
- L0110: `        userService.findByEmail(normalizedEmail).ifPresent(this::assertLoginNotLocked);` - Service interaction applies business logic or orchestration.
- L0111: `    }` - Closes the current scope block.
- L0112: `` - Blank line used to separate logical blocks for readability.
- L0113: `    /** Increments failed login attempts and applies temporary lockout when threshold is crossed. */` - JavaDoc/comment line documenting intent and behavior.
- L0114: `    public void recordFailedLogin(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0115: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0116: `            return;` - Implements part of the file's concrete application logic.
- L0117: `        }` - Closes the current scope block.
- L0118: `` - Blank line used to separate logical blocks for readability.
- L0119: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0120: `        userService.findByEmail(normalizedEmail).ifPresent(this::applyFailedLoginAttempt);` - Service interaction applies business logic or orchestration.
- L0121: `    }` - Closes the current scope block.
- L0122: `` - Blank line used to separate logical blocks for readability.
- L0123: `    /** Clears login brute-force counters on successful authentication. */` - JavaDoc/comment line documenting intent and behavior.
- L0124: `    public void clearLoginFailures(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0125: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0126: `            return;` - Implements part of the file's concrete application logic.
- L0127: `        }` - Closes the current scope block.
- L0128: `` - Blank line used to separate logical blocks for readability.
- L0129: `        if (user == null) {` - Conditional branch enforcing a business rule or guard path.
- L0130: `            return;` - Implements part of the file's concrete application logic.
- L0131: `        }` - Closes the current scope block.
- L0132: `` - Blank line used to separate logical blocks for readability.
- L0133: `        if (!hasLoginFailureState(user)) {` - Conditional branch enforcing a business rule or guard path.
- L0134: `            return;` - Implements part of the file's concrete application logic.
- L0135: `        }` - Closes the current scope block.
- L0136: `` - Blank line used to separate logical blocks for readability.
- L0137: `        user.setFailedLoginAttempts(0);` - Implements part of the file's concrete application logic.
- L0138: `        user.setAccountLockedUntil(null);` - Implements part of the file's concrete application logic.
- L0139: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0140: `    }` - Closes the current scope block.
- L0141: `` - Blank line used to separate logical blocks for readability.
- L0142: `    /** Checks OTP verification rate limits and OTP-specific lock state. */` - JavaDoc/comment line documenting intent and behavior.
- L0143: `    public void guardOtpVerification(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0144: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0145: `            return;` - Implements part of the file's concrete application logic.
- L0146: `        }` - Closes the current scope block.
- L0147: `` - Blank line used to separate logical blocks for readability.
- L0148: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0149: `        String clientIp = resolveClientIp();` - Implements part of the file's concrete application logic.
- L0150: `` - Blank line used to separate logical blocks for readability.
- L0151: `        enforce(buildRateLimitKey(RATE_LIMIT_OTP_VERIFY_IP_KEY_PREFIX, clientIp), otpVerifyIpLimit,` - Credential or recovery logic for authentication safety.
- L0152: `                otpVerifyIpWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0153: `                "Too many OTP verification attempts from this IP. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0154: `        enforce(buildRateLimitKey(RATE_LIMIT_OTP_VERIFY_EMAIL_KEY_PREFIX, normalizedEmail), otpVerifyEmailLimit,` - Credential or recovery logic for authentication safety.
- L0155: `                otpVerifyEmailWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0156: `                "Too many OTP verification attempts for this email. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0157: `` - Blank line used to separate logical blocks for readability.
- L0158: `        userService.findByEmail(normalizedEmail).ifPresent(this::assertOtpNotLocked);` - Service interaction applies business logic or orchestration.
- L0159: `    }` - Closes the current scope block.
- L0160: `` - Blank line used to separate logical blocks for readability.
- L0161: `    /** Increments OTP failure counter and applies temporary OTP lockout. */` - JavaDoc/comment line documenting intent and behavior.
- L0162: `    public void recordFailedOtp(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0163: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0164: `            return;` - Implements part of the file's concrete application logic.
- L0165: `        }` - Closes the current scope block.
- L0166: `` - Blank line used to separate logical blocks for readability.
- L0167: `        if (user == null) {` - Conditional branch enforcing a business rule or guard path.
- L0168: `            return;` - Implements part of the file's concrete application logic.
- L0169: `        }` - Closes the current scope block.
- L0170: `` - Blank line used to separate logical blocks for readability.
- L0171: `        int nextFailedAttempts = user.getFailedOtpAttempts() + 1;` - Credential or recovery logic for authentication safety.
- L0172: `        if (nextFailedAttempts >= otpMaxAttempts) {` - Conditional branch enforcing a business rule or guard path.
- L0173: `            user.setFailedOtpAttempts(0);` - Credential or recovery logic for authentication safety.
- L0174: `            user.setOtpLockedUntil(LocalDateTime.now().plusMinutes(otpLockMinutes));` - Credential or recovery logic for authentication safety.
- L0175: `        } else {` - Opens a new scope block for type, method, or control flow.
- L0176: `            user.setFailedOtpAttempts(nextFailedAttempts);` - Credential or recovery logic for authentication safety.
- L0177: `        }` - Closes the current scope block.
- L0178: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0179: `    }` - Closes the current scope block.
- L0180: `` - Blank line used to separate logical blocks for readability.
- L0181: `    /** Clears OTP brute-force counters after successful OTP verification. */` - JavaDoc/comment line documenting intent and behavior.
- L0182: `    public void clearOtpFailures(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0183: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0184: `            return;` - Implements part of the file's concrete application logic.
- L0185: `        }` - Closes the current scope block.
- L0186: `` - Blank line used to separate logical blocks for readability.
- L0187: `        if (user == null) {` - Conditional branch enforcing a business rule or guard path.
- L0188: `            return;` - Implements part of the file's concrete application logic.
- L0189: `        }` - Closes the current scope block.
- L0190: `` - Blank line used to separate logical blocks for readability.
- L0191: `        if (!hasOtpFailureState(user)) {` - Conditional branch enforcing a business rule or guard path.
- L0192: `            return;` - Implements part of the file's concrete application logic.
- L0193: `        }` - Closes the current scope block.
- L0194: `` - Blank line used to separate logical blocks for readability.
- L0195: `        user.setFailedOtpAttempts(0);` - Credential or recovery logic for authentication safety.
- L0196: `        user.setOtpLockedUntil(null);` - Credential or recovery logic for authentication safety.
- L0197: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0198: `    }` - Closes the current scope block.
- L0199: `` - Blank line used to separate logical blocks for readability.
- L0200: `    /** Applies resend-OTP endpoint limits (cooldown + window limits per email and IP). */` - JavaDoc/comment line documenting intent and behavior.
- L0201: `    public void guardResendOtp(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0202: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0203: `            return;` - Implements part of the file's concrete application logic.
- L0204: `        }` - Closes the current scope block.
- L0205: `` - Blank line used to separate logical blocks for readability.
- L0206: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0207: `        String clientIp = resolveClientIp();` - Implements part of the file's concrete application logic.
- L0208: `` - Blank line used to separate logical blocks for readability.
- L0209: `        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_EMAIL_COOLDOWN_KEY_PREFIX, normalizedEmail), 1,` - Credential or recovery logic for authentication safety.
- L0210: `                resendOtpCooldownSeconds,` - Credential or recovery logic for authentication safety.
- L0211: `                "Please wait before requesting another OTP.");` - Credential or recovery logic for authentication safety.
- L0212: `        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_EMAIL_KEY_PREFIX, normalizedEmail), resendOtpEmailLimit,` - Credential or recovery logic for authentication safety.
- L0213: `                resendOtpEmailWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0214: `                "Too many OTP resend requests for this email. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0215: `        enforce(buildRateLimitKey(RATE_LIMIT_RESEND_OTP_IP_KEY_PREFIX, clientIp), resendOtpIpLimit,` - Credential or recovery logic for authentication safety.
- L0216: `                resendOtpIpWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0217: `                "Too many OTP resend requests from this IP. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0218: `    }` - Closes the current scope block.
- L0219: `` - Blank line used to separate logical blocks for readability.
- L0220: `    /** Applies forgot-password endpoint limits per email and IP. */` - JavaDoc/comment line documenting intent and behavior.
- L0221: `    public void guardResetPassword(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0222: `        if (!protectionEnabled) {` - Conditional branch enforcing a business rule or guard path.
- L0223: `            return;` - Implements part of the file's concrete application logic.
- L0224: `        }` - Closes the current scope block.
- L0225: `` - Blank line used to separate logical blocks for readability.
- L0226: `        String normalizedEmail = normalizeEmail(email);` - Implements part of the file's concrete application logic.
- L0227: `        String clientIp = resolveClientIp();` - Implements part of the file's concrete application logic.
- L0228: `` - Blank line used to separate logical blocks for readability.
- L0229: `        enforce(buildRateLimitKey(RATE_LIMIT_RESET_PASSWORD_EMAIL_KEY_PREFIX, normalizedEmail), resetPasswordEmailLimit,` - Credential or recovery logic for authentication safety.
- L0230: `                resetPasswordEmailWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0231: `                "Too many password reset requests for this email. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0232: `        enforce(buildRateLimitKey(RATE_LIMIT_RESET_PASSWORD_IP_KEY_PREFIX, clientIp), resetPasswordIpLimit,` - Credential or recovery logic for authentication safety.
- L0233: `                resetPasswordIpWindowSeconds,` - Credential or recovery logic for authentication safety.
- L0234: `                "Too many password reset requests from this IP. Please retry later.");` - Credential or recovery logic for authentication safety.
- L0235: `    }` - Closes the current scope block.
- L0236: `` - Blank line used to separate logical blocks for readability.
- L0237: `    private void enforce(String key, long limit, long windowSeconds, String message) {` - Declares a method signature, contract, or constructor entry point.
- L0238: `        RateLimitService.RateLimitDecision decision = rateLimitService.consume(` - Service interaction applies business logic or orchestration.
- L0239: `                key,` - Implements part of the file's concrete application logic.
- L0240: `                limit,` - Implements part of the file's concrete application logic.
- L0241: `                Duration.ofSeconds(windowSeconds));` - Implements part of the file's concrete application logic.
- L0242: `        if (decision.allowed()) {` - Conditional branch enforcing a business rule or guard path.
- L0243: `            return;` - Implements part of the file's concrete application logic.
- L0244: `        }` - Closes the current scope block.
- L0245: `` - Blank line used to separate logical blocks for readability.
- L0246: `        long retryAfterSeconds = Math.max(1, decision.retryAfterSeconds());` - Implements part of the file's concrete application logic.
- L0247: `        throw new RateLimitExceededException(message, retryAfterSeconds);` - Raises an exception for invalid state or request path.
- L0248: `    }` - Closes the current scope block.
- L0249: `` - Blank line used to separate logical blocks for readability.
- L0250: `    private void assertLoginNotLocked(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0251: `        LocalDateTime lockedUntil = user.getAccountLockedUntil();` - Implements part of the file's concrete application logic.
- L0252: `        if (!isActiveLock(lockedUntil)) {` - Conditional branch enforcing a business rule or guard path.
- L0253: `            return;` - Implements part of the file's concrete application logic.
- L0254: `        }` - Closes the current scope block.
- L0255: `` - Blank line used to separate logical blocks for readability.
- L0256: `        long retryAfterSeconds = computeRetryAfterSeconds(lockedUntil);` - Implements part of the file's concrete application logic.
- L0257: `        throw new AccountLockedException("Account is temporarily locked due to repeated failed logins.",` - Raises an exception for invalid state or request path.
- L0258: `                Math.max(1, retryAfterSeconds));` - Implements part of the file's concrete application logic.
- L0259: `    }` - Closes the current scope block.
- L0260: `` - Blank line used to separate logical blocks for readability.
- L0261: `    private void assertOtpNotLocked(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0262: `        LocalDateTime lockedUntil = user.getOtpLockedUntil();` - Credential or recovery logic for authentication safety.
- L0263: `        if (!isActiveLock(lockedUntil)) {` - Conditional branch enforcing a business rule or guard path.
- L0264: `            return;` - Implements part of the file's concrete application logic.
- L0265: `        }` - Closes the current scope block.
- L0266: `` - Blank line used to separate logical blocks for readability.
- L0267: `        long retryAfterSeconds = computeRetryAfterSeconds(lockedUntil);` - Implements part of the file's concrete application logic.
- L0268: `        throw new AccountLockedException("OTP verification is temporarily locked due to repeated failed attempts.",` - Raises an exception for invalid state or request path.
- L0269: `                Math.max(1, retryAfterSeconds));` - Implements part of the file's concrete application logic.
- L0270: `    }` - Closes the current scope block.
- L0271: `` - Blank line used to separate logical blocks for readability.
- L0272: `    private String resolveClientIp() {` - Declares a method signature, contract, or constructor entry point.
- L0273: `        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();` - Implements part of the file's concrete application logic.
- L0274: `        if (attributes == null) {` - Conditional branch enforcing a business rule or guard path.
- L0275: `            return UNKNOWN_IP;` - Returns data to caller after applying current method logic.
- L0276: `        }` - Closes the current scope block.
- L0277: `` - Blank line used to separate logical blocks for readability.
- L0278: `        HttpServletRequest request = attributes.getRequest();` - Implements part of the file's concrete application logic.
- L0279: `        String forwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);` - Implements part of the file's concrete application logic.
- L0280: `        if (forwardedFor != null && !forwardedFor.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0281: `            String[] chain = forwardedFor.split(FORWARDED_FOR_SEPARATOR);` - Implements part of the file's concrete application logic.
- L0282: `            if (chain.length > 0 && !chain[0].isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0283: `                return chain[0].trim();` - Returns data to caller after applying current method logic.
- L0284: `            }` - Closes the current scope block.
- L0285: `        }` - Closes the current scope block.
- L0286: `` - Blank line used to separate logical blocks for readability.
- L0287: `        String realIp = request.getHeader(HEADER_X_REAL_IP);` - Implements part of the file's concrete application logic.
- L0288: `        if (realIp != null && !realIp.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0289: `            return realIp.trim();` - Returns data to caller after applying current method logic.
- L0290: `        }` - Closes the current scope block.
- L0291: `` - Blank line used to separate logical blocks for readability.
- L0292: `        String remoteAddress = request.getRemoteAddr();` - Implements part of the file's concrete application logic.
- L0293: `        if (remoteAddress == null) {` - Conditional branch enforcing a business rule or guard path.
- L0294: `            return UNKNOWN_IP;` - Returns data to caller after applying current method logic.
- L0295: `        }` - Closes the current scope block.
- L0296: `` - Blank line used to separate logical blocks for readability.
- L0297: `        if (remoteAddress.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0298: `            return UNKNOWN_IP;` - Returns data to caller after applying current method logic.
- L0299: `        }` - Closes the current scope block.
- L0300: `` - Blank line used to separate logical blocks for readability.
- L0301: `        return remoteAddress.trim();` - Returns data to caller after applying current method logic.
- L0302: `    }` - Closes the current scope block.
- L0303: `` - Blank line used to separate logical blocks for readability.
- L0304: `    private String normalizeEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0305: `        if (email == null || email.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0306: `            return UNKNOWN_EMAIL;` - Returns data to caller after applying current method logic.
- L0307: `        }` - Closes the current scope block.
- L0308: `        return email.trim().toLowerCase(Locale.ROOT);` - Returns data to caller after applying current method logic.
- L0309: `    }` - Closes the current scope block.
- L0310: `` - Blank line used to separate logical blocks for readability.
- L0311: `    private String buildRateLimitKey(String prefix, String keyPart) {` - Declares a method signature, contract, or constructor entry point.
- L0312: `        return prefix + keyPart;` - Returns data to caller after applying current method logic.
- L0313: `    }` - Closes the current scope block.
- L0314: `` - Blank line used to separate logical blocks for readability.
- L0315: `    private void applyFailedLoginAttempt(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0316: `        int nextFailedAttempts = user.getFailedLoginAttempts() + 1;` - Implements part of the file's concrete application logic.
- L0317: `        if (nextFailedAttempts >= loginMaxAttempts) {` - Conditional branch enforcing a business rule or guard path.
- L0318: `            user.setFailedLoginAttempts(0);` - Implements part of the file's concrete application logic.
- L0319: `            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(loginLockMinutes));` - Implements part of the file's concrete application logic.
- L0320: `        } else {` - Opens a new scope block for type, method, or control flow.
- L0321: `            user.setFailedLoginAttempts(nextFailedAttempts);` - Implements part of the file's concrete application logic.
- L0322: `        }` - Closes the current scope block.
- L0323: `` - Blank line used to separate logical blocks for readability.
- L0324: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0325: `    }` - Closes the current scope block.
- L0326: `` - Blank line used to separate logical blocks for readability.
- L0327: `    private boolean hasLoginFailureState(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0328: `        return user.getFailedLoginAttempts() != 0 || user.getAccountLockedUntil() != null;` - Returns data to caller after applying current method logic.
- L0329: `    }` - Closes the current scope block.
- L0330: `` - Blank line used to separate logical blocks for readability.
- L0331: `    private boolean hasOtpFailureState(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0332: `        return user.getFailedOtpAttempts() != 0 || user.getOtpLockedUntil() != null;` - Returns data to caller after applying current method logic.
- L0333: `    }` - Closes the current scope block.
- L0334: `` - Blank line used to separate logical blocks for readability.
- L0335: `    private boolean isActiveLock(LocalDateTime lockedUntil) {` - Declares a method signature, contract, or constructor entry point.
- L0336: `        if (lockedUntil == null) {` - Conditional branch enforcing a business rule or guard path.
- L0337: `            return false;` - Returns data to caller after applying current method logic.
- L0338: `        }` - Closes the current scope block.
- L0339: `` - Blank line used to separate logical blocks for readability.
- L0340: `        LocalDateTime now = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0341: `        return lockedUntil.isAfter(now);` - Returns data to caller after applying current method logic.
- L0342: `    }` - Closes the current scope block.
- L0343: `` - Blank line used to separate logical blocks for readability.
- L0344: `    private long computeRetryAfterSeconds(LocalDateTime lockedUntil) {` - Declares a method signature, contract, or constructor entry point.
- L0345: `        LocalDateTime now = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0346: `        return Duration.between(now, lockedUntil).getSeconds();` - Returns data to caller after applying current method logic.
- L0347: `    }` - Closes the current scope block.
- L0348: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 48: `backend/src/main/java/com/auth/service/auth/AuthTokenService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 147

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AuthResponse;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.exception.TokenValidationException;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.security.JwtUtil;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0010: `import com.auth.service.support.TokenHashService;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.transaction.annotation.Transactional;` - Imports a type required by this file to compile and run.
- L0014: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `import java.security.SecureRandom;` - Imports a type required by this file to compile and run.
- L0017: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0018: `import java.util.Base64;` - Imports a type required by this file to compile and run.
- L0019: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0020: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0021: `import java.util.stream.Collectors;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0024: ` * Handles issuing, rotating, and invalidating access/refresh tokens.` - JavaDoc/comment line documenting intent and behavior.
- L0025: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0026: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `public class AuthTokenService {` - Defines the core type and responsibility boundary for this file.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    private static final int REFRESH_TOKEN_BYTE_LENGTH = 64;` - Security-related logic for tokens, OAuth, or authentication state.
- L0031: `    private static final long MILLISECONDS_PER_SECOND = 1000L;` - Implements part of the file's concrete application logic.
- L0032: `    private static final String BEARER_TOKEN_TYPE = "Bearer";` - Security-related logic for tokens, OAuth, or authentication state.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `    private static final SecureRandom SECURE_RANDOM = new SecureRandom();` - Declares a method signature, contract, or constructor entry point.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    private final JwtUtil jwtUtil;` - Security-related logic for tokens, OAuth, or authentication state.
- L0037: `` - Blank line used to separate logical blocks for readability.
- L0038: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    private final TokenHashService tokenHashService;` - Service interaction applies business logic or orchestration.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    @Value("${jwt.refresh.expiration}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0043: `    private long refreshTokenExpirationMs;` - Security-related logic for tokens, OAuth, or authentication state.
- L0044: `` - Blank line used to separate logical blocks for readability.
- L0045: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0046: `     * Issue a new access token and refresh token for the user.` - JavaDoc/comment line documenting intent and behavior.
- L0047: `     * Refresh token is rotated and persisted.` - JavaDoc/comment line documenting intent and behavior.
- L0048: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0049: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0050: `    public AuthTokens issueTokens(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0051: `        String accessToken = jwtUtil.generateTokenFromEmail(user.getEmail());` - Security-related logic for tokens, OAuth, or authentication state.
- L0052: `        String refreshToken = generateRefreshToken();` - Security-related logic for tokens, OAuth, or authentication state.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `        user.setRefreshToken(tokenHashService.hash(refreshToken));` - Service interaction applies business logic or orchestration.
- L0055: `        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / MILLISECONDS_PER_SECOND));` - Security-related logic for tokens, OAuth, or authentication state.
- L0056: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0057: `` - Blank line used to separate logical blocks for readability.
- L0058: `        AuthResponse response = buildAuthResponse(user, accessToken);` - Security-related logic for tokens, OAuth, or authentication state.
- L0059: `        return new AuthTokens(response, refreshToken);` - Returns data to caller after applying current method logic.
- L0060: `    }` - Closes the current scope block.
- L0061: `` - Blank line used to separate logical blocks for readability.
- L0062: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0063: `     * Rotate refresh token and issue a fresh access token.` - JavaDoc/comment line documenting intent and behavior.
- L0064: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0065: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0066: `    public AuthTokens refreshTokens(String refreshToken) {` - Declares a method signature, contract, or constructor entry point.
- L0067: `        if (isBlank(refreshToken)) {` - Conditional branch enforcing a business rule or guard path.
- L0068: `            throw new TokenValidationException("Refresh token is required.");` - Raises an exception for invalid state or request path.
- L0069: `        }` - Closes the current scope block.
- L0070: `` - Blank line used to separate logical blocks for readability.
- L0071: `        String refreshTokenHash = tokenHashService.hash(refreshToken);` - Service interaction applies business logic or orchestration.
- L0072: `        User user = userService.findByRefreshToken(refreshTokenHash)` - Service interaction applies business logic or orchestration.
- L0073: `                .orElseThrow(() -> new TokenValidationException("Invalid refresh token."));` - Security-related logic for tokens, OAuth, or authentication state.
- L0074: `` - Blank line used to separate logical blocks for readability.
- L0075: `        if (isRefreshTokenExpired(user.getRefreshTokenExpiry())) {` - Conditional branch enforcing a business rule or guard path.
- L0076: `            user.setRefreshToken(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0077: `            user.setRefreshTokenExpiry(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0078: `            userService.save(user);` - Service interaction applies business logic or orchestration.
- L0079: `            throw new TokenValidationException("Refresh token has expired. Please login again.");` - Raises an exception for invalid state or request path.
- L0080: `        }` - Closes the current scope block.
- L0081: `` - Blank line used to separate logical blocks for readability.
- L0082: `        return issueTokens(user);` - Returns data to caller after applying current method logic.
- L0083: `    }` - Closes the current scope block.
- L0084: `` - Blank line used to separate logical blocks for readability.
- L0085: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0086: `     * Invalidate refresh token if present.` - JavaDoc/comment line documenting intent and behavior.
- L0087: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0088: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0089: `    public void revokeRefreshToken(String refreshToken) {` - Declares a method signature, contract, or constructor entry point.
- L0090: `        if (isBlank(refreshToken)) {` - Conditional branch enforcing a business rule or guard path.
- L0091: `            return;` - Implements part of the file's concrete application logic.
- L0092: `        }` - Closes the current scope block.
- L0093: `` - Blank line used to separate logical blocks for readability.
- L0094: `        String refreshTokenHash = tokenHashService.hash(refreshToken);` - Service interaction applies business logic or orchestration.
- L0095: `        Optional<User> userOpt = userService.findByRefreshToken(refreshTokenHash);` - Service interaction applies business logic or orchestration.
- L0096: `        if (userOpt.isEmpty()) {` - Conditional branch enforcing a business rule or guard path.
- L0097: `            return;` - Implements part of the file's concrete application logic.
- L0098: `        }` - Closes the current scope block.
- L0099: `` - Blank line used to separate logical blocks for readability.
- L0100: `        User user = userOpt.get();` - Implements part of the file's concrete application logic.
- L0101: `        user.setRefreshToken(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0102: `        user.setRefreshTokenExpiry(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0103: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0104: `    }` - Closes the current scope block.
- L0105: `` - Blank line used to separate logical blocks for readability.
- L0106: `    /** Builds API auth response payload with token metadata and current user details. */` - JavaDoc/comment line documenting intent and behavior.
- L0107: `    private AuthResponse buildAuthResponse(User user, String accessToken) {` - Declares a method signature, contract, or constructor entry point.
- L0108: `        List<String> roles = user.getRoles().stream()` - Authorization rule, authority mapping, or role handling line.
- L0109: `                .map(Role::getName)` - Authorization rule, authority mapping, or role handling line.
- L0110: `                .map(Enum::name)` - Implements part of the file's concrete application logic.
- L0111: `                .collect(Collectors.toList());` - Implements part of the file's concrete application logic.
- L0112: `` - Blank line used to separate logical blocks for readability.
- L0113: `        return new AuthResponse(` - Returns data to caller after applying current method logic.
- L0114: `                accessToken,` - Security-related logic for tokens, OAuth, or authentication state.
- L0115: `                BEARER_TOKEN_TYPE,` - Security-related logic for tokens, OAuth, or authentication state.
- L0116: `                jwtUtil.getAccessTokenExpiration(),` - Security-related logic for tokens, OAuth, or authentication state.
- L0117: `                refreshTokenExpirationMs,` - Security-related logic for tokens, OAuth, or authentication state.
- L0118: `                user.getId(),` - Implements part of the file's concrete application logic.
- L0119: `                user.getName(),` - Implements part of the file's concrete application logic.
- L0120: `                user.getEmail(),` - Implements part of the file's concrete application logic.
- L0121: `                roles);` - Authorization rule, authority mapping, or role handling line.
- L0122: `    }` - Closes the current scope block.
- L0123: `` - Blank line used to separate logical blocks for readability.
- L0124: `    /** Generates a high-entropy URL-safe refresh token. */` - JavaDoc/comment line documenting intent and behavior.
- L0125: `    private String generateRefreshToken() {` - Declares a method signature, contract, or constructor entry point.
- L0126: `        byte[] randomBytes = new byte[REFRESH_TOKEN_BYTE_LENGTH];` - Security-related logic for tokens, OAuth, or authentication state.
- L0127: `        SECURE_RANDOM.nextBytes(randomBytes);` - Implements part of the file's concrete application logic.
- L0128: `        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);` - Returns data to caller after applying current method logic.
- L0129: `    }` - Closes the current scope block.
- L0130: `` - Blank line used to separate logical blocks for readability.
- L0131: `    private boolean isBlank(String value) {` - Declares a method signature, contract, or constructor entry point.
- L0132: `        if (value == null) {` - Conditional branch enforcing a business rule or guard path.
- L0133: `            return true;` - Returns data to caller after applying current method logic.
- L0134: `        }` - Closes the current scope block.
- L0135: `        return value.isBlank();` - Returns data to caller after applying current method logic.
- L0136: `    }` - Closes the current scope block.
- L0137: `` - Blank line used to separate logical blocks for readability.
- L0138: `    private boolean isRefreshTokenExpired(LocalDateTime expiry) {` - Declares a method signature, contract, or constructor entry point.
- L0139: `        if (expiry == null) {` - Conditional branch enforcing a business rule or guard path.
- L0140: `            return true;` - Returns data to caller after applying current method logic.
- L0141: `        }` - Closes the current scope block.
- L0142: `` - Blank line used to separate logical blocks for readability.
- L0143: `        LocalDateTime now = LocalDateTime.now();` - Implements part of the file's concrete application logic.
- L0144: `        return expiry.isBefore(now);` - Returns data to caller after applying current method logic.
- L0145: `    }` - Closes the current scope block.
- L0146: `` - Blank line used to separate logical blocks for readability.
- L0147: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 49: `backend/src/main/java/com/auth/service/auth/OAuth2UserProvisioningService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 182

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.RoleService;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.security.oauth2.core.user.OAuth2User;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `import java.util.HashSet;` - Imports a type required by this file to compile and run.
- L0014: `import java.util.Map;` - Imports a type required by this file to compile and run.
- L0015: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0016: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0017: `import java.util.UUID;` - Imports a type required by this file to compile and run.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0020: ` * Resolves OAuth2 user data and creates/updates local users.` - JavaDoc/comment line documenting intent and behavior.
- L0021: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0022: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `public class OAuth2UserProvisioningService {` - Defines the core type and responsibility boundary for this file.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    private static final String EMAIL_ATTRIBUTE = "email";` - Implements part of the file's concrete application logic.
- L0027: `    private static final String NAME_ATTRIBUTE = "name";` - Implements part of the file's concrete application logic.
- L0028: `    private static final String PREFERRED_USERNAME_ATTRIBUTE = "preferred_username";` - Implements part of the file's concrete application logic.
- L0029: `    private static final String LOGIN_ATTRIBUTE = "login";` - Implements part of the file's concrete application logic.
- L0030: `    private static final String GIVEN_NAME_ATTRIBUTE = "given_name";` - Implements part of the file's concrete application logic.
- L0031: `    private static final String FAMILY_NAME_ATTRIBUTE = "family_name";` - Implements part of the file's concrete application logic.
- L0032: `    private static final String GITHUB_PROVIDER = "github";` - Implements part of the file's concrete application logic.
- L0033: `    private static final String GITHUB_NO_REPLY_SUFFIX = "@users.noreply.github.com";` - Implements part of the file's concrete application logic.
- L0034: `    private static final char EMAIL_SEPARATOR = '@';` - Implements part of the file's concrete application logic.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0037: `` - Blank line used to separate logical blocks for readability.
- L0038: `    private final RoleService roleService;` - Service interaction applies business logic or orchestration.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    private final PasswordEncoder passwordEncoder;` - Credential or recovery logic for authentication safety.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    /** Loads an existing OAuth user or creates a local enabled user profile when first seen. */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    public User loadOrCreateUser(OAuth2AuthenticationToken authenticationToken, OAuth2User oauth2User) {` - Declares a method signature, contract, or constructor entry point.
- L0044: `        String provider = normalize(authenticationToken.getAuthorizedClientRegistrationId());` - Security-related logic for tokens, OAuth, or authentication state.
- L0045: `        Map<String, Object> attributes = oauth2User.getAttributes();` - Security-related logic for tokens, OAuth, or authentication state.
- L0046: `` - Blank line used to separate logical blocks for readability.
- L0047: `        String email = normalize(extractEmail(provider, attributes));` - Implements part of the file's concrete application logic.
- L0048: `        String name = extractDisplayName(attributes, email);` - Implements part of the file's concrete application logic.
- L0049: `` - Blank line used to separate logical blocks for readability.
- L0050: `        Optional<User> existingUser = userService.findByEmail(email);` - Service interaction applies business logic or orchestration.
- L0051: `        if (existingUser.isPresent()) {` - Conditional branch enforcing a business rule or guard path.
- L0052: `            return updateExistingUserIfNeeded(existingUser.get(), provider, name);` - Returns data to caller after applying current method logic.
- L0053: `        }` - Closes the current scope block.
- L0054: `` - Blank line used to separate logical blocks for readability.
- L0055: `        User newUser = new User();` - Implements part of the file's concrete application logic.
- L0056: `        newUser.setName(name);` - Implements part of the file's concrete application logic.
- L0057: `        newUser.setEmail(email);` - Implements part of the file's concrete application logic.
- L0058: `        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));` - Credential or recovery logic for authentication safety.
- L0059: `        newUser.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0060: `        newUser.setAuthProvider(provider);` - Implements part of the file's concrete application logic.
- L0061: `` - Blank line used to separate logical blocks for readability.
- L0062: `        Set<Role> roles = new HashSet<>();` - Authorization rule, authority mapping, or role handling line.
- L0063: `        roles.add(roleService.findOrCreateRole(Role.RoleName.ROLE_USER));` - Service interaction applies business logic or orchestration.
- L0064: `        newUser.setRoles(roles);` - Authorization rule, authority mapping, or role handling line.
- L0065: `` - Blank line used to separate logical blocks for readability.
- L0066: `        return userService.save(newUser);` - Returns data to caller after applying current method logic.
- L0067: `    }` - Closes the current scope block.
- L0068: `` - Blank line used to separate logical blocks for readability.
- L0069: `    /** Updates existing OAuth-linked users only when persistence changes are required. */` - JavaDoc/comment line documenting intent and behavior.
- L0070: `    private User updateExistingUserIfNeeded(User user, String provider, String displayName) {` - Declares a method signature, contract, or constructor entry point.
- L0071: `        boolean changed = false;` - Implements part of the file's concrete application logic.
- L0072: `` - Blank line used to separate logical blocks for readability.
- L0073: `        if (!user.isEnabled()) {` - Conditional branch enforcing a business rule or guard path.
- L0074: `            user.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0075: `            changed = true;` - Implements part of the file's concrete application logic.
- L0076: `        }` - Closes the current scope block.
- L0077: `` - Blank line used to separate logical blocks for readability.
- L0078: `        if (!hasText(user.getAuthProvider()) && hasText(provider)) {` - Conditional branch enforcing a business rule or guard path.
- L0079: `            user.setAuthProvider(provider);` - Implements part of the file's concrete application logic.
- L0080: `            changed = true;` - Implements part of the file's concrete application logic.
- L0081: `        }` - Closes the current scope block.
- L0082: `` - Blank line used to separate logical blocks for readability.
- L0083: `        if (!hasText(user.getName()) && hasText(displayName)) {` - Conditional branch enforcing a business rule or guard path.
- L0084: `            user.setName(displayName);` - Implements part of the file's concrete application logic.
- L0085: `            changed = true;` - Implements part of the file's concrete application logic.
- L0086: `        }` - Closes the current scope block.
- L0087: `` - Blank line used to separate logical blocks for readability.
- L0088: `        if (!changed) {` - Conditional branch enforcing a business rule or guard path.
- L0089: `            return user;` - Returns data to caller after applying current method logic.
- L0090: `        }` - Closes the current scope block.
- L0091: `        return userService.save(user);` - Returns data to caller after applying current method logic.
- L0092: `    }` - Closes the current scope block.
- L0093: `` - Blank line used to separate logical blocks for readability.
- L0094: `    /** Extracts a reliable email from provider attributes with provider-specific fallback logic. */` - JavaDoc/comment line documenting intent and behavior.
- L0095: `    private String extractEmail(String provider, Map<String, Object> attributes) {` - Declares a method signature, contract, or constructor entry point.
- L0096: `        String email = normalize(toString(attributes.get(EMAIL_ATTRIBUTE)));` - Implements part of the file's concrete application logic.
- L0097: `        if (hasText(email)) {` - Conditional branch enforcing a business rule or guard path.
- L0098: `            return email;` - Returns data to caller after applying current method logic.
- L0099: `        }` - Closes the current scope block.
- L0100: `` - Blank line used to separate logical blocks for readability.
- L0101: `        if (GITHUB_PROVIDER.equals(provider)) {` - Conditional branch enforcing a business rule or guard path.
- L0102: `            String login = normalize(toString(attributes.get(LOGIN_ATTRIBUTE)));` - Implements part of the file's concrete application logic.
- L0103: `            if (hasText(login)) {` - Conditional branch enforcing a business rule or guard path.
- L0104: `                return login + GITHUB_NO_REPLY_SUFFIX;` - Returns data to caller after applying current method logic.
- L0105: `            }` - Closes the current scope block.
- L0106: `        }` - Closes the current scope block.
- L0107: `` - Blank line used to separate logical blocks for readability.
- L0108: `        throw new IllegalArgumentException("Email is not available from " + provider + " OAuth profile.");` - Raises an exception for invalid state or request path.
- L0109: `    }` - Closes the current scope block.
- L0110: `` - Blank line used to separate logical blocks for readability.
- L0111: `    /** Resolves a display name from known OAuth profile attributes. */` - JavaDoc/comment line documenting intent and behavior.
- L0112: `    private String extractDisplayName(Map<String, Object> attributes, String email) {` - Declares a method signature, contract, or constructor entry point.
- L0113: `        String name = firstNonBlank(` - Implements part of the file's concrete application logic.
- L0114: `                normalize(toString(attributes.get(NAME_ATTRIBUTE))),` - Implements part of the file's concrete application logic.
- L0115: `                normalize(toString(attributes.get(PREFERRED_USERNAME_ATTRIBUTE))),` - Implements part of the file's concrete application logic.
- L0116: `                normalize(toString(attributes.get(LOGIN_ATTRIBUTE))));` - Implements part of the file's concrete application logic.
- L0117: `` - Blank line used to separate logical blocks for readability.
- L0118: `        if (name != null) {` - Conditional branch enforcing a business rule or guard path.
- L0119: `            return name;` - Returns data to caller after applying current method logic.
- L0120: `        }` - Closes the current scope block.
- L0121: `` - Blank line used to separate logical blocks for readability.
- L0122: `        String givenName = normalize(toString(attributes.get(GIVEN_NAME_ATTRIBUTE)));` - Implements part of the file's concrete application logic.
- L0123: `        String familyName = normalize(toString(attributes.get(FAMILY_NAME_ATTRIBUTE)));` - Implements part of the file's concrete application logic.
- L0124: `        String combinedName = firstNonBlank(` - Implements part of the file's concrete application logic.
- L0125: `                joinWithSpace(givenName, familyName),` - Implements part of the file's concrete application logic.
- L0126: `                givenName,` - Implements part of the file's concrete application logic.
- L0127: `                familyName);` - Implements part of the file's concrete application logic.
- L0128: `` - Blank line used to separate logical blocks for readability.
- L0129: `        if (combinedName != null) {` - Conditional branch enforcing a business rule or guard path.
- L0130: `            return combinedName;` - Returns data to caller after applying current method logic.
- L0131: `        }` - Closes the current scope block.
- L0132: `` - Blank line used to separate logical blocks for readability.
- L0133: `        int separatorIndex = email.indexOf(EMAIL_SEPARATOR);` - Implements part of the file's concrete application logic.
- L0134: `        if (separatorIndex <= 0) {` - Conditional branch enforcing a business rule or guard path.
- L0135: `            return email;` - Returns data to caller after applying current method logic.
- L0136: `        }` - Closes the current scope block.
- L0137: `        return email.substring(0, separatorIndex);` - Returns data to caller after applying current method logic.
- L0138: `    }` - Closes the current scope block.
- L0139: `` - Blank line used to separate logical blocks for readability.
- L0140: `    /** Joins two non-blank strings with a single space. */` - JavaDoc/comment line documenting intent and behavior.
- L0141: `    private String joinWithSpace(String left, String right) {` - Declares a method signature, contract, or constructor entry point.
- L0142: `        if (!hasText(left)) {` - Conditional branch enforcing a business rule or guard path.
- L0143: `            return right;` - Returns data to caller after applying current method logic.
- L0144: `        }` - Closes the current scope block.
- L0145: `        if (!hasText(right)) {` - Conditional branch enforcing a business rule or guard path.
- L0146: `            return left;` - Returns data to caller after applying current method logic.
- L0147: `        }` - Closes the current scope block.
- L0148: `        return left + " " + right;` - Returns data to caller after applying current method logic.
- L0149: `    }` - Closes the current scope block.
- L0150: `` - Blank line used to separate logical blocks for readability.
- L0151: `    /** Returns the first non-blank string in priority order. */` - JavaDoc/comment line documenting intent and behavior.
- L0152: `    private String firstNonBlank(String... values) {` - Declares a method signature, contract, or constructor entry point.
- L0153: `        for (String value : values) {` - Iterates through values to process collections or repeated logic.
- L0154: `            if (hasText(value)) {` - Conditional branch enforcing a business rule or guard path.
- L0155: `                return value;` - Returns data to caller after applying current method logic.
- L0156: `            }` - Closes the current scope block.
- L0157: `        }` - Closes the current scope block.
- L0158: `        return null;` - Returns data to caller after applying current method logic.
- L0159: `    }` - Closes the current scope block.
- L0160: `` - Blank line used to separate logical blocks for readability.
- L0161: `    /** Safely converts an arbitrary OAuth attribute value to String. */` - JavaDoc/comment line documenting intent and behavior.
- L0162: `    private String toString(Object value) {` - Declares a method signature, contract, or constructor entry point.
- L0163: `        if (value == null) {` - Conditional branch enforcing a business rule or guard path.
- L0164: `            return null;` - Returns data to caller after applying current method logic.
- L0165: `        }` - Closes the current scope block.
- L0166: `        return String.valueOf(value);` - Returns data to caller after applying current method logic.
- L0167: `    }` - Closes the current scope block.
- L0168: `` - Blank line used to separate logical blocks for readability.
- L0169: `    private String normalize(String value) {` - Declares a method signature, contract, or constructor entry point.
- L0170: `        if (value == null) {` - Conditional branch enforcing a business rule or guard path.
- L0171: `            return null;` - Returns data to caller after applying current method logic.
- L0172: `        }` - Closes the current scope block.
- L0173: `        return value.trim();` - Returns data to caller after applying current method logic.
- L0174: `    }` - Closes the current scope block.
- L0175: `` - Blank line used to separate logical blocks for readability.
- L0176: `    private boolean hasText(String value) {` - Declares a method signature, contract, or constructor entry point.
- L0177: `        if (value == null) {` - Conditional branch enforcing a business rule or guard path.
- L0178: `            return false;` - Returns data to caller after applying current method logic.
- L0179: `        }` - Closes the current scope block.
- L0180: `        return !value.isBlank();` - Returns data to caller after applying current method logic.
- L0181: `    }` - Closes the current scope block.
- L0182: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 50: `backend/src/main/java/com/auth/service/impl/AdminServiceImpl.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 185

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AdminDashboardDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.mapper.UserMapper;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.repository.UserRepository;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.service.AdminService;` - Imports a type required by this file to compile and run.
- L0010: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.data.domain.Page;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.data.domain.PageRequest;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.data.domain.Pageable;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.data.domain.Sort;` - Imports a type required by this file to compile and run.
- L0015: `import org.springframework.data.jpa.domain.Specification;` - Imports a type required by this file to compile and run.
- L0016: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0019: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0020: `import java.util.Locale;` - Imports a type required by this file to compile and run.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0023: ` * Admin-specific business logic for dashboard metrics and user listing.` - JavaDoc/comment line documenting intent and behavior.
- L0024: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0025: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0026: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `public class AdminServiceImpl implements AdminService {` - Defines the core type and responsibility boundary for this file.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    private static final int MIN_PAGE = 0;` - Implements part of the file's concrete application logic.
- L0030: `    private static final int MIN_PAGE_SIZE = 1;` - Implements part of the file's concrete application logic.
- L0031: `    private static final int MAX_PAGE_SIZE = 100;` - Implements part of the file's concrete application logic.
- L0032: `    private static final String DEFAULT_SORT_FIELD = "createdAt";` - Implements part of the file's concrete application logic.
- L0033: `    private static final String ROLE_PREFIX = "ROLE_";` - Authorization rule, authority mapping, or role handling line.
- L0034: `    private static final String SORT_DIRECTION_ASC = "asc";` - Implements part of the file's concrete application logic.
- L0035: `    private static final String ERROR_INVALID_ROLE_FILTER =` - Authorization rule, authority mapping, or role handling line.
- L0036: `            "Invalid role filter. Use USER, ADMIN, ROLE_USER, or ROLE_ADMIN.";` - Authorization rule, authority mapping, or role handling line.
- L0037: `    private static final String FIELD_ENABLED = "enabled";` - Implements part of the file's concrete application logic.
- L0038: `    private static final String FIELD_ROLES = "roles";` - Authorization rule, authority mapping, or role handling line.
- L0039: `    private static final String FIELD_ROLE_NAME = "name";` - Authorization rule, authority mapping, or role handling line.
- L0040: `    private static final String FIELD_USER_NAME = "name";` - Implements part of the file's concrete application logic.
- L0041: `    private static final String FIELD_EMAIL = "email";` - Implements part of the file's concrete application logic.
- L0042: `    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(` - Declares a method signature, contract, or constructor entry point.
- L0043: `            "id",` - Implements part of the file's concrete application logic.
- L0044: `            FIELD_USER_NAME,` - Implements part of the file's concrete application logic.
- L0045: `            FIELD_EMAIL,` - Implements part of the file's concrete application logic.
- L0046: `            FIELD_ENABLED,` - Implements part of the file's concrete application logic.
- L0047: `            DEFAULT_SORT_FIELD);` - Implements part of the file's concrete application logic.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `    private final UserRepository userRepository;` - Repository usage handles persistence access to database records.
- L0050: `    private final UserMapper userMapper;` - Implements part of the file's concrete application logic.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0053: `    public AdminDashboardDto getDashboard(String adminEmail) {` - Declares a method signature, contract, or constructor entry point.
- L0054: `        long totalUsers = userRepository.count();` - Repository usage handles persistence access to database records.
- L0055: `        long activeUsers = userRepository.countByEnabledTrue();` - Repository usage handles persistence access to database records.
- L0056: `` - Blank line used to separate logical blocks for readability.
- L0057: `        return new AdminDashboardDto(` - Returns data to caller after applying current method logic.
- L0058: `                "Welcome to Admin Dashboard!",` - Implements part of the file's concrete application logic.
- L0059: `                adminEmail,` - Implements part of the file's concrete application logic.
- L0060: `                totalUsers,` - Implements part of the file's concrete application logic.
- L0061: `                activeUsers,` - Implements part of the file's concrete application logic.
- L0062: `                LocalDateTime.now().toString());` - Implements part of the file's concrete application logic.
- L0063: `    }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0066: `    public Page<UserDto> getUsers(` - Declares a method signature, contract, or constructor entry point.
- L0067: `            int page,` - Implements part of the file's concrete application logic.
- L0068: `            int size,` - Implements part of the file's concrete application logic.
- L0069: `            String search,` - Implements part of the file's concrete application logic.
- L0070: `            Boolean enabled,` - Implements part of the file's concrete application logic.
- L0071: `            String role,` - Authorization rule, authority mapping, or role handling line.
- L0072: `            String sortBy,` - Implements part of the file's concrete application logic.
- L0073: `            String sortDir) {` - Opens a new scope block for type, method, or control flow.
- L0074: `        int normalizedPage = normalizePage(page);` - Implements part of the file's concrete application logic.
- L0075: `        int normalizedSize = normalizePageSize(size);` - Implements part of the file's concrete application logic.
- L0076: `` - Blank line used to separate logical blocks for readability.
- L0077: `        Sort.Direction direction = resolveSortDirection(sortDir);` - Implements part of the file's concrete application logic.
- L0078: `        String safeSortField = resolveSortField(sortBy);` - Implements part of the file's concrete application logic.
- L0079: `        Sort sort = Sort.by(direction, safeSortField);` - Implements part of the file's concrete application logic.
- L0080: `        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, sort);` - Implements part of the file's concrete application logic.
- L0081: `` - Blank line used to separate logical blocks for readability.
- L0082: `        Specification<User> specification = buildUserSpecification(search, enabled, role);` - Authorization rule, authority mapping, or role handling line.
- L0083: `        Page<User> usersPage = userRepository.findAll(specification, pageable);` - Repository usage handles persistence access to database records.
- L0084: `        return usersPage.map(userMapper::toDto);` - Returns data to caller after applying current method logic.
- L0085: `    }` - Closes the current scope block.
- L0086: `` - Blank line used to separate logical blocks for readability.
- L0087: `    /** Builds optional user filters for search/status/role queries. */` - JavaDoc/comment line documenting intent and behavior.
- L0088: `    private Specification<User> buildUserSpecification(String search, Boolean enabled, String role) {` - Declares a method signature, contract, or constructor entry point.
- L0089: `        Specification<User> specification = null;` - Implements part of the file's concrete application logic.
- L0090: `` - Blank line used to separate logical blocks for readability.
- L0091: `        if (hasText(search)) {` - Conditional branch enforcing a business rule or guard path.
- L0092: `            specification = andSpecification(specification, buildSearchSpecification(search));` - Implements part of the file's concrete application logic.
- L0093: `        }` - Closes the current scope block.
- L0094: `` - Blank line used to separate logical blocks for readability.
- L0095: `        if (enabled != null) {` - Conditional branch enforcing a business rule or guard path.
- L0096: `            specification = andSpecification(` - Implements part of the file's concrete application logic.
- L0097: `                    specification,` - Implements part of the file's concrete application logic.
- L0098: `                    (root, query, cb) -> cb.equal(root.get(FIELD_ENABLED), enabled));` - Implements part of the file's concrete application logic.
- L0099: `        }` - Closes the current scope block.
- L0100: `` - Blank line used to separate logical blocks for readability.
- L0101: `        if (hasText(role)) {` - Conditional branch enforcing a business rule or guard path.
- L0102: `            Role.RoleName roleName = normalizeRoleName(role);` - Authorization rule, authority mapping, or role handling line.
- L0103: `            specification = andSpecification(specification, (root, query, cb) -> {` - Opens a new scope block for type, method, or control flow.
- L0104: `                query.distinct(true);` - Implements part of the file's concrete application logic.
- L0105: `                return cb.equal(root.join(FIELD_ROLES).get(FIELD_ROLE_NAME), roleName);` - Returns data to caller after applying current method logic.
- L0106: `            });` - Implements part of the file's concrete application logic.
- L0107: `        }` - Closes the current scope block.
- L0108: `` - Blank line used to separate logical blocks for readability.
- L0109: `        return specification;` - Returns data to caller after applying current method logic.
- L0110: `    }` - Closes the current scope block.
- L0111: `` - Blank line used to separate logical blocks for readability.
- L0112: `    /** Accepts USER/ADMIN or ROLE_USER/ROLE_ADMIN query values. */` - JavaDoc/comment line documenting intent and behavior.
- L0113: `    private Role.RoleName normalizeRoleName(String rawRole) {` - Declares a method signature, contract, or constructor entry point.
- L0114: `        String trimmedRole = rawRole.trim().toUpperCase(Locale.ROOT);` - Authorization rule, authority mapping, or role handling line.
- L0115: `        if (!trimmedRole.startsWith(ROLE_PREFIX)) {` - Conditional branch enforcing a business rule or guard path.
- L0116: `            trimmedRole = ROLE_PREFIX + trimmedRole;` - Authorization rule, authority mapping, or role handling line.
- L0117: `        }` - Closes the current scope block.
- L0118: `` - Blank line used to separate logical blocks for readability.
- L0119: `        try {` - Exception handling block for controlled failure behavior.
- L0120: `            return Role.RoleName.valueOf(trimmedRole);` - Returns data to caller after applying current method logic.
- L0121: `        } catch (IllegalArgumentException exception) {` - Opens a new scope block for type, method, or control flow.
- L0122: `            throw new IllegalArgumentException(ERROR_INVALID_ROLE_FILTER);` - Raises an exception for invalid state or request path.
- L0123: `        }` - Closes the current scope block.
- L0124: `    }` - Closes the current scope block.
- L0125: `` - Blank line used to separate logical blocks for readability.
- L0126: `    /** Restricts client sort field to safe, indexed-ish columns. */` - JavaDoc/comment line documenting intent and behavior.
- L0127: `    private String resolveSortField(String rawSortBy) {` - Declares a method signature, contract, or constructor entry point.
- L0128: `        if (rawSortBy == null || rawSortBy.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0129: `            return DEFAULT_SORT_FIELD;` - Returns data to caller after applying current method logic.
- L0130: `        }` - Closes the current scope block.
- L0131: `` - Blank line used to separate logical blocks for readability.
- L0132: `        if (ALLOWED_SORT_FIELDS.contains(rawSortBy)) {` - Conditional branch enforcing a business rule or guard path.
- L0133: `            return rawSortBy;` - Returns data to caller after applying current method logic.
- L0134: `        }` - Closes the current scope block.
- L0135: `        return DEFAULT_SORT_FIELD;` - Returns data to caller after applying current method logic.
- L0136: `    }` - Closes the current scope block.
- L0137: `` - Blank line used to separate logical blocks for readability.
- L0138: `    private int normalizePage(int page) {` - Declares a method signature, contract, or constructor entry point.
- L0139: `        if (page < MIN_PAGE) {` - Conditional branch enforcing a business rule or guard path.
- L0140: `            return MIN_PAGE;` - Returns data to caller after applying current method logic.
- L0141: `        }` - Closes the current scope block.
- L0142: `        return page;` - Returns data to caller after applying current method logic.
- L0143: `    }` - Closes the current scope block.
- L0144: `` - Blank line used to separate logical blocks for readability.
- L0145: `    private int normalizePageSize(int size) {` - Declares a method signature, contract, or constructor entry point.
- L0146: `        int boundedSize = size;` - Implements part of the file's concrete application logic.
- L0147: `        if (boundedSize < MIN_PAGE_SIZE) {` - Conditional branch enforcing a business rule or guard path.
- L0148: `            boundedSize = MIN_PAGE_SIZE;` - Implements part of the file's concrete application logic.
- L0149: `        }` - Closes the current scope block.
- L0150: `        if (boundedSize > MAX_PAGE_SIZE) {` - Conditional branch enforcing a business rule or guard path.
- L0151: `            boundedSize = MAX_PAGE_SIZE;` - Implements part of the file's concrete application logic.
- L0152: `        }` - Closes the current scope block.
- L0153: `        return boundedSize;` - Returns data to caller after applying current method logic.
- L0154: `    }` - Closes the current scope block.
- L0155: `` - Blank line used to separate logical blocks for readability.
- L0156: `    private Sort.Direction resolveSortDirection(String sortDir) {` - Declares a method signature, contract, or constructor entry point.
- L0157: `        if (sortDir != null && sortDir.equalsIgnoreCase(SORT_DIRECTION_ASC)) {` - Conditional branch enforcing a business rule or guard path.
- L0158: `            return Sort.Direction.ASC;` - Returns data to caller after applying current method logic.
- L0159: `        }` - Closes the current scope block.
- L0160: `        return Sort.Direction.DESC;` - Returns data to caller after applying current method logic.
- L0161: `    }` - Closes the current scope block.
- L0162: `` - Blank line used to separate logical blocks for readability.
- L0163: `    private Specification<User> buildSearchSpecification(String search) {` - Declares a method signature, contract, or constructor entry point.
- L0164: `        String loweredSearch = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";` - Implements part of the file's concrete application logic.
- L0165: `        return (root, query, cb) -> cb.or(` - Returns data to caller after applying current method logic.
- L0166: `                cb.like(cb.lower(root.get(FIELD_USER_NAME)), loweredSearch),` - Implements part of the file's concrete application logic.
- L0167: `                cb.like(cb.lower(root.get(FIELD_EMAIL)), loweredSearch));` - Implements part of the file's concrete application logic.
- L0168: `    }` - Closes the current scope block.
- L0169: `` - Blank line used to separate logical blocks for readability.
- L0170: `    private boolean hasText(String value) {` - Declares a method signature, contract, or constructor entry point.
- L0171: `        if (value == null) {` - Conditional branch enforcing a business rule or guard path.
- L0172: `            return false;` - Returns data to caller after applying current method logic.
- L0173: `        }` - Closes the current scope block.
- L0174: `        return !value.isBlank();` - Returns data to caller after applying current method logic.
- L0175: `    }` - Closes the current scope block.
- L0176: `` - Blank line used to separate logical blocks for readability.
- L0177: `    private Specification<User> andSpecification(` - Declares a method signature, contract, or constructor entry point.
- L0178: `            Specification<User> baseSpecification,` - Implements part of the file's concrete application logic.
- L0179: `            Specification<User> clause) {` - Opens a new scope block for type, method, or control flow.
- L0180: `        if (baseSpecification == null) {` - Conditional branch enforcing a business rule or guard path.
- L0181: `            return clause;` - Returns data to caller after applying current method logic.
- L0182: `        }` - Closes the current scope block.
- L0183: `        return baseSpecification.and(clause);` - Returns data to caller after applying current method logic.
- L0184: `    }` - Closes the current scope block.
- L0185: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 51: `backend/src/main/java/com/auth/service/impl/AuthServiceImpl.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 378

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.exception.ResourceNotFoundException;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.exception.TokenValidationException;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.exception.UserAlreadyExistsException;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.dto.LoginRequest;` - Imports a type required by this file to compile and run.
- L0010: `import com.auth.dto.OtpVerifyRequest;` - Imports a type required by this file to compile and run.
- L0011: `import com.auth.dto.RegisterRequest;` - Imports a type required by this file to compile and run.
- L0012: `import com.auth.dto.ResetPasswordRequest;` - Imports a type required by this file to compile and run.
- L0013: `import com.auth.dto.UpdatePasswordRequest;` - Imports a type required by this file to compile and run.
- L0014: `import com.auth.service.auth.AuthAbuseProtectionService;` - Imports a type required by this file to compile and run.
- L0015: `import com.auth.service.AuthService;` - Imports a type required by this file to compile and run.
- L0016: `import com.auth.service.auth.AuthTokenService;` - Imports a type required by this file to compile and run.
- L0017: `import com.auth.service.support.EmailService;` - Imports a type required by this file to compile and run.
- L0018: `import com.auth.service.support.OtpService;` - Imports a type required by this file to compile and run.
- L0019: `import com.auth.service.support.PasswordPolicyService;` - Imports a type required by this file to compile and run.
- L0020: `import com.auth.service.support.TokenHashService;` - Imports a type required by this file to compile and run.
- L0021: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0022: `import com.auth.mapper.UserMapper;` - Imports a type required by this file to compile and run.
- L0023: `import com.auth.dto.ChangePasswordRequest;` - Imports a type required by this file to compile and run.
- L0024: `import com.auth.service.RoleService;` - Imports a type required by this file to compile and run.
- L0025: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0026: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0027: `import lombok.extern.slf4j.Slf4j;` - Imports a type required by this file to compile and run.
- L0028: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0029: `import org.springframework.security.authentication.AuthenticationManager;` - Imports a type required by this file to compile and run.
- L0030: `import org.springframework.security.authentication.BadCredentialsException;` - Imports a type required by this file to compile and run.
- L0031: `import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;` - Imports a type required by this file to compile and run.
- L0032: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0033: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0034: `import org.springframework.transaction.annotation.Transactional;` - Imports a type required by this file to compile and run.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0037: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0038: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0041: ` * Implementation of AuthService.` - JavaDoc/comment line documenting intent and behavior.
- L0042: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `@Slf4j` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `public class AuthServiceImpl implements AuthService {` - Defines the core type and responsibility boundary for this file.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `    private static final String ERROR_EMAIL_ALREADY_REGISTERED = "Email already registered!";` - Implements part of the file's concrete application logic.
- L0049: `    private static final String ERROR_EMAIL_ALREADY_VERIFIED = "Email already verified!";` - Implements part of the file's concrete application logic.
- L0050: `    private static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password!";` - Credential or recovery logic for authentication safety.
- L0051: `    private static final String ERROR_EMAIL_NOT_VERIFIED = "Please verify your email first!";` - Implements part of the file's concrete application logic.
- L0052: `    private static final String ERROR_INVALID_OTP = "Invalid OTP!";` - Credential or recovery logic for authentication safety.
- L0053: `    private static final String ERROR_EXPIRED_OTP = "OTP has expired! Please request a new one.";` - Credential or recovery logic for authentication safety.
- L0054: `    private static final String ERROR_INVALID_RESET_TOKEN = "Invalid or expired reset token!";` - Security-related logic for tokens, OAuth, or authentication state.
- L0055: `    private static final String ERROR_EXPIRED_RESET_TOKEN = "Reset token has expired! Please request a new one.";` - Security-related logic for tokens, OAuth, or authentication state.
- L0056: `    private static final String ERROR_INCORRECT_CURRENT_PASSWORD = "Incorrect current password!";` - Credential or recovery logic for authentication safety.
- L0057: `    private static final String MESSAGE_REGISTER_SUCCESS =` - Implements part of the file's concrete application logic.
- L0058: `            "Registration successful! Please check your email for OTP verification.";` - Credential or recovery logic for authentication safety.
- L0059: `    private static final String MESSAGE_VERIFY_OTP_SUCCESS = "Email verified successfully! You can now login.";` - Credential or recovery logic for authentication safety.
- L0060: `    private static final String MESSAGE_RESET_PASSWORD_GENERIC =` - Credential or recovery logic for authentication safety.
- L0061: `            "If an account exists with this email, a reset link will be sent.";` - Credential or recovery logic for authentication safety.
- L0062: `    private static final String MESSAGE_UPDATE_PASSWORD_SUCCESS = "Password updated successfully! You can now login.";` - Credential or recovery logic for authentication safety.
- L0063: `    private static final String MESSAGE_RESEND_OTP_SUCCESS = "OTP sent successfully! Please check your email.";` - Credential or recovery logic for authentication safety.
- L0064: `    private static final String MESSAGE_CHANGE_PASSWORD_SUCCESS = "Password changed successfully!";` - Credential or recovery logic for authentication safety.
- L0065: `    private static final String CONTEXT_REGISTRATION = "registration";` - Implements part of the file's concrete application logic.
- L0066: `    private static final String CONTEXT_RESEND = "resend";` - Implements part of the file's concrete application logic.
- L0067: `` - Blank line used to separate logical blocks for readability.
- L0068: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `    private final RoleService roleService;` - Service interaction applies business logic or orchestration.
- L0071: `` - Blank line used to separate logical blocks for readability.
- L0072: `    private final PasswordEncoder passwordEncoder;` - Credential or recovery logic for authentication safety.
- L0073: `` - Blank line used to separate logical blocks for readability.
- L0074: `    private final AuthenticationManager authenticationManager;` - Implements part of the file's concrete application logic.
- L0075: `` - Blank line used to separate logical blocks for readability.
- L0076: `    private final AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0077: `` - Blank line used to separate logical blocks for readability.
- L0078: `    private final EmailService emailService;` - Service interaction applies business logic or orchestration.
- L0079: `` - Blank line used to separate logical blocks for readability.
- L0080: `    private final OtpService otpService;` - Service interaction applies business logic or orchestration.
- L0081: `` - Blank line used to separate logical blocks for readability.
- L0082: `    private final UserMapper userMapper;` - Implements part of the file's concrete application logic.
- L0083: `` - Blank line used to separate logical blocks for readability.
- L0084: `    private final TokenHashService tokenHashService;` - Service interaction applies business logic or orchestration.
- L0085: `` - Blank line used to separate logical blocks for readability.
- L0086: `    private final PasswordPolicyService passwordPolicyService;` - Service interaction applies business logic or orchestration.
- L0087: `` - Blank line used to separate logical blocks for readability.
- L0088: `    private final AuthAbuseProtectionService authAbuseProtectionService;` - Service interaction applies business logic or orchestration.
- L0089: `` - Blank line used to separate logical blocks for readability.
- L0090: `    @Value("${otp.expiration.minutes:5}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0091: `    private int otpExpirationMinutes;` - Credential or recovery logic for authentication safety.
- L0092: `` - Blank line used to separate logical blocks for readability.
- L0093: `    @Value("${auth.reset-token.expiration.minutes:30}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0094: `    private int resetTokenExpirationMinutes;` - Security-related logic for tokens, OAuth, or authentication state.
- L0095: `` - Blank line used to separate logical blocks for readability.
- L0096: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0097: `     * Registers a new user with the provided details.` - JavaDoc/comment line documenting intent and behavior.
- L0098: `     * Checks for existing email, encodes password, generates OTP, and sends` - JavaDoc/comment line documenting intent and behavior.
- L0099: `     * verification email.` - JavaDoc/comment line documenting intent and behavior.
- L0100: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0101: `     * @param request The registration request containing user details.` - JavaDoc/comment line documenting intent and behavior.
- L0102: `     * @return MessageResponse indicating success status and message.` - JavaDoc/comment line documenting intent and behavior.
- L0103: `     * @throws UserAlreadyExistsException if the email is already registered.` - JavaDoc/comment line documenting intent and behavior.
- L0104: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0105: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0106: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0107: `    public MessageResponse register(RegisterRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0108: `        if (userService.existsByEmail(request.getEmail())) {` - Conditional branch enforcing a business rule or guard path.
- L0109: `            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_REGISTERED);` - Raises an exception for invalid state or request path.
- L0110: `        }` - Closes the current scope block.
- L0111: `` - Blank line used to separate logical blocks for readability.
- L0112: `        passwordPolicyService.validate(request.getPassword(), request.getEmail());` - Service interaction applies business logic or orchestration.
- L0113: `` - Blank line used to separate logical blocks for readability.
- L0114: `        User user = userMapper.toEntity(request);` - Implements part of the file's concrete application logic.
- L0115: `        user.setPassword(passwordEncoder.encode(request.getPassword()));` - Credential or recovery logic for authentication safety.
- L0116: `` - Blank line used to separate logical blocks for readability.
- L0117: `        String otp = assignVerificationOtp(user);` - Credential or recovery logic for authentication safety.
- L0118: `        assignDefaultUserRole(user);` - Authorization rule, authority mapping, or role handling line.
- L0119: `` - Blank line used to separate logical blocks for readability.
- L0120: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0121: `        sendOtpEmailSafely(user, otp, CONTEXT_REGISTRATION);` - Credential or recovery logic for authentication safety.
- L0122: `` - Blank line used to separate logical blocks for readability.
- L0123: `        return new MessageResponse(MESSAGE_REGISTER_SUCCESS, true);` - Returns data to caller after applying current method logic.
- L0124: `    }` - Closes the current scope block.
- L0125: `` - Blank line used to separate logical blocks for readability.
- L0126: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0127: `     * Verifies the email address using the provided OTP.` - JavaDoc/comment line documenting intent and behavior.
- L0128: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0129: `     * @param request The request containing email and OTP.` - JavaDoc/comment line documenting intent and behavior.
- L0130: `     * @return MessageResponse indicating success.` - JavaDoc/comment line documenting intent and behavior.
- L0131: `     * @throws ResourceNotFoundException  if user not found.` - JavaDoc/comment line documenting intent and behavior.
- L0132: `     * @throws UserAlreadyExistsException if email is already verified.` - JavaDoc/comment line documenting intent and behavior.
- L0133: `     * @throws TokenValidationException   if OTP is invalid or expired.` - JavaDoc/comment line documenting intent and behavior.
- L0134: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0135: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0136: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0137: `    public MessageResponse verifyOtp(OtpVerifyRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0138: `        authAbuseProtectionService.guardOtpVerification(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0139: `` - Blank line used to separate logical blocks for readability.
- L0140: `        User user = requireUserByEmail(request.getEmail());` - Implements part of the file's concrete application logic.
- L0141: `` - Blank line used to separate logical blocks for readability.
- L0142: `        if (user.isEnabled()) {` - Conditional branch enforcing a business rule or guard path.
- L0143: `            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_VERIFIED);` - Raises an exception for invalid state or request path.
- L0144: `        }` - Closes the current scope block.
- L0145: `` - Blank line used to separate logical blocks for readability.
- L0146: `        if (!tokenHashService.matches(request.getOtp(), user.getVerificationOtp())) {` - Conditional branch enforcing a business rule or guard path.
- L0147: `            authAbuseProtectionService.recordFailedOtp(user);` - Service interaction applies business logic or orchestration.
- L0148: `            throw new TokenValidationException(ERROR_INVALID_OTP);` - Raises an exception for invalid state or request path.
- L0149: `        }` - Closes the current scope block.
- L0150: `` - Blank line used to separate logical blocks for readability.
- L0151: `        if (isExpired(user.getOtpExpiry())) {` - Conditional branch enforcing a business rule or guard path.
- L0152: `            throw new TokenValidationException(ERROR_EXPIRED_OTP);` - Raises an exception for invalid state or request path.
- L0153: `        }` - Closes the current scope block.
- L0154: `` - Blank line used to separate logical blocks for readability.
- L0155: `        user.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0156: `        user.setVerificationOtp(null);` - Credential or recovery logic for authentication safety.
- L0157: `        user.setOtpExpiry(null);` - Credential or recovery logic for authentication safety.
- L0158: `        authAbuseProtectionService.clearOtpFailures(user);` - Service interaction applies business logic or orchestration.
- L0159: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0160: `` - Blank line used to separate logical blocks for readability.
- L0161: `        return new MessageResponse(MESSAGE_VERIFY_OTP_SUCCESS, true);` - Returns data to caller after applying current method logic.
- L0162: `    }` - Closes the current scope block.
- L0163: `` - Blank line used to separate logical blocks for readability.
- L0164: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0165: `     * Authenticates a user and generates a JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0166: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0167: `     * @param request The login request containing email and password.` - JavaDoc/comment line documenting intent and behavior.
- L0168: `     * @return AuthResponse containing user details and JWT token.` - JavaDoc/comment line documenting intent and behavior.
- L0169: `     * @throws org.springframework.security.authentication.BadCredentialsException if` - JavaDoc/comment line documenting intent and behavior.
- L0170: `     *                                                                             credentials` - JavaDoc/comment line documenting intent and behavior.
- L0171: `     *                                                                             are` - JavaDoc/comment line documenting intent and behavior.
- L0172: `     *                                                                             invalid.` - JavaDoc/comment line documenting intent and behavior.
- L0173: `     * @throws TokenValidationException                                            if` - JavaDoc/comment line documenting intent and behavior.
- L0174: `     *                                                                             email` - JavaDoc/comment line documenting intent and behavior.
- L0175: `     *                                                                             is` - JavaDoc/comment line documenting intent and behavior.
- L0176: `     *                                                                             not` - JavaDoc/comment line documenting intent and behavior.
- L0177: `     *                                                                             verified.` - JavaDoc/comment line documenting intent and behavior.
- L0178: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0179: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0180: `    public AuthTokens login(LoginRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0181: `        authAbuseProtectionService.guardLoginAttempt(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0182: `` - Blank line used to separate logical blocks for readability.
- L0183: `        Optional<User> user = userService.findByEmail(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0184: `        if (user.isEmpty()) {` - Conditional branch enforcing a business rule or guard path.
- L0185: `            authAbuseProtectionService.recordFailedLogin(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0186: `            throw new BadCredentialsException(ERROR_INVALID_CREDENTIALS);` - Raises an exception for invalid state or request path.
- L0187: `        }` - Closes the current scope block.
- L0188: `        User existingUser = user.get();` - Implements part of the file's concrete application logic.
- L0189: `` - Blank line used to separate logical blocks for readability.
- L0190: `        if (!existingUser.isEnabled()) {` - Conditional branch enforcing a business rule or guard path.
- L0191: `            throw new TokenValidationException(ERROR_EMAIL_NOT_VERIFIED);` - Raises an exception for invalid state or request path.
- L0192: `        }` - Closes the current scope block.
- L0193: `` - Blank line used to separate logical blocks for readability.
- L0194: `        authenticateLoginCredentials(request);` - Implements part of the file's concrete application logic.
- L0195: `` - Blank line used to separate logical blocks for readability.
- L0196: `        authAbuseProtectionService.clearLoginFailures(existingUser);` - Service interaction applies business logic or orchestration.
- L0197: `` - Blank line used to separate logical blocks for readability.
- L0198: `        return authTokenService.issueTokens(existingUser);` - Returns data to caller after applying current method logic.
- L0199: `    }` - Closes the current scope block.
- L0200: `` - Blank line used to separate logical blocks for readability.
- L0201: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0202: `     * Initiates the password reset process by generating a token and sending an` - JavaDoc/comment line documenting intent and behavior.
- L0203: `     * email.` - JavaDoc/comment line documenting intent and behavior.
- L0204: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0205: `     * @param request The request containing the user's email.` - JavaDoc/comment line documenting intent and behavior.
- L0206: `     * @return MessageResponse indicating the email was sent (generic message for` - JavaDoc/comment line documenting intent and behavior.
- L0207: `     *         security).` - JavaDoc/comment line documenting intent and behavior.
- L0208: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0209: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0210: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0211: `    public MessageResponse resetPassword(ResetPasswordRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0212: `        authAbuseProtectionService.guardResetPassword(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0213: `` - Blank line used to separate logical blocks for readability.
- L0214: `        Optional<User> userOpt = userService.findByEmail(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0215: `        if (userOpt.isEmpty()) {` - Conditional branch enforcing a business rule or guard path.
- L0216: `            return new MessageResponse(MESSAGE_RESET_PASSWORD_GENERIC, true);` - Returns data to caller after applying current method logic.
- L0217: `        }` - Closes the current scope block.
- L0218: `        User user = userOpt.get();` - Implements part of the file's concrete application logic.
- L0219: `` - Blank line used to separate logical blocks for readability.
- L0220: `        String resetToken = assignResetToken(user);` - Security-related logic for tokens, OAuth, or authentication state.
- L0221: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0222: `` - Blank line used to separate logical blocks for readability.
- L0223: `        sendResetEmailSafely(user, resetToken);` - Security-related logic for tokens, OAuth, or authentication state.
- L0224: `` - Blank line used to separate logical blocks for readability.
- L0225: `        return new MessageResponse(MESSAGE_RESET_PASSWORD_GENERIC, true);` - Returns data to caller after applying current method logic.
- L0226: `    }` - Closes the current scope block.
- L0227: `` - Blank line used to separate logical blocks for readability.
- L0228: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0229: `     * Updates the user's password using a valid reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0230: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0231: `     * @param request The request containing the reset token and new password.` - JavaDoc/comment line documenting intent and behavior.
- L0232: `     * @return MessageResponse indicating success.` - JavaDoc/comment line documenting intent and behavior.
- L0233: `     * @throws TokenValidationException if token is invalid or expired.` - JavaDoc/comment line documenting intent and behavior.
- L0234: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0235: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0236: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0237: `    public MessageResponse updatePassword(UpdatePasswordRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0238: `        String resetTokenHash = tokenHashService.hash(request.getToken());` - Service interaction applies business logic or orchestration.
- L0239: `        User user = userService.findByResetToken(resetTokenHash)` - Service interaction applies business logic or orchestration.
- L0240: `                .orElseThrow(() -> new TokenValidationException(ERROR_INVALID_RESET_TOKEN));` - Security-related logic for tokens, OAuth, or authentication state.
- L0241: `` - Blank line used to separate logical blocks for readability.
- L0242: `        if (isExpired(user.getResetTokenExpiry())) {` - Conditional branch enforcing a business rule or guard path.
- L0243: `            throw new TokenValidationException(ERROR_EXPIRED_RESET_TOKEN);` - Raises an exception for invalid state or request path.
- L0244: `        }` - Closes the current scope block.
- L0245: `` - Blank line used to separate logical blocks for readability.
- L0246: `        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());` - Service interaction applies business logic or orchestration.
- L0247: `` - Blank line used to separate logical blocks for readability.
- L0248: `        user.setPassword(passwordEncoder.encode(request.getNewPassword()));` - Credential or recovery logic for authentication safety.
- L0249: `        clearResetToken(user);` - Security-related logic for tokens, OAuth, or authentication state.
- L0250: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0251: `` - Blank line used to separate logical blocks for readability.
- L0252: `        return new MessageResponse(MESSAGE_UPDATE_PASSWORD_SUCCESS, true);` - Returns data to caller after applying current method logic.
- L0253: `    }` - Closes the current scope block.
- L0254: `` - Blank line used to separate logical blocks for readability.
- L0255: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0256: `     * Resends the OTP for email verification.` - JavaDoc/comment line documenting intent and behavior.
- L0257: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0258: `     * @param email The email address to resend OTP to.` - JavaDoc/comment line documenting intent and behavior.
- L0259: `     * @return MessageResponse indicating success.` - JavaDoc/comment line documenting intent and behavior.
- L0260: `     * @throws ResourceNotFoundException  if user not found.` - JavaDoc/comment line documenting intent and behavior.
- L0261: `     * @throws UserAlreadyExistsException if email is already verified.` - JavaDoc/comment line documenting intent and behavior.
- L0262: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0263: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0264: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0265: `    public MessageResponse resendOtp(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0266: `        authAbuseProtectionService.guardResendOtp(email);` - Service interaction applies business logic or orchestration.
- L0267: `` - Blank line used to separate logical blocks for readability.
- L0268: `        User user = requireUserByEmail(email);` - Implements part of the file's concrete application logic.
- L0269: `` - Blank line used to separate logical blocks for readability.
- L0270: `        if (user.isEnabled()) {` - Conditional branch enforcing a business rule or guard path.
- L0271: `            throw new UserAlreadyExistsException(ERROR_EMAIL_ALREADY_VERIFIED);` - Raises an exception for invalid state or request path.
- L0272: `        }` - Closes the current scope block.
- L0273: `` - Blank line used to separate logical blocks for readability.
- L0274: `        String otp = assignVerificationOtp(user);` - Credential or recovery logic for authentication safety.
- L0275: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0276: `        sendOtpEmailSafely(user, otp, CONTEXT_RESEND);` - Credential or recovery logic for authentication safety.
- L0277: `` - Blank line used to separate logical blocks for readability.
- L0278: `        return new MessageResponse(MESSAGE_RESEND_OTP_SUCCESS, true);` - Returns data to caller after applying current method logic.
- L0279: `    }` - Closes the current scope block.
- L0280: `` - Blank line used to separate logical blocks for readability.
- L0281: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0282: `     * Changes the authenticated user's password.` - JavaDoc/comment line documenting intent and behavior.
- L0283: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0284: `     * @param email   The email of the authenticated user.` - JavaDoc/comment line documenting intent and behavior.
- L0285: `     * @param request The request containing current and new password.` - JavaDoc/comment line documenting intent and behavior.
- L0286: `     * @return MessageResponse indicating success.` - JavaDoc/comment line documenting intent and behavior.
- L0287: `     * @throws ResourceNotFoundException                                           if` - JavaDoc/comment line documenting intent and behavior.
- L0288: `     *                                                                             user` - JavaDoc/comment line documenting intent and behavior.
- L0289: `     *                                                                             not` - JavaDoc/comment line documenting intent and behavior.
- L0290: `     *                                                                             found.` - JavaDoc/comment line documenting intent and behavior.
- L0291: `     * @throws org.springframework.security.authentication.BadCredentialsException if` - JavaDoc/comment line documenting intent and behavior.
- L0292: `     *                                                                             current` - JavaDoc/comment line documenting intent and behavior.
- L0293: `     *                                                                             password` - JavaDoc/comment line documenting intent and behavior.
- L0294: `     *                                                                             is` - JavaDoc/comment line documenting intent and behavior.
- L0295: `     *                                                                             incorrect.` - JavaDoc/comment line documenting intent and behavior.
- L0296: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0297: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0298: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0299: `    public MessageResponse changePassword(String email, ChangePasswordRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0300: `        User user = requireUserByEmail(email);` - Implements part of the file's concrete application logic.
- L0301: `` - Blank line used to separate logical blocks for readability.
- L0302: `        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {` - Conditional branch enforcing a business rule or guard path.
- L0303: `            throw new BadCredentialsException(ERROR_INCORRECT_CURRENT_PASSWORD);` - Raises an exception for invalid state or request path.
- L0304: `        }` - Closes the current scope block.
- L0305: `` - Blank line used to separate logical blocks for readability.
- L0306: `        passwordPolicyService.validate(request.getNewPassword(), user.getEmail());` - Service interaction applies business logic or orchestration.
- L0307: `` - Blank line used to separate logical blocks for readability.
- L0308: `        user.setPassword(passwordEncoder.encode(request.getNewPassword()));` - Credential or recovery logic for authentication safety.
- L0309: `        userService.save(user);` - Service interaction applies business logic or orchestration.
- L0310: `` - Blank line used to separate logical blocks for readability.
- L0311: `        return new MessageResponse(MESSAGE_CHANGE_PASSWORD_SUCCESS, true);` - Returns data to caller after applying current method logic.
- L0312: `    }` - Closes the current scope block.
- L0313: `` - Blank line used to separate logical blocks for readability.
- L0314: `    private User requireUserByEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0315: `        return userService.findByEmail(email)` - Returns data to caller after applying current method logic.
- L0316: `                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));` - Implements part of the file's concrete application logic.
- L0317: `    }` - Closes the current scope block.
- L0318: `` - Blank line used to separate logical blocks for readability.
- L0319: `    private String assignVerificationOtp(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0320: `        String otp = otpService.generateOtp();` - Service interaction applies business logic or orchestration.
- L0321: `        String otpHash = tokenHashService.hash(otp);` - Service interaction applies business logic or orchestration.
- L0322: `        LocalDateTime expiry = LocalDateTime.now().plusMinutes(otpExpirationMinutes);` - Credential or recovery logic for authentication safety.
- L0323: `` - Blank line used to separate logical blocks for readability.
- L0324: `        user.setVerificationOtp(otpHash);` - Credential or recovery logic for authentication safety.
- L0325: `        user.setOtpExpiry(expiry);` - Credential or recovery logic for authentication safety.
- L0326: `        return otp;` - Returns data to caller after applying current method logic.
- L0327: `    }` - Closes the current scope block.
- L0328: `` - Blank line used to separate logical blocks for readability.
- L0329: `    private void assignDefaultUserRole(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0330: `        Role userRole = roleService.findOrCreateRole(Role.RoleName.ROLE_USER);` - Service interaction applies business logic or orchestration.
- L0331: `        user.setRoles(Set.of(userRole));` - Authorization rule, authority mapping, or role handling line.
- L0332: `    }` - Closes the current scope block.
- L0333: `` - Blank line used to separate logical blocks for readability.
- L0334: `    private String assignResetToken(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0335: `        String resetToken = otpService.generateResetToken();` - Service interaction applies business logic or orchestration.
- L0336: `        String resetTokenHash = tokenHashService.hash(resetToken);` - Service interaction applies business logic or orchestration.
- L0337: `        LocalDateTime expiry = LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes);` - Security-related logic for tokens, OAuth, or authentication state.
- L0338: `` - Blank line used to separate logical blocks for readability.
- L0339: `        user.setResetToken(resetTokenHash);` - Security-related logic for tokens, OAuth, or authentication state.
- L0340: `        user.setResetTokenExpiry(expiry);` - Security-related logic for tokens, OAuth, or authentication state.
- L0341: `        return resetToken;` - Returns data to caller after applying current method logic.
- L0342: `    }` - Closes the current scope block.
- L0343: `` - Blank line used to separate logical blocks for readability.
- L0344: `    private boolean isExpired(LocalDateTime expiry) {` - Declares a method signature, contract, or constructor entry point.
- L0345: `        return expiry == null || expiry.isBefore(LocalDateTime.now());` - Returns data to caller after applying current method logic.
- L0346: `    }` - Closes the current scope block.
- L0347: `` - Blank line used to separate logical blocks for readability.
- L0348: `    private void sendOtpEmailSafely(User user, String otp, String context) {` - Declares a method signature, contract, or constructor entry point.
- L0349: `        try {` - Exception handling block for controlled failure behavior.
- L0350: `            emailService.sendOtpEmail(user.getEmail(), otp);` - Service interaction applies business logic or orchestration.
- L0351: `        } catch (RuntimeException exception) {` - Opens a new scope block for type, method, or control flow.
- L0352: `            log.warn("Failed to send OTP email during {} for {}", context, user.getEmail(), exception);` - Credential or recovery logic for authentication safety.
- L0353: `        }` - Closes the current scope block.
- L0354: `    }` - Closes the current scope block.
- L0355: `` - Blank line used to separate logical blocks for readability.
- L0356: `    private void sendResetEmailSafely(User user, String resetToken) {` - Declares a method signature, contract, or constructor entry point.
- L0357: `        try {` - Exception handling block for controlled failure behavior.
- L0358: `            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);` - Service interaction applies business logic or orchestration.
- L0359: `        } catch (RuntimeException exception) {` - Opens a new scope block for type, method, or control flow.
- L0360: `            log.warn("Failed to send password reset email for {}", user.getEmail(), exception);` - Credential or recovery logic for authentication safety.
- L0361: `        }` - Closes the current scope block.
- L0362: `    }` - Closes the current scope block.
- L0363: `` - Blank line used to separate logical blocks for readability.
- L0364: `    private void authenticateLoginCredentials(LoginRequest request) {` - Declares a method signature, contract, or constructor entry point.
- L0365: `        try {` - Exception handling block for controlled failure behavior.
- L0366: `            authenticationManager.authenticate(` - Implements part of the file's concrete application logic.
- L0367: `                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));` - Security-related logic for tokens, OAuth, or authentication state.
- L0368: `        } catch (BadCredentialsException exception) {` - Opens a new scope block for type, method, or control flow.
- L0369: `            authAbuseProtectionService.recordFailedLogin(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0370: `            throw exception;` - Raises an exception for invalid state or request path.
- L0371: `        }` - Closes the current scope block.
- L0372: `    }` - Closes the current scope block.
- L0373: `` - Blank line used to separate logical blocks for readability.
- L0374: `    private void clearResetToken(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0375: `        user.setResetToken(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0376: `        user.setResetTokenExpiry(null);` - Security-related logic for tokens, OAuth, or authentication state.
- L0377: `    }` - Closes the current scope block.
- L0378: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 52: `backend/src/main/java/com/auth/service/impl/RoleServiceImpl.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 40

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.repository.RoleRepository;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.RoleService;` - Imports a type required by this file to compile and run.
- L0006: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0007: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.transaction.annotation.Transactional;` - Imports a type required by this file to compile and run.
- L0009: `` - Blank line used to separate logical blocks for readability.
- L0010: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` * Implementation of RoleService.` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0015: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0016: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `public class RoleServiceImpl implements RoleService {` - Defines the core type and responsibility boundary for this file.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    private final RoleRepository roleRepository;` - Repository usage handles persistence access to database records.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    /** Returns an existing role or persists a new one when missing. */` - JavaDoc/comment line documenting intent and behavior.
- L0022: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `    public Role findOrCreateRole(Role.RoleName roleName) {` - Declares a method signature, contract, or constructor entry point.
- L0025: `        return roleRepository.findByName(roleName)` - Returns data to caller after applying current method logic.
- L0026: `                .orElseGet(() -> createRole(roleName));` - Authorization rule, authority mapping, or role handling line.
- L0027: `    }` - Closes the current scope block.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    /** Returns a role lookup result for the provided enum role name. */` - JavaDoc/comment line documenting intent and behavior.
- L0030: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `    public Optional<Role> findByName(Role.RoleName roleName) {` - Declares a method signature, contract, or constructor entry point.
- L0032: `        return roleRepository.findByName(roleName);` - Returns data to caller after applying current method logic.
- L0033: `    }` - Closes the current scope block.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `    private Role createRole(Role.RoleName roleName) {` - Declares a method signature, contract, or constructor entry point.
- L0036: `        Role role = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0037: `        role.setName(roleName);` - Authorization rule, authority mapping, or role handling line.
- L0038: `        return roleRepository.save(role);` - Returns data to caller after applying current method logic.
- L0039: `    }` - Closes the current scope block.
- L0040: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 53: `backend/src/main/java/com/auth/service/impl/UserPortalServiceImpl.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 40

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.UserDashboardDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.mapper.UserMapper;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.service.UserPortalService;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0009: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0010: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0015: ` * User-facing dashboard/profile composition logic.` - JavaDoc/comment line documenting intent and behavior.
- L0016: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0017: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `public class UserPortalServiceImpl implements UserPortalService {` - Defines the core type and responsibility boundary for this file.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    private static final String DASHBOARD_WELCOME_MESSAGE = "Welcome to User Dashboard!";` - Implements part of the file's concrete application logic.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    private final UserService userService;` - Service interaction applies business logic or orchestration.
- L0024: `    private final UserMapper userMapper;` - Implements part of the file's concrete application logic.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0027: `    public UserDashboardDto getDashboard(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0028: `        User user = userService.getUserByEmail(email);` - Service interaction applies business logic or orchestration.
- L0029: `        UserDashboardDto response = userMapper.toUserDashboardDto(user);` - Implements part of the file's concrete application logic.
- L0030: `        response.setMessage(DASHBOARD_WELCOME_MESSAGE);` - Implements part of the file's concrete application logic.
- L0031: `        response.setTimestamp(LocalDateTime.now().toString());` - Implements part of the file's concrete application logic.
- L0032: `        return response;` - Returns data to caller after applying current method logic.
- L0033: `    }` - Closes the current scope block.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0036: `    public UserDto getProfile(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0037: `        User user = userService.getUserByEmail(email);` - Service interaction applies business logic or orchestration.
- L0038: `        return userMapper.toDto(user);` - Returns data to caller after applying current method logic.
- L0039: `    }` - Closes the current scope block.
- L0040: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 54: `backend/src/main/java/com/auth/service/impl/UserServiceImpl.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 81

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.exception.ResourceNotFoundException;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.repository.UserRepository;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0008: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0009: `import org.springframework.transaction.annotation.Transactional;` - Imports a type required by this file to compile and run.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0012: `` - Blank line used to separate logical blocks for readability.
- L0013: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` * Implementation of UserService.` - JavaDoc/comment line documenting intent and behavior.
- L0015: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0016: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `public class UserServiceImpl implements UserService {` - Defines the core type and responsibility boundary for this file.
- L0019: `` - Blank line used to separate logical blocks for readability.
- L0020: `    private final UserRepository userRepository;` - Repository usage handles persistence access to database records.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `    /** Retrieves a user by email or throws a domain-level not-found exception. */` - JavaDoc/comment line documenting intent and behavior.
- L0023: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0024: `    public User getUserByEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0025: `        return userRepository.findByEmail(email)` - Returns data to caller after applying current method logic.
- L0026: `                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));` - Implements part of the file's concrete application logic.
- L0027: `    }` - Closes the current scope block.
- L0028: `` - Blank line used to separate logical blocks for readability.
- L0029: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0030: `     * Finds a user by email, returning an Optional.` - JavaDoc/comment line documenting intent and behavior.
- L0031: `     * Useful for checks where the user might not exist.` - JavaDoc/comment line documenting intent and behavior.
- L0032: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0033: `     * @param email The email to search for.` - JavaDoc/comment line documenting intent and behavior.
- L0034: `     * @return Optional containing the User if found, or empty.` - JavaDoc/comment line documenting intent and behavior.
- L0035: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0036: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0037: `    public Optional<User> findByEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0038: `        return userRepository.findByEmail(email);` - Returns data to caller after applying current method logic.
- L0039: `    }` - Closes the current scope block.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0042: `     * Checks if a user exists with the given email.` - JavaDoc/comment line documenting intent and behavior.
- L0043: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0044: `     * @param email The email to check.` - JavaDoc/comment line documenting intent and behavior.
- L0045: `     * @return true if a user exists, false otherwise.` - JavaDoc/comment line documenting intent and behavior.
- L0046: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0047: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0048: `    public boolean existsByEmail(String email) {` - Declares a method signature, contract, or constructor entry point.
- L0049: `        return userRepository.existsByEmail(email);` - Returns data to caller after applying current method logic.
- L0050: `    }` - Closes the current scope block.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0053: `     * Saves a user entity to the database.` - JavaDoc/comment line documenting intent and behavior.
- L0054: `     * Handles both create and update operations.` - JavaDoc/comment line documenting intent and behavior.
- L0055: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0056: `     * @param user The user entity to save.` - JavaDoc/comment line documenting intent and behavior.
- L0057: `     * @return The saved user entity.` - JavaDoc/comment line documenting intent and behavior.
- L0058: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0059: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0060: `    @Transactional` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0061: `    public User save(User user) {` - Declares a method signature, contract, or constructor entry point.
- L0062: `        return userRepository.save(user);` - Returns data to caller after applying current method logic.
- L0063: `    }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0066: `     * Finds a user associated with a password reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0067: `     *` - JavaDoc/comment line documenting intent and behavior.
- L0068: `     * @param token The reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0069: `     * @return Optional containing the User if found, or empty.` - JavaDoc/comment line documenting intent and behavior.
- L0070: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0071: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0072: `    public Optional<User> findByResetToken(String token) {` - Declares a method signature, contract, or constructor entry point.
- L0073: `        return userRepository.findByResetToken(token);` - Returns data to caller after applying current method logic.
- L0074: `    }` - Closes the current scope block.
- L0075: `` - Blank line used to separate logical blocks for readability.
- L0076: `    /** Finds a user associated with a persisted refresh token. */` - JavaDoc/comment line documenting intent and behavior.
- L0077: `    @Override` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0078: `    public Optional<User> findByRefreshToken(String token) {` - Declares a method signature, contract, or constructor entry point.
- L0079: `        return userRepository.findByRefreshToken(token);` - Returns data to caller after applying current method logic.
- L0080: `    }` - Closes the current scope block.
- L0081: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 55: `backend/src/main/java/com/auth/service/support/EmailService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 67

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.support;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.mail.SimpleMailMessage;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.mail.javamail.JavaMailSender;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0007: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0008: `` - Blank line used to separate logical blocks for readability.
- L0009: `import java.net.URLEncoder;` - Imports a type required by this file to compile and run.
- L0010: `import java.nio.charset.StandardCharsets;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` * Service for sending emails (OTP verification, password reset).` - JavaDoc/comment line documenting intent and behavior.
- L0014: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0015: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0016: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `public class EmailService {` - Defines the core type and responsibility boundary for this file.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    private static final String OTP_SUBJECT = "Email Verification OTP";` - Credential or recovery logic for authentication safety.
- L0020: `    private static final String PASSWORD_RESET_SUBJECT = "Password Reset Request";` - Credential or recovery logic for authentication safety.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `    private final JavaMailSender mailSender;` - Implements part of the file's concrete application logic.
- L0023: `` - Blank line used to separate logical blocks for readability.
- L0024: `    @Value("${spring.mail.username}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    private String fromEmail;` - Implements part of the file's concrete application logic.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    @Value("${otp.expiration.minutes:5}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `    private int otpExpirationMinutes;` - Credential or recovery logic for authentication safety.
- L0029: `` - Blank line used to separate logical blocks for readability.
- L0030: `    @Value("${app.frontend-reset-password-url:http://localhost:5173/reset-password}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0031: `    private String resetPasswordUrl;` - Credential or recovery logic for authentication safety.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `    @Value("${auth.reset-token.expiration.minutes:30}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `    private int resetTokenExpirationMinutes;` - Security-related logic for tokens, OAuth, or authentication state.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0037: `     * Send OTP verification email.` - JavaDoc/comment line documenting intent and behavior.
- L0038: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0039: `    public void sendOtpEmail(String toEmail, String otp) {` - Declares a method signature, contract, or constructor entry point.
- L0040: `        SimpleMailMessage message = new SimpleMailMessage();` - Implements part of the file's concrete application logic.
- L0041: `        message.setFrom(fromEmail);` - Implements part of the file's concrete application logic.
- L0042: `        message.setTo(toEmail);` - Implements part of the file's concrete application logic.
- L0043: `        message.setSubject(OTP_SUBJECT);` - Credential or recovery logic for authentication safety.
- L0044: `        message.setText("Your OTP for email verification is: " + otp +` - Credential or recovery logic for authentication safety.
- L0045: `                "\n\nThis OTP will expire in " + otpExpirationMinutes + " minutes." +` - Credential or recovery logic for authentication safety.
- L0046: `                "\n\nIf you didn't request this, please ignore this email.");` - Implements part of the file's concrete application logic.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `        mailSender.send(message);` - Implements part of the file's concrete application logic.
- L0049: `    }` - Closes the current scope block.
- L0050: `` - Blank line used to separate logical blocks for readability.
- L0051: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0052: `     * Send password reset email with token link.` - JavaDoc/comment line documenting intent and behavior.
- L0053: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0054: `    public void sendPasswordResetEmail(String toEmail, String resetToken) {` - Declares a method signature, contract, or constructor entry point.
- L0055: `        String resetLink = resetPasswordUrl + "?token=" + URLEncoder.encode(resetToken, StandardCharsets.UTF_8);` - Security-related logic for tokens, OAuth, or authentication state.
- L0056: `` - Blank line used to separate logical blocks for readability.
- L0057: `        SimpleMailMessage message = new SimpleMailMessage();` - Implements part of the file's concrete application logic.
- L0058: `        message.setFrom(fromEmail);` - Implements part of the file's concrete application logic.
- L0059: `        message.setTo(toEmail);` - Implements part of the file's concrete application logic.
- L0060: `        message.setSubject(PASSWORD_RESET_SUBJECT);` - Credential or recovery logic for authentication safety.
- L0061: `        message.setText("Click the link below to reset your password:\n\n" + resetLink +` - Credential or recovery logic for authentication safety.
- L0062: `                "\n\nThis link will expire in " + resetTokenExpirationMinutes + " minutes." +` - Security-related logic for tokens, OAuth, or authentication state.
- L0063: `                "\n\nIf you didn't request this, please ignore this email.");` - Implements part of the file's concrete application logic.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `        mailSender.send(message);` - Implements part of the file's concrete application logic.
- L0066: `    }` - Closes the current scope block.
- L0067: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 56: `backend/src/main/java/com/auth/service/support/OtpService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 32

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.support;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `import java.security.SecureRandom;` - Imports a type required by this file to compile and run.
- L0006: `import java.util.Base64;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0009: ` * Service for generating OTP codes and reset tokens.` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0011: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0012: `public class OtpService {` - Defines the core type and responsibility boundary for this file.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `    private static final SecureRandom random = new SecureRandom();` - Declares a method signature, contract, or constructor entry point.
- L0015: `` - Blank line used to separate logical blocks for readability.
- L0016: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0017: `     * Generate a 6-digit OTP code.` - JavaDoc/comment line documenting intent and behavior.
- L0018: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0019: `    public String generateOtp() {` - Declares a method signature, contract, or constructor entry point.
- L0020: `        int otp = 100000 + random.nextInt(900000);` - Credential or recovery logic for authentication safety.
- L0021: `        return String.valueOf(otp);` - Returns data to caller after applying current method logic.
- L0022: `    }` - Closes the current scope block.
- L0023: `` - Blank line used to separate logical blocks for readability.
- L0024: `    /**` - JavaDoc/comment line documenting intent and behavior.
- L0025: `     * Generate a unique password reset token.` - JavaDoc/comment line documenting intent and behavior.
- L0026: `     */` - JavaDoc/comment line documenting intent and behavior.
- L0027: `    public String generateResetToken() {` - Declares a method signature, contract, or constructor entry point.
- L0028: `        byte[] randomBytes = new byte[32];` - Implements part of the file's concrete application logic.
- L0029: `        random.nextBytes(randomBytes);` - Implements part of the file's concrete application logic.
- L0030: `        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);` - Returns data to caller after applying current method logic.
- L0031: `    }` - Closes the current scope block.
- L0032: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 57: `backend/src/main/java/com/auth/service/support/PasswordPolicyService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 80

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.support;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0004: `` - Blank line used to separate logical blocks for readability.
- L0005: `import java.util.Locale;` - Imports a type required by this file to compile and run.
- L0006: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0007: `import java.util.regex.Pattern;` - Imports a type required by this file to compile and run.
- L0008: `` - Blank line used to separate logical blocks for readability.
- L0009: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0010: ` * Enforces strong password requirements for registration and password updates.` - JavaDoc/comment line documenting intent and behavior.
- L0011: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0012: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0013: `public class PasswordPolicyService {` - Defines the core type and responsibility boundary for this file.
- L0014: `` - Blank line used to separate logical blocks for readability.
- L0015: `    private static final int MIN_PASSWORD_LENGTH = 6;` - Credential or recovery logic for authentication safety.
- L0016: `    private static final int MIN_EMAIL_LOCAL_PART_LENGTH = 3;` - Implements part of the file's concrete application logic.
- L0017: `    private static final int EMAIL_LOCAL_PART_INDEX = 0;` - Implements part of the file's concrete application logic.
- L0018: `    private static final String EMAIL_SEPARATOR = "@";` - Implements part of the file's concrete application logic.
- L0019: `    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[A-Za-z].*");` - Declares a method signature, contract, or constructor entry point.
- L0020: `    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");` - Declares a method signature, contract, or constructor entry point.
- L0021: `    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(".*\\s.*");` - Declares a method signature, contract, or constructor entry point.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `    private static final Set<String> COMMON_PASSWORD_BLOCKLIST = Set.of(` - Declares a method signature, contract, or constructor entry point.
- L0024: `            "password",` - Credential or recovery logic for authentication safety.
- L0025: `            "password123",` - Credential or recovery logic for authentication safety.
- L0026: `            "password1",` - Credential or recovery logic for authentication safety.
- L0027: `            "123456",` - Implements part of the file's concrete application logic.
- L0028: `            "12345678",` - Implements part of the file's concrete application logic.
- L0029: `            "123456789",` - Implements part of the file's concrete application logic.
- L0030: `            "1234567890",` - Implements part of the file's concrete application logic.
- L0031: `            "qwerty",` - Implements part of the file's concrete application logic.
- L0032: `            "qwerty123",` - Implements part of the file's concrete application logic.
- L0033: `            "letmein",` - Implements part of the file's concrete application logic.
- L0034: `            "welcome",` - Implements part of the file's concrete application logic.
- L0035: `            "admin",` - Implements part of the file's concrete application logic.
- L0036: `            "admin123",` - Implements part of the file's concrete application logic.
- L0037: `            "iloveyou",` - Implements part of the file's concrete application logic.
- L0038: `            "abc123",` - Implements part of the file's concrete application logic.
- L0039: `            "111111",` - Implements part of the file's concrete application logic.
- L0040: `            "123123");` - Implements part of the file's concrete application logic.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    /** Validates password strength and rejects common or guessable passwords. */` - JavaDoc/comment line documenting intent and behavior.
- L0043: `    public void validate(String password, String emailHint) {` - Declares a method signature, contract, or constructor entry point.
- L0044: `        if (password == null || password.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0045: `            throw new IllegalArgumentException("Password is required.");` - Raises an exception for invalid state or request path.
- L0046: `        }` - Closes the current scope block.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `        if (password.length() < MIN_PASSWORD_LENGTH) {` - Conditional branch enforcing a business rule or guard path.
- L0049: `            throw new IllegalArgumentException(` - Raises an exception for invalid state or request path.
- L0050: `                    "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");` - Credential or recovery logic for authentication safety.
- L0051: `        }` - Closes the current scope block.
- L0052: `` - Blank line used to separate logical blocks for readability.
- L0053: `        if (WHITESPACE_PATTERN.matcher(password).matches()) {` - Conditional branch enforcing a business rule or guard path.
- L0054: `            throw new IllegalArgumentException("Password must not contain spaces.");` - Raises an exception for invalid state or request path.
- L0055: `        }` - Closes the current scope block.
- L0056: `` - Blank line used to separate logical blocks for readability.
- L0057: `        if (!LETTER_PATTERN.matcher(password).matches()) {` - Conditional branch enforcing a business rule or guard path.
- L0058: `            throw new IllegalArgumentException("Password must contain at least one letter.");` - Raises an exception for invalid state or request path.
- L0059: `        }` - Closes the current scope block.
- L0060: `` - Blank line used to separate logical blocks for readability.
- L0061: `        if (!DIGIT_PATTERN.matcher(password).matches()) {` - Conditional branch enforcing a business rule or guard path.
- L0062: `            throw new IllegalArgumentException("Password must contain at least one number.");` - Raises an exception for invalid state or request path.
- L0063: `        }` - Closes the current scope block.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `        String normalized = password.toLowerCase(Locale.ROOT);` - Credential or recovery logic for authentication safety.
- L0066: `        if (COMMON_PASSWORD_BLOCKLIST.contains(normalized)) {` - Conditional branch enforcing a business rule or guard path.
- L0067: `            throw new IllegalArgumentException("Password is too common. Choose a less predictable password.");` - Raises an exception for invalid state or request path.
- L0068: `        }` - Closes the current scope block.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `        if (emailHint != null && !emailHint.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0071: `            String[] emailSegments = emailHint.toLowerCase(Locale.ROOT).split(EMAIL_SEPARATOR);` - Implements part of the file's concrete application logic.
- L0072: `            if (emailSegments.length > EMAIL_LOCAL_PART_INDEX) {` - Conditional branch enforcing a business rule or guard path.
- L0073: `                String localPart = emailSegments[EMAIL_LOCAL_PART_INDEX];` - Implements part of the file's concrete application logic.
- L0074: `                if (localPart.length() >= MIN_EMAIL_LOCAL_PART_LENGTH && normalized.contains(localPart)) {` - Conditional branch enforcing a business rule or guard path.
- L0075: `                    throw new IllegalArgumentException("Password must not include your email username.");` - Raises an exception for invalid state or request path.
- L0076: `                }` - Closes the current scope block.
- L0077: `            }` - Closes the current scope block.
- L0078: `        }` - Closes the current scope block.
- L0079: `    }` - Closes the current scope block.
- L0080: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 58: `backend/src/main/java/com/auth/service/support/RateLimitService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 50

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.support;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import lombok.RequiredArgsConstructor;` - Imports a type required by this file to compile and run.
- L0004: `import lombok.extern.slf4j.Slf4j;` - Imports a type required by this file to compile and run.
- L0005: `import org.springframework.data.redis.core.StringRedisTemplate;` - Imports a type required by this file to compile and run.
- L0006: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0007: `` - Blank line used to separate logical blocks for readability.
- L0008: `import java.time.Duration;` - Imports a type required by this file to compile and run.
- L0009: `import java.util.concurrent.TimeUnit;` - Imports a type required by this file to compile and run.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0012: ` * Redis-backed fixed-window rate limiting helper.` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0014: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `@RequiredArgsConstructor` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0016: `@Slf4j` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0017: `public class RateLimitService {` - Defines the core type and responsibility boundary for this file.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `    private final StringRedisTemplate redisTemplate;` - Implements part of the file's concrete application logic.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    /** Consumes one request token and returns allowance metadata. */` - JavaDoc/comment line documenting intent and behavior.
- L0022: `    public RateLimitDecision consume(String key, long limit, Duration window) {` - Declares a method signature, contract, or constructor entry point.
- L0023: `        if (limit <= 0 || window == null || window.isNegative() || window.isZero()) {` - Conditional branch enforcing a business rule or guard path.
- L0024: `            return new RateLimitDecision(true, -1, 0, limit);` - Returns data to caller after applying current method logic.
- L0025: `        }` - Closes the current scope block.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `        try {` - Exception handling block for controlled failure behavior.
- L0028: `            Long currentCount = redisTemplate.opsForValue().increment(key);` - Implements part of the file's concrete application logic.
- L0029: `            if (currentCount == null) {` - Conditional branch enforcing a business rule or guard path.
- L0030: `                return new RateLimitDecision(true, -1, 0, limit);` - Returns data to caller after applying current method logic.
- L0031: `            }` - Closes the current scope block.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `            if (currentCount == 1) {` - Conditional branch enforcing a business rule or guard path.
- L0034: `                redisTemplate.expire(key, window);` - Implements part of the file's concrete application logic.
- L0035: `            }` - Closes the current scope block.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);` - Implements part of the file's concrete application logic.
- L0038: `            long retryAfterSeconds = ttl == null || ttl < 0 ? window.getSeconds() : ttl;` - Implements part of the file's concrete application logic.
- L0039: `            boolean allowed = currentCount <= limit;` - Implements part of the file's concrete application logic.
- L0040: `            return new RateLimitDecision(allowed, retryAfterSeconds, currentCount, limit);` - Returns data to caller after applying current method logic.
- L0041: `        } catch (Exception exception) {` - Opens a new scope block for type, method, or control flow.
- L0042: `            // Fail open on Redis outages to avoid full auth downtime.` - Inline comment for maintainability and context.
- L0043: `            log.warn("Rate limiting unavailable for key={}", key, exception);` - Structured log statement for traceability and diagnostics.
- L0044: `            return new RateLimitDecision(true, -1, 0, limit);` - Returns data to caller after applying current method logic.
- L0045: `        }` - Closes the current scope block.
- L0046: `    }` - Closes the current scope block.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `    public record RateLimitDecision(boolean allowed, long retryAfterSeconds, long count, long limit) {` - Declares a method signature, contract, or constructor entry point.
- L0049: `    }` - Closes the current scope block.
- L0050: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 59: `backend/src/main/java/com/auth/service/support/TokenHashService.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 50

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.support;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import org.springframework.beans.factory.annotation.Value;` - Imports a type required by this file to compile and run.
- L0004: `import org.springframework.stereotype.Service;` - Imports a type required by this file to compile and run.
- L0005: `` - Blank line used to separate logical blocks for readability.
- L0006: `import java.nio.charset.StandardCharsets;` - Imports a type required by this file to compile and run.
- L0007: `import java.security.MessageDigest;` - Imports a type required by this file to compile and run.
- L0008: `import java.security.NoSuchAlgorithmException;` - Imports a type required by this file to compile and run.
- L0009: `import java.util.Base64;` - Imports a type required by this file to compile and run.
- L0010: `` - Blank line used to separate logical blocks for readability.
- L0011: `/**` - JavaDoc/comment line documenting intent and behavior.
- L0012: ` * Provides deterministic one-way hashing for opaque authentication tokens.` - JavaDoc/comment line documenting intent and behavior.
- L0013: ` */` - JavaDoc/comment line documenting intent and behavior.
- L0014: `@Service` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0015: `public class TokenHashService {` - Defines the core type and responsibility boundary for this file.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `    @Value("${security.token-hash-pepper:${jwt.secret}}")` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0018: `    private String tokenHashPepper;` - Security-related logic for tokens, OAuth, or authentication state.
- L0019: `` - Blank line used to separate logical blocks for readability.
- L0020: `    /** Hashes a raw token using SHA-256 + server-side pepper. */` - JavaDoc/comment line documenting intent and behavior.
- L0021: `    public String hash(String rawToken) {` - Declares a method signature, contract, or constructor entry point.
- L0022: `        if (rawToken == null || rawToken.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0023: `            throw new IllegalArgumentException("Token cannot be null or blank.");` - Raises an exception for invalid state or request path.
- L0024: `        }` - Closes the current scope block.
- L0025: `` - Blank line used to separate logical blocks for readability.
- L0026: `        String normalizedToken = rawToken.trim();` - Security-related logic for tokens, OAuth, or authentication state.
- L0027: `        byte[] digest = sha256((tokenHashPepper + ":" + normalizedToken).getBytes(StandardCharsets.UTF_8));` - Security-related logic for tokens, OAuth, or authentication state.
- L0028: `        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);` - Returns data to caller after applying current method logic.
- L0029: `    }` - Closes the current scope block.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    /** Constant-time token/hash comparison helper. */` - JavaDoc/comment line documenting intent and behavior.
- L0032: `    public boolean matches(String rawToken, String tokenHash) {` - Declares a method signature, contract, or constructor entry point.
- L0033: `        if (rawToken == null || rawToken.isBlank() || tokenHash == null || tokenHash.isBlank()) {` - Conditional branch enforcing a business rule or guard path.
- L0034: `            return false;` - Returns data to caller after applying current method logic.
- L0035: `        }` - Closes the current scope block.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `        byte[] computedHash = hash(rawToken).getBytes(StandardCharsets.UTF_8);` - Security-related logic for tokens, OAuth, or authentication state.
- L0038: `        byte[] storedHash = tokenHash.getBytes(StandardCharsets.UTF_8);` - Security-related logic for tokens, OAuth, or authentication state.
- L0039: `        return MessageDigest.isEqual(computedHash, storedHash);` - Returns data to caller after applying current method logic.
- L0040: `    }` - Closes the current scope block.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    private byte[] sha256(byte[] payload) {` - Declares a method signature, contract, or constructor entry point.
- L0043: `        try {` - Exception handling block for controlled failure behavior.
- L0044: `            MessageDigest digest = MessageDigest.getInstance("SHA-256");` - Implements part of the file's concrete application logic.
- L0045: `            return digest.digest(payload);` - Returns data to caller after applying current method logic.
- L0046: `        } catch (NoSuchAlgorithmException exception) {` - Opens a new scope block for type, method, or control flow.
- L0047: `            throw new IllegalStateException("SHA-256 algorithm is not available.", exception);` - Raises an exception for invalid state or request path.
- L0048: `        }` - Closes the current scope block.
- L0049: `    }` - Closes the current scope block.
- L0050: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 60: `backend/src/test/java/com/auth/controller/AuthControllerTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 92

### Line-by-Line Walkthrough
- L0001: `package com.auth.controller;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AuthResponse;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.dto.TokenRefreshRequest;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.security.RefreshTokenCookieService;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.service.AuthService;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.service.auth.AuthTokenService;` - Imports a type required by this file to compile and run.
- L0010: `import jakarta.servlet.http.Cookie;` - Imports a type required by this file to compile and run.
- L0011: `import jakarta.servlet.http.HttpServletRequest;` - Imports a type required by this file to compile and run.
- L0012: `import jakarta.servlet.http.HttpServletResponse;` - Imports a type required by this file to compile and run.
- L0013: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0014: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0015: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0016: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0017: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0018: `import org.springframework.http.HttpHeaders;` - Imports a type required by this file to compile and run.
- L0019: `import org.springframework.http.ResponseEntity;` - Imports a type required by this file to compile and run.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0024: `import static org.junit.jupiter.api.Assertions.assertTrue;` - Imports a type required by this file to compile and run.
- L0025: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0026: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `class AuthControllerTest {` - Defines the core type and responsibility boundary for this file.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0032: `    private AuthService authService;` - Service interaction applies business logic or orchestration.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0035: `    private AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0038: `    private RefreshTokenCookieService refreshTokenCookieService;` - Service interaction applies business logic or orchestration.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0041: `    private HttpServletRequest httpRequest;` - Implements part of the file's concrete application logic.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0044: `    private HttpServletResponse httpResponse;` - Implements part of the file's concrete application logic.
- L0045: `` - Blank line used to separate logical blocks for readability.
- L0046: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0047: `    private AuthController authController;` - Controller wiring handles HTTP-level request or response flow.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0050: `    void refreshToken_whenBodyTokenProvided_prefersBodyOverCookie() {` - Opens a new scope block for type, method, or control flow.
- L0051: `        TokenRefreshRequest requestBody = new TokenRefreshRequest();` - Security-related logic for tokens, OAuth, or authentication state.
- L0052: `        requestBody.setRefreshToken("body-refresh-token");` - Security-related logic for tokens, OAuth, or authentication state.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `        AuthResponse authResponse = new AuthResponse(` - Implements part of the file's concrete application logic.
- L0055: `                "access-token",` - Security-related logic for tokens, OAuth, or authentication state.
- L0056: `                "Bearer",` - Implements part of the file's concrete application logic.
- L0057: `                900_000L,` - Implements part of the file's concrete application logic.
- L0058: `                3_600_000L,` - Implements part of the file's concrete application logic.
- L0059: `                1L,` - Implements part of the file's concrete application logic.
- L0060: `                "Alice",` - Implements part of the file's concrete application logic.
- L0061: `                "alice@example.com",` - Implements part of the file's concrete application logic.
- L0062: `                List.of("ROLE_USER"));` - Authorization rule, authority mapping, or role handling line.
- L0063: `        AuthTokens tokens = new AuthTokens(authResponse, "new-refresh-token");` - Security-related logic for tokens, OAuth, or authentication state.
- L0064: `` - Blank line used to separate logical blocks for readability.
- L0065: `        when(authTokenService.refreshTokens("body-refresh-token")).thenReturn(tokens);` - Service interaction applies business logic or orchestration.
- L0066: `        when(refreshTokenCookieService.buildRefreshTokenCookie("new-refresh-token")).thenReturn("set-cookie-value");` - Service interaction applies business logic or orchestration.
- L0067: `` - Blank line used to separate logical blocks for readability.
- L0068: `        ResponseEntity<AuthResponse> response = authController.refreshToken(httpRequest, httpResponse, requestBody);` - Controller wiring handles HTTP-level request or response flow.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `        verify(authTokenService).refreshTokens("body-refresh-token");` - Service interaction applies business logic or orchestration.
- L0071: `        verify(httpResponse).addHeader(HttpHeaders.SET_COOKIE, "set-cookie-value");` - Implements part of the file's concrete application logic.
- L0072: `        assertEquals(authResponse, response.getBody());` - Implements part of the file's concrete application logic.
- L0073: `    }` - Closes the current scope block.
- L0074: `` - Blank line used to separate logical blocks for readability.
- L0075: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0076: `    void logout_whenBodyMissing_usesCookieTokenAndClearsCookie() {` - Opens a new scope block for type, method, or control flow.
- L0077: `        when(refreshTokenCookieService.getCookieName()).thenReturn("refreshToken");` - Service interaction applies business logic or orchestration.
- L0078: `        when(httpRequest.getCookies()).thenReturn(new Cookie[] {` - Opens a new scope block for type, method, or control flow.
- L0079: `                new Cookie("other", "ignored"),` - Implements part of the file's concrete application logic.
- L0080: `                new Cookie("refreshToken", "cookie-refresh-token")` - Security-related logic for tokens, OAuth, or authentication state.
- L0081: `        });` - Implements part of the file's concrete application logic.
- L0082: `        when(refreshTokenCookieService.clearRefreshTokenCookie()).thenReturn("expired-cookie");` - Service interaction applies business logic or orchestration.
- L0083: `` - Blank line used to separate logical blocks for readability.
- L0084: `        ResponseEntity<MessageResponse> response = authController.logout(httpRequest, httpResponse, null);` - Controller wiring handles HTTP-level request or response flow.
- L0085: `` - Blank line used to separate logical blocks for readability.
- L0086: `        verify(authTokenService).revokeRefreshToken("cookie-refresh-token");` - Service interaction applies business logic or orchestration.
- L0087: `        verify(httpResponse).addHeader(HttpHeaders.SET_COOKIE, "expired-cookie");` - Implements part of the file's concrete application logic.
- L0088: `` - Blank line used to separate logical blocks for readability.
- L0089: `        assertEquals("Logged out successfully.", response.getBody().getMessage());` - Implements part of the file's concrete application logic.
- L0090: `        assertTrue(response.getBody().isSuccess());` - Implements part of the file's concrete application logic.
- L0091: `    }` - Closes the current scope block.
- L0092: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 61: `backend/src/test/java/com/auth/security/CustomUserDetailsServiceTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 61

### Line-by-Line Walkthrough
- L0001: `package com.auth.security;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0006: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0007: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0008: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0009: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0010: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0011: `import org.springframework.security.core.userdetails.UserDetails;` - Imports a type required by this file to compile and run.
- L0012: `import org.springframework.security.core.userdetails.UsernameNotFoundException;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0015: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0016: `` - Blank line used to separate logical blocks for readability.
- L0017: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0018: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0019: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0020: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0021: `` - Blank line used to separate logical blocks for readability.
- L0022: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0023: `class CustomUserDetailsServiceTest {` - Defines the core type and responsibility boundary for this file.
- L0024: `` - Blank line used to separate logical blocks for readability.
- L0025: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0026: `    private UserService userService;` - Service interaction applies business logic or orchestration.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `    private CustomUserDetailsService customUserDetailsService;` - Service interaction applies business logic or orchestration.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0032: `    void loadUserByUsername_normalizesEmailAndBuildsAuthorities() {` - Opens a new scope block for type, method, or control flow.
- L0033: `        Role userRole = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0034: `        userRole.setName(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `        Role adminRole = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0037: `        adminRole.setName(Role.RoleName.ROLE_ADMIN);` - Authorization rule, authority mapping, or role handling line.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `        User user = new User();` - Implements part of the file's concrete application logic.
- L0040: `        user.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0041: `        user.setPassword("encoded");` - Credential or recovery logic for authentication safety.
- L0042: `        user.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0043: `        user.setRoles(Set.of(userRole, adminRole));` - Authorization rule, authority mapping, or role handling line.
- L0044: `` - Blank line used to separate logical blocks for readability.
- L0045: `        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(user));` - Service interaction applies business logic or orchestration.
- L0046: `` - Blank line used to separate logical blocks for readability.
- L0047: `        UserDetails userDetails = customUserDetailsService.loadUserByUsername(" Alice@Example.com ");` - Service interaction applies business logic or orchestration.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `        verify(userService).findByEmail("alice@example.com");` - Service interaction applies business logic or orchestration.
- L0050: `        assertEquals("alice@example.com", userDetails.getUsername());` - Implements part of the file's concrete application logic.
- L0051: `        assertEquals(2, userDetails.getAuthorities().size());` - Authorization rule, authority mapping, or role handling line.
- L0052: `    }` - Closes the current scope block.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0055: `    void loadUserByUsername_whenUserMissing_throwsUsernameNotFoundException() {` - Opens a new scope block for type, method, or control flow.
- L0056: `        when(userService.findByEmail("missing@example.com")).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0057: `` - Blank line used to separate logical blocks for readability.
- L0058: `        assertThrows(UsernameNotFoundException.class,` - Implements part of the file's concrete application logic.
- L0059: `                () -> customUserDetailsService.loadUserByUsername("missing@example.com"));` - Service interaction applies business logic or orchestration.
- L0060: `    }` - Closes the current scope block.
- L0061: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 62: `backend/src/test/java/com/auth/service/auth/AuthTokenServiceTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 127

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.AuthTokens;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.exception.TokenValidationException;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.security.JwtUtil;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.service.support.TokenHashService;` - Imports a type required by this file to compile and run.
- L0010: `import org.junit.jupiter.api.BeforeEach;` - Imports a type required by this file to compile and run.
- L0011: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0012: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0013: `import org.mockito.ArgumentCaptor;` - Imports a type required by this file to compile and run.
- L0014: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0015: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0016: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0017: `import org.springframework.test.util.ReflectionTestUtils;` - Imports a type required by this file to compile and run.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `import java.time.LocalDateTime;` - Imports a type required by this file to compile and run.
- L0020: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0021: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0024: `import static org.junit.jupiter.api.Assertions.assertNotNull;` - Imports a type required by this file to compile and run.
- L0025: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0026: `import static org.junit.jupiter.api.Assertions.assertTrue;` - Imports a type required by this file to compile and run.
- L0027: `import static org.mockito.ArgumentMatchers.any;` - Imports a type required by this file to compile and run.
- L0028: `import static org.mockito.Mockito.never;` - Imports a type required by this file to compile and run.
- L0029: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0030: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0033: `class AuthTokenServiceTest {` - Defines the core type and responsibility boundary for this file.
- L0034: `` - Blank line used to separate logical blocks for readability.
- L0035: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0036: `    private JwtUtil jwtUtil;` - Security-related logic for tokens, OAuth, or authentication state.
- L0037: `` - Blank line used to separate logical blocks for readability.
- L0038: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0039: `    private UserService userService;` - Service interaction applies business logic or orchestration.
- L0040: `` - Blank line used to separate logical blocks for readability.
- L0041: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0042: `    private TokenHashService tokenHashService;` - Service interaction applies business logic or orchestration.
- L0043: `` - Blank line used to separate logical blocks for readability.
- L0044: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0045: `    private AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0046: `` - Blank line used to separate logical blocks for readability.
- L0047: `    @BeforeEach` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0048: `    void setUp() {` - Opens a new scope block for type, method, or control flow.
- L0049: `        ReflectionTestUtils.setField(authTokenService, "refreshTokenExpirationMs", 3_600_000L);` - Service interaction applies business logic or orchestration.
- L0050: `    }` - Closes the current scope block.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0053: `    void issueTokens_whenUserIsValid_persistsHashedRefreshTokenAndBuildsResponse() {` - Opens a new scope block for type, method, or control flow.
- L0054: `        User user = buildUser();` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `        when(jwtUtil.generateTokenFromEmail(user.getEmail())).thenReturn("access-token");` - Security-related logic for tokens, OAuth, or authentication state.
- L0057: `        when(jwtUtil.getAccessTokenExpiration()).thenReturn(900_000L);` - Security-related logic for tokens, OAuth, or authentication state.
- L0058: `        when(tokenHashService.hash(any(String.class))).thenReturn("hashed-refresh-token");` - Service interaction applies business logic or orchestration.
- L0059: `        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));` - Service interaction applies business logic or orchestration.
- L0060: `` - Blank line used to separate logical blocks for readability.
- L0061: `        AuthTokens tokens = authTokenService.issueTokens(user);` - Service interaction applies business logic or orchestration.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `        assertEquals("access-token", tokens.response().getAccessToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0064: `        assertEquals("Bearer", tokens.response().getTokenType());` - Security-related logic for tokens, OAuth, or authentication state.
- L0065: `        assertEquals(2, tokens.response().getRoles().size());` - Security-related logic for tokens, OAuth, or authentication state.
- L0066: `        assertNotNull(tokens.refreshToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0067: `        assertEquals("hashed-refresh-token", user.getRefreshToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0068: `        assertNotNull(user.getRefreshTokenExpiry());` - Security-related logic for tokens, OAuth, or authentication state.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);` - Security-related logic for tokens, OAuth, or authentication state.
- L0071: `        verify(tokenHashService).hash(tokenCaptor.capture());` - Service interaction applies business logic or orchestration.
- L0072: `        assertEquals(tokens.refreshToken(), tokenCaptor.getValue());` - Security-related logic for tokens, OAuth, or authentication state.
- L0073: `    }` - Closes the current scope block.
- L0074: `` - Blank line used to separate logical blocks for readability.
- L0075: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0076: `    void refreshTokens_whenRefreshTokenBlank_throwsTokenValidationException() {` - Opens a new scope block for type, method, or control flow.
- L0077: `        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("  "));` - Service interaction applies business logic or orchestration.
- L0078: `    }` - Closes the current scope block.
- L0079: `` - Blank line used to separate logical blocks for readability.
- L0080: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0081: `    void refreshTokens_whenStoredTokenMissing_throwsTokenValidationException() {` - Opens a new scope block for type, method, or control flow.
- L0082: `        when(tokenHashService.hash("raw-refresh-token")).thenReturn("hashed");` - Service interaction applies business logic or orchestration.
- L0083: `        when(userService.findByRefreshToken("hashed")).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0084: `` - Blank line used to separate logical blocks for readability.
- L0085: `        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("raw-refresh-token"));` - Service interaction applies business logic or orchestration.
- L0086: `    }` - Closes the current scope block.
- L0087: `` - Blank line used to separate logical blocks for readability.
- L0088: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0089: `    void refreshTokens_whenStoredTokenExpired_clearsTokenAndThrowsTokenValidationException() {` - Opens a new scope block for type, method, or control flow.
- L0090: `        User user = buildUser();` - Implements part of the file's concrete application logic.
- L0091: `        user.setRefreshToken("hashed");` - Security-related logic for tokens, OAuth, or authentication state.
- L0092: `        user.setRefreshTokenExpiry(LocalDateTime.now().minusMinutes(1));` - Security-related logic for tokens, OAuth, or authentication state.
- L0093: `` - Blank line used to separate logical blocks for readability.
- L0094: `        when(tokenHashService.hash("raw-refresh-token")).thenReturn("hashed");` - Service interaction applies business logic or orchestration.
- L0095: `        when(userService.findByRefreshToken("hashed")).thenReturn(Optional.of(user));` - Service interaction applies business logic or orchestration.
- L0096: `` - Blank line used to separate logical blocks for readability.
- L0097: `        assertThrows(TokenValidationException.class, () -> authTokenService.refreshTokens("raw-refresh-token"));` - Service interaction applies business logic or orchestration.
- L0098: `` - Blank line used to separate logical blocks for readability.
- L0099: `        assertEquals(null, user.getRefreshToken());` - Security-related logic for tokens, OAuth, or authentication state.
- L0100: `        assertEquals(null, user.getRefreshTokenExpiry());` - Security-related logic for tokens, OAuth, or authentication state.
- L0101: `        verify(userService).save(user);` - Service interaction applies business logic or orchestration.
- L0102: `    }` - Closes the current scope block.
- L0103: `` - Blank line used to separate logical blocks for readability.
- L0104: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0105: `    void revokeRefreshToken_whenTokenBlank_doesNotCallRepository() {` - Opens a new scope block for type, method, or control flow.
- L0106: `        authTokenService.revokeRefreshToken(" ");` - Service interaction applies business logic or orchestration.
- L0107: `` - Blank line used to separate logical blocks for readability.
- L0108: `        verify(tokenHashService, never()).hash(any(String.class));` - Service interaction applies business logic or orchestration.
- L0109: `        verify(userService, never()).findByRefreshToken(any(String.class));` - Service interaction applies business logic or orchestration.
- L0110: `    }` - Closes the current scope block.
- L0111: `` - Blank line used to separate logical blocks for readability.
- L0112: `    private User buildUser() {` - Declares a method signature, contract, or constructor entry point.
- L0113: `        Role userRole = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0114: `        userRole.setName(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0115: `` - Blank line used to separate logical blocks for readability.
- L0116: `        Role adminRole = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0117: `        adminRole.setName(Role.RoleName.ROLE_ADMIN);` - Authorization rule, authority mapping, or role handling line.
- L0118: `` - Blank line used to separate logical blocks for readability.
- L0119: `        User user = new User();` - Implements part of the file's concrete application logic.
- L0120: `        user.setId(7L);` - Implements part of the file's concrete application logic.
- L0121: `        user.setName("Alice");` - Implements part of the file's concrete application logic.
- L0122: `        user.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0123: `        user.setRoles(Set.of(userRole, adminRole));` - Authorization rule, authority mapping, or role handling line.
- L0124: `        user.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0125: `        return user;` - Returns data to caller after applying current method logic.
- L0126: `    }` - Closes the current scope block.
- L0127: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 63: `backend/src/test/java/com/auth/service/auth/OAuth2UserProvisioningServiceTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 159

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.auth;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.service.RoleService;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0007: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0008: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0009: `import org.mockito.ArgumentCaptor;` - Imports a type required by this file to compile and run.
- L0010: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0011: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0012: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.security.core.authority.SimpleGrantedAuthority;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0015: `import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;` - Imports a type required by this file to compile and run.
- L0016: `import org.springframework.security.oauth2.core.user.DefaultOAuth2User;` - Imports a type required by this file to compile and run.
- L0017: `import org.springframework.security.oauth2.core.user.OAuth2User;` - Imports a type required by this file to compile and run.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `import java.util.Map;` - Imports a type required by this file to compile and run.
- L0020: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0021: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0022: `` - Blank line used to separate logical blocks for readability.
- L0023: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0024: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0025: `import static org.junit.jupiter.api.Assertions.assertTrue;` - Imports a type required by this file to compile and run.
- L0026: `import static org.mockito.ArgumentMatchers.any;` - Imports a type required by this file to compile and run.
- L0027: `import static org.mockito.ArgumentMatchers.anyString;` - Imports a type required by this file to compile and run.
- L0028: `import static org.mockito.Mockito.never;` - Imports a type required by this file to compile and run.
- L0029: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0030: `import static org.mockito.Mockito.verifyNoInteractions;` - Imports a type required by this file to compile and run.
- L0031: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0032: `` - Blank line used to separate logical blocks for readability.
- L0033: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0034: `class OAuth2UserProvisioningServiceTest {` - Defines the core type and responsibility boundary for this file.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0037: `    private UserService userService;` - Service interaction applies business logic or orchestration.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0040: `    private RoleService roleService;` - Service interaction applies business logic or orchestration.
- L0041: `` - Blank line used to separate logical blocks for readability.
- L0042: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0043: `    private PasswordEncoder passwordEncoder;` - Credential or recovery logic for authentication safety.
- L0044: `` - Blank line used to separate logical blocks for readability.
- L0045: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0046: `    private OAuth2UserProvisioningService service;` - Service interaction applies business logic or orchestration.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0049: `    void loadOrCreateUser_whenExistingUserNeedsNoUpdate_returnsWithoutSaving() {` - Opens a new scope block for type, method, or control flow.
- L0050: `        User existing = new User();` - Implements part of the file's concrete application logic.
- L0051: `        existing.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0052: `        existing.setName("Alice");` - Implements part of the file's concrete application logic.
- L0053: `        existing.setEnabled(true);` - Implements part of the file's concrete application logic.
- L0054: `        existing.setAuthProvider("google");` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `        OAuth2AuthenticationToken token = oauthToken(` - Security-related logic for tokens, OAuth, or authentication state.
- L0057: `                "google",` - Implements part of the file's concrete application logic.
- L0058: `                Map.of("email", "alice@example.com", "name", "Alice Changed"));` - Implements part of the file's concrete application logic.
- L0059: `` - Blank line used to separate logical blocks for readability.
- L0060: `        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));` - Service interaction applies business logic or orchestration.
- L0061: `` - Blank line used to separate logical blocks for readability.
- L0062: `        User resolved = service.loadOrCreateUser(token, token.getPrincipal());` - Service interaction applies business logic or orchestration.
- L0063: `` - Blank line used to separate logical blocks for readability.
- L0064: `        assertEquals(existing, resolved);` - Implements part of the file's concrete application logic.
- L0065: `        verify(userService, never()).save(any(User.class));` - Service interaction applies business logic or orchestration.
- L0066: `        verifyNoInteractions(roleService, passwordEncoder);` - Service interaction applies business logic or orchestration.
- L0067: `    }` - Closes the current scope block.
- L0068: `` - Blank line used to separate logical blocks for readability.
- L0069: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0070: `    void loadOrCreateUser_whenExistingUserHasMissingData_updatesOnlyChangedFields() {` - Opens a new scope block for type, method, or control flow.
- L0071: `        User existing = new User();` - Implements part of the file's concrete application logic.
- L0072: `        existing.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0073: `        existing.setName(" ");` - Implements part of the file's concrete application logic.
- L0074: `        existing.setEnabled(false);` - Implements part of the file's concrete application logic.
- L0075: `        existing.setAuthProvider(" ");` - Implements part of the file's concrete application logic.
- L0076: `` - Blank line used to separate logical blocks for readability.
- L0077: `        OAuth2AuthenticationToken token = oauthToken(` - Security-related logic for tokens, OAuth, or authentication state.
- L0078: `                "google",` - Implements part of the file's concrete application logic.
- L0079: `                Map.of("email", "  alice@example.com  ", "name", "  Alice  "));` - Implements part of the file's concrete application logic.
- L0080: `` - Blank line used to separate logical blocks for readability.
- L0081: `        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));` - Service interaction applies business logic or orchestration.
- L0082: `        when(userService.save(existing)).thenReturn(existing);` - Service interaction applies business logic or orchestration.
- L0083: `` - Blank line used to separate logical blocks for readability.
- L0084: `        User resolved = service.loadOrCreateUser(token, token.getPrincipal());` - Service interaction applies business logic or orchestration.
- L0085: `` - Blank line used to separate logical blocks for readability.
- L0086: `        assertTrue(resolved.isEnabled());` - Implements part of the file's concrete application logic.
- L0087: `        assertEquals("google", resolved.getAuthProvider());` - Implements part of the file's concrete application logic.
- L0088: `        assertEquals("Alice", resolved.getName());` - Implements part of the file's concrete application logic.
- L0089: `        verify(userService).save(existing);` - Service interaction applies business logic or orchestration.
- L0090: `        verifyNoInteractions(roleService, passwordEncoder);` - Service interaction applies business logic or orchestration.
- L0091: `    }` - Closes the current scope block.
- L0092: `` - Blank line used to separate logical blocks for readability.
- L0093: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0094: `    void loadOrCreateUser_whenNewUser_persistsOAuthUserWithDefaultRole() {` - Opens a new scope block for type, method, or control flow.
- L0095: `        Role role = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0096: `        role.setName(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0097: `` - Blank line used to separate logical blocks for readability.
- L0098: `        OAuth2AuthenticationToken token = oauthToken(` - Security-related logic for tokens, OAuth, or authentication state.
- L0099: `                "google",` - Implements part of the file's concrete application logic.
- L0100: `                Map.of("email", "new.user@example.com", "name", "New User"));` - Implements part of the file's concrete application logic.
- L0101: `` - Blank line used to separate logical blocks for readability.
- L0102: `        when(userService.findByEmail("new.user@example.com")).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0103: `        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");` - Credential or recovery logic for authentication safety.
- L0104: `        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(role);` - Service interaction applies business logic or orchestration.
- L0105: `        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));` - Service interaction applies business logic or orchestration.
- L0106: `` - Blank line used to separate logical blocks for readability.
- L0107: `        User resolved = service.loadOrCreateUser(token, token.getPrincipal());` - Service interaction applies business logic or orchestration.
- L0108: `` - Blank line used to separate logical blocks for readability.
- L0109: `        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);` - Implements part of the file's concrete application logic.
- L0110: `        verify(userService).save(captor.capture());` - Service interaction applies business logic or orchestration.
- L0111: `        User saved = captor.getValue();` - Implements part of the file's concrete application logic.
- L0112: `` - Blank line used to separate logical blocks for readability.
- L0113: `        assertEquals("new.user@example.com", saved.getEmail());` - Implements part of the file's concrete application logic.
- L0114: `        assertEquals("New User", saved.getName());` - Implements part of the file's concrete application logic.
- L0115: `        assertEquals("google", saved.getAuthProvider());` - Implements part of the file's concrete application logic.
- L0116: `        assertTrue(saved.isEnabled());` - Implements part of the file's concrete application logic.
- L0117: `        assertEquals("encoded-password", saved.getPassword());` - Credential or recovery logic for authentication safety.
- L0118: `        assertEquals(Set.of(role), saved.getRoles());` - Authorization rule, authority mapping, or role handling line.
- L0119: `        assertEquals(saved, resolved);` - Implements part of the file's concrete application logic.
- L0120: `    }` - Closes the current scope block.
- L0121: `` - Blank line used to separate logical blocks for readability.
- L0122: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0123: `    void loadOrCreateUser_whenGithubEmailMissing_usesNoReplyFallbackEmail() {` - Opens a new scope block for type, method, or control flow.
- L0124: `        Role role = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0125: `        role.setName(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0126: `` - Blank line used to separate logical blocks for readability.
- L0127: `        OAuth2AuthenticationToken token = oauthToken(` - Security-related logic for tokens, OAuth, or authentication state.
- L0128: `                "github",` - Implements part of the file's concrete application logic.
- L0129: `                Map.of("login", "octocat", "name", "The Cat"));` - Implements part of the file's concrete application logic.
- L0130: `` - Blank line used to separate logical blocks for readability.
- L0131: `        when(userService.findByEmail("octocat@users.noreply.github.com")).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0132: `        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");` - Credential or recovery logic for authentication safety.
- L0133: `        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(role);` - Service interaction applies business logic or orchestration.
- L0134: `        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));` - Service interaction applies business logic or orchestration.
- L0135: `` - Blank line used to separate logical blocks for readability.
- L0136: `        User resolved = service.loadOrCreateUser(token, token.getPrincipal());` - Service interaction applies business logic or orchestration.
- L0137: `` - Blank line used to separate logical blocks for readability.
- L0138: `        assertEquals("octocat@users.noreply.github.com", resolved.getEmail());` - Implements part of the file's concrete application logic.
- L0139: `    }` - Closes the current scope block.
- L0140: `` - Blank line used to separate logical blocks for readability.
- L0141: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0142: `    void loadOrCreateUser_whenEmailUnavailableForProvider_throwsIllegalArgumentException() {` - Opens a new scope block for type, method, or control flow.
- L0143: `        OAuth2AuthenticationToken token = oauthToken("google", Map.of("name", "Missing Email"));` - Security-related logic for tokens, OAuth, or authentication state.
- L0144: `` - Blank line used to separate logical blocks for readability.
- L0145: `        assertThrows(IllegalArgumentException.class, () -> service.loadOrCreateUser(token, token.getPrincipal()));` - Service interaction applies business logic or orchestration.
- L0146: `` - Blank line used to separate logical blocks for readability.
- L0147: `        verify(userService, never()).save(any(User.class));` - Service interaction applies business logic or orchestration.
- L0148: `        verifyNoInteractions(roleService, passwordEncoder);` - Service interaction applies business logic or orchestration.
- L0149: `    }` - Closes the current scope block.
- L0150: `` - Blank line used to separate logical blocks for readability.
- L0151: `    private OAuth2AuthenticationToken oauthToken(String registrationId, Map<String, Object> attributes) {` - Declares a method signature, contract, or constructor entry point.
- L0152: `        String nameAttributeKey = attributes.containsKey("email") ? "email" : "name";` - Implements part of the file's concrete application logic.
- L0153: `        OAuth2User oauth2User = new DefaultOAuth2User(` - Security-related logic for tokens, OAuth, or authentication state.
- L0154: `                Set.of(new SimpleGrantedAuthority("ROLE_USER")),` - Authorization rule, authority mapping, or role handling line.
- L0155: `                attributes,` - Implements part of the file's concrete application logic.
- L0156: `                nameAttributeKey);` - Implements part of the file's concrete application logic.
- L0157: `        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), registrationId);` - Returns data to caller after applying current method logic.
- L0158: `    }` - Closes the current scope block.
- L0159: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 64: `backend/src/test/java/com/auth/service/impl/AdminServiceImplTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 76

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.UserDto;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.mapper.UserMapper;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.repository.UserRepository;` - Imports a type required by this file to compile and run.
- L0007: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0008: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0009: `import org.mockito.ArgumentCaptor;` - Imports a type required by this file to compile and run.
- L0010: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0011: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0012: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0013: `import org.springframework.data.domain.Page;` - Imports a type required by this file to compile and run.
- L0014: `import org.springframework.data.domain.PageImpl;` - Imports a type required by this file to compile and run.
- L0015: `import org.springframework.data.domain.Pageable;` - Imports a type required by this file to compile and run.
- L0016: `import org.springframework.data.domain.Sort;` - Imports a type required by this file to compile and run.
- L0017: `import org.springframework.data.jpa.domain.Specification;` - Imports a type required by this file to compile and run.
- L0018: `` - Blank line used to separate logical blocks for readability.
- L0019: `import java.util.List;` - Imports a type required by this file to compile and run.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0022: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0023: `import static org.mockito.ArgumentMatchers.any;` - Imports a type required by this file to compile and run.
- L0024: `import static org.mockito.Mockito.never;` - Imports a type required by this file to compile and run.
- L0025: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0026: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0027: `` - Blank line used to separate logical blocks for readability.
- L0028: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0029: `class AdminServiceImplTest {` - Defines the core type and responsibility boundary for this file.
- L0030: `` - Blank line used to separate logical blocks for readability.
- L0031: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0032: `    private UserRepository userRepository;` - Repository usage handles persistence access to database records.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0035: `    private UserMapper userMapper;` - Implements part of the file's concrete application logic.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0038: `    private AdminServiceImpl adminService;` - Service interaction applies business logic or orchestration.
- L0039: `` - Blank line used to separate logical blocks for readability.
- L0040: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0041: `    void getUsers_whenInputsOutOfRange_normalizesPagingAndSort() {` - Opens a new scope block for type, method, or control flow.
- L0042: `        User user = new User();` - Implements part of the file's concrete application logic.
- L0043: `        user.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0044: `` - Blank line used to separate logical blocks for readability.
- L0045: `        UserDto userDto = new UserDto();` - Implements part of the file's concrete application logic.
- L0046: `        userDto.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0047: `` - Blank line used to separate logical blocks for readability.
- L0048: `        when(userRepository.findAll(org.mockito.ArgumentMatchers.<Specification<User>>isNull(), any(Pageable.class)))` - Repository usage handles persistence access to database records.
- L0049: `                .thenReturn(new PageImpl<>(List.of(user)));` - Implements part of the file's concrete application logic.
- L0050: `        when(userMapper.toDto(user)).thenReturn(userDto);` - Implements part of the file's concrete application logic.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `        Page<UserDto> result = adminService.getUsers(-5, 500, null, null, null, "unsupportedField", "asc");` - Service interaction applies business logic or orchestration.
- L0053: `` - Blank line used to separate logical blocks for readability.
- L0054: `        assertEquals(1, result.getContent().size());` - Implements part of the file's concrete application logic.
- L0055: `` - Blank line used to separate logical blocks for readability.
- L0056: `        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);` - Implements part of the file's concrete application logic.
- L0057: `        verify(userRepository).findAll(org.mockito.ArgumentMatchers.<Specification<User>>isNull(), pageableCaptor.capture());` - Repository usage handles persistence access to database records.
- L0058: `` - Blank line used to separate logical blocks for readability.
- L0059: `        Pageable pageable = pageableCaptor.getValue();` - Implements part of the file's concrete application logic.
- L0060: `        assertEquals(0, pageable.getPageNumber());` - Implements part of the file's concrete application logic.
- L0061: `        assertEquals(100, pageable.getPageSize());` - Implements part of the file's concrete application logic.
- L0062: `` - Blank line used to separate logical blocks for readability.
- L0063: `        Sort.Order order = pageable.getSort().getOrderFor("createdAt");` - Implements part of the file's concrete application logic.
- L0064: `        assertEquals(Sort.Direction.ASC, order.getDirection());` - Implements part of the file's concrete application logic.
- L0065: `    }` - Closes the current scope block.
- L0066: `` - Blank line used to separate logical blocks for readability.
- L0067: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0068: `    void getUsers_whenRoleFilterInvalid_throwsIllegalArgumentException() {` - Opens a new scope block for type, method, or control flow.
- L0069: `        assertThrows(IllegalArgumentException.class,` - Implements part of the file's concrete application logic.
- L0070: `                () -> adminService.getUsers(0, 20, null, null, "manager", "createdAt", "desc"));` - Service interaction applies business logic or orchestration.
- L0071: `` - Blank line used to separate logical blocks for readability.
- L0072: `        verify(userRepository, never()).findAll(` - Repository usage handles persistence access to database records.
- L0073: `                org.mockito.ArgumentMatchers.<Specification<User>>isNull(),` - Implements part of the file's concrete application logic.
- L0074: `                any(Pageable.class));` - Implements part of the file's concrete application logic.
- L0075: `    }` - Closes the current scope block.
- L0076: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 65: `backend/src/test/java/com/auth/service/impl/AuthServiceImplTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 195

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.dto.ChangePasswordRequest;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.dto.LoginRequest;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.dto.MessageResponse;` - Imports a type required by this file to compile and run.
- L0006: `import com.auth.dto.RegisterRequest;` - Imports a type required by this file to compile and run.
- L0007: `import com.auth.dto.ResetPasswordRequest;` - Imports a type required by this file to compile and run.
- L0008: `import com.auth.dto.UpdatePasswordRequest;` - Imports a type required by this file to compile and run.
- L0009: `import com.auth.entity.Role;` - Imports a type required by this file to compile and run.
- L0010: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0011: `import com.auth.exception.TokenValidationException;` - Imports a type required by this file to compile and run.
- L0012: `import com.auth.exception.UserAlreadyExistsException;` - Imports a type required by this file to compile and run.
- L0013: `import com.auth.mapper.UserMapper;` - Imports a type required by this file to compile and run.
- L0014: `import com.auth.service.RoleService;` - Imports a type required by this file to compile and run.
- L0015: `import com.auth.service.UserService;` - Imports a type required by this file to compile and run.
- L0016: `import com.auth.service.auth.AuthAbuseProtectionService;` - Imports a type required by this file to compile and run.
- L0017: `import com.auth.service.auth.AuthTokenService;` - Imports a type required by this file to compile and run.
- L0018: `import com.auth.service.support.EmailService;` - Imports a type required by this file to compile and run.
- L0019: `import com.auth.service.support.OtpService;` - Imports a type required by this file to compile and run.
- L0020: `import com.auth.service.support.PasswordPolicyService;` - Imports a type required by this file to compile and run.
- L0021: `import com.auth.service.support.TokenHashService;` - Imports a type required by this file to compile and run.
- L0022: `import org.junit.jupiter.api.BeforeEach;` - Imports a type required by this file to compile and run.
- L0023: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0024: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0025: `import org.mockito.ArgumentCaptor;` - Imports a type required by this file to compile and run.
- L0026: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0027: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0028: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0029: `import org.springframework.security.authentication.AuthenticationManager;` - Imports a type required by this file to compile and run.
- L0030: `import org.springframework.security.authentication.BadCredentialsException;` - Imports a type required by this file to compile and run.
- L0031: `import org.springframework.security.crypto.password.PasswordEncoder;` - Imports a type required by this file to compile and run.
- L0032: `import org.springframework.test.util.ReflectionTestUtils;` - Imports a type required by this file to compile and run.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0035: `import java.util.Set;` - Imports a type required by this file to compile and run.
- L0036: `` - Blank line used to separate logical blocks for readability.
- L0037: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0038: `import static org.junit.jupiter.api.Assertions.assertFalse;` - Imports a type required by this file to compile and run.
- L0039: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0040: `import static org.junit.jupiter.api.Assertions.assertTrue;` - Imports a type required by this file to compile and run.
- L0041: `import static org.mockito.ArgumentMatchers.any;` - Imports a type required by this file to compile and run.
- L0042: `import static org.mockito.Mockito.verify;` - Imports a type required by this file to compile and run.
- L0043: `import static org.mockito.Mockito.verifyNoInteractions;` - Imports a type required by this file to compile and run.
- L0044: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0045: `` - Blank line used to separate logical blocks for readability.
- L0046: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0047: `class AuthServiceImplTest {` - Defines the core type and responsibility boundary for this file.
- L0048: `` - Blank line used to separate logical blocks for readability.
- L0049: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0050: `    private UserService userService;` - Service interaction applies business logic or orchestration.
- L0051: `` - Blank line used to separate logical blocks for readability.
- L0052: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0053: `    private RoleService roleService;` - Service interaction applies business logic or orchestration.
- L0054: `` - Blank line used to separate logical blocks for readability.
- L0055: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0056: `    private PasswordEncoder passwordEncoder;` - Credential or recovery logic for authentication safety.
- L0057: `` - Blank line used to separate logical blocks for readability.
- L0058: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0059: `    private AuthenticationManager authenticationManager;` - Implements part of the file's concrete application logic.
- L0060: `` - Blank line used to separate logical blocks for readability.
- L0061: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0062: `    private AuthTokenService authTokenService;` - Service interaction applies business logic or orchestration.
- L0063: `` - Blank line used to separate logical blocks for readability.
- L0064: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0065: `    private EmailService emailService;` - Service interaction applies business logic or orchestration.
- L0066: `` - Blank line used to separate logical blocks for readability.
- L0067: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0068: `    private OtpService otpService;` - Service interaction applies business logic or orchestration.
- L0069: `` - Blank line used to separate logical blocks for readability.
- L0070: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0071: `    private UserMapper userMapper;` - Implements part of the file's concrete application logic.
- L0072: `` - Blank line used to separate logical blocks for readability.
- L0073: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0074: `    private TokenHashService tokenHashService;` - Service interaction applies business logic or orchestration.
- L0075: `` - Blank line used to separate logical blocks for readability.
- L0076: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0077: `    private PasswordPolicyService passwordPolicyService;` - Service interaction applies business logic or orchestration.
- L0078: `` - Blank line used to separate logical blocks for readability.
- L0079: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0080: `    private AuthAbuseProtectionService authAbuseProtectionService;` - Service interaction applies business logic or orchestration.
- L0081: `` - Blank line used to separate logical blocks for readability.
- L0082: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0083: `    private AuthServiceImpl authService;` - Service interaction applies business logic or orchestration.
- L0084: `` - Blank line used to separate logical blocks for readability.
- L0085: `    @BeforeEach` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0086: `    void setUp() {` - Opens a new scope block for type, method, or control flow.
- L0087: `        ReflectionTestUtils.setField(authService, "otpExpirationMinutes", 5);` - Service interaction applies business logic or orchestration.
- L0088: `        ReflectionTestUtils.setField(authService, "resetTokenExpirationMinutes", 30);` - Service interaction applies business logic or orchestration.
- L0089: `    }` - Closes the current scope block.
- L0090: `` - Blank line used to separate logical blocks for readability.
- L0091: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0092: `    void register_whenEmailAlreadyExists_throwsUserAlreadyExistsException() {` - Opens a new scope block for type, method, or control flow.
- L0093: `        RegisterRequest request = new RegisterRequest();` - Implements part of the file's concrete application logic.
- L0094: `        request.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0095: `` - Blank line used to separate logical blocks for readability.
- L0096: `        when(userService.existsByEmail(request.getEmail())).thenReturn(true);` - Service interaction applies business logic or orchestration.
- L0097: `` - Blank line used to separate logical blocks for readability.
- L0098: `        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));` - Service interaction applies business logic or orchestration.
- L0099: `    }` - Closes the current scope block.
- L0100: `` - Blank line used to separate logical blocks for readability.
- L0101: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0102: `    void register_whenValidRequest_savesUserAndSendsOtp() {` - Opens a new scope block for type, method, or control flow.
- L0103: `        RegisterRequest request = new RegisterRequest();` - Implements part of the file's concrete application logic.
- L0104: `        request.setName("Alice");` - Implements part of the file's concrete application logic.
- L0105: `        request.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0106: `        request.setPassword("Password1");` - Credential or recovery logic for authentication safety.
- L0107: `` - Blank line used to separate logical blocks for readability.
- L0108: `        User mappedUser = new User();` - Implements part of the file's concrete application logic.
- L0109: `        mappedUser.setName(request.getName());` - Implements part of the file's concrete application logic.
- L0110: `        mappedUser.setEmail(request.getEmail());` - Implements part of the file's concrete application logic.
- L0111: `` - Blank line used to separate logical blocks for readability.
- L0112: `        Role userRole = new Role();` - Authorization rule, authority mapping, or role handling line.
- L0113: `        userRole.setName(Role.RoleName.ROLE_USER);` - Authorization rule, authority mapping, or role handling line.
- L0114: `` - Blank line used to separate logical blocks for readability.
- L0115: `        when(userService.existsByEmail(request.getEmail())).thenReturn(false);` - Service interaction applies business logic or orchestration.
- L0116: `        when(userMapper.toEntity(request)).thenReturn(mappedUser);` - Implements part of the file's concrete application logic.
- L0117: `        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");` - Credential or recovery logic for authentication safety.
- L0118: `        when(otpService.generateOtp()).thenReturn("123456");` - Service interaction applies business logic or orchestration.
- L0119: `        when(tokenHashService.hash("123456")).thenReturn("otp-hash");` - Service interaction applies business logic or orchestration.
- L0120: `        when(roleService.findOrCreateRole(Role.RoleName.ROLE_USER)).thenReturn(userRole);` - Service interaction applies business logic or orchestration.
- L0121: `        when(userService.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));` - Service interaction applies business logic or orchestration.
- L0122: `` - Blank line used to separate logical blocks for readability.
- L0123: `        MessageResponse response = authService.register(request);` - Service interaction applies business logic or orchestration.
- L0124: `` - Blank line used to separate logical blocks for readability.
- L0125: `        assertTrue(response.isSuccess());` - Implements part of the file's concrete application logic.
- L0126: `        verify(passwordPolicyService).validate(request.getPassword(), request.getEmail());` - Service interaction applies business logic or orchestration.
- L0127: `        verify(emailService).sendOtpEmail(request.getEmail(), "123456");` - Service interaction applies business logic or orchestration.
- L0128: `` - Blank line used to separate logical blocks for readability.
- L0129: `        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);` - Implements part of the file's concrete application logic.
- L0130: `        verify(userService).save(userCaptor.capture());` - Service interaction applies business logic or orchestration.
- L0131: `        User savedUser = userCaptor.getValue();` - Implements part of the file's concrete application logic.
- L0132: `` - Blank line used to separate logical blocks for readability.
- L0133: `        assertEquals("encoded-password", savedUser.getPassword());` - Credential or recovery logic for authentication safety.
- L0134: `        assertEquals("otp-hash", savedUser.getVerificationOtp());` - Credential or recovery logic for authentication safety.
- L0135: `        assertEquals(Set.of(userRole), savedUser.getRoles());` - Authorization rule, authority mapping, or role handling line.
- L0136: `        assertFalse(savedUser.isEnabled());` - Implements part of the file's concrete application logic.
- L0137: `    }` - Closes the current scope block.
- L0138: `` - Blank line used to separate logical blocks for readability.
- L0139: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0140: `    void login_whenUserMissing_recordsFailedAttemptAndThrows() {` - Opens a new scope block for type, method, or control flow.
- L0141: `        LoginRequest request = new LoginRequest();` - Implements part of the file's concrete application logic.
- L0142: `        request.setEmail("missing@example.com");` - Implements part of the file's concrete application logic.
- L0143: `        request.setPassword("Password1");` - Credential or recovery logic for authentication safety.
- L0144: `` - Blank line used to separate logical blocks for readability.
- L0145: `        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0146: `` - Blank line used to separate logical blocks for readability.
- L0147: `        assertThrows(BadCredentialsException.class, () -> authService.login(request));` - Service interaction applies business logic or orchestration.
- L0148: `` - Blank line used to separate logical blocks for readability.
- L0149: `        verify(authAbuseProtectionService).guardLoginAttempt(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0150: `        verify(authAbuseProtectionService).recordFailedLogin(request.getEmail());` - Service interaction applies business logic or orchestration.
- L0151: `        verifyNoInteractions(authenticationManager);` - Implements part of the file's concrete application logic.
- L0152: `    }` - Closes the current scope block.
- L0153: `` - Blank line used to separate logical blocks for readability.
- L0154: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0155: `    void resetPassword_whenUserMissing_returnsGenericSuccessMessage() {` - Opens a new scope block for type, method, or control flow.
- L0156: `        ResetPasswordRequest request = new ResetPasswordRequest();` - Credential or recovery logic for authentication safety.
- L0157: `        request.setEmail("missing@example.com");` - Implements part of the file's concrete application logic.
- L0158: `` - Blank line used to separate logical blocks for readability.
- L0159: `        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0160: `` - Blank line used to separate logical blocks for readability.
- L0161: `        MessageResponse response = authService.resetPassword(request);` - Service interaction applies business logic or orchestration.
- L0162: `` - Blank line used to separate logical blocks for readability.
- L0163: `        assertTrue(response.isSuccess());` - Implements part of the file's concrete application logic.
- L0164: `        assertEquals("If an account exists with this email, a reset link will be sent.", response.getMessage());` - Credential or recovery logic for authentication safety.
- L0165: `    }` - Closes the current scope block.
- L0166: `` - Blank line used to separate logical blocks for readability.
- L0167: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0168: `    void updatePassword_whenTokenNotFound_throwsTokenValidationException() {` - Opens a new scope block for type, method, or control flow.
- L0169: `        UpdatePasswordRequest request = new UpdatePasswordRequest();` - Credential or recovery logic for authentication safety.
- L0170: `        request.setToken("reset-token");` - Security-related logic for tokens, OAuth, or authentication state.
- L0171: `        request.setNewPassword("Password2");` - Credential or recovery logic for authentication safety.
- L0172: `` - Blank line used to separate logical blocks for readability.
- L0173: `        when(tokenHashService.hash(request.getToken())).thenReturn("reset-token-hash");` - Service interaction applies business logic or orchestration.
- L0174: `        when(userService.findByResetToken("reset-token-hash")).thenReturn(Optional.empty());` - Service interaction applies business logic or orchestration.
- L0175: `` - Blank line used to separate logical blocks for readability.
- L0176: `        assertThrows(TokenValidationException.class, () -> authService.updatePassword(request));` - Service interaction applies business logic or orchestration.
- L0177: `    }` - Closes the current scope block.
- L0178: `` - Blank line used to separate logical blocks for readability.
- L0179: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0180: `    void changePassword_whenCurrentPasswordDoesNotMatch_throwsBadCredentials() {` - Opens a new scope block for type, method, or control flow.
- L0181: `        ChangePasswordRequest request = new ChangePasswordRequest();` - Credential or recovery logic for authentication safety.
- L0182: `        request.setCurrentPassword("wrong-current");` - Credential or recovery logic for authentication safety.
- L0183: `        request.setNewPassword("Password2");` - Credential or recovery logic for authentication safety.
- L0184: `` - Blank line used to separate logical blocks for readability.
- L0185: `        User user = new User();` - Implements part of the file's concrete application logic.
- L0186: `        user.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0187: `        user.setPassword("stored-password");` - Credential or recovery logic for authentication safety.
- L0188: `` - Blank line used to separate logical blocks for readability.
- L0189: `        when(userService.findByEmail("alice@example.com")).thenReturn(Optional.of(user));` - Service interaction applies business logic or orchestration.
- L0190: `        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);` - Credential or recovery logic for authentication safety.
- L0191: `` - Blank line used to separate logical blocks for readability.
- L0192: `        assertThrows(BadCredentialsException.class,` - Implements part of the file's concrete application logic.
- L0193: `                () -> authService.changePassword("alice@example.com", request));` - Service interaction applies business logic or orchestration.
- L0194: `    }` - Closes the current scope block.
- L0195: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## File 66: `backend/src/test/java/com/auth/service/impl/UserServiceImplTest.java`

### Purpose
- This file is part of the layered backend implementation.
- The walkthrough below explains each line and what role it plays.
- Total lines in this file: 45

### Line-by-Line Walkthrough
- L0001: `package com.auth.service.impl;` - Declares the package namespace so the class resolves in the correct module.
- L0002: `` - Blank line used to separate logical blocks for readability.
- L0003: `import com.auth.entity.User;` - Imports a type required by this file to compile and run.
- L0004: `import com.auth.exception.ResourceNotFoundException;` - Imports a type required by this file to compile and run.
- L0005: `import com.auth.repository.UserRepository;` - Imports a type required by this file to compile and run.
- L0006: `import org.junit.jupiter.api.Test;` - Imports a type required by this file to compile and run.
- L0007: `import org.junit.jupiter.api.extension.ExtendWith;` - Imports a type required by this file to compile and run.
- L0008: `import org.mockito.InjectMocks;` - Imports a type required by this file to compile and run.
- L0009: `import org.mockito.Mock;` - Imports a type required by this file to compile and run.
- L0010: `import org.mockito.junit.jupiter.MockitoExtension;` - Imports a type required by this file to compile and run.
- L0011: `` - Blank line used to separate logical blocks for readability.
- L0012: `import java.util.Optional;` - Imports a type required by this file to compile and run.
- L0013: `` - Blank line used to separate logical blocks for readability.
- L0014: `import static org.junit.jupiter.api.Assertions.assertEquals;` - Imports a type required by this file to compile and run.
- L0015: `import static org.junit.jupiter.api.Assertions.assertThrows;` - Imports a type required by this file to compile and run.
- L0016: `import static org.mockito.Mockito.when;` - Imports a type required by this file to compile and run.
- L0017: `` - Blank line used to separate logical blocks for readability.
- L0018: `@ExtendWith(MockitoExtension.class)` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0019: `class UserServiceImplTest {` - Defines the core type and responsibility boundary for this file.
- L0020: `` - Blank line used to separate logical blocks for readability.
- L0021: `    @Mock` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0022: `    private UserRepository userRepository;` - Repository usage handles persistence access to database records.
- L0023: `` - Blank line used to separate logical blocks for readability.
- L0024: `    @InjectMocks` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0025: `    private UserServiceImpl userService;` - Service interaction applies business logic or orchestration.
- L0026: `` - Blank line used to separate logical blocks for readability.
- L0027: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0028: `    void getUserByEmail_whenUserExists_returnsUser() {` - Opens a new scope block for type, method, or control flow.
- L0029: `        User user = new User();` - Implements part of the file's concrete application logic.
- L0030: `        user.setEmail("alice@example.com");` - Implements part of the file's concrete application logic.
- L0031: `` - Blank line used to separate logical blocks for readability.
- L0032: `        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));` - Repository usage handles persistence access to database records.
- L0033: `` - Blank line used to separate logical blocks for readability.
- L0034: `        User result = userService.getUserByEmail("alice@example.com");` - Service interaction applies business logic or orchestration.
- L0035: `` - Blank line used to separate logical blocks for readability.
- L0036: `        assertEquals(user, result);` - Implements part of the file's concrete application logic.
- L0037: `    }` - Closes the current scope block.
- L0038: `` - Blank line used to separate logical blocks for readability.
- L0039: `    @Test` - Annotation used by Spring, Lombok, validation, or JPA to configure behavior.
- L0040: `    void getUserByEmail_whenUserMissing_throwsResourceNotFoundException() {` - Opens a new scope block for type, method, or control flow.
- L0041: `        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());` - Repository usage handles persistence access to database records.
- L0042: `` - Blank line used to separate logical blocks for readability.
- L0043: `        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("missing@example.com"));` - Service interaction applies business logic or orchestration.
- L0044: `    }` - Closes the current scope block.
- L0045: `}` - Closes the current scope block.

### File Integration Notes
1. Verify this file compiles before moving to the next file.
2. Keep package names and imports exactly aligned with your folder structure.
3. Run tests after completing each major package group.

---

## Final Verification Checklist
1. Run `mvn test` from the `backend` directory.
2. Start backend and verify auth endpoints manually.
3. Validate JWT login, refresh, logout, and OAuth callback flows.
4. Confirm admin/user authorization boundaries with role-based endpoints.
5. Confirm password reset and OTP verification flows end-to-end.

## Maintenance Notes
1. Regenerate this guide whenever major backend files are added or removed.
2. Keep all secret values in environment variables or deployment secret stores.
3. Add new tests before refactoring authentication and authorization flows.
