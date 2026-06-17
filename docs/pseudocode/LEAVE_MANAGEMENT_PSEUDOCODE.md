# LEAVE_MANAGEMENT_PSEUDOCODE.md

# MODULE 4 - LEAVE MANAGEMENT

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage:

* Leave Applications
* Leave Approval
* Leave Rejection
* Leave Cancellation
* Leave Balance Calculation
* Leave Reports
* Leave Notifications

---

# DATABASE TABLES USED

leave_requests

users

user_profiles

user_employment

notifications

attendance

---

# RELATED ENTITIES

LeaveRequest

User

UserProfile

UserEmployment

Notification

Attendance

---

# ROLE PERMISSIONS

EMPLOYEE

Can:

* Apply Leave
* Cancel Own Leave
* View Own Leave History
* View Leave Balance

---

HR

Can:

* Apply Leave
* Approve Employee Leave
* Reject Employee Leave
* View Employee Leave Records

---

ADMIN

Can:

* Approve HR Leave
* Reject HR Leave
* View HR Leave Records

---

# BUSINESS RULES

Rule 1

Annual Paid Leave = 25 Days

---

Rule 2

Paid Leave Pool Includes:

CASUAL

SICK

---

Rule 3

Unused Leave Lost At Year End.

No Carry Forward.

---

Rule 4

Leave Reason Mandatory.

---

Rule 5

Past-Date Leave Not Allowed.

---

Rule 6

Today Leave Allowed.

---

Rule 7

Employee Cannot Apply Leave
For Dates Already Covered
By Approved Leave.

---

Rule 8

Weekend Days

Saturday

Sunday

Do Not Reduce Leave Balance.

---

Rule 9

Employee Leave Approval

Employee
↓
HR

---

Rule 10

HR Leave Approval

HR
↓
Admin

---

Rule 11

Leave Can Be Cancelled.

---

Rule 12

Approved Leave Can Be Converted
To Unpaid Leave.

---

# LEAVE TYPES

CASUAL

SICK

UNPAID

---

# DURATION TYPES

HALF_DAY

FULL_DAY

MULTI_DAY

---

# LEAVE STATUS

PENDING

APPROVED

REJECTED

CANCELLED

---

# APPLY LEAVE WORKFLOW

ROLE REQUIRED

EMPLOYEE

OR

HR

---

INPUT

leaveType

durationType

startDate

endDate

reason

---

FUNCTION applyLeave()

STEP 1

Validate fields.

---

IF reason is empty

RETURN

"Leave Reason Required"

---

STEP 2

Validate dates.

IF startDate < currentDate

RETURN

"Past Leave Not Allowed"

---

STEP 3

Check overlap.

existingLeave =
leaveRequestRepository
.findApprovedLeaveBetweenDates(
userId,
startDate,
endDate
)

IF exists

RETURN

"Leave Already Exists For Selected Date"

---

STEP 4

Check current leave status.

IF user already has approved leave
for today

RETURN

"You Are Already In Leave On That Date"

---

STEP 5

Calculate leaveDays.

leaveDays =
calculateLeaveDays(
startDate,
endDate,
durationType
)

---

STEP 6

Create leave request.

leaveRequest.userId = userId

leaveRequest.leaveType = leaveType

leaveRequest.durationType = durationType

leaveRequest.startDate = startDate

leaveRequest.endDate = endDate

leaveRequest.leaveDays = leaveDays

leaveRequest.reason = reason

leaveRequest.status = PENDING

SAVE

---

STEP 7

Create notification for approver.

---

STEP 8

RETURN success

---

# CALCULATE LEAVE DAYS

FUNCTION calculateLeaveDays()

IF durationType == HALF_DAY

RETURN 0.5

---

IF durationType == FULL_DAY

RETURN 1

---

IF durationType == MULTI_DAY

days = 0

FOR each date

BETWEEN startDate and endDate

IF date is Saturday

SKIP

---

IF date is Sunday

SKIP

---

days++

END LOOP

RETURN days

---

# APPROVE LEAVE

FUNCTION approveLeave(leaveId)

STEP 1

Fetch leave request.

---

STEP 2

Validate status.

IF status != PENDING

RETURN error

---

STEP 3

Determine approver.

IF applicant role == EMPLOYEE

approver = HR

---

IF applicant role == HR

approver = ADMIN

---

STEP 4

Update status.

status = APPROVED

approvedBy = currentUserId

SAVE

---

STEP 5

Create notification.

---

STEP 6

Send email.

---

STEP 7

RETURN success

---

# REJECT LEAVE

FUNCTION rejectLeave(leaveId)

STEP 1

Fetch leave request.

---

STEP 2

Validate status.

IF status != PENDING

RETURN error

---

STEP 3

status = REJECTED

approvedBy = currentUserId

SAVE

---

STEP 4

Notification

---

STEP 5

Email

---

STEP 6

RETURN success

---

# CANCEL LEAVE

ROLE REQUIRED

Owner Of Leave

---

FUNCTION cancelLeave(leaveId)

STEP 1

Fetch leave.

---

STEP 2

Verify ownership.

---

STEP 3

status = CANCELLED

cancelledAt = currentTimestamp

SAVE

---

STEP 4

Notification

---

STEP 5

RETURN success

---

# CONVERT TO UNPAID LEAVE

ROLE REQUIRED

HR

OR

ADMIN

---

FUNCTION convertToUnpaidLeave(leaveId)

STEP 1

Fetch leave.

---

STEP 2

leaveType = UNPAID

SAVE

---

STEP 3

Notification

---

STEP 4

RETURN success

---

# LEAVE BALANCE CALCULATION

FUNCTION getLeaveBalance(userId)

paidLeaveUsed =

SUM

CASUAL

*

SICK

WHERE status = APPROVED

---

remainingPaidLeave =

25 - paidLeaveUsed

---

unpaidLeaveUsed =

SUM

UNPAID

WHERE status = APPROVED

---

RETURN

paidLeaveUsed

remainingPaidLeave

unpaidLeaveUsed

---

# LEAVE HISTORY

FUNCTION getMyLeaveHistory()

FETCH user leave records

ORDER BY createdAt DESC

RETURN list

---

# HR VIEW EMPLOYEE LEAVE

FUNCTION getEmployeeLeaveHistory(
employeeId
)

RETURN leave records

---

# ADMIN VIEW HR LEAVE

FUNCTION getHrLeaveHistory(
hrId
)

RETURN leave records

---

# LEAVE REPORT

FUNCTION generateLeaveReport()

INPUT

fromDate

toDate

userId(optional)

---

FETCH leave records

CALCULATE balances

GENERATE PDF

RETURN file

---

# PDF REPORT COLUMNS

SNo

Date

Leave Type

Duration Type

Leave Days

Remaining Paid Leave

Total Paid Leave Used

Total Unpaid Leave Used

---

# DASHBOARD SUMMARY

EMPLOYEE

Display

Paid Leave Remaining

Paid Leave Used

Unpaid Leave Used

---

HR

Display

Pending Leave Requests

Approved Leave Requests

Rejected Leave Requests

---

ADMIN

Display

Pending HR Leave Requests

Approved HR Leave Requests

Rejected HR Leave Requests

---

# API LIST

POST

/api/leaves

Apply Leave

---

PUT

/api/leaves/{id}/approve

Approve Leave

---

PUT

/api/leaves/{id}/reject

Reject Leave

---

PUT

/api/leaves/{id}/cancel

Cancel Leave

---

PUT

/api/leaves/{id}/convert-unpaid

Convert To Unpaid

---

GET

/api/leaves/my-history

---

GET

/api/leaves/balance

---

GET

/api/leaves/employee/{id}

---

GET

/api/leaves/hr/{id}

---

GET

/api/leaves/report

---

# DTO STRUCTURE

ApplyLeaveRequestDto

leaveType

durationType

startDate

endDate

reason

---

ApproveLeaveRequestDto

leaveId

---

RejectLeaveRequestDto

leaveId

---

CancelLeaveRequestDto

leaveId

---

LeaveResponseDto

leaveId

leaveType

durationType

startDate

endDate

leaveDays

status

reason

---

LeaveBalanceResponseDto

paidLeaveUsed

remainingPaidLeave

unpaidLeaveUsed

---

# VALIDATION RULES

Leave Type Required

Duration Type Required

Reason Required

Start Date Required

End Date Required

No Past Leave

No Overlapping Leave

Weekend Excluded

Approver Required

---

# EXCEPTION SCENARIOS

Leave Not Found

Unauthorized Approval

Unauthorized Cancellation

Past Date Leave

Duplicate Leave

Invalid Leave Type

Invalid Duration Type

---

# SUCCESS OUTPUTS

Leave Applied Successfully

Leave Approved Successfully

Leave Rejected Successfully

Leave Cancelled Successfully

Leave Converted To Unpaid Successfully

Leave Report Generated Successfully

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION
