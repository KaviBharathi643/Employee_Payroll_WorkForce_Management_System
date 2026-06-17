# PROJECT_MASTER_SPEC.md

# Employee Payroll & Workforce Management System

## 1. Project Overview

### Project Name

Employee Payroll & Workforce Management System

### Objective

The system is designed to manage workforce operations including:

* Authentication & Authorization
* Employee Management
* Attendance Tracking
* Leave Management
* Payroll Processing
* Payslip Generation
* Reports & Analytics
* Notifications
* System Administration

The application supports three roles:

* Admin
* HR
* Employee

---

# 2. User Roles

## Admin

Responsibilities:

* Create HR Accounts
* Deactivate HR Accounts
* Approve HR Leave Requests
* Generate HR Payroll
* Credit HR Salary
* View Reports
* Manage System Settings

---

## HR

Responsibilities:

* Create Employee Accounts
* Deactivate Employee Accounts
* Approve Employee Leave Requests
* Generate Employee Payroll
* Credit Employee Salary
* View Reports
* Apply Leave

---

## Employee

Responsibilities:

* Manage Own Profile
* Manage Own Bank Details
* Mark Attendance
* Apply Leave
* View Payroll
* Download Payslips

---

# 3. Authentication & Authorization

## Login

Users login using:

* Email
* Password

Authentication Method:

* JWT

---

## Registration Rules

Employee:

* Cannot Self Register

HR:

* Cannot Self Register

Admin:

* Default System Account

Account Creation Flow:

Admin → Creates HR

HR → Creates Employee

---

## Forgot Password

Uses:

* Email OTP

OTP Rules:

* 6 Digits
* 2 Minute Expiry

---

# 4. Employee Management

## Employee Code Format

Examples:

* EMP001
* EMP002
* EMP003

Rules:

* Unique
* Permanent
* Never Reused

---

## HR Code Format

Examples:

* HR001
* HR002
* HR003

Rules:

* Unique
* Permanent
* Never Reused

---

## Employment Types

* FULL_TIME
* INTERN

---

## Employee Status

* ACTIVE
* INACTIVE

Rules:

* No Delete
* Only Deactivate

---

## Profile Information

### Personal Information

* Full Name
* Phone Number
* Address
* Profile Photo

### Profile Picture Management

Users can upload profile pictures.

Supported Formats:
JPG
JPEG
PNG

Maximum File Size:
2 MB

Profile pictures stored in:

backend/uploads/profile/

Database stores:

profile_photo_url

Default avatar displayed when no image exists.

Users can only update their own profile picture.

### Employment Information

* Department
* Designation
* Joining Date
* Salary

### Bank Information

* Bank Name
* Account Number
* IFSC Code

---

## Bank Rules

Bank Details Mandatory

Payroll generation blocked if bank details are missing.

Employee:

* View Own Bank Details
* Edit Own Bank Details

HR:

* View Employee Bank Details
* Account Number Masked

Admin:

* View HR Bank Details
* Account Number Masked

---

# 5. Attendance Management

## Attendance Process

Employee:

* Check In
* Check Out

---

## Attendance Logic

### Rule 1

No Leave + Check In

Result:

Present

---

### Rule 2

No Leave + No Check In

Result:

Absent

---

### Rule 3

Full-Day Leave Approved

Result:

Leave

---

### Rule 4

Full-Day Leave Approved + Check In

Result:

Still Leave

---

### Rule 5

Half-Day Leave Approved + Check In

Result:

Half-Day Leave

---

### Rule 6

Half-Day Leave Approved + No Check In

Result:

Absent

---

## Missing Checkout Policy

Office End Time + 1 Hour

System Actions:

* Employee Notification
* Employee Email
* HR Notification

Penalty:

Half-Day Leave Deduction

---

# 6. Leave Management

## Leave Types

* Casual Leave
* Sick Leave
* Unpaid Leave

---

## Annual Leave Policy

Paid Leave Pool:

25 Days

Includes:

* Casual Leave
* Sick Leave

Unused Leave:

Lost At Year End

No Carry Forward

---

## Leave Durations

* Half Day
* Full Day
* Multi Day

---

## Leave Status

* Pending
* Approved
* Rejected
* Cancelled

---

## Leave Approval Flow

Employee Leave

Employee
↓
HR

---

HR Leave

HR
↓
Admin

---

## Weekend Handling

Saturday:

Holiday

Sunday:

Holiday

Weekend days do not reduce leave balance.

---

## Leave Restrictions

Employee cannot apply leave for dates already covered by approved leave.

Past-date leave requests not allowed.

Leave reason mandatory.

Today leave allowed.

---

# 7. Payroll Management

## Payroll Flow

Generate Payroll
↓
Generate Payslip
↓
Credit Salary

---

## Important Rule

Payroll generation does NOT credit salary automatically.

Salary is credited only after:

HR or Admin clicks:

Credit Salary

---

## Salary Formula

Final Salary

=

Basic Salary

*

Bonus

*

PF Deduction

*

Unpaid Leave Deduction

---

## PF Policy

Default:

8%

Configurable Through System Settings

---

## Bonus

Optional

Added During Payroll Generation

---

## Payroll Status

* GENERATED
* CREDITED

---

## Payroll Restrictions

One Payroll Per Employee Per Month

Payroll History Cannot Change

Only Active Employees Included

Payroll Requires Bank Details

---

# 8. Payslip Management

## Payslip Generation

Payroll Generated
↓
Payslip Generated Automatically

---

## Payslip Number Format

PSL-2026-0001

PSL-2026-0002

PSL-2026-0003

---

## Payslip Contents

* Employee Details
* Employee Code
* Department
* Designation
* Basic Salary
* Bonus
* PF Percentage
* PF Amount
* Unpaid Leave Deduction
* Final Salary
* Generated Date

Bank Account Number:

Masked

---

## Access

Employee:

* View Payslip
* Download PDF

---

# 9. Reports & Analytics

## Available Reports

* Employee Report
* Attendance Report
* Leave Report
* Payroll Report

---

## Export Format

PDF Only

---

## Dashboard Charts

Attendance Chart

* Present Count
* Absent Count

Payroll Chart

* Salary Paid
* Deductions

---

# 10. Dashboard Architecture

## Employee Dashboard

* Attendance Summary
* Leave Summary
* Payroll Summary
* Notifications

---

## HR Dashboard

* Employee Summary
* Attendance Summary
* Leave Requests
* Payroll Summary
* Notifications

---

## Admin Dashboard

* HR Summary
* HR Leave Requests
* HR Payroll Summary
* Reports
* Notifications

---

# 11. Notifications & Email Management

## Notification Bell

Displays:

Latest 10 Notifications

---

## Retention Policy

30 Days

---

## Notification Types

* Account Created
* OTP
* Leave Approved
* Leave Rejected
* Leave Cancelled
* Payroll Generated
* Salary Credited
* Missing Checkout
* Payroll Reminder

---

## Notification Behavior

* Clickable
* Read/Unread Support

---

# 12. System Settings

Managed By:

Admin

---

## Company Information

* Company Name
* Company Address
* Company Email
* Company Phone

---

## Attendance Settings

* Office Start Time
* Office End Time
* Checkout Reminder Time

---

## Leave Settings

* Annual Paid Leave Limit

---

## Payroll Settings

* PF Percentage
* Salary Credit Day

---

# 13. Global Business Rules

* No Employee Delete
* No HR Delete
* Deactivate Only
* Employee IDs Never Reused
* HR IDs Never Reused
* Payroll Snapshot Storage
* Notifications Retained For 30 Days
* Saturday And Sunday Are Holidays
* Payroll Requires Bank Details
* One Payroll Per Month Per Employee

---

# 14. Validation Rules

Mandatory Fields:

* Email
* Password
* Leave Reason
* Bank Details

Validation Rules:

* No Duplicate Attendance Per Day
* No Duplicate Payroll Per Month
* No Leave Overlap
* No Past-Date Leave
* OTP Expires After 2 Minutes

---

# 15. Role Permission Summary

Employee:

Own Data Only

HR:

Employee Management

Admin:

HR Management

---

# 16. Future Enhancements

Not Included In Current Scope

* GPS Attendance
* Face Recognition Attendance
* Public Holiday Management
* Shift Management
* Overtime Management
* Tax Management
* Audit Logs

---

# PROJECT STATUS

STATUS: FINALIZED

This document is the official source of truth for all business rules, workflows, validations, permissions, and module behavior.
