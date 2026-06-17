# AUTHENTICATION_PSEUDOCODE.md

# MODULE 1 - AUTHENTICATION & AUTHORIZATION

STATUS: FINALIZED

---

# MODULE PURPOSE

Provide secure authentication and authorization for:

* ADMIN
* HR
* EMPLOYEE

The module is responsible for:

* Login
* JWT Token Generation
* JWT Validation
* Role Based Access Control
* Forgot Password
* OTP Verification
* Password Reset
* Logout

---

# DATABASE TABLES USED

users

otp_verifications

---

# RELATED ENTITIES

User

OtpVerification

---

# USER ROLES

ADMIN

HR

EMPLOYEE

---

# BUSINESS RULES

Rule 1

Employee cannot self register.

---

Rule 2

HR cannot self register.

---

Rule 3

Only one default ADMIN account exists.

Created manually during system setup.

---

Rule 4

Admin creates HR accounts.

---

Rule 5

HR creates Employee accounts.

---

Rule 6

Only ACTIVE users can login.

---

Rule 7

INACTIVE users cannot login.

---

Rule 8

Passwords must be stored using BCrypt encryption.

---

Rule 9

JWT authentication required for all protected APIs.

---

Rule 10

Forgot Password uses:

6 Digit OTP

2 Minute Expiry

Email Delivery

---

# ROLE ACCESS MATRIX

ADMIN

Can access:

/api/admin/**

/api/settings/**

/api/reports/**

/api/notifications/**

---

HR

Can access:

/api/employees/**

/api/attendance/**

/api/leaves/**

/api/payrolls/**

/api/payslips/**

/api/reports/**

/api/notifications/**

---

EMPLOYEE

Can access:

Own Attendance

Own Leave

Own Payroll

Own Payslips

Own Profile

Own Notifications

---

# LOGIN WORKFLOW

INPUT

email

password

---

FUNCTION login(email, password)

STEP 1

Validate request fields.

IF email is empty

RETURN validation error

---

IF password is empty

RETURN validation error

---

STEP 2

Find user by email.

user = userRepository.findByEmail(email)

IF user not found

RETURN

"Invalid Email Or Password"

---

STEP 3

Check user status.

IF user.status != ACTIVE

RETURN

"Account Is Inactive"

---

STEP 4

Verify password.

passwordMatches = BCrypt.matches(
password,
user.password
)

IF passwordMatches == false

RETURN

"Invalid Email Or Password"

---

STEP 5

Generate JWT token.

token = jwtTokenProvider.generateToken(
user.id,
user.email,
user.role
)

---

STEP 6

Build response.

RETURN

token

role

userId

employeeCode

fullName

---

# JWT GENERATION FLOW

FUNCTION generateToken(user)

SET subject = user.email

SET claims

userId

role

email

---

SET issuedAt = currentTime

SET expirationTime = currentTime + JWT_EXPIRY

---

GENERATE signed JWT

RETURN token

---

# JWT VALIDATION FLOW

FUNCTION validateToken(token)

CHECK signature

CHECK expiration

CHECK subject

CHECK user exists

CHECK user status ACTIVE

IF any validation fails

RETURN unauthorized

ELSE

RETURN valid

---

# LOAD USER DETAILS

FUNCTION loadUserByUsername(email)

user = userRepository.findByEmail(email)

IF user not found

THROW UsernameNotFoundException

RETURN authenticatedUser

---

# FORGOT PASSWORD WORKFLOW

INPUT

email

---

FUNCTION sendOtp(email)

STEP 1

Find user.

user = userRepository.findByEmail(email)

IF user not found

RETURN

"User Not Found"

---

STEP 2

Generate OTP.

otpCode = random 6 digit number

---

STEP 3

Set expiry.

expiresAt = currentTime + 2 minutes

---

STEP 4

Save OTP.

CREATE otpVerification

userId = user.id

otpCode = otpCode

expiresAt = expiresAt

SAVE

---

STEP 5

Send Email.

emailService.sendOtpEmail(
user.email,
otpCode
)

---

STEP 6

RETURN

"OTP Sent Successfully"

---

# VERIFY OTP WORKFLOW

INPUT

email

otpCode

---

FUNCTION verifyOtp(email, otpCode)

STEP 1

Find user.

user = userRepository.findByEmail(email)

IF user not found

RETURN invalid

---

STEP 2

Get latest OTP.

otpRecord =
otpVerificationRepository.findLatestByUserId(
user.id
)

IF otpRecord not found

RETURN invalid

---

STEP 3

Check expiry.

IF currentTime > otpRecord.expiresAt

RETURN

"OTP Expired"

---

STEP 4

Compare OTP.

IF otpRecord.otpCode != otpCode

RETURN

"Invalid OTP"

---

STEP 5

RETURN success

---

# RESET PASSWORD WORKFLOW

INPUT

email

otpCode

newPassword

confirmPassword

---

FUNCTION resetPassword()

STEP 1

Verify OTP

CALL verifyOtp()

IF failed

RETURN error

---

STEP 2

Validate passwords.

IF newPassword != confirmPassword

RETURN error

---

STEP 3

Encrypt password.

encodedPassword =
passwordEncoder.encode(newPassword)

---

STEP 4

Update user password.

user.password = encodedPassword

SAVE user

---

STEP 5

Delete old OTP records.

otpVerificationRepository.deleteByUserId(
user.id
)

---

STEP 6

RETURN success

---

# LOGOUT WORKFLOW

FUNCTION logout()

Frontend removes:

JWT Token

User Context

Session Data

Redirect To Login

---

# AUTHORIZATION FLOW

FUNCTION checkAccess(userRole, requestedResource)

IF userRole == ADMIN

ALLOW admin resources

---

IF userRole == HR

ALLOW hr resources

---

IF userRole == EMPLOYEE

ALLOW employee resources

---

ELSE

DENY access

RETURN 403

---

# API LIST

POST

/api/auth/login

---

POST

/api/auth/forgot-password

---

POST

/api/auth/verify-otp

---

POST

/api/auth/reset-password

---

GET

/api/auth/me

---

# VALIDATION RULES

Email Required

Password Required

Valid Email Format Required

OTP Must Be 6 Digits

OTP Expires In 2 Minutes

Password Minimum Length = 8

Confirm Password Must Match

Only ACTIVE Users Can Login

---

# EXCEPTION SCENARIOS

Invalid Email

Invalid Password

Inactive Account

Expired OTP

Invalid OTP

User Not Found

JWT Expired

JWT Invalid

Unauthorized Access

Forbidden Access

---

# SUCCESS OUTPUTS

Successful Login

Returns:

JWT Token

User Role

User Information

---

Successful OTP Verification

Returns:

OTP Verified

---

Successful Password Reset

Returns:

Password Updated Successfully

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION
