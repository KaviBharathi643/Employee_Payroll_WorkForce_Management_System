# PAYROLL_MANAGEMENT_PSEUDOCODE.md

# MODULE 5 - PAYROLL MANAGEMENT

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage:

* Payroll Generation
* Payroll Calculation
* PF Deduction
* Bonus Handling
* Salary Credit
* Payroll History
* Payroll Dashboard
* Payroll Notifications

---

# DATABASE TABLES USED

payrolls

users

user_profiles

user_employment

user_bank_details

leave_requests

payslips

notifications

system_settings

---

# RELATED ENTITIES

Payroll

User

UserProfile

UserEmployment

UserBankDetails

LeaveRequest

Payslip

Notification

SystemSettings

---

# ROLE PERMISSIONS

HR

Can:

* Generate Employee Payroll
* Credit Employee Salary
* View Employee Payroll

---

ADMIN

Can:

* Generate HR Payroll
* Credit HR Salary
* View HR Payroll

---

EMPLOYEE

Can:

* View Own Payroll
* View Payroll History

---

# BUSINESS RULES

Rule 1

One payroll per user per month.

---

Rule 2

Payroll generation does not credit salary.

---

Rule 3

Salary credit requires manual action.

---

Rule 4

Only ACTIVE employees included.

---

Rule 5

Bank details mandatory.

---

Rule 6

Payroll history never changes.

Payroll uses snapshot storage.

---

Rule 7

PF deducted from salary.

---

Rule 8

Bonus optional.

---

Rule 9

Payslip generated automatically.

---

Rule 10

Salary changes affect current payroll month.

---

Rule 11

Payroll month always represents the previous working month.

Example:

July Payroll Run

Processes June Payroll.

---

Rule 12

Bonus amount is applied equally to all users included in that payroll generation run.

---

# PAYROLL STATUS

GENERATED

CREDITED

---

# PAYROLL GENERATION WORKFLOW

ROLE REQUIRED

HR

(Employee Payroll)

OR

ADMIN

(HR Payroll)

---

INPUT

payrollYear

payrollMonth

bonusAmount

---

FUNCTION generatePayroll()

STEP 1

Validate payroll period.

---

STEP 2

Initialize

processedCount = 0

skippedCount = 0

skippedEmployeesList = []

---

STEP 3

Fetch active users.

Employee Payroll

role = EMPLOYEE

status = ACTIVE

---

HR Payroll

role = HR

status = ACTIVE

---

STEP 4

FOR EACH user

result = generateSinglePayroll()

IF success

processedCount++

ELSE

skippedCount++

ADD employeeCode

ADD employeeName

TO skippedEmployeesList

END LOOP

---

STEP 5

RETURN

processedCount

skippedCount

skippedEmployeesList

---

# GENERATE SINGLE PAYROLL

FUNCTION generateSinglePayroll(

userId,

payrollYear,

payrollMonth,

bonusAmount

)

STEP 1

Check payroll existence.

existingPayroll =

payrollRepository

.findByUserAndMonth(

userId,

payrollYear,

payrollMonth

)

IF exists

RETURN failure

---

STEP 2

Fetch employee data.

user

profile

employment

bankDetails

---

STEP 3

Validate bank details.

IF bankDetails missing

RETURN failure

---

STEP 4

basicSalary =

employment.basicSalary

---

STEP 5

pfPercentage =

systemSettings.pfPercentage

---

STEP 6

Calculate PF.

pfAmount =

basicSalary

*

pfPercentage

/

100

---

STEP 7

Calculate unpaid leave.

unpaidLeaveCount =

calculateUnpaidLeaveCount(

userId,

payrollYear,

payrollMonth

)

---

STEP 8

Calculate deduction.

dailySalary =

basicSalary / 30

unpaidLeaveDeduction =

dailySalary * unpaidLeaveCount

---

STEP 9

Calculate final salary.

finalSalary =

basicSalary

*

bonusAmount

*

pfAmount

*

unpaidLeaveDeduction

---

STEP 10

Create payroll snapshot.

userId

employeeCode

employeeName

department

designation

payrollYear

payrollMonth

basicSalary

bonus

pfPercentage

pfAmount

unpaidLeaveCount

unpaidLeaveDeduction

finalSalary

generatedDate

generatedBy

status = GENERATED

SAVE payroll

---

STEP 11

Generate payslip automatically.

CALL generatePayslip(

payrollId

)

---

STEP 12

Create notification.

title =

"Payroll Generated"

---

STEP 13

RETURN success

---

# CALCULATE UNPAID LEAVE

FUNCTION calculateUnpaidLeaveCount()

FETCH approved leave

WHERE

leaveType = UNPAID

AND

status = APPROVED

AND

month(startDate) = payrollMonth

AND

year(startDate) = payrollYear

---

SUM leaveDays

RETURN unpaidLeaveCount

---

# CREDIT SALARY

ROLE REQUIRED

HR

(Employee Payroll)

OR

ADMIN

(HR Payroll)

---

FUNCTION creditSalary(payrollId)

STEP 1

Fetch payroll.

---

STEP 2

Validate status.

IF payroll.status == CREDITED

RETURN

"Salary Already Credited"

---

STEP 3

Update payroll.

status = CREDITED

creditedDate = currentTimestamp

creditedBy = currentUserId

SAVE

---

STEP 4

Create notification.

title =

"Salary Credited"

---

STEP 5

Send email.

---

STEP 6

RETURN success

---

# PAYROLL HISTORY

FUNCTION getPayrollHistory(

userId

)

FETCH payrolls

ORDER BY

payrollYear DESC,

payrollMonth DESC

RETURN list

---

# GET PAYROLL DETAILS

FUNCTION getPayrollDetails(

payrollId

)

RETURN payroll snapshot

---

# DASHBOARD CALCULATIONS

HR DASHBOARD

Display

Total Salary Paid

Total Employees Paid

Total Unpaid Leave Deductions

Pending Salary Credits

---

ADMIN DASHBOARD

Display

Total HR Salary Paid

Total HR Paid

Total HR Unpaid Leave Deductions

Pending HR Salary Credits

---

EMPLOYEE DASHBOARD

Display

Latest Payroll

Salary Credited Status

Latest Payslip

---

# PAYROLL REPORT

FUNCTION generatePayrollReport()

INPUT

payrollYear

payrollMonth

---

FETCH payrolls

GENERATE PDF

RETURN file

---

# PAYROLL REMINDER SCHEDULER

RUN MONTHLY

CHECK previous month payroll.

IF payroll not generated

SEND dashboard reminder

SEND email reminder

TO

HR

OR

ADMIN

---

# API LIST

POST

/api/payrolls/generate

---

POST

/api/payrolls/{id}/credit

---

GET

/api/payrolls

---

GET

/api/payrolls/{id}

---

GET

/api/payrolls/history

---

GET

/api/payrolls/report

---

# DTO STRUCTURE

GeneratePayrollRequestDto

payrollYear

payrollMonth

bonusAmount

---

CreditSalaryRequestDto

payrollId

---

PayrollResponseDto

payrollId

payrollYear

payrollMonth

employeeCode

employeeName

department

designation

basicSalary

bonus

pfPercentage

pfAmount

unpaidLeaveCount

unpaidLeaveDeduction

finalSalary

status

generatedDate

creditedDate

---

PayrollSummaryResponseDto

totalSalaryPaid

totalEmployeesPaid

totalUnpaidLeaveDeductions

pendingSalaryCredits

---

# VALIDATION RULES

Payroll Month Required

Payroll Year Required

Bank Details Required

PF Percentage Required

One Payroll Per User Per Month

Only Active Users Included

---

# EXCEPTION SCENARIOS

Payroll Already Generated

Bank Details Missing

Employee Not Found

Payroll Not Found

Salary Already Credited

Unauthorized Access

---

# SUCCESS OUTPUTS

Payroll Generated Successfully

Payslip Generated Successfully

Salary Credited Successfully

Payroll Report Generated Successfully

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION
