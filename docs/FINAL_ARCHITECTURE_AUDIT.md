# FINAL_ARCHITECTURE_AUDIT.md

**Audit Date:** 2026-06-04  
**Auditor Role:** Senior Architecture / Security / Database / QA Review  
**Reference:** `FINAL_PROJECT_BLUEPRINT.md` (v1.0.0-FREEZE)  
**Method:** Adversarial review of architecture, database, security, schedulers, APIs, RBAC, and runtime workflows. Stylistic preferences excluded.

---

## Audit Scope

| Area | Challenged |
|------|------------|
| Architecture | Layering, module boundaries, coupling |
| Database | Constraints, 1:1 integrity, snapshot model |
| Security | JWT, OTP, files, RBAC, ownership |
| Runtime | Attendance status, payroll concurrency, leave/payroll edge cases |
| Schedulers | Timing, idempotency, side effects |
| APIs | Consistency, role alignment, missing endpoints |
| Dependencies | Circular calls, shared services |
| Infrastructure | PaaS file storage, health, CORS |
| Documentation | Missing repo files |

**Resolved issue baseline:** 40 frozen decisions (R-01–R-40) in blueprint Section 29 — not re-opened unless mitigation is insufficient.

---

## Mitigation Adequacy Review (Former High-Risk Items)

| Former Risk | Blueprint Mitigation | Adequate? |
|-------------|---------------------|-----------|
| Notification schema mismatch | R-02, R-17, R-18 | Yes |
| Salary formula ambiguity | R-01 | Yes |
| Settings reminder field mismatch | R-03, R-04 | Yes |
| DOB/gender NOT NULL vs DTO | R-06, R-39, R-40 | Yes |
| employment/bank 1:1 not in DDL | R-12 | Yes — service enforcement mandatory |
| Payroll duplicate month | R-07, R-31 | Yes |
| Profile photo enumeration | R-32 | Yes |
| OTP brute force | R-28 | Yes |
| RBAC path /api/admin/** | R-05, Blueprint §18 | Yes |
| Payslip PDF storage | R-10, R-33 | Yes with MD-01 caveat below |

---

## Open Issues (Post-Freeze)

### Critical Issues

**None identified.** No issue blocks safe implementation if blueprint rules are followed.

---

### High Issues

**None unmitigated.** All prior high-severity items have frozen resolutions (R-01–R-40).

---

### Medium Issues

#### MD-01 — Ephemeral payslip PDF storage on PaaS

| Field | Detail |
|-------|--------|
| **Problem** | Payslip PDFs stored on local filesystem (`backend/uploads/payslips/`) are lost on container redeploy without persistent volumes. |
| **Impact** | `pdf_generated=true` but file missing → download fails after redeploy. |
| **Recommended Fix** | Implement `PayslipService.getPdfBytes()`: if file missing, regenerate PDF from payroll snapshot (idempotent). Document ops requirement for persistent volume in production. |
| **Severity** | Medium |

#### MD-02 — JWT stored in browser (typical React localStorage)

| Field | Detail |
|-------|--------|
| **Problem** | XSS could exfiltrate JWT; no server-side revocation list. |
| **Impact** | Stolen token valid until expiry (frozen 24h prod). |
| **Recommended Fix** | Short expiry (R-29), sanitize inputs, CSP headers; post-MVP consider httpOnly cookie + refresh token pattern (out of current scope). |
| **Severity** | Medium |

#### MD-03 — Same-day attendance status vs leave approval order

| Field | Detail |
|-------|--------|
| **Problem** | Employee checks in (PRESENT), then leave approved same day — status may remain PRESENT until recalculation. |
| **Impact** | Dashboard/report mismatch for that day until reconcile. |
| **Recommended Fix** | On leave approve/reject: invoke `AttendanceService.recalculateStatus(userId, date)` for each day in range (R-30). Optional nightly batch for all users. |
| **Severity** | Medium |

#### MD-04 — Multi-day leave spanning calendar months (payroll)

| Field | Detail |
|-------|--------|
| **Problem** | Unpaid leave sum uses `month(startDate)` and `year(startDate)` per payroll pseudocode — leave spanning months may attribute all days to start month only. |
| **Impact** | Deduction may apply to wrong payroll month for cross-month unpaid leave. |
| **Recommended Fix** | Frozen implementation rule: allocate each leave day to its calendar month when summing unpaid days for payroll (aligns with business intent without schema change). Document in PayrollCalculationService. |
| **Severity** | Medium |

#### MD-05 — Missing checkout penalty as synthetic leave row

| Field | Detail |
|-------|--------|
| **Problem** | Penalty creates UNPAID `leave_requests` (0.5 day) — may appear in leave history UI unless filtered. |
| **Impact** | HR/employee confusion; possible double-penalty if scheduler runs twice without idempotency. |
| **Recommended Fix** | Tag reason prefix `"Missing Checkout Penalty"`; scheduler checks no existing penalty row for same user+date before insert; filter from employee leave history UI OR show with distinct badge. |
| **Severity** | Medium |

#### MD-06 — Repository documentation gaps

| Field | Detail |
|-------|--------|
| **Problem** | `FRONTEND_ARCHITECTURE.md` and `database-rules.mdc` not in repository. |
| **Impact** | Onboarding drift if developers use wrong reference. |
| **Recommended Fix** | Blueprint Section 22 + `project-rules.mdc` are authoritative; optionally add stub docs pointing to blueprint (post-freeze, non-blocking). |
| **Severity** | Medium |

---

### Low Issues

#### LD-01 — Notification real-time delivery

| Field | Detail |
|-------|--------|
| **Problem** | Bell uses polling (60s), not WebSocket. |
| **Impact** | Delayed unread badge. |
| **Recommended Fix** | Accept for MVP; poll interval configurable. |
| **Severity** | Low |

#### LD-02 — `personal_email` column unused in primary flows

| Field | Detail |
|-------|--------|
| **Problem** | DB column exists; DTOs omit unless profile extended. |
| **Impact** | Unused field always NULL. |
| **Recommended Fix** | Optional on profile update (R-26) or ignore. |
| **Severity** | Low |

#### LD-03 — No automated E2E test spec document

| Field | Detail |
|-------|--------|
| **Problem** | Testing checklist not yet written (Phase D deliverable). |
| **Impact** | QA coverage risk at release. |
| **Recommended Fix** | Generate testing checklist before release per project plan. |
| **Severity** | Low |

#### LD-04 — Default password emailed once

| Field | Detail |
|-------|--------|
| **Problem** | Welcome email contains temporary password; not in pseudocode but required operationally (R-14). |
| **Impact** | Email interception risk. |
| **Recommended Fix** | Encourage immediate password change via forgot-password flow; document in admin runbook. |
| **Severity** | Low |

#### LD-05 — Actuator exposure

| Field | Detail |
|-------|--------|
| **Problem** | `/actuator/health` public if not restricted. |
| **Impact** | Information disclosure minimal. |
| **Recommended Fix** | Permit health only; secure other actuator endpoints in prod config. |
| **Severity** | Low |

---

## Issues Explicitly Rejected (Not Real Defects)

| Item | Reason |
|------|--------|
| "Need GPS attendance" | Out of scope per master spec §16 |
| "Need tax module" | Out of scope |
| "Add MANAGER role" | Violates frozen role set |
| "Change to session auth" | Violates project-rules |
| "Modify DATABASE_SCHEMA tables" | Forbidden — mitigations used instead |
| "Merge payroll and payslip modules" | Violates module list |

---

## RBAC Challenge Matrix (Sample — Pass)

| Action | EMPLOYEE | HR | ADMIN | Verdict |
|--------|----------|-----|-------|---------|
| Create employee | Deny | Allow | Deny | Pass |
| Create HR | Deny | Deny | Allow | Pass |
| Check-in | Allow | Deny | Deny | Pass |
| Approve employee leave | Deny | Allow | Deny | Pass |
| Approve HR leave | Deny | Deny | Allow | Pass |
| Generate employee payroll | Deny | Allow | Deny | Pass |
| Generate HR payroll | Deny | Deny | Allow | Pass |
| View employee payslip | Own | Allow | Deny | Pass |
| View HR payslip | Deny | Deny | Allow | Pass |
| System settings | Deny | Deny | Allow | Pass |
| Reports | Deny | Allow | Allow | Pass |

---

## Scheduler Challenge (Pass with conditions)

| Scheduler | Risk | Verdict |
|-----------|------|---------|
| Payroll reminder | Wrong timezone | Use UTC or configurable zone in prod yml |
| Attendance reminder | Duplicate notifications | Idempotent: check notification exists for user+date+type |
| Notification cleanup | Hard delete | Accept per spec; use expires_at index |

---

## API Dependency Challenge (Pass)

- No circular module dependency (Notifications depend on domain events, not vice versa for business logic).
- Payslip generation called from PayrollService only — not exposed as duplicate public generator.
- Reports read-only — no write side effects.

---

## Database Challenge (Pass with R-12 enforcement)

- Snapshot payroll: immutable by design — Pass.
- Missing UNIQUE on `user_employment.user_id` / `user_bank_details.user_id`: **Pass if R-12 enforced** in code review gate.
- `attendance` UNIQUE(user_id, attendance_date): present — Pass.

---

## Audit Summary

| Severity | Count (Open) |
|----------|----------------|
| Critical | 0 |
| High | 0 |
| Medium | 6 |
| Low | 5 |

**Conclusion:** Medium/Low issues are addressable during implementation without blueprint or schema changes. No freeze blockers.

---

## Audit Status

**AUDIT COMPLETE** — Refer to `PROJECT_FREEZE_REPORT.md` for freeze decision.
