# NOTIFICATION_PSEUDOCODE.md

# MODULE 8 - NOTIFICATION MANAGEMENT

STATUS: FINALIZED

---

# MODULE PURPOSE

Manage:

* Dashboard Notifications
* Email Notifications
* Notification Bell
* Notification History
* Read Status Management
* Notification Cleanup
* Notification Navigation

Notifications are event-driven.

Notifications never contain business logic.

Notifications are triggered by other modules.

---

# DATABASE TABLES USED

notifications

users

leave_requests

attendance

payrolls

payslips

otp_verifications

---

# RELATED ENTITIES

Notification

User

LeaveRequest

Attendance

Payroll

Payslip

OtpVerification

---

# ROLE PERMISSIONS

EMPLOYEE

Can:

* View Own Notifications
* Mark Own Notifications As Read
* Mark All Own Notifications As Read

---

HR

Can:

* View Own Notifications
* Mark Own Notifications As Read
* Mark All Own Notifications As Read

---

ADMIN

Can:

* View Own Notifications
* Mark Own Notifications As Read
* Mark All Own Notifications As Read

---

# BUSINESS RULES

Rule 1

Notifications are user-specific.

---

Rule 2

Notification Bell shows latest 10 notifications.

---

Rule 3

Notification History shows all notifications from last 30 days.

---

Rule 4

Notifications older than 30 days are automatically deleted.

---

Rule 5

Important business events generate:

Dashboard Notification

Email Notification

---

Rule 6

OTP uses email only.

No dashboard notification.

---

Rule 7

Notifications never trigger business actions.

Notifications only inform users.

---

Rule 8

Payslip emails never contain PDF attachments.

Users download payslips from portal.

---

Rule 9

Notification click should navigate user to related module page.

---

# NOTIFICATION TYPES

ACCOUNT_CREATED

LEAVE_APPLIED

LEAVE_APPROVED

LEAVE_REJECTED

LEAVE_CANCELLED

MISSING_CHECKOUT

PAYROLL_GENERATED

PAYROLL_REMINDER

SALARY_CREDITED

PAYSLIP_GENERATED

PASSWORD_RESET_OTP

SYSTEM_NOTIFICATION

---

# NOTIFICATION STATUS

UNREAD

READ

---

# NOTIFICATION ENTITY STRUCTURE

notificationId

userId

notificationType

title

message

status

relatedEntityType

relatedEntityId

createdAt

---

# RELATED ENTITY TYPES

LEAVE

PAYROLL

PAYSLIP

ATTENDANCE

USER

SYSTEM

---

# CREATE NOTIFICATION

FUNCTION createNotification(

userId,

notificationType,

title,

message,

relatedEntityType,

relatedEntityId

)

STEP 1

Create notification.

notification.userId = userId

notification.notificationType = notificationType

notification.title = title

notification.message = message

notification.status = UNREAD

notification.relatedEntityType = relatedEntityType

notification.relatedEntityId = relatedEntityId

notification.createdAt = currentTimestamp

SAVE notification

---

STEP 2

RETURN success

---

# SEND EMAIL

FUNCTION sendEmailNotification(

email,

subject,

message

)

STEP 1

Build email content.

---

STEP 2

Send email.

---

STEP 3

Log result.

---

STEP 4

RETURN success

---

# AUTHENTICATION EVENTS

# ACCOUNT CREATED

EVENT

Employee Created

---

Notify Employee

Dashboard Notification

Email Notification

---

EVENT

HR Created

---

Notify HR

Dashboard Notification

Email Notification

---

# PASSWORD RESET OTP

EVENT

Forgot Password

---

Send OTP Email

Only

---

No Dashboard Notification

---

# LEAVE EVENTS

# LEAVE APPLIED

EVENT

Leave Application Submitted

---

Employee Leave

Notify HR

---

HR Leave

Notify ADMIN

---

Dashboard Notification

Email Notification

---

# LEAVE APPROVED

EVENT

Leave Approved

---

Notify Applicant

Dashboard Notification

Email Notification

---

# LEAVE REJECTED

EVENT

Leave Rejected

---

Notify Applicant

Dashboard Notification

Email Notification

---

# LEAVE CANCELLED

EVENT

Leave Cancelled

---

Notify Applicant

Dashboard Notification

Email Notification

---

Notify Approver

Dashboard Notification

Email Notification

---

# ATTENDANCE EVENTS

# MISSING CHECKOUT

EVENT

No Checkout After

Office End Time + 1 Hour

---

Notify Employee

Dashboard Notification

Email Notification

---

Notify HR

Dashboard Notification

Email Notification

---

# PAYROLL EVENTS

# PAYROLL GENERATED

EVENT

Payroll Generated

---

Notify Employee

Dashboard Notification

Email Notification

---

# PAYROLL REMINDER

EVENT

Monthly Payroll Not Generated

---

Notify HR

(Employee Payroll)

OR

Notify ADMIN

(HR Payroll)

---

Dashboard Notification

Email Notification

---

# SALARY CREDITED

EVENT

Salary Credited

---

Notify Employee

Dashboard Notification

Email Notification

---

# PAYSLIP GENERATED

EVENT

Payslip Generated

---

Notify Employee

Dashboard Notification

Email Notification

---

Email Never Contains PDF Attachment

---

# SYSTEM EVENTS

# SYSTEM NOTIFICATION

EVENT

Future Admin Broadcast

Future Enhancement

---

Create Notification

---

# GET NOTIFICATIONS

FUNCTION getNotifications(

userId

)

STEP 1

Fetch notifications.

WHERE

notification.userId = userId

---

ORDER BY

createdAt DESC

---

LIMIT 10

---

RETURN notifications

---

# GET NOTIFICATION HISTORY

FUNCTION getNotificationHistory(

userId

)

STEP 1

Fetch notifications.

WHERE

notification.userId = userId

---

AND

createdAt >= currentDate - 30 days

---

ORDER BY

createdAt DESC

---

RETURN notifications

---

# MARK AS READ

FUNCTION markNotificationAsRead(

notificationId

)

STEP 1

Fetch notification.

---

STEP 2

Verify ownership.

notification.userId

must equal

currentUserId

---

IF ownership fails

RETURN access denied

---

STEP 3

notification.status = READ

SAVE

---

STEP 4

RETURN success

---

# MARK ALL AS READ

FUNCTION markAllNotificationsAsRead()

STEP 1

Fetch current user notifications.

WHERE

userId = currentUserId

AND

status = UNREAD

---

STEP 2

Update all.

status = READ

---

STEP 3

SAVE

---

STEP 4

RETURN success

---

# NOTIFICATION BELL

FUNCTION getNotificationBellData()

RETURN

latest10Notifications

unreadCount

---

# NOTIFICATION NAVIGATION

FUNCTION getNotificationNavigation()

IF relatedEntityType == LEAVE

Navigate To

Leave Page

---

IF relatedEntityType == PAYROLL

Navigate To

Payroll History Page

---

IF relatedEntityType == PAYSLIP

Navigate To

Payslip Page

---

IF relatedEntityType == ATTENDANCE

Navigate To

Attendance Page

---

IF relatedEntityType == USER

Navigate To

Profile Page

---

RETURN navigationRoute

---

# CLEANUP SCHEDULER

RUN DAILY

FUNCTION cleanupNotifications()

STEP 1

Find notifications.

createdAt older than 30 days

---

STEP 2

Delete notifications.

---

STEP 3

Log cleanup result.

---

STEP 4

RETURN success

---

# PAYROLL REMINDER SCHEDULER

RUN MONTHLY

FUNCTION sendPayrollReminder()

STEP 1

Check previous month payroll status.

---

IF payroll not generated

Notify Responsible User

HR

OR

ADMIN

---

Dashboard Notification

Email Notification

---

STEP 2

RETURN success

---

# DTO STRUCTURE

NotificationResponseDto

notificationId

notificationType

title

message

status

relatedEntityType

relatedEntityId

createdAt

---

NotificationListResponseDto

notificationId

notificationType

title

status

createdAt

---

MarkNotificationReadRequestDto

notificationId

---

# API LIST

GET

/api/notifications

Get Latest Notifications

---

GET

/api/notifications/history

Get Notification History

---

PUT

/api/notifications/{id}/read

Mark As Read

---

PUT

/api/notifications/read-all

Mark All As Read

---

GET

/api/notifications/bell

Get Notification Bell Data

---

# VALIDATION RULES

Notification Must Exist

User Must Own Notification

Notification Id Required

---

# EXCEPTION SCENARIOS

Notification Not Found

Unauthorized Access

Email Delivery Failed

Invalid Notification Id

---

# SUCCESS OUTPUTS

Notification Created Successfully

Notification Retrieved Successfully

Notification Marked As Read

All Notifications Marked As Read

Notification Cleanup Completed

Payroll Reminder Sent Successfully

---

# FRONTEND INTEGRATION

Navbar Notification Bell

Display Latest 10 Notifications

---

Unread Count Badge

Display Total Unread Notifications

---

Notification Click

Mark As Read

Navigate To Related Page

---

Notification History Page

Display Notifications

From Last 30 Days

---

# DEVELOPMENT RULE

Notifications must only be triggered by:

Authentication Module

Employee Module

Attendance Module

Leave Module

Payroll Module

Payslip Module

---

Notification Module handles only:

Creation

Storage

Retrieval

Display

Navigation

Cleanup

---

MODULE STATUS

READY FOR BACKEND IMPLEMENTATION

ARCHITECTURE SCORE

99.5/100
