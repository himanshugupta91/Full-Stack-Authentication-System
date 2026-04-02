# 🎓 Beginner's Guide: How This Authentication System Works Internally

Welcome! If you are a student or a beginner to full-stack development, authentication can seem like magic. This guide breaks down exactly how our system works, the technologies we chose, and the step-by-step flow of every major feature in plain English.

---

## 🛠️ The Technology Stack (What we use and why)

Think of a web application like a restaurant. 

1. **Frontend (React & Vite)**: The dining room. It’s what the user sees, clicks, and interacts with. It holds the temporary state (like remembering you are logged in right now).
2. **Backend (Spring Boot & Java)**: The kitchen. It processes requests, checks rules (like "is this password long enough?"), and prepares the data to send back to the user.
3. **Database (PostgreSQL)**: The main filing cabinet. It permanently stores user accounts, emails, and hashed passwords. It is reliable but setting up connections to it takes a tiny bit of time.
4. **Cache (Redis)**: The bouncer's notepad. It is an "in-memory" database, meaning it is incredibly fast. We use it to quickly count things, like "How many times did this IP address fail to log in over the last 5 minutes?"

---

## 📖 How Every Feature Works Internally (Step-by-Step)

Here is exactly what happens behind the scenes when a user clicks a button.

### 1. Registration & OTP (Email Verification)
*Goal: Create an account, safely store the password, and prove the user owns the email.*

* **Step 1: The Request.** The user types their email and password into React. React sends this as a JSON package to Spring Boot.
* **Step 2: Verification.** Spring Boot checks PostgreSQL to see if the email already exists. It also checks if the password meets our security rules (e.g., has numbers and symbols).
* **Step 3: Hashing.** We **never** save real passwords. Instead, we put the password through a math function called `BCrypt`. It jumbles the password into a random string (a "hash"). Even if a hacker steals the database, they only see the hashes, not the real passwords.
* **Step 4: The Account is Asleep.** The user is saved to PostgreSQL, but their account state is marked as `DISABLED`. They cannot log in yet.
* **Step 5: Generating the OTP.** The backend generates a random 6-digit number (One Time Password). Just like passwords, we *hash* this OTP before saving it to PostgreSQL, so even developers can't peek at it.
* **Step 6: Sending the Email.** We send the real 6-digit number to the user's email. When the user types it in, we hash what they typed and compare it to the hash in our database. If they match, the account flips to `ENABLED`!

### 2. Login, JWT Access Tokens & Refresh Tokens
*Goal: Keep a user logged in securely without making the database work too hard.*

* **Step 1: The Check.** The user submits their email and password. We pull the user from PostgreSQL and check if the password matches the hash.
* **Step 2: The Access Token (The "Wristband").** If successful, Spring Boot creates a **JSON Web Token (JWT)**. Think of this like a paper wristband at a concert. It expires quickly (e.g., 15 minutes), and the frontend attaches it to every API request so the backend knows who is asking for data.
* **Step 3: The Refresh Token (The "VIP Card").** Because the wristband expires quickly, we also create a Refresh Token. This lasts much longer (e.g., 7 days). 
* **Step 4: The Secure Cookie.** We send the Refresh Token to the browser inside a special box called an **HttpOnly Cookie**. This is critical! By making it `HttpOnly`, malicious JavaScript (created by hackers) cannot read the cookie. Only the browser can send it back to the server.
* **Step 5: Silent Refreshing.** When the 15-minute Access Token expires, React notices. Without bothering the user, React silently asks the backend for a new Access Token, sending the secure Refresh cookie as proof of identity.
* **Step 6: Token Rotation (Ultimate Security).** Every time a Refresh token is used to get a new Access Token, we *delete the old Refresh token* and give the browser a brand new one. If a hacker somehow steals a cookie, the moment the real user logs in, the hacker's stolen cookie becomes useless.

### 3. Rate Limiting & Lockout Protection (The Bouncer)
*Goal: Stop bots from guessing passwords millions of times a second.*

* **Step 1: The Redis Counter.** Every time someone tries to log in and fails, the backend shouts over to Redis: *"Hey, IP address 192.168.1.1 just failed!"*
* **Step 2: Tracking IP Addresses.** Redis keeps a fast tally. If an IP address fails 10 times in a row, Redis flags them. Spring Boot asks Redis before letting anyone try to log in. If the IP is flagged, Spring Boot immediately rejects the request without even checking PostgreSQL.
* **Step 3: Tracking Accounts.** Hackers might try to use thousands of different IP addresses to guess *one* user's password. So, Redis also tracks failures by *email address*. If `alice@example.com` has 5 failed attempts from anywhere in the world, the account is temporarily locked for 15 minutes.
* **Step 4: Spam Prevention.** We use Redis to limit OTP emails, too. If a user clicks "Resend Email" 5 times in 10 seconds, Redis blocks them. This prevents our email system from being used to spam people.

### 4. OAuth2 Social Login (Login with Google/GitHub)
*Goal: Let users skip making a password by trusting Google to verify who they are.*

* **Step 1: The Redirect.** The user clicks "Login with Google." React redirects the browser to our Spring Boot backend, which immediately redirects them to Google's official login page.
* **Step 2: The Secret Code.** After the user logs into Google and clicks "Approve", Google sends the browser back to our backend with a temporary, secret URL code.
* **Step 3: The Backchannel.** Our Spring Boot server takes that secret code and talks to Google's servers directly, behind the scenes. It trades the code for the user's profile info (Name and Email).
* **Step 4: The Local Proxy User.** We check our PostgreSQL database. Do we have a user with this email? If not, we create one instantly, skipping the OTP verification (since Google already verified their email).
* **Step 5: Normalization.** Once the "Google User" is matched to our database, we issue them the exact same **JWT** and **Refresh Cookie** that a normal password-user gets. From React's perspective, it doesn't matter how the user logged in!

### 5. Password Resets (Account Recovery)
*Goal: Let users securely reset a forgotten password without allowing hackers to see who has an account.*

* **Step 1: The Anti-Spying Rule.** A user submits their email to reset their password. Even if the email doesn't exist in our database, our backend *always* responds with: *"If that email exists, we sent a link."* If we told them "Email not found," hackers could use a script to guess millions of emails and figure out exactly who uses our application!
* **Step 2: The Secure Link.** If the email *does* exist, we generate a massive, random 64-character token. 
* **Step 3: Time-Bombed Hashing.** Like OTPs, we hash this token before storing it in PostgreSQL, and we put a strict expiration time on it (e.g., 5 minutes). We email the user the raw, unhashed link.
* **Step 4: The Reset.** The user clicks the link, types a new password in React, and submits it. The backend hashes the token from the URL, compares it to the database, and if it matches and isn't expired, it BCrypt hashes the *new* password and saves it.
* **Step 5: Global Logout.** Because the user forgot their password, we assume their account might be at risk. The moment the password changes, the backend deletes *all* their active Refresh Tokens in PostgreSQL. This immediately logs them out of any other laptops or phones they might have left logged in.

---

### 🎉 Summary for Students
Building a full-stack auth system is ultimately about **layers of security**:
1. You secure the **Database** by hashing passwords and tokens.
2. You secure the **Browser** by using HttpOnly cookies so JavaScript can't steal them.
3. You secure the **Network** by using short-lived JWTs.
4. You secure the **Server** by using Redis to block automated hacker bots.
