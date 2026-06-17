# FINAL_PROJECT_BLUEPRINT.md

**Project:** Employee Payroll & Workforce Management System  
**Version:** 1.0.0-FREEZE  
**Status:** FROZEN — Single Source of Truth for Implementation  
**Freeze Date:** 2026-06-04  
**Authority Order:** Pseudocode → PROJECT_MASTER_SPEC.md → DATABASE_SCHEMA.md → SYSTEM_ARCHITECTURE.md → This Blueprint

---

## Document Control

| Item | Value |
|------|--------|
| Modules | 9 (fixed — no add/remove) |
| Roles | ADMIN, HR, EMPLOYEE only |
| Backend | Java 21, Spring Boot 3, Maven, MySQL 8, JWT |
| Frontend | React 18, Vite, Tailwind, Axios, React Router |
| Missing repo docs | `FRONTEND_ARCHITECTURE.md`, `database-rules.mdc` — superseded by this blueprint + `project-rules.mdc` |

---

## 1. Project Overview

A role-based web application automating workforce operations: authentication, employee/HR lifecycle, attendance, leave, payroll, payslips, reports, notifications, and system settings. Three portals share one API with JWT authorization.

---

## 2. Business Objectives

| Objective | Success Criteria |
|-----------|------------------|
| Automate payroll | Snapshot payroll, manual credit, auto payslip |
| Track attendance | Check-in/out, status rules, missing-checkout policy |
| Manage leave | Approval chains, balance from settings, weekend exclusion |
| Secure access | JWT, RBAC, no self-registration for HR/Employee |
| Auditability | `created_at`/`updated_at`, immutable payroll history |
| Compliance with spec | Zero invented business rules; pseudocode is law |

---

## 3. User Roles

### ADMIN
- Create/deactivate HR
- Approve HR leave
- Generate/credit HR payroll
- HR-scoped reports
- System settings (only role)
- Own profile, notifications

### HR
- Create/deactivate employees
- Approve employee leave
- Generate/credit employee payroll
- Employee-scoped reports
- Apply leave (approved by Admin)
- Own profile, bank, photo, notifications

### EMPLOYEE
- Own profile, bank, photo
- Check-in/out
- Apply/cancel own leave
- View payroll & download payslips
- Own notifications

**No additional roles.**

---

## 4. Complete Module List

| # | Module | Controllers | Primary Tables |
|---|--------|-------------|------------------|
| 1 | Authentication | AuthController | users, otp_verifications |
| 2 | Employee Management | EmployeeController, ProfileController | users, user_profiles, user_employment, user_bank_details |
| 3 | Attendance | AttendanceController | attendance, leave_requests, system_settings |
| 4 | Leave | LeaveController | leave_requests |
| 5 | Payroll | PayrollController | payrolls |
| 6 | Payslip | PayslipController | payslips |
| 7 | Reports & Analytics | ReportController | read-only aggregate |
| 8 | Notifications | NotificationController | notifications |
| 9 | System Settings | SystemSettingsController | system_settings |

---

## 5. System Architecture

```
[Browser: React + Vite]
        ↓ HTTPS / REST (JSON)
[Spring Boot API — Controller → Service → Repository]
        ↓ JDBC/JPA
[MySQL 8]
        ↓ async
[SMTP Email]   [File System: uploads/profile, uploads/payslips]
        ↓ scheduled
[3 Schedulers: Payroll Reminder, Attendance Reminder, Notification Cleanup]
```

**Patterns (frozen):** Layered backend, DTO + Mapper, global exception handler, standard `ApiResponse<T>`, soft delete via status, payroll snapshot storage, event-driven notifications.

---

## 6. Database Architecture

### 6.1 Tables (11)

| Table | PK | Purpose |
|-------|-----|---------|
| users | id | Auth, role, status |
| user_profiles | id | Personal info, employee_code, profile_photo_url |
| user_employment | id | Job, salary, manager |
| user_bank_details | id | Bank (required for payroll) |
| attendance | id | Daily attendance |
| leave_requests | id | Leave workflow |
| payrolls | id | Monthly snapshot |
| payslips | id | 1:1 payroll, PDF flag |
| notifications | id | User alerts |
| otp_verifications | id | Password reset OTP |
| system_settings | id | Singleton config |

### 6.2 Relationships (frozen)

- users 1:1 user_profiles, user_employment, user_bank_details (logical; see Issue Registry #12)
- users 1:M attendance, leave_requests, payrolls, notifications, otp_verifications
- payrolls 1:1 payslips

### 6.3 Naming & Audit

- snake_case columns, BIGINT PK, AUTO_INCREMENT
- All major tables: `created_at`, `updated_at` (except notifications: no `updated_at` per schema)

### 6.4 Profile & Required Fields (frozen — stakeholder decision)

**`user_profiles` on create/update must include:**
- `date_of_birth` (DATE, NOT NULL)
- `gender` (ENUM: MALE, FEMALE, OTHER, NOT NULL)
- `full_name`, `phone_number`, `address`, `employee_code`
- `personal_email` optional (nullable UNIQUE)

---

## 7. Module Dependency Map

```
System Settings ─────────────────────────────────────────┐
       │                                                  │
Auth ──┼──► Employee ──► Leave ◄──► Attendance          │
       │        │           │            │               │
       │        └───────────┼────────────┘               │
       │                    ▼                            │
       │                 Payroll ──► Payslip             │
       │                    │                            │
       └────────────► Notifications ◄── Email ◄────────┘
                            ▲
Reports (read-only) ────────┴── uses all domain services
```

**Rule:** Reports/dashboards MUST use shared summary methods (`DashboardAnalyticsService` / `ReportService` internals) — no duplicate calculations.

---

## 8. Authentication Flow

```
Login(email, password)
  → validate fields
  → find user by email
  → status == ACTIVE
  → BCrypt.matches
  → JWT(subject=email, claims: userId, role, email)
  → response: token, role, userId, employeeCode, fullName, email

Forgot Password
  → find user → 6-digit OTP → expires +2 min → save otp_verifications → email (no dashboard notif)

Verify OTP → latest OTP by userId → expiry → match

Reset Password → verify OTP → confirm match → BCrypt encode → delete OTPs

GET /api/auth/me → current user + profile summary

Logout → client removes token (stateless JWT)
```

**APIs:** `POST /api/auth/login`, `forgot-password`, `verify-otp`, `reset-password`, `GET /api/auth/me`

---

## 9. Employee Lifecycle Flow

```
ADMIN: createHr → generate HR001.. → user(HR,ACTIVE) + profile + employment(FULL_TIME)
HR: createEmployee → generate EMP001.. → user(EMPLOYEE,ACTIVE) + profile(DOB,gender) + employment(reportingManager=HR)
  → notification + welcome email (default temp password — frozen: random 12-char, force change on first login OPTIONAL not in spec — use secure random + email body)

Deactivate: status INACTIVE + employment_status INACTIVE (no delete, codes never reused)

Employee/HR/Admin: update own profile (name, phone, address, DOB, gender, photo)
HR: update employee fields | Admin: update HR fields
Owner: update bank details (mandatory before payroll)
```

**Profile photo:** `POST/DELETE /api/profile/photo` → `backend/uploads/profile/{uuid}.{ext}` → `profile_photo_url` in DB.

**APIs:** See Section 17.

---

## 10. Attendance Workflow

```
Check-In (EMPLOYEE only)
  → reject Sat/Sun
  → reject duplicate day
  → create attendance: check_in_time, status=PRESENT

Check-Out
  → require check-in
  → reject duplicate checkout
  → set check_out_time

Daily Status (for reports/dashboard)
  → approved FULL_DAY leave → LEAVE (even if checked in)
  → approved HALF_DAY + check-in → HALF_DAY_LEAVE
  → approved HALF_DAY + no check-in → ABSENT
  → no leave + record exists → PRESENT
  → else → ABSENT

Missing Checkout Scheduler (daily)
  → reminderTime = office_end_time + checkoutReminderHours (from settings)
  → records with check-in, no check-out → notify employee + HR + email
  → penalty: create UNPAID leave 0.5 days (Missing Checkout Penalty) — frozen as leave_requests row
```

---

## 11. Leave Workflow

```
Apply (EMPLOYEE or HR)
  → reason required, no past dates, no overlap with approved leave
  → calculate leaveDays (weekends skipped for MULTI_DAY)
  → status PENDING → notify approver (Employee→HR, HR→Admin)

Approve/Reject (role-based approver)
Cancel (owner only)
Convert to UNPAID (HR/Admin)

Balance: paidLeaveUsed = sum(CASUAL+SICK APPROVED); remaining = annualPaidLeaveLimit - used (from settings, NOT hardcoded 25)
```

---

## 12. Payroll Workflow

```
Generate (HR=employees, ADMIN=HR)
  → input: payrollYear, payrollMonth, bonusAmount (equal bonus per employee in batch)
  → Rule 11: payroll month label represents the payroll period; unpaid leave counted where month(startDate)=payrollMonth AND year=startDate)=payrollYear
  → foreach ACTIVE user in scope:
      → skip if payroll exists for user+month+year
      → skip if bank missing
      → pfAmount = basicSalary * pfPercentage / 100
      → unpaidLeaveCount = sum APPROVED UNPAID leave days in period
      → unpaidLeaveDeduction = (basicSalary/30) * unpaidLeaveCount
      → finalSalary = basicSalary + bonusAmount - pfAmount - unpaidLeaveDeduction
      → snapshot row status=GENERATED
      → auto generatePayslip(payrollId)
      → notify employee

Credit Salary (manual, separate)
  → status CREDITED, creditedDate, creditedBy
  → notify + email
```

**Formula (frozen):** `finalSalary = basicSalary + bonus - pfAmount - unpaidLeaveDeduction`

---

## 13. Payslip Workflow

```
generatePayslip(payrollId) [internal — not public API]
  → payslipNumber = PSL-{year}-{seq4}
  → save payslip pdf_generated=false
  → build PDF from payroll snapshot ONLY (never user_employment salary)
  → mask bank account (XXXXXXXX + last4)
  → save PDF to backend/uploads/payslips/{payslipNumber}.pdf
  → pdf_generated=true
  → notification (no PDF email attachment)

Access:
  EMPLOYEE → own | HR → employee payslips | ADMIN → HR payslips only
```

---

## 14. Reports Workflow

Read-only PDF exports + dashboard analytics.

| Report | Role | Scope |
|--------|------|-------|
| Employee | HR / Admin | HR→employees, Admin→HR |
| Attendance | HR / Admin | same scope split |
| Leave | HR / Admin | same |
| Payroll | HR / Admin | employee vs HR payroll snapshots |

Empty data → PDF with "No Records Available" (not an error).

**APIs:** `GET /api/reports/employees|attendance|leaves|payroll|dashboard`

---

## 15. Notification Workflow

Event-driven creation from modules 1–7; module 8 stores/retrieves only.

| Rule | Value |
|------|--------|
| Bell | Latest 10 |
| History | 30 days |
| Cleanup | Daily delete where expires_at < now |
| OTP | Email only |
| Payslip email | No PDF attachment |

**DB mapping (frozen):**
- `is_read` ↔ READ/UNREAD
- `redirect_type` ∈ LEAVE, PAYROLL, ATTENDANCE, PROFILE (+ PAYSLIP navigates to payslip page)
- `expires_at` = created_at + 30 days on insert
- Entity routing token: `redirect_type` format `TYPE` or `TYPE:{id}` (max 30 chars)

---

## 16. System Settings Workflow

Singleton row — never deleted. ADMIN only.

| Section | Fields | Consumers |
|---------|--------|-----------|
| Company | name, address, email, phone | PDFs, emails |
| Attendance | office_start, office_end, checkout_reminder_hours | schedulers |
| Leave | annual_paid_leave_limit | balance, reports |
| Payroll | pf_percentage, salary_credit_day | calculation, reminders |

**DB mapping (frozen):** API exposes `checkoutReminderHours`; persist `checkout_reminder_time` = `office_end_time` + N hours.

**Init on startup:** If no row, seed defaults per SYSTEM_SETTINGS pseudocode.

**salary_credit_day:** Reminder only — never auto-credits salary.

---

## 17. API Dependency Map

```
/api/auth/*           → no JWT (except /me)
/api/settings/*       → JWT + ADMIN
/api/employees/*      → JWT + ADMIN|HR (method-level)
/api/employees/hr/*   → JWT + ADMIN
/api/profile/photo    → JWT + all roles (own only)
/api/attendance/*     → JWT + role scope
/api/leaves/*         → JWT + role scope
/api/payrolls/*       → JWT + HR|ADMIN|EMPLOYEE(read own)
/api/payslips/*       → JWT + role scope
/api/reports/*        → JWT + HR|ADMIN (not EMPLOYEE)
/api/notifications/*  → JWT + own user
```

**Response envelope (all modules):**
```json
{ "success": true, "message": "...", "data": { } }
```

---

## 18. Security Architecture

| Layer | Control |
|-------|---------|
| Transport | HTTPS in production |
| Auth | JWT Bearer, BCrypt passwords |
| Authz | `@PreAuthorize` / SecurityFilterChain role matchers |
| Validation | Jakarta Bean Validation on DTOs |
| Errors | Global handler — no stack traces to client |
| CORS | Whitelist frontend origin per environment |
| Files | UUID filenames, MIME whitelist (image/jpeg,png), 2MB max |
| Profile access | Frozen #32: download via authenticated endpoint OR static with non-guessable UUID path |
| OTP | 6 digits, 2-min expiry, rate limit 5 attempts/email/15min (frozen #28) |
| JWT | HS256, secret from env, expiry configurable; no server session |
| SQL | Parameterized JPA only |
| Sensitive data | Mask bank account for non-owner |

**Security path mapping (frozen — replaces `/api/admin/**` from auth pseudocode):**

| Role | Ant patterns |
|------|----------------|
| ADMIN | `/api/employees/hr/**`, `/api/settings/**`, `/api/reports/**`, HR payroll endpoints |
| HR | `/api/employees/**` (except hr subpaths), `/api/attendance/**`, `/api/leaves/**`, `/api/payrolls/**`, `/api/payslips/**`, `/api/reports/**` |
| EMPLOYEE | `/api/attendance/check-*`, `/api/leaves/**` (own), `/api/payrolls/**` (own), `/api/payslips/**` (own), profile/bank |

**Ownership checks:** Service layer validates `userId == principal` for EMPLOYEE scoped resources.

---

## 19. File Upload Architecture

| Type | Path | DB field | Rules |
|------|------|----------|-------|
| Profile photo | `backend/uploads/profile/` | user_profiles.profile_photo_url | JPG/JPEG/PNG, 2MB, own user only |
| Payslip PDF | `backend/uploads/payslips/` | payslips.pdf_generated | Generated server-side |

Delete photo: remove file + NULL url.

---

## 20. PDF Generation Architecture

Single `PdfService` (or module-specific builders) using company settings header.

| Output | Trigger | Data source |
|--------|---------|-------------|
| Payslip | Payroll generation | payroll snapshot + masked bank |
| Employee report | API request | profiles + employment |
| Attendance report | API request | attendance + calculated status |
| Leave report | API request | leave_requests + balance |
| Payroll report | API request | payroll snapshots |

Library: OpenPDF or iText (frozen: OpenPDF for licensing).

---

## 21. Scheduler Architecture

| Scheduler | Cron (frozen default) | Action |
|-----------|----------------------|--------|
| PayrollReminderScheduler | `0 0 9 1 * *` (9AM 1st of month) | If previous month payroll missing → notify HR or Admin |
| AttendanceReminderScheduler | `0 0 * * * *` hourly check after reminder time | Missing checkout notifications |
| NotificationCleanupScheduler | `0 30 2 * * *` daily 2:30AM | DELETE notifications WHERE expires_at < NOW() |

All schedulers idempotent, log outcomes, `@Transactional` where DB writes occur.

---

## 22. Frontend Architecture

*(Consolidated — `FRONTEND_ARCHITECTURE.md` not in repo)*

### 22.1 Stack
React 18, Vite, Tailwind, Axios, React Router v6.

### 22.2 Structure
`components/`, `pages/`, `layouts/`, `services/`, `hooks/`, `context/`, `routes/`, `utils/`, `assets/`.

### 22.3 Layouts
`AuthLayout`, `EmployeeLayout`, `HRLayout`, `AdminLayout` — each: Navbar + Sidebar + NotificationBell + Outlet.

### 22.4 State
- `AuthContext` — JWT, user, role, login/logout
- `NotificationContext` — bell data, poll on interval (60s frozen)

### 22.5 Routing
- Public: `/login`, `/forgot-password`, `/reset-password`
- `/employee/*`, `/hr/*`, `/admin/*` — `RoleProtectedRoutes`
- Axios interceptor attaches Bearer; 401 → logout redirect

### 22.6 API base
`import.meta.env.VITE_API_BASE_URL` — never hardcode production URL.

### 22.7 Forms (DOB + gender frozen)
Create Employee/HR and Profile forms include date picker + gender select (MALE/FEMALE/OTHER).

### 22.8 Charts
Dashboard: Attendance (present/absent), Payroll (paid/deductions) — Recharts frozen.

---

## 23. Folder Structure

See `PROJECT_STRUCTURE.md` — frozen paths:

```
Employee-Payroll-Workforce-Management-System/
├── docs/
├── backend/          (Maven, com.company.payroll)
│   ├── uploads/profile/
│   ├── uploads/payslips/
│   └── src/main/java|resources/
└── frontend/src/     (React)
```

Package: `com.company.payroll` — all layers as documented in PROJECT_STRUCTURE.md.

---

## 24. Coding Standards

| Area | Standard |
|------|----------|
| Java | PascalCase classes, camelCase methods, constructor injection |
| DB | snake_case via `@Column(name=)` |
| React | PascalCase components, camelCase hooks |
| API | REST nouns, plural collections, HTTP verbs per action |
| Errors | `BusinessRuleException`, `ResourceNotFoundException` + `ApiResponse` |
| Logging | SLF4J — INFO business events, WARN validation, ERROR exceptions |
| Tests | Service-layer unit tests per module minimum |

---

## 25. Development Rules

1. One module at a time — backend complete before frontend.
2. No new modules, no business rule invention.
3. Pseudocode exact for workflows.
4. DTO pattern mandatory — entities never exposed on API.
5. Mappers between entity ↔ DTO.
6. Do not modify DATABASE_SCHEMA.md table definitions — implement as specified.
7. Conflicts resolved per authority order — decisions in Section 29.
8. Dashboard totals = report totals (shared services).
9. Payroll never auto-credits.
10. Payslip never recalculates from live employment data.

---

## 26. Production Constraints

| Constraint | Requirement |
|------------|-------------|
| Deployment | Frontend: Vercel; Backend: Render/Railway; DB: cloud MySQL |
| Profiles | `application-prod.yml` — env vars for secrets |
| File storage | Ephemeral disk on PaaS — document backup limitation; future: S3 (out of scope) |
| JWT secret | Min 256-bit, env-only |
| MySQL | UTF8MB4, timezone UTC |
| CORS | Production frontend origin only |
| Health | `/actuator/health` exposed (frozen #38) |
| Migrations | `schema.sql` + optional Flyway — DDL matches DATABASE_SCHEMA.md exactly |

---

## 27. Known Business Rules

| Rule | Detail |
|------|--------|
| No delete | Employees/HR deactivate only |
| Codes | EMP###, HR###, PSL-YYYY-#### — never reused |
| Weekends | Sat/Sun holidays — no attendance, no leave deduction |
| Leave pool | Casual + Sick from `annual_paid_leave_limit` |
| Unpaid leave | Deducted in payroll |
| One payroll/user/month | Service-enforced |
| Bank required | Block payroll if missing |
| Snapshot | Payroll/payslip immutable |
| OTP | 6 digits, 2 minutes |
| Notifications | 30-day retention |
| Half-day leave | Requires check-in for HALF_DAY_LEAVE status |
| Bonus | Same amount all employees in one generation run |
| DOB/Gender | Required on create and available on profile update |

---

## 28. Final Implementation Sequence

### Phase A — Foundation
1. Maven + Spring Boot skeleton
2. `schema.sql`, entities, repositories
3. ApiResponse, exceptions, CORS, Security shell
4. Seed ADMIN + system_settings

### Phase B — Backend Modules
1. Authentication  
2. Employee Management (+ DOB/gender + profile photo)  
3. System Settings  
4. Leave  
5. Attendance (+ schedulers)  
6. Payroll (+ PayrollCalculationService + reminder scheduler)  
7. Payslip (+ PdfService)  
8. Notifications (+ cleanup scheduler)  
9. Reports (+ DashboardAnalyticsService)

### Phase C — Frontend
1. Auth + layouts + routing  
2. Employee portal  
3. HR portal  
4. Admin portal  
5. Notification bell + charts

### Phase D — Quality & Release
1. Testing checklist execution  
2. Deployment checklist  
3. Smoke test all role paths

---

## 29. Resolved Architecture Decisions Registry (40 Issues — FROZEN)

| ID | Category | Problem | Frozen Resolution |
|----|----------|---------|-------------------|
| R-01 | Business | Master spec salary formula uses `*` | Use additive formula per project-rules |
| R-02 | DB/API | Notification columns vs pseudocode | Map is_read, redirect_type, expires_at; encode TYPE:id in redirect_type |
| R-03 | Settings | checkout_reminder_time vs hours | API hours → compute TIME column on save |
| R-04 | Attendance | +1 hour vs configurable hours | Use checkoutReminderHours from settings |
| R-05 | Security | /api/admin/** vs real paths | SecurityConfig ant patterns in Section 18 |
| R-06 | Schema/DTO | DOB/gender missing from create DTOs | Required on create/update — stakeholder confirmed |
| R-07 | Payroll | No DB unique on user+month | Service + `@Transactional` check before insert |
| R-08 | Payroll | Previous month semantics | Document in payroll service; pass year/month explicitly |
| R-09 | Docs | database-rules.mdc missing | project-rules.mdc + this blueprint |
| R-10 | Payslip | No pdf_path column | Filesystem path by payslipNumber |
| R-11 | Attendance | Missing checkout penalty | Insert UNPAID 0.5 day leave_requests record |
| R-12 | DB | employment/bank 1:1 not UNIQUE in DDL | Repository `findByUserId` + enforce single row in service |
| R-13 | Auth | Login needs employeeCode | Join user_profiles in AuthService |
| R-14 | Employee | Default password on create | Random 12-char; email plaintext once; BCrypt store |
| R-15 | API | GET my profile endpoint | GET /api/auth/me + GET /api/employees/profile (frozen) |
| R-16 | Config | application-prod.yml absent in structure | Add in resources — frozen |
| R-17 | Notification | No notificationType column | Derive from template enum in service; store in redirect_type prefix optional |
| R-18 | Notification | No related_entity_id | Encode in redirect_type `LEAVE:42` |
| R-19 | Leave | Hardcoded 25 days | Read annual_paid_leave_limit from settings |
| R-20 | Reports | Duplicate dashboard math | Shared DashboardAnalyticsService |
| R-21 | Payslip | HR cannot see HR payslips | Role filter in PayslipService |
| R-22 | Payslip | Admin cannot see employee payslips | Role filter in PayslipService |
| R-23 | Payroll | Auto credit misconception | Separate credit endpoint only |
| R-24 | Leave | Weekend in multi-day calc | Skip Sat/Sun in calculateLeaveDays |
| R-25 | Attendance | Full-day leave + check-in | Status remains LEAVE |
| R-26 | Schema | personal_email unused | Optional field on profile update only |
| R-27 | API | Profile photo path | ProfileController at /api/profile/photo |
| R-28 | Security | OTP brute force | Rate limit 5/15min per email in OtpService |
| R-29 | Security | JWT logout stateless | Document client-side token removal; short expiry 24h prod |
| R-30 | Runtime | Same-day leave after check-in | Recalculate status on leave approval + nightly reconciliation job (frozen optional cron) |
| R-31 | Concurrency | Duplicate payroll race | Synchronized generateSinglePayroll + DB check |
| R-32 | Security | Profile URL guessing | UUID filename + auth download endpoint GET /api/profile/photo/{userId} |
| R-33 | PDF | Library choice | OpenPDF |
| R-34 | Payroll | Bonus per employee vs batch | Same bonusAmount for all in one generate call |
| R-35 | PF | Zero percent allowed | pf_percentage 0 disables deduction per settings pseudocode |
| R-36 | Email | Send failure | Log error; do not roll back DB notification record |
| R-37 | Frontend | Missing FRONTEND_ARCHITECTURE.md | Section 22 replaces |
| R-38 | Ops | Health check | Spring Actuator health endpoint |
| R-39 | Validation | DOB future date | Reject DOB > today |
| R-40 | Validation | DOB minimum age | Reject age < 18 years for employment create |

---

## 30. Blueprint Approval

This document supersedes informal analysis notes. Implementation MUST reference issue IDs R-01 through R-40 when making design decisions.

**Blueprint Status:** APPROVED FOR FREEZE
