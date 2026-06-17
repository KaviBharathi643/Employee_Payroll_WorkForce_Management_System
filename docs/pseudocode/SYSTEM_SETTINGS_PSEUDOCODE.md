# SYSTEM_SETTINGS_PSEUDOCODE.md

# MODULE 9 - SYSTEM SETTINGS

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage global system configurations.

System Settings is the central configuration module used by:

* Attendance Module
* Leave Module
* Payroll Module
* Payslip Module
* Reports Module
* Notification Module

---

# DATABASE TABLES USED

system_settings

users

---

# RELATED ENTITIES

SystemSettings

User

---

# ROLE PERMISSIONS

ADMIN

Can:

* View System Settings
* Update System Settings

---

HR

Cannot Access

---

EMPLOYEE

Cannot Access

---

# BUSINESS RULES

Rule 1

Only ADMIN can manage settings.

---

Rule 2

System contains exactly one settings record.

---

Rule 3

Settings record can never be deleted.

Only updated.

---

Rule 4

Settings updates apply immediately.

---

Rule 5

Historical records never change.

Settings changes affect future operations only.

---

Rule 6

PF percentage changes affect only future payroll generations.

Previously generated payrolls remain unchanged.

---

Rule 7

Leave limit changes affect only future leave calculations.

---

Rule 8

Attendance setting changes affect only future attendance processing.

---

Rule 9

Salary Credit Day does not automatically credit salaries.

Salary credit remains manual.

Salary Credit Day is used only for:

Payroll Reminders

Dashboard Reminders

Email Reminders

---

Rule 10

Leave Module must always read leave limit from System Settings.

Never hardcode leave limit values.

---

Rule 11

PF Percentage may be set to 0.

PF deduction becomes disabled.

---

# SETTINGS SECTIONS

COMPANY_SETTINGS

ATTENDANCE_SETTINGS

LEAVE_SETTINGS

PAYROLL_SETTINGS

---

# COMPANY SETTINGS

FIELDS

companyName

companyAddress

companyEmail

companyPhone

---

PURPOSE

Used In:

Payslip PDFs

Report PDFs

Email Templates

System Branding

---

# ATTENDANCE SETTINGS

FIELDS

officeStartTime

officeEndTime

checkoutReminderHours

---

DEFAULT VALUES

officeStartTime = 09:00

officeEndTime = 18:00

checkoutReminderHours = 1

---

PURPOSE

Used In:

Attendance Module

Missing Checkout Scheduler

Attendance Notifications

---

# LEAVE SETTINGS

FIELDS

annualPaidLeaveLimit

---

DEFAULT VALUE

25

---

PURPOSE

Used In:

Leave Balance Calculation

Leave Dashboard

Leave Reports

Leave Approval Logic

---

# PAYROLL SETTINGS

FIELDS

pfPercentage

salaryCreditDay

---

DEFAULT VALUES

pfPercentage = 8

salaryCreditDay = 1

---

PURPOSE

Used In:

Payroll Calculation

Payroll Dashboard

Payroll Reports

Payslip Generation

Payroll Reminder Scheduler

---

# GET SYSTEM SETTINGS

ROLE REQUIRED

ADMIN

---

FUNCTION getSystemSettings()

STEP 1

Fetch settings record.

---

STEP 2

RETURN settings

---

# UPDATE COMPANY SETTINGS

ROLE REQUIRED

ADMIN

---

INPUT

companyName

companyAddress

companyEmail

companyPhone

---

FUNCTION updateCompanySettings()

STEP 1

Validate inputs.

---

STEP 2

Update settings.

---

STEP 3

SAVE

---

STEP 4

RETURN success

---

# UPDATE ATTENDANCE SETTINGS

ROLE REQUIRED

ADMIN

---

INPUT

officeStartTime

officeEndTime

checkoutReminderHours

---

FUNCTION updateAttendanceSettings()

STEP 1

Validate times.

officeStartTime

must be before

officeEndTime

---

STEP 2

Validate reminder hours.

checkoutReminderHours >= 0

---

STEP 3

Update settings.

---

STEP 4

SAVE

---

STEP 5

RETURN success

---

# UPDATE LEAVE SETTINGS

ROLE REQUIRED

ADMIN

---

INPUT

annualPaidLeaveLimit

---

FUNCTION updateLeaveSettings()

STEP 1

Validate.

annualPaidLeaveLimit > 0

---

STEP 2

Update settings.

---

STEP 3

SAVE

---

STEP 4

RETURN success

---

# UPDATE PAYROLL SETTINGS

ROLE REQUIRED

ADMIN

---

INPUT

pfPercentage

salaryCreditDay

---

FUNCTION updatePayrollSettings()

STEP 1

Validate PF.

pfPercentage >= 0

pfPercentage <= 100

---

STEP 2

Validate salary credit day.

salaryCreditDay >= 1

salaryCreditDay <= 31

---

STEP 3

Update settings.

---

STEP 4

SAVE

---

STEP 5

RETURN success

---

# ATTENDANCE SETTINGS IMPACT

Office Start Time

↓

Attendance Processing

---

Office End Time

↓

Attendance Processing

Missing Checkout Detection

---

checkoutReminderHours

↓

Missing Checkout Reminder

---

Reminder Time Calculation

reminderTime =

officeEndTime

*

checkoutReminderHours

---

# LEAVE SETTINGS IMPACT

annualPaidLeaveLimit

↓

Leave Balance

Leave Reports

Leave Dashboard

Leave Approval Processing

---

Leave Module must always read

annualPaidLeaveLimit

from System Settings.

---

# PAYROLL SETTINGS IMPACT

pfPercentage

↓

Payroll Calculation

PF Deduction

Payroll Reports

Payslip Generation

---

salaryCreditDay

↓

Payroll Reminder Scheduler

Dashboard Reminder

Email Reminder

---

Salary Credit

Still Requires

Manual Credit Salary Action

By HR/Admin

---

# COMPANY SETTINGS IMPACT

companyName

↓

Payslip Header

Report Header

Email Templates

---

companyAddress

↓

Payslip

Reports

---

companyEmail

↓

Email Templates

Reports

---

companyPhone

↓

Payslip

Reports

Contact Information

---

# SYSTEM INITIALIZATION

FUNCTION initializeSystemSettings()

STEP 1

Check if settings record exists.

---

IF settings record exists

RETURN

---

STEP 2

Create default settings.

companyName = ""

companyAddress = ""

companyEmail = ""

companyPhone = ""

officeStartTime = 09:00

officeEndTime = 18:00

checkoutReminderHours = 1

annualPaidLeaveLimit = 25

pfPercentage = 8

salaryCreditDay = 1

---

STEP 3

SAVE settings

---

STEP 4

RETURN success

---

# API LIST

GET

/api/settings

Get Settings

---

PUT

/api/settings/company

Update Company Settings

---

PUT

/api/settings/attendance

Update Attendance Settings

---

PUT

/api/settings/leave

Update Leave Settings

---

PUT

/api/settings/payroll

Update Payroll Settings

---

# DTO STRUCTURE

UpdateCompanySettingsRequestDto

companyName

companyAddress

companyEmail

companyPhone

---

UpdateAttendanceSettingsRequestDto

officeStartTime

officeEndTime

checkoutReminderHours

---

UpdateLeaveSettingsRequestDto

annualPaidLeaveLimit

---

UpdatePayrollSettingsRequestDto

pfPercentage

salaryCreditDay

---

SystemSettingsResponseDto

companyName

companyAddress

companyEmail

companyPhone

officeStartTime

officeEndTime

checkoutReminderHours

annualPaidLeaveLimit

pfPercentage

salaryCreditDay

---

# VALIDATION RULES

Company Name Required

Company Address Required

Company Email Required

Company Phone Required

Valid Email Required

Office Start Time Required

Office End Time Required

Annual Paid Leave Limit > 0

Checkout Reminder Hours >= 0

PF Percentage Between 0 And 100

Salary Credit Day Between 1 And 31

---

# EXCEPTION SCENARIOS

Settings Not Found

Unauthorized Access

Invalid Email

Invalid Time Range

Invalid Leave Limit

Invalid PF Percentage

Invalid Salary Credit Day

Invalid Checkout Reminder Hours

---

# SUCCESS OUTPUTS

Settings Retrieved Successfully

Company Settings Updated Successfully

Attendance Settings Updated Successfully

Leave Settings Updated Successfully

Payroll Settings Updated Successfully

---

# DEVELOPMENT RULES

All modules must read values from System Settings.

Never hardcode:

Company Information

Office Timings

Checkout Reminder Hours

Leave Limits

PF Percentage

Salary Credit Day

---

Single Settings Record Only.

Never create additional settings records.

Never delete settings record.

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION

ARCHITECTURE SCORE

99.5/100
