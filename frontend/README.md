# Workforce Portal — Frontend

React 18 + Vite + Tailwind CSS + Axios + React Router v6.

## Phase 4.1 — Foundation (complete)

- Vite project with Tailwind v4
- Axios client with JWT interceptor and 401 handling
- `AuthContext` + `NotificationContext` (60s bell poll)
- Route guards: `ProtectedRoute`, `RoleProtectedRoute`, `GuestRoute`
- Layouts: `AuthLayout`, `EmployeeLayout`, `HRLayout`, `AdminLayout`
- API contract: `docs/FRONTEND_API_CONTRACT.md`

## Phase 4.2 — Authentication pages (complete)

- Login with post-reset success message and return-url redirect
- Forgot password: email → OTP send → OTP verify (2-step)
- Reset password: new + confirm password (min 8 chars)
- Resend OTP, change email, session recovery storage
- Shared auth UI components (`AuthAlert`, `AuthTextField`, etc.)
- Guest route redirects authenticated users away from auth pages
- 401 interceptor skips redirect on public auth paths

## Setup

```bash
cd frontend
cp .env.example .env   # optional — dev proxy works with empty VITE_API_BASE_URL
npm install
npm run dev
```

Dev server: `http://localhost:5173`  
API proxy: `/api` → `http://localhost:8080`

## Environment

| Variable | Description |
|----------|-------------|
| `VITE_API_BASE_URL` | API origin (empty = same-origin + Vite proxy) |

## Phase 4.3 — Profile & employee management (complete)

- **My Profile** (`/employee/profile`, `/hr/profile`, `/admin/profile`)
  - Personal tab: photo upload/delete, edit name/phone/address/DOB/gender
  - Employment tab: read-only employment details
  - Bank tab: edit bank details (Employee & HR only)
- **HR Employees** (`/hr/employees`, `/hr/employees/:id`)
  - Paginated list with search/filters, create employee modal, view/edit/deactivate
- **Admin HR Management** (`/admin/hr`, `/admin/hr/:id`)
  - HR list, create HR modal, view/edit/deactivate
- Profile photos via authenticated `GET /api/profile/photo/{userId}` blob fetch
- `employeeService` + shared form/profile components

## Phase 4.4 — Attendance pages (complete)

- **Employee** (`/employee/attendance`)
  - Check-in / check-out with weekend guard and today status
  - Monthly summary cards and date-range history table
- **HR** (`/hr/attendance`)
  - Employee attendance report with date range and optional employee filter
  - Aggregate or per-employee summary (present, absent, leave, missing checkout)
- **Admin** (`/admin/attendance`)
  - HR attendance report with the same filters scoped to HR users
- `attendanceService` + shared attendance UI components

## Phase 4.5 — Leave pages (complete)

- **Employee** (`/employee/leaves`)
  - Leave balance cards (annual limit, paid used, remaining, unpaid used)
  - Apply-for-leave modal (type, duration, dates, reason)
  - History table with cancel for pending requests
- **HR** (`/hr/leaves`)
  - **My leave** tab — same as employee (HR can apply for leave)
  - **Employee requests** tab — date-range report, employee filter, approve / reject / convert to unpaid
- **Admin** (`/admin/leaves`)
  - HR leave approval report with date range and HR user filter
  - Approve, reject, and convert-to-unpaid actions
- `leaveService` + shared leave UI components

## Phase 4.6 — Payroll & payslip pages (complete)

- **Employee** (`/employee/payroll`, `/employee/payslips`)
  - Payroll history table with detail modal
  - Payslip list with view modal and PDF download
- **HR** (`/hr/payroll`)
  - Summary cards, generate payroll (year/month/bonus), monthly report
  - Credit salary for `GENERATED` records, payroll detail modal
- **Admin** (`/admin/payroll`)
  - HR payroll management (same flow scoped to HR users)
- `payrollService`, `payslipService` + shared payroll/payslip UI components
- PDF download via `GET /api/payslips/{id}/download` blob helper

## Phase 4.7 — Notifications pages (complete)

- **All roles** (`/employee/notifications`, `/hr/notifications`, `/admin/notifications`)
  - Recent (latest 10) and history (30 days) tabs
  - Mark as read on open, mark all as read, detail modal with navigation
- **Header bell dropdown** in `PortalShell` — quick preview, mark all, view all
  - 60s poll via existing `NotificationContext`
- `notificationService` + role-based redirect paths (`LEAVE`, `PAYROLL`, `PAYSLIP`, `ATTENDANCE`, `PROFILE`)

## Phase 4.8 — Reports & system settings (complete)

- **HR / Admin dashboards** (`/hr`, `/admin`)
  - Analytics from `GET /api/reports/dashboard` (workforce counts, attendance, leave, payroll)
- **Reports** (`/hr/reports`, `/admin/reports`)
  - PDF download: employees/HR roster, attendance, leave, payroll (filtered)
- **Admin settings** (`/admin/settings`)
  - Tabs: Company, Attendance, Leave, Payroll — each section saves via dedicated PUT endpoint
- `reportService`, `settingsService` + dashboard/report UI components
- Employee home redirects to profile (no reports/settings access)

## Frontend phases complete

All Phase 4.1–4.8 portal pages are implemented. Run backend + `npm run dev` for end-to-end testing.
