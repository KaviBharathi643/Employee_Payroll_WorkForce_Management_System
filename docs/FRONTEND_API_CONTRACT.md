# Frontend API Contract

**Authority:** Backend implementation (`backend/`) + `FINAL_PROJECT_BLUEPRINT.md`  
**Version:** 1.0 — aligned with backend Phases 3.1–3.11  
**Base URL:** `import.meta.env.VITE_API_BASE_URL` (default dev: `http://localhost:8080`)

---

## 1. Transport

| Item | Value |
|------|--------|
| Protocol | HTTPS in production; HTTP localhost in dev |
| Content-Type (JSON) | `application/json` |
| Auth header | `Authorization: Bearer <jwt>` |
| CORS origin (dev) | `http://localhost:5173` |
| Date format | ISO-8601 `YYYY-MM-DD` |
| DateTime format | ISO-8601 `YYYY-MM-DDTHH:mm:ss` |
| Decimal numbers | JSON number or string (use `BigDecimal`-safe parsing for money) |

### 1.1 Response envelope (JSON endpoints)

All JSON controllers return:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": { }
}
```

| HTTP | When |
|------|------|
| 200 | Success |
| 400 | Validation / business rule (`success: false`) |
| 401 | Unauthorized / invalid credentials |
| 403 | Access denied |
| 404 | Resource not found |
| 500 | Unexpected server error |

Frontend must check `response.data.success` (Axios) before using `data`.

### 1.2 Binary endpoints

These return **raw bytes** (not `ApiResponse`):

| Endpoint | Content-Type | Disposition |
|----------|--------------|-------------|
| `GET /api/payslips/{id}/download` | `application/pdf` | `attachment; filename="{payslipNumber}.pdf"` |
| `GET /api/reports/employees` | `application/pdf` | `attachment; filename="employee-report-YYYY-MM-DD.pdf"` |
| `GET /api/reports/attendance` | `application/pdf` | `attachment; filename="attendance-report-YYYY-MM-DD.pdf"` |
| `GET /api/reports/leaves` | `application/pdf` | `attachment; filename="leave-report-YYYY-MM-DD.pdf"` |
| `GET /api/reports/payroll` | `application/pdf` | `attachment; filename="payroll-report-YYYY-MM-DD.pdf"` |
| `GET /api/profile/photo/{userId}` | `image/jpeg` or `image/png` | inline body |

Use `responseType: 'blob'` in Axios. Create object URLs for preview/download.

### 1.3 Multipart upload

| Endpoint | Field | Max size |
|----------|-------|----------|
| `POST /api/profile/photo` | `profilePhoto` (file) | 2 MB, JPG/JPEG/PNG |

---

## 2. Authentication

| Method | Path | Auth | Body | Response `data` |
|--------|------|------|------|-----------------|
| POST | `/api/auth/login` | Public | `LoginRequest` | `LoginResponse` |
| POST | `/api/auth/forgot-password` | Public | `{ email }` | `null` |
| POST | `/api/auth/verify-otp` | Public | `{ email, otpCode }` | `null` |
| POST | `/api/auth/reset-password` | Public | `ResetPasswordRequest` | `null` |
| GET | `/api/auth/me` | **JWT required** | — | `MeResponse` |

**LoginRequest:** `{ email, password }`  
**ResetPasswordRequest:** `{ email, otpCode, newPassword, confirmPassword }`  
**LoginResponse:** `{ token, userId, employeeCode, fullName, email, role }`  
**MeResponse:** `{ userId, email, role, status, employeeCode, fullName, dateOfBirth, gender, phoneNumber, address, profilePhotoUrl, department, designation }`

**Post-login routing:**

| `role` | Portal prefix |
|--------|----------------|
| `EMPLOYEE` | `/employee` |
| `HR` | `/hr` |
| `ADMIN` | `/admin` |

**401 handling:** Clear token, redirect to `/login`.

---

## 3. Roles & access matrix

| Resource | EMPLOYEE | HR | ADMIN |
|----------|----------|-----|-------|
| Settings | — | — | ✓ |
| Create employee | — | ✓ | — |
| Create HR | — | — | ✓ |
| Employee list/detail | — | ✓ | — |
| HR list/detail | — | — | ✓ |
| Own profile | ✓ | ✓ | ✓ |
| Bank details (own) | ✓ | ✓ | — |
| Attendance check-in/out | ✓ | — | — |
| Attendance view scope | own | employees | HR |
| Leave apply | ✓ | ✓ | — |
| Leave approve | — | employee leaves | HR leaves |
| Payroll generate | — | employees | HR |
| Payroll credit | — | employees | HR |
| Payroll view | own | employees | HR |
| Payslips | own | employees | HR |
| Reports / dashboard | — | ✓ (employees) | ✓ (HR) |
| Notifications | own | own | own |
| Profile photo download | own | employees + own | HR + own |

---

## 4. Endpoints by module

### 4.1 Health

| GET | `/api/health` | Public |

### 4.2 Employees & profile

| Method | Path | Roles | Notes |
|--------|------|-------|-------|
| POST | `/api/employees` | HR | `CreateEmployeeRequest` |
| POST | `/api/employees/hr` | ADMIN | `CreateHrRequest` |
| GET | `/api/employees` | HR | Query: `search?, department?, designation?, page=0, size=10, sortBy=fullName, sortDir=asc` → `PagedResponse<EmployeeListItem>` |
| GET | `/api/employees/{id}` | HR | `EmployeeResponse` |
| GET | `/api/employees/hr` | ADMIN | `EmployeeListItem[]` |
| GET | `/api/employees/hr/{id}` | ADMIN | `EmployeeResponse` |
| GET | `/api/employees/profile` | ALL | `EmployeeResponse` (own) |
| PUT | `/api/employees/profile` | ALL | `UpdateProfileRequest` |
| PUT | `/api/employees/bank-details` | HR, EMPLOYEE | `UpdateBankDetailsRequest` |
| PUT | `/api/employees/{id}` | HR | `UpdateEmployeeRequest` |
| PUT | `/api/employees/hr/{id}` | ADMIN | `UpdateHrRequest` |
| PATCH | `/api/employees/{id}/deactivate` | HR | — |
| PATCH | `/api/employees/hr/{id}/deactivate` | ADMIN | — |
| GET | `/api/profile/photo/{userId}` | ALL (scoped) | **Blob** — do not use static `/uploads/profile/` URLs |
| POST | `/api/profile/photo` | ALL | multipart `profilePhoto` |
| DELETE | `/api/profile/photo` | ALL | — |

**CreateEmployeeRequest / CreateHrRequest (required fields):**  
`email, fullName, dateOfBirth, gender, phone, address, department, designation, joiningDate, basicSalary`  
+ `employmentType` (`FULL_TIME` \| `INTERN`) for employees only.

**EmployeeResponse:**  
`userId, employeeCode, fullName, email, dateOfBirth, gender, phone, address, profilePhotoUrl, department, designation, joiningDate, employmentType, basicSalary, employmentStatus, userStatus, bankName, accountNumber?, maskedAccountNumber?, ifscCode?`  
— Non-owner HR/Admin views mask bank as `maskedAccountNumber` only.

### 4.3 System settings (ADMIN)

| Method | Path | Body | Response `data` |
|--------|------|------|-----------------|
| GET | `/api/settings` | — | `SystemSettings` |
| PUT | `/api/settings/company` | `UpdateCompanySettings` | `SystemSettings` |
| PUT | `/api/settings/attendance` | `UpdateAttendanceSettings` | `SystemSettings` |
| PUT | `/api/settings/leave` | `UpdateLeaveSettings` | `SystemSettings` |
| PUT | `/api/settings/payroll` | `UpdatePayrollSettings` | `SystemSettings` |

**SystemSettings:**  
`companyName, companyAddress, companyEmail, companyPhone, officeStartTime, officeEndTime, checkoutReminderHours, annualPaidLeaveLimit, pfPercentage, salaryCreditDay`

### 4.4 Attendance

| Method | Path | Roles | Query / body |
|--------|------|-------|--------------|
| POST | `/api/attendance/check-in` | EMPLOYEE | — |
| POST | `/api/attendance/check-out` | EMPLOYEE | — |
| GET | `/api/attendance/me` | EMPLOYEE | `fromDate?, toDate?` |
| GET | `/api/attendance/employee/{id}` | HR, ADMIN | `fromDate?, toDate?` |
| GET | `/api/attendance/summary` | ALL | `userId?, fromDate?, toDate?` |
| GET | `/api/attendance/report` | HR, ADMIN | `fromDate, toDate, userId?` → JSON list |

**AttendanceRecord:**  
`attendanceId, attendanceDate, checkInTime, checkOutTime, status, userId, employeeCode, employeeName`

**AttendanceSummary:**  
`presentCount, absentCount, leaveCount, halfDayLeaveCount, missingCheckoutCount`

**Status values:** `PRESENT`, `ABSENT`, `LEAVE`, `HALF_DAY_LEAVE`

### 4.5 Leave

| Method | Path | Roles | Notes |
|--------|------|-------|-------|
| POST | `/api/leaves` | EMPLOYEE, HR | `ApplyLeaveRequest` |
| PUT | `/api/leaves/{id}/approve` | HR, ADMIN | service validates approver chain |
| PUT | `/api/leaves/{id}/reject` | HR, ADMIN | |
| PUT | `/api/leaves/{id}/cancel` | ALL | owner only |
| PUT | `/api/leaves/{id}/convert-unpaid` | HR, ADMIN | |
| GET | `/api/leaves/my-history` | ALL | own history |
| GET | `/api/leaves/balance` | ALL | own balance |
| GET | `/api/leaves/employee/{id}` | HR | |
| GET | `/api/leaves/hr/{id}` | ADMIN | |
| GET | `/api/leaves/report` | HR, ADMIN | `fromDate, toDate, userId?` |

**ApplyLeaveRequest:** `{ leaveType, durationType, startDate, endDate, reason }`  
**leaveType:** `CASUAL`, `SICK`, `UNPAID`  
**durationType:** `HALF_DAY`, `FULL_DAY`, `MULTI_DAY`  
**status:** `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`

**LeaveBalance:** `{ paidLeaveUsed, remainingPaidLeave, unpaidLeaveUsed, annualPaidLeaveLimit }`

### 4.6 Payroll

| Method | Path | Roles | Notes |
|--------|------|-------|-------|
| POST | `/api/payrolls/generate` | HR, ADMIN | `{ payrollYear, payrollMonth, bonusAmount? }` → `GeneratePayrollResponse` |
| POST | `/api/payrolls/{id}/credit` | HR, ADMIN | scoped by target user role |
| GET | `/api/payrolls` | ALL | role-scoped list |
| GET | `/api/payrolls/summary` | HR, ADMIN | `PayrollSummary` |
| GET | `/api/payrolls/history` | ALL | own history |
| GET | `/api/payrolls/report` | HR, ADMIN | `payrollYear, payrollMonth` → JSON list |
| GET | `/api/payrolls/{id}` | ALL | scoped detail |

**PayrollRecord:**  
`payrollId, userId, payrollYear, payrollMonth, employeeCode, employeeName, department, designation, basicSalary, bonus, pfPercentage, pfAmount, unpaidLeaveCount, unpaidLeaveDeduction, finalSalary, status, generatedDate, creditedDate`

**status:** `GENERATED`, `CREDITED`  
**Formula (display only):** `finalSalary = basicSalary + bonus - pfAmount - unpaidLeaveDeduction`

### 4.7 Payslips

| Method | Path | Roles | Response |
|--------|------|-------|----------|
| GET | `/api/payslips` | ALL (scoped) | `PayslipListItem[]` |
| GET | `/api/payslips/{id}` | ALL (scoped) | `PayslipDetail` |
| GET | `/api/payslips/{id}/download` | ALL (scoped) | PDF blob |

**PayslipListItem:** `payslipId, payslipNumber, payrollMonth, payrollYear, generatedDate, employeeCode, employeeName`  
**PayslipDetail:** snapshot fields + `maskedAccountNumber`, `pdfGenerated`

### 4.8 Notifications

| Method | Path | Response `data` |
|--------|------|-----------------|
| GET | `/api/notifications` | `NotificationListItem[]` (latest 10) |
| GET | `/api/notifications/history` | `NotificationListItem[]` (30 days) |
| GET | `/api/notifications/bell` | `{ latestNotifications, unreadCount }` |
| PUT | `/api/notifications/{id}/read` | `NotificationDetail` |
| PUT | `/api/notifications/read-all` | `null` |

**Poll interval (frozen):** 60 seconds for bell badge.

**redirect navigation (`notificationType` / `relatedEntityType`):**

| Type | Route hint |
|------|------------|
| `LEAVE` | `/…/leaves` + `relatedEntityId` |
| `PAYROLL` | `/…/payroll` + id |
| `PAYSLIP` | `/…/payslips` + id |
| `ATTENDANCE` | `/…/attendance` + id |
| `PROFILE` | `/…/profile` |

**status:** `READ`, `UNREAD`

### 4.9 Reports & dashboard

| Method | Path | Roles | Response |
|--------|------|-------|----------|
| GET | `/api/reports/employees` | HR, ADMIN | PDF |
| GET | `/api/reports/attendance` | HR, ADMIN | PDF — query: `fromDate?, toDate?, department?, designation?, employeeCode?` |
| GET | `/api/reports/leaves` | HR, ADMIN | PDF — query: `fromDate?, toDate?, department?, designation?, employeeCode?, leaveType?, leaveStatus?` |
| GET | `/api/reports/payroll` | HR, ADMIN | PDF — `payrollYear, payrollMonth` required |
| GET | `/api/reports/dashboard` | HR, ADMIN | `DashboardAnalytics` JSON |

**DashboardAnalytics:**

```ts
{
  employeeSummary?: { totalUsers, activeUsers, inactiveUsers },  // HR only
  hrSummary?: { totalUsers, activeUsers, inactiveUsers },        // ADMIN only
  attendanceSummary: AttendanceSummary,
  leaveSummary: LeaveSummary,
  payrollSummary: {
    totalSalaryPaid, totalPfDeduction, totalUnpaidLeaveDeduction,
    generatedPayrollCount, creditedPayrollCount, pendingSalaryCredits
  }
}
```

---

## 5. Enumerations

| Domain | Values |
|--------|--------|
| `role` | `ADMIN`, `HR`, `EMPLOYEE` |
| `gender` | `MALE`, `FEMALE`, `OTHER` |
| `userStatus` / `employmentStatus` | `ACTIVE`, `INACTIVE` |
| `employmentType` | `FULL_TIME`, `INTERN` |
| `leaveType` | `CASUAL`, `SICK`, `UNPAID` |
| `durationType` | `HALF_DAY`, `FULL_DAY`, `MULTI_DAY` |
| `leaveStatus` | `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED` |
| `attendanceStatus` | `PRESENT`, `ABSENT`, `LEAVE`, `HALF_DAY_LEAVE` |
| `payrollStatus` | `GENERATED`, `CREDITED` |
| `notificationStatus` | `READ`, `UNREAD` |
| `report employmentStatus filter` | `ACTIVE`, `INACTIVE`, `ALL` |

---

## 6. Frontend integration rules

1. Store JWT in memory + `sessionStorage` (or `localStorage` per team choice); attach via Axios interceptor.
2. Never hardcode production API URL — use `VITE_API_BASE_URL`.
3. Profile images: always fetch via `GET /api/profile/photo/{userId}` with Bearer token.
4. PDF downloads: blob → `URL.createObjectURL` → trigger `<a download>`.
5. Dashboard charts must use `GET /api/reports/dashboard` (same totals as reports).
6. EMPLOYEE portal must not call `/api/reports/*` or `/api/settings/*`.
7. Forms for create employee/HR and profile update must include **dateOfBirth** + **gender**.
8. On `success: false` or 4xx, surface `message` to the user.

---

## 7. Dev defaults

| Item | Value |
|------|--------|
| API | `http://localhost:8080` |
| Frontend | `http://localhost:5173` |
| Admin login | `admin@company.com` / `Admin@123456` |

---

## 8. Endpoint count

| Module | Endpoints |
|--------|-----------|
| Health | 1 |
| Auth | 5 |
| Employees + Profile | 16 |
| Settings | 5 |
| Attendance | 6 |
| Leaves | 10 |
| Payrolls | 7 |
| Payslips | 3 |
| Notifications | 5 |
| Reports | 5 |
| **Total** | **63** |
