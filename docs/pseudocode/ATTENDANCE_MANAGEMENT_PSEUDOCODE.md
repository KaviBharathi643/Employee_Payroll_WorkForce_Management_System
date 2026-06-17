# ATTENDANCE_MANAGEMENT_PSEUDOCODE.md

# MODULE 3 - ATTENDANCE MANAGEMENT

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage daily employee attendance.

The module is responsible for:

* Check In
* Check Out
* Attendance Tracking
* Attendance Status Calculation
* Missing Checkout Detection
* Attendance Dashboard Data
* Attendance Reports
* Attendance Notifications

---

# DATABASE TABLES USED

attendance

users

user_profiles

leave_requests

notifications

system_settings

---

# RELATED ENTITIES

Attendance

User

UserProfile

LeaveRequest

Notification

SystemSettings

---

# ROLE PERMISSIONS

EMPLOYEE

Can:

* Check In
* Check Out
* View Own Attendance
* View Own Attendance Summary

---

HR

Can:

* View Employee Attendance
* View Attendance Reports
* Monitor Missing Checkout Records

---

ADMIN

Can:

* View HR Attendance
* View Attendance Reports

---

# BUSINESS RULES

Rule 1

One attendance record per user per day.

---

Rule 2

One check-in allowed per day.

---

Rule 3

One check-out allowed per day.

---

Rule 4

Employee cannot check-out without check-in.

---

Rule 5

Saturday and Sunday are holidays.

Attendance not required.

---

Rule 6

Approved full-day leave overrides attendance.

---

Rule 7

Approved half-day leave requires attendance check-in.

---

Rule 8

Missing checkout after office end time + 1 hour triggers reminder.

---

Rule 9

Missing checkout penalty:

Half-day leave deduction.

---

# ATTENDANCE STATUS VALUES

PRESENT

ABSENT

LEAVE

HALF_DAY_LEAVE

---

# CHECK-IN WORKFLOW

ROLE REQUIRED

EMPLOYEE

---

INPUT

userId

---

FUNCTION checkIn()

STEP 1

today = currentDate

---

STEP 2

Check weekend.

IF today is Saturday OR Sunday

RETURN

"Attendance Not Required On Holidays"

---

STEP 3

Check existing attendance.

attendanceRecord =
attendanceRepository.findByUserIdAndDate(
userId,
today
)

IF attendanceRecord exists

RETURN

"Already Checked In"

---

STEP 4

Create attendance record.

attendance.userId = userId

attendance.attendanceDate = today

attendance.checkInTime = currentTimestamp

attendance.status = PRESENT

SAVE attendance

---

STEP 5

RETURN success

---

# CHECK-OUT WORKFLOW

ROLE REQUIRED

EMPLOYEE

---

INPUT

userId

---

FUNCTION checkOut()

STEP 1

today = currentDate

---

STEP 2

attendanceRecord =
attendanceRepository.findByUserIdAndDate(
userId,
today
)

IF attendanceRecord not found

RETURN

"Check In Required"

---

STEP 3

IF attendanceRecord.checkOutTime exists

RETURN

"Already Checked Out"

---

STEP 4

attendanceRecord.checkOutTime =
currentTimestamp

SAVE attendanceRecord

---

STEP 5

RETURN success

---

# DAILY ATTENDANCE STATUS CALCULATION

FUNCTION calculateAttendanceStatus(
userId,
attendanceDate
)

STEP 1

Check approved leave.

approvedLeave =
leaveRequestRepository
.findApprovedLeaveForDate(
userId,
attendanceDate
)

---

IF approvedLeave exists

IF approvedLeave.durationType == FULL_DAY

RETURN LEAVE

---

IF approvedLeave.durationType == HALF_DAY

attendanceRecord =
attendanceRepository.findByUserIdAndDate(
userId,
attendanceDate
)

IF attendanceRecord exists

RETURN HALF_DAY_LEAVE

ELSE

RETURN ABSENT

---

STEP 2

attendanceRecord =
attendanceRepository.findByUserIdAndDate(
userId,
attendanceDate
)

---

IF attendanceRecord exists

RETURN PRESENT

---

RETURN ABSENT

---

# MISSING CHECKOUT SCHEDULER

RUN DAILY

officeEndTime =
systemSettings.officeEndTime

reminderTime =
officeEndTime + 1 hour

---

FUNCTION processMissingCheckout()

STEP 1

Find attendance records.

attendanceList =
attendanceRepository.findTodayWithoutCheckout()

---

FOR EACH attendanceRecord

CREATE notification

title =
"Checkout Missing"

message =
"Please complete your checkout."

SAVE

---

SEND email

---

SEND notification to HR

---

END LOOP

---

# APPLY PENALTY FOR MISSING CHECKOUT

FUNCTION applyMissingCheckoutPenalty()

STEP 1

Find attendance records

without checkout.

---

STEP 2

FOR EACH record

CREATE leave deduction entry

leaveType = UNPAID

leaveDays = 0.5

reason =
"Missing Checkout Penalty"

---

SAVE

---

END LOOP

---

# GET MY ATTENDANCE

ROLE REQUIRED

EMPLOYEE

---

FUNCTION getMyAttendance()

FETCH current user attendance

RETURN attendance list

---

# GET ATTENDANCE BY EMPLOYEE

ROLE REQUIRED

HR

---

FUNCTION getEmployeeAttendance(
employeeId
)

FETCH attendance records

RETURN list

---

# ATTENDANCE SUMMARY

FUNCTION getAttendanceSummary(
userId
)

CALCULATE

presentCount

absentCount

leaveCount

halfDayLeaveCount

---

RETURN summary

---

# DASHBOARD CALCULATIONS

EMPLOYEE DASHBOARD

Display:

Present Days

Absent Days

Leave Days

Half-Day Leave Days

---

HR DASHBOARD

Display:

Total Employees Present

Total Employees Absent

Employees On Leave

Employees Missing Checkout

---

ADMIN DASHBOARD

Display:

HR Present

HR Absent

HR Leave

---

# ATTENDANCE REPORT

FUNCTION generateAttendanceReport()

INPUT

fromDate

toDate

employeeId (optional)

---

FETCH attendance records

CALCULATE status

GENERATE PDF

RETURN download file

---

# API LIST

POST

/api/attendance/check-in

---

POST

/api/attendance/check-out

---

GET

/api/attendance/me

---

GET

/api/attendance/employee/{id}

---

GET

/api/attendance/summary

---

GET

/api/attendance/report

---

# DTO STRUCTURE

CheckInRequestDto

No fields

Authenticated user only

---

CheckOutRequestDto

No fields

Authenticated user only

---

AttendanceResponseDto

attendanceDate

checkInTime

checkOutTime

status

---

AttendanceSummaryResponseDto

presentCount

absentCount

leaveCount

halfDayLeaveCount

---

AttendanceListResponseDto

attendanceDate

status

checkInTime

checkOutTime

---

# VALIDATION RULES

One Check-In Per Day

One Check-Out Per Day

Check-Out Requires Check-In

Attendance Not Allowed On Weekend

Only Employee Can Check-In

Only Employee Can Check-Out

---

# EXCEPTION SCENARIOS

Already Checked In

Already Checked Out

Attendance Record Not Found

Weekend Attendance

Unauthorized Access

Missing Checkout

---

# SUCCESS OUTPUTS

Check-In Successful

Check-Out Successful

Attendance Retrieved Successfully

Attendance Summary Generated

Attendance Report Generated

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION
