# Payroll Workforce API — Backend

Spring Boot 3 / Java 21 backend for the Employee Payroll & Workforce Management System.

## Phase 2 (Foundation) — Included

- Maven project (`pom.xml`)
- Application configuration (`application.yml`, `application-dev.yml`, `application-prod.yml`)
- MySQL reference DDL (`src/main/resources/schema.sql`)
- 11 JPA entities + repositories
- Global `ApiResponse` + exception handling
- JWT security shell (filter, token provider, `SecurityConfig`)
- CORS, mail, static profile uploads
- Data seeder: default **ADMIN** + **system_settings**
- Upload directories: `uploads/profile`, `uploads/payslips`

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8+

## Configuration

Default dev database:

```
jdbc:mysql://localhost:3306/payroll_db
username: root
password: root
```

Override with environment variables:

- `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (min 32 characters)
- `ADMIN_EMAIL`, `ADMIN_PASSWORD`

## Run

```bash
cd backend
mvn spring-boot:run
```

Health check: `GET http://localhost:8080/api/health`

Actuator: `GET http://localhost:8080/actuator/health`

## Default admin (dev seed)

| Field | Default |
|-------|---------|
| Email | admin@company.com |
| Password | Admin@123456 |
| Employee code | ADM001 |

Change credentials via environment variables before production deploy.

## Phase 3.1 — Authentication (complete)

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/auth/login` | Public |
| POST | `/api/auth/forgot-password` | Public |
| POST | `/api/auth/verify-otp` | Public |
| POST | `/api/auth/reset-password` | Public |
| GET | `/api/auth/me` | Bearer JWT |

Default admin login: `admin@company.com` / `Admin@123456`

## Phase 3.2 — Employee Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/api/employees` | HR |
| POST | `/api/employees/hr` | ADMIN |
| GET | `/api/employees` | HR (paged, search) |
| GET | `/api/employees/{id}` | HR |
| GET | `/api/employees/hr` | ADMIN |
| GET | `/api/employees/hr/{id}` | ADMIN |
| GET | `/api/employees/profile` | All |
| PUT | `/api/employees/profile` | All |
| PUT | `/api/employees/bank-details` | HR, EMPLOYEE |
| PUT | `/api/employees/{id}` | HR |
| PUT | `/api/employees/hr/{id}` | ADMIN |
| PATCH | `/api/employees/{id}/deactivate` | HR |
| PATCH | `/api/employees/hr/{id}/deactivate` | ADMIN |
| GET | `/api/profile/photo/{userId}` | EMPLOYEE (own), HR (employees), ADMIN (HR) |
| POST | `/api/profile/photo` | All (multipart `profilePhoto`) |
| DELETE | `/api/profile/photo` | All |

Profile photos are served only via the authenticated download endpoint (no public `/uploads/profile/**` access).

Create requests include **dateOfBirth** and **gender**. Temporary password emailed on account creation.

## Phase 3.3 — System Settings (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| GET | `/api/settings` | ADMIN |
| PUT | `/api/settings/company` | ADMIN |
| PUT | `/api/settings/attendance` | ADMIN |
| PUT | `/api/settings/leave` | ADMIN |
| PUT | `/api/settings/payroll` | ADMIN |

API exposes `checkoutReminderHours`; stored as `checkout_reminder_time = office_end_time + hours`.

Other modules use `SystemSettingsService.requireSettings()` to read the singleton record.

## Phase 3.4 — Leave Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/api/leaves` | EMPLOYEE, HR |
| PUT | `/api/leaves/{id}/approve` | HR (employee leave), ADMIN (HR leave) |
| PUT | `/api/leaves/{id}/reject` | HR, ADMIN |
| PUT | `/api/leaves/{id}/cancel` | Owner |
| PUT | `/api/leaves/{id}/convert-unpaid` | HR, ADMIN |
| GET | `/api/leaves/my-history` | All |
| GET | `/api/leaves/balance` | All |
| GET | `/api/leaves/employee/{id}` | HR |
| GET | `/api/leaves/hr/{id}` | ADMIN |
| GET | `/api/leaves/report?fromDate&toDate&userId` | HR, ADMIN |

Leave balance reads `annualPaidLeaveLimit` from system settings. Weekends excluded from multi-day leave.

## Phase 3.5 — Attendance Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/api/attendance/check-in` | EMPLOYEE |
| POST | `/api/attendance/check-out` | EMPLOYEE |
| GET | `/api/attendance/me` | EMPLOYEE |
| GET | `/api/attendance/employee/{id}` | HR (employees), ADMIN (HR) |
| GET | `/api/attendance/summary` | All (?userId for HR/ADMIN) |
| GET | `/api/attendance/report` | HR, ADMIN |

Scheduler: `AttendanceReminderScheduler` — missing checkout after `checkout_reminder_time` from settings; notifies employee + HR; applies 0.5 UNPAID penalty (idempotent).

## Phase 3.6 — Payroll Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/api/payrolls/generate` | HR (employees), ADMIN (HR) |
| POST | `/api/payrolls/{id}/credit` | HR, ADMIN |
| GET | `/api/payrolls` | HR, ADMIN, EMPLOYEE (own) |
| GET | `/api/payrolls/summary` | HR, ADMIN |
| GET | `/api/payrolls/history` | All (own history) |
| GET | `/api/payrolls/report` | HR, ADMIN |
| GET | `/api/payrolls/{id}` | Scoped by role |

Formula: `finalSalary = basicSalary + bonus - pfAmount - unpaidLeaveDeduction`. Auto-creates payslip record on generate. `PayrollReminderScheduler` runs daily on `salary_credit_day`.

## Phase 3.7 — Payslip Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| GET | `/api/payslips` | EMPLOYEE (own), HR (employees), ADMIN (HR) |
| GET | `/api/payslips/{id}` | Role-scoped |
| GET | `/api/payslips/{id}/download` | Role-scoped PDF download |

PDF stored in `uploads/payslips/{payslipNumber}.pdf`. Regenerates from payroll snapshot if file missing (MD-01). OpenPDF library.

## Phase 3.8 — Notification Management (complete)

| Method | Endpoint | Role |
|--------|----------|------|
| GET | `/api/notifications` | All roles (own only) — latest 10 |
| GET | `/api/notifications/history` | All roles (own only) — last 30 days |
| GET | `/api/notifications/bell` | All roles — latest 10 + unread count |
| PUT | `/api/notifications/{id}/read` | All roles (own only) |
| PUT | `/api/notifications/read-all` | All roles (own only) |

`NotificationHelperService` creates alerts from other modules. `NotificationCleanupScheduler` runs daily at 2:30 AM and deletes rows where `expires_at < now`.

## Phase 3.9 — Reports & Analytics (complete)

| Method | Endpoint | Role | Response |
|--------|----------|------|----------|
| GET | `/api/reports/employees` | HR (employees), ADMIN (HR) | PDF |
| GET | `/api/reports/attendance` | HR, ADMIN | PDF |
| GET | `/api/reports/leaves` | HR, ADMIN | PDF |
| GET | `/api/reports/payroll` | HR, ADMIN | PDF |
| GET | `/api/reports/dashboard` | HR, ADMIN | JSON analytics |

`DashboardAnalyticsService` provides shared summary calculations used by dashboard and PDF reports. Empty data renders **"No Records Available"** in PDF (not an error).

## Backend complete

All 9 backend modules (Phases 3.1–3.9) are implemented. **Next: Phase 4 — Frontend (React + Vite + Tailwind).**
