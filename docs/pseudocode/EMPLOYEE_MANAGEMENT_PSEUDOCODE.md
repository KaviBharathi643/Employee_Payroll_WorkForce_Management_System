# EMPLOYEE_MANAGEMENT_PSEUDOCODE.md

# MODULE 2 - EMPLOYEE MANAGEMENT

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage:

* Employee Accounts
* HR Accounts
* Personal Information
* Employment Information
* Bank Information
* Employee Status

The module provides:

* Create Employee
* Create HR
* View Employee
* View HR
* Update Employee
* Update HR
* Deactivate Employee
* Deactivate HR
* Update Own Profile
* Update Own Bank Details

---

# DATABASE TABLES USED

users

user_profiles

user_employment

user_bank_details

notifications

---

# RELATED ENTITIES

User

UserProfile

UserEmployment

UserBankDetails

Notification

---

# ROLE PERMISSIONS

ADMIN

Can:

* Create HR
* View HR
* Update HR
* Deactivate HR

Cannot:

* Create Employee

---

HR

Can:

* Create Employee
* View Employee
* Update Employee
* Deactivate Employee

Cannot:

* Create HR

---

EMPLOYEE

Can:

* View Own Profile
* Update Own Profile
* Update Own Bank Details

Cannot:

* Create Employee
* Update Other Employees

---

# BUSINESS RULES

Rule 1

Employee cannot self register.

---

Rule 2

HR cannot self register.

---

Rule 3

Employee IDs never reused.

---

Rule 4

HR IDs never reused.

---

Rule 5

Employees cannot be deleted.

Only deactivated.

---

Rule 6

HR cannot be deleted.

Only deactivated.

---

Rule 7

Bank details mandatory before payroll.

---

Rule 8

Account number visible only to owner.

Others see masked value.

---

Rule 9

Employment Type:

FULL_TIME

INTERN

Only.

Rule 10

Users may upload profile picture.

Supported Formats:

JPG
JPEG
PNG

Maximum Size:

2 MB

---

# EMPLOYEE CODE GENERATION

FORMAT

EMP001

EMP002

EMP003

---

FUNCTION generateEmployeeCode()

FETCH latest employee code

IF no employee exists

RETURN EMP001

ELSE

extract numeric part

increment by 1

RETURN new employee code

---

# HR CODE GENERATION

FORMAT

HR001

HR002

HR003

---

FUNCTION generateHrCode()

FETCH latest HR code

IF no HR exists

RETURN HR001

ELSE

extract numeric part

increment by 1

RETURN new HR code

---

# CREATE EMPLOYEE WORKFLOW

ROLE REQUIRED

HR

---

INPUT

email

fullName

phone

address

department

designation

joiningDate

employmentType

basicSalary

---

FUNCTION createEmployee()

STEP 1

Validate request.

---

STEP 2

Check email uniqueness.

userRepository.existsByEmail(email)

IF true

RETURN

"Email Already Exists"

---

STEP 3

Generate employee code.

employeeCode =
generateEmployeeCode()

---

STEP 4

Create User

role = EMPLOYEE

status = ACTIVE

password = BCrypt(defaultPassword)

SAVE user

---

STEP 5

Create UserProfile

userId

employeeCode

fullName

phone

address

profilePhoto = NULL

SAVE

---

STEP 6

Create UserEmployment

userId

department

designation

joiningDate

employmentType

basicSalary

employmentStatus = ACTIVE

reportingManagerId = currentHrId

SAVE

---

STEP 7

Create notification.

---

STEP 8

Send account creation email.

---

STEP 9

RETURN success.

---

# CREATE HR WORKFLOW

ROLE REQUIRED

ADMIN

---

INPUT

email

fullName

phone

address

department

designation

joiningDate

basicSalary

---

FUNCTION createHr()

STEP 1

Validate request.

---

STEP 2

Check email uniqueness.

---

STEP 3

Generate HR code.

hrCode =
generateHrCode()

---

STEP 4

Create User

role = HR

status = ACTIVE

password = BCrypt(defaultPassword)

SAVE

---

STEP 5

Create UserProfile

SAVE

---

STEP 6

Create UserEmployment

employmentType = FULL_TIME

reportingManagerId = NULL

SAVE

---

STEP 7

Notification

---

STEP 8

Email

---

STEP 9

RETURN success

---

# VIEW EMPLOYEE LIST

ROLE REQUIRED

HR

---

FUNCTION getEmployees()

RETURN

ACTIVE employees only

with pagination

search

filter

sorting

---

SEARCH FIELDS

employeeCode

fullName

email

department

designation

---

# VIEW HR LIST

ROLE REQUIRED

ADMIN

---

FUNCTION getHrList()

RETURN

ACTIVE HR accounts

---

# VIEW EMPLOYEE DETAILS

ROLE REQUIRED

HR

---

FUNCTION getEmployeeDetails(employeeId)

FETCH

User

UserProfile

UserEmployment

UserBankDetails

RETURN combined response

---

# VIEW OWN PROFILE

ROLE REQUIRED

EMPLOYEE

HR

ADMIN

---

FUNCTION getMyProfile()

FETCH current logged user

RETURN profile details

---

# UPDATE EMPLOYEE

ROLE REQUIRED

HR

---

FUNCTION updateEmployee()

Allowed Fields

fullName

phone

address

department

designation

basicSalary

employmentType

---

RULE

Salary changes apply from current payroll month onward.

---

SAVE changes

RETURN success

---

# UPDATE HR

ROLE REQUIRED

ADMIN

---

FUNCTION updateHr()

Allowed Fields

fullName

phone

address

department

designation

basicSalary

---

SAVE

RETURN success

---

# UPDATE OWN PROFILE

ROLE REQUIRED

EMPLOYEE

HR

ADMIN

---

FUNCTION updateMyProfile()

Allowed Fields

fullName

phone

address

profilePhoto

---

SAVE

RETURN success

---

# UPDATE BANK DETAILS

ROLE REQUIRED

EMPLOYEE

HR

---

INPUT

bankName

accountNumber

ifscCode

---

FUNCTION updateBankDetails()

STEP 1

Validate fields.

IF any field missing

RETURN validation error

---

STEP 2

Create or update bank record.

---

STEP 3

SAVE

---

STEP 4

RETURN success

---

# DEACTIVATE EMPLOYEE

ROLE REQUIRED

HR

---

FUNCTION deactivateEmployee(employeeId)

user.status = INACTIVE

employmentStatus = INACTIVE

SAVE

---

RETURN success

---

# DEACTIVATE HR

ROLE REQUIRED

ADMIN

---

FUNCTION deactivateHr(hrId)

user.status = INACTIVE

employmentStatus = INACTIVE

SAVE

---

RETURN success

---

# BANK DETAIL MASKING

FUNCTION maskAccountNumber(accountNumber)

Example

123456789012

RETURN

XXXXXXXX9012

---

RULES

Owner

Full value

---

HR viewing Employee

Masked

---

Admin viewing HR

Masked

---

# upload profile photo

FUNCTION uploadProfilePhoto()

ROLE REQUIRED

EMPLOYEE
HR
ADMIN

STEP 1

Validate file type.

STEP 2

Validate file size.

STEP 3

Generate file name.

STEP 4

Store file.

backend/uploads/profile/

STEP 5

Update

profile_photo_url

STEP 6

SAVE

STEP 7

RETURN success

------
# deleteProfilePhoto

FUNCTION deleteProfilePhoto()

ROLE REQUIRED

EMPLOYEE
HR
ADMIN

STEP 1

Remove file.

STEP 2

Set

profile_photo_url = NULL

STEP 3

SAVE

STEP 4

RETURN success

# API LIST

POST

/api/employees

Create Employee

---

POST

/api/employees/hr

Create HR

---

POST

/api/profile/photo

Upload Profile Photo

---

DELETE

/api/profile/photo

Delete Profile Photo

---

GET

/api/employees

Employee List

---

GET

/api/employees/{id}

Employee Details

---

GET

/api/employees/hr

HR List

---

PUT

/api/employees/{id}

Update Employee

---

PUT

/api/employees/hr/{id}

Update HR

---

PUT

/api/employees/profile

Update Own Profile

---

PUT

/api/employees/bank-details

Update Bank Details

---

PATCH

/api/employees/{id}/deactivate

Deactivate Employee

---

PATCH

/api/employees/hr/{id}/deactivate

Deactivate HR

---

# VALIDATION RULES

Email Required

Full Name Required

Phone Required

Address Required

Department Required

Designation Required

Joining Date Required

Salary Required

Employment Type Required

Bank Name Required

Account Number Required

IFSC Required

Unique Email Required

---

# profile picture DTO

UpdateProfilePhotoRequestDto

MultipartFile profilePhoto

---

# EXCEPTION SCENARIOS

Email Already Exists

Employee Not Found

HR Not Found

Unauthorized Access

Inactive Account

Invalid Employment Type

Bank Details Missing

---

# SUCCESS OUTPUTS

Employee Created Successfully

HR Created Successfully

Employee Updated Successfully

HR Updated Successfully

Bank Details Updated Successfully

Employee Deactivated Successfully

HR Deactivated Successfully

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION
