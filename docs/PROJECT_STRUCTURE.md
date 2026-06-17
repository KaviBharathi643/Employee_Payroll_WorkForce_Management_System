# PROJECT_STRUCTURE.md

# Employee Payroll & Workforce Management System

## Project Structure Philosophy

The project follows:

* Layered Backend Architecture
* Modular Frontend Architecture
* REST API Design
* Role-Based Navigation
* Separation Of Concerns

This structure must be followed throughout development.

---

# Root Structure

Employee-Payroll-Workforce-Management-System/

docs/

backend/

frontend/

---

# Backend Structure

backend/

src/
├── main/
│   ├── java/com/company/payroll/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── mapper/
│   │   ├── security/
│   │   ├── config/
│   │   ├── exception/
│   │   ├── scheduler/
│   │   ├── constants/
│   │   └── util/
│   │
│   └── resources/
│       ├── application.yml
│       └── application-dev.yml
│
uploads/
└── profile/

pom.xml

---

# Controller Layer

Purpose:

Handle HTTP Requests.

Files:

AuthController

EmployeeController

AttendanceController

LeaveController

PayrollController

PayslipController

ReportController

NotificationController

SystemSettingsController

---

# Service Layer

Purpose:

Business Logic.

Files:

AuthService

EmployeeService

AttendanceService

LeaveService

PayrollService

PayrollCalculationService

PayslipService

ReportService

NotificationService

SystemSettingsService

EmailService

OtpService

---

# Repository Layer

Purpose:

Database Access.

Files:

UserRepository

UserProfileRepository

UserEmploymentRepository

UserBankDetailsRepository

AttendanceRepository

LeaveRequestRepository

PayrollRepository

PayslipRepository

NotificationRepository

OtpVerificationRepository

SystemSettingsRepository

---

# Entity Layer

Entities:

User

UserProfile

UserEmployment

UserBankDetails

Attendance

LeaveRequest

Payroll

Payslip

Notification

OtpVerification

SystemSettings

---

# DTO Layer

Purpose:

Request & Response Objects.

Structure:

dto/

├── auth/
│   ├── LoginRequestDto
│   │   ├── email
│   │   └── password
│   │
│   ├── LoginResponseDto
│   │   ├── token
│   │   ├── userId
│   │   ├── employeeCode
│   │   ├── fullName
│   │   ├── email
│   │   └── role
│   │
│   ├── ForgotPasswordRequestDto
│   │   └── email
│   │
│   ├── VerifyOtpRequestDto
│   │   ├── email
│   │   └── otpCode
│   │
│   └── ResetPasswordRequestDto
│       ├── email
│       ├── otpCode
│       ├── newPassword
│       └── confirmPassword
│
├── employee/
│   ├── CreateEmployeeRequestDto
│   │   ├── email
│   │   ├── fullName
│   │   ├── phone
│   │   ├── address
│   │   ├── department
│   │   ├── designation
│   │   ├── joiningDate
│   │   ├── employmentType
│   │   └── basicSalary
│   │
│   ├── UpdateEmployeeRequestDto
│   │   ├── fullName
│   │   ├── phone
│   │   ├── address
│   │   ├── department
│   │   ├── designation
│   │   ├── employmentType
│   │   └── basicSalary
│   │
│   ├── CreateHrRequestDto
│   │   ├── email
│   │   ├── fullName
│   │   ├── phone
│   │   ├── address
│   │   ├── department
│   │   ├── designation
│   │   ├── joiningDate
│   │   └── basicSalary
│   │
│   ├── UpdateHrRequestDto
│   │   ├── fullName
│   │   ├── phone
│   │   ├── address
│   │   ├── department
│   │   ├── designation
│   │   └── basicSalary
│   │
│   ├── UpdateProfileRequestDto
│   │   ├── fullName
│   │   ├── phone
│   │   ├── address
│   │   └── profilePhoto
│   │
│   ├── UpdateBankDetailsRequestDto
│   │   ├── bankName
│   │   ├── accountNumber
│   │   └── ifscCode
│   │
│   ├── EmployeeResponseDto
│   │   ├── userId
│   │   ├── employeeCode
│   │   ├── fullName
│   │   ├── email
│   │   ├── phone
│   │   ├── address
│   │   ├── department
│   │   ├── designation
│   │   ├── joiningDate
│   │   ├── employmentType
│   │   ├── basicSalary
│   │   ├── employmentStatus
│   │   ├── bankName
│   │   ├── maskedAccountNumber
│   │   └── ifscCode
│   │
│   └── EmployeeListResponseDto
│       ├── userId
│       ├── employeeCode
│       ├── fullName
│       ├── email
│       ├── department
│       ├── designation
│       └── employmentStatus
│
├── attendance/
│   ├── CheckInRequestDto
│   ├── CheckOutRequestDto
│   ├── AttendanceResponseDto
│   ├── AttendanceSummaryResponseDto
│   └── AttendanceListResponseDto
│
├── leave/
│   ├── ApplyLeaveRequestDto
│   ├── ApproveLeaveRequestDto
│   ├── RejectLeaveRequestDto
│   ├── CancelLeaveRequestDto
│   ├── LeaveResponseDto
│   ├── LeaveListResponseDto
│   └── LeaveBalanceResponseDto
│
├── payroll/
│   ├── GeneratePayrollRequestDto
│   ├── CreditSalaryRequestDto
│   ├── PayrollResponseDto
│   ├── PayrollListResponseDto
│   └── PayrollSummaryResponseDto
│
├── payslip/
│   ├── PayslipResponseDto
│   └── PayslipListResponseDto
│
├── report/
│   ├── EmployeeReportRequestDto
│   ├── AttendanceReportRequestDto
│   ├── LeaveReportRequestDto
│   ├── PayrollReportRequestDto
│   └── ReportResponseDto
│
├── notification/
│   ├── NotificationResponseDto
│   ├── NotificationListResponseDto
│   └── MarkNotificationReadRequestDto
│
└── settings/
    ├── UpdateCompanySettingsRequestDto
    ├── UpdateAttendanceSettingsRequestDto
    ├── UpdateLeaveSettingsRequestDto
    ├── UpdatePayrollSettingsRequestDto
    └── SystemSettingsResponseDto

---

# Security Layer

Files:

JwtAuthenticationFilter

JwtTokenProvider

CustomUserDetailsService

SecurityConfig

---

# Config Layer

Files:

ApplicationConfig

MailConfig

CorsConfig

---

# Exception Layer

Files:

GlobalExceptionHandler

ResourceNotFoundException

ValidationException

BusinessRuleException

UnauthorizedException

---

# Scheduler Layer

Files:

PayrollReminderScheduler

AttendanceReminderScheduler

NotificationCleanupScheduler

---

# Constants Layer

Files:

RoleConstants

AttendanceConstants

LeaveConstants

PayrollConstants

NotificationConstants

---

# Frontend Structure

frontend/

src/

assets/

components/

layouts/

pages/

services/

hooks/

context/

routes/

utils/

---

# Layouts

EmployeeLayout

HRLayout

AdminLayout

AuthLayout

---

# Components

common/

forms/

tables/

cards/

charts/

modals/

notifications/

---

# Pages

auth/

employee/

hr/

admin/

---

# Authentication Pages

LoginPage

ForgotPasswordPage

ResetPasswordPage

---

# Employee Pages

Dashboard

MyProfile

Attendance

Leave

PayrollHistory

Payslips

Notifications

---

# HR Pages

Dashboard

Employees

Attendance

Leave

Payroll

Reports

MyProfile

Notifications

---

# Admin Pages

Dashboard

HRManagement

HRLeaveApproval

HRPayroll

Reports

SystemSettings

MyProfile

Notifications

---

# Services

Purpose:

API Calls.

Files:

authService

employeeService

attendanceService

leaveService

payrollService

payslipService

reportService

notificationService

settingsService

---

# Context

Files:

AuthContext

NotificationContext

---

# Routes

Files:

AppRoutes

ProtectedRoutes

RoleProtectedRoutes

---

# API Structure

Base URL:

/api

Endpoints:

/api/auth

/api/employees

/api/attendance

/api/leaves

/api/payrolls

/api/payslips

/api/reports

/api/notifications

/api/settings

---

# Naming Conventions

Backend:

Class Names:

PascalCase

Examples:

EmployeeService

PayrollController

LeaveRequest

---

Methods:

camelCase

Examples:

createEmployee()

generatePayroll()

approveLeave()

---

Database:

snake_case

Examples:

employee_code

joining_date

basic_salary

---

Frontend:

Components:

PascalCase

Examples:

EmployeeTable

PayrollCard

LeaveForm

---

Hooks:

camelCase

Examples:

useAuth

useNotification

---

# Development Rules

Never Create New Modules.

Never Change Business Rules.

Follow PROJECT_MASTER_SPEC.md.

Follow DATABASE_SCHEMA.md.

Follow SYSTEM_ARCHITECTURE.md.

One Module Development At A Time.

Complete Backend Before Frontend.

Complete Testing Before Next Module.

---

# PROJECT STATUS

STATUS: FINALIZED

This document is the official reference for project folder structure, package structure, naming conventions, API organization, frontend organization, and development standards.
