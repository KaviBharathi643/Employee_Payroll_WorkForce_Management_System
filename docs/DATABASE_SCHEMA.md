# DATABASE_SCHEMA.md

# Employee Payroll & Workforce Management System

## Database Information

Database: MySQL 8+

Naming Convention: snake_case

Primary Key Type: BIGINT AUTO_INCREMENT

Soft Delete Strategy: ACTIVE / INACTIVE

Audit Fields:

* created_at
* updated_at

---

# TABLE 1: users

Purpose:

Authentication and Role Management

| Column     | Type         | Constraints        |
| ---------- | ------------ | ------------------ |
| id         | BIGINT       | PK, AUTO_INCREMENT |
| email      | VARCHAR(255) | UNIQUE, NOT NULL   |
| password   | VARCHAR(255) | NOT NULL           |
| role       | VARCHAR(20)  | NOT NULL           |
| status     | VARCHAR(20)  | NOT NULL           |
| created_at | TIMESTAMP    | NOT NULL           |
| updated_at | TIMESTAMP    | NOT NULL           |

Role Values:

* ADMIN
* HR
* EMPLOYEE

Status Values:

* ACTIVE
* INACTIVE

Indexes:

* UNIQUE(email)
* INDEX(role)

---

# TABLE 2: user_profiles

Purpose:

Personal Information

user_profiles

| Column            | Type                              | Constraints                      |
| ----------------- | --------------------------------- | -------------------------------- |
| id                | BIGINT                            | PK, AUTO_INCREMENT               |
| user_id           | BIGINT                            | FK → users.id, UNIQUE, NOT NULL  |
| employee_code     | VARCHAR(20)                       | UNIQUE, NOT NULL                 |
| full_name         | VARCHAR(150)                      | NOT NULL                         |
| date_of_birth     | DATE                              | NOT NULL                         |
| gender            | ENUM('MALE','FEMALE','OTHER')     | NOT NULL                         |
| phone_number      | VARCHAR(15)                       | NOT NULL                         |
| personal_email    | VARCHAR(150)                      | UNIQUE                           |
| address           | TEXT                              | NOT NULL                         |
| profile_photo_url | VARCHAR(255)                      | NULL                             |
| created_at        | TIMESTAMP                         | DEFAULT CURRENT_TIMESTAMP        |
| updated_at        | TIMESTAMP                         | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

Examples:

* EMP001
* EMP002
* HR001
* HR002

Indexes:

* UNIQUE(employee_code)
* INDEX(user_id)



---

# TABLE 3: user_employment

Purpose:

Employment Information

| Column               | Type          | Constraints         |
| -------------------- | ------------- | ------------------- |
| id                   | BIGINT        | PK, AUTO_INCREMENT  |
| user_id              | BIGINT        | FK → users.id       |
| reporting_manager_id | BIGINT        | FK → users.id, NULL |
| department           | VARCHAR(100)  | NOT NULL            |
| designation          | VARCHAR(100)  | NOT NULL            |
| joining_date         | DATE          | NOT NULL            |
| employment_type      | VARCHAR(20)   | NOT NULL            |
| basic_salary         | DECIMAL(10,2) | NOT NULL            |
| employment_status    | VARCHAR(20)   | NOT NULL            |
| created_at           | TIMESTAMP     | NOT NULL            |
| updated_at           | TIMESTAMP     | NOT NULL            |

Employment Type:

* FULL_TIME
* INTERN

Employment Status:

* ACTIVE
* INACTIVE

Indexes:

* INDEX(user_id)
* INDEX(department)
* INDEX(employment_status)

---

# TABLE 4: user_bank_details

Purpose:

Bank Information

| Column         | Type         | Constraints        |
| -------------- | ------------ | ------------------ |
| id             | BIGINT       | PK, AUTO_INCREMENT |
| user_id        | BIGINT       | FK → users.id      |
| bank_name      | VARCHAR(150) | NOT NULL           |
| account_number | VARCHAR(50)  | NOT NULL           |
| ifsc_code      | VARCHAR(20)  | NOT NULL           |
| created_at     | TIMESTAMP    | NOT NULL           |
| updated_at     | TIMESTAMP    | NOT NULL           |

Business Rule:

Payroll cannot be generated if bank details are missing.

---

# TABLE 5: attendance

Purpose:

Attendance Tracking

| Column          | Type        | Constraints        |
| --------------- | ----------- | ------------------ |
| id              | BIGINT      | PK, AUTO_INCREMENT |
| user_id         | BIGINT      | FK → users.id      |
| attendance_date | DATE        | NOT NULL           |
| check_in_time   | TIMESTAMP   | NULL               |
| check_out_time  | TIMESTAMP   | NULL               |
| status          | VARCHAR(20) | NOT NULL           |
| created_at      | TIMESTAMP   | NOT NULL           |
| updated_at      | TIMESTAMP   | NOT NULL           |

Status:

PRESENT

ABSENT

LEAVE

HALF_DAY_LEAVE

Constraints:

One attendance record per employee per day.

Indexes:

* UNIQUE(user_id, attendance_date)
* INDEX(attendance_date)

---

# TABLE 6: leave_requests

Purpose:

Leave Management

| Column        | Type         | Constraints        |
| ------------- | ------------ | ------------------ |
| id            | BIGINT       | PK, AUTO_INCREMENT |
| user_id       | BIGINT       | FK → users.id      |
| leave_type    | VARCHAR(20)  | NOT NULL           |
| duration_type | VARCHAR(20)  | NOT NULL           |
| start_date    | DATE         | NOT NULL           |
| end_date      | DATE         | NOT NULL           |
| leave_days    | DECIMAL(4,1) | NOT NULL           |
| reason        | TEXT         | NOT NULL           |
| status        | VARCHAR(20)  | NOT NULL           |
| approved_by   | BIGINT       | FK → users.id      |
| cancelled_at  | TIMESTAMP    | NULL               |
| created_at    | TIMESTAMP    | NOT NULL           |
| updated_at    | TIMESTAMP    | NOT NULL           |

Leave Types:

* CASUAL
* SICK
* UNPAID

Duration Types:

* HALF_DAY
* FULL_DAY
* MULTI_DAY

Status:

* PENDING
* APPROVED
* REJECTED
* CANCELLED

Indexes:

* INDEX(user_id)
* INDEX(status)
* INDEX(start_date)

---

# TABLE 7: payrolls

Purpose:

Payroll History

| Column                 | Type          | Constraints        |
| ---------------------- | ------------- | ------------------ |
| id                     | BIGINT        | PK, AUTO_INCREMENT |
| user_id                | BIGINT        | FK → users.id      |
| payroll_year           | INT           | NOT NULL           |
| payroll_month          | INT           | NOT NULL           |
| employee_code          | VARCHAR(20)   | NOT NULL           |
| employee_name          | VARCHAR(150)  | NOT NULL           |
| department             | VARCHAR(100)  | NOT NULL           |
| designation            | VARCHAR(100)  | NOT NULL           |
| basic_salary           | DECIMAL(10,2) | NOT NULL           |
| bonus                  | DECIMAL(10,2) | NOT NULL           |
| pf_percentage          | DECIMAL(5,2)  | NOT NULL           |
| pf_amount              | DECIMAL(10,2) | NOT NULL           |
| unpaid_leave_count     | DECIMAL(4,1)  | NOT NULL           |
| unpaid_leave_deduction | DECIMAL(10,2) | NOT NULL           |
| final_salary           | DECIMAL(10,2) | NOT NULL           |
| generated_date         | TIMESTAMP     | NOT NULL           |
| credited_date          | TIMESTAMP     | NULL               |
| generated_by           | BIGINT        | FK → users.id      |
| credited_by            | BIGINT        | FK → users.id      |
| status                 | VARCHAR(20)   | NOT NULL           |
| created_at             | TIMESTAMP     | NOT NULL           |
| updated_at             | TIMESTAMP     | NOT NULL           |

Status:

* GENERATED
* CREDITED

Business Rules:

* One payroll per employee per month
* Payroll stores historical snapshots
* Payroll history never changes

Indexes:

* INDEX(user_id)
* INDEX(payroll_year)
* INDEX(payroll_month)
* INDEX(status)

---

# TABLE 8: payslips

Purpose:

Payslip Records

| Column         | Type        | Constraints        |
| -------------- | ----------- | ------------------ |
| id             | BIGINT      | PK, AUTO_INCREMENT |
| payroll_id     | BIGINT      | FK → payrolls.id   |
| payslip_number | VARCHAR(50) | UNIQUE             |
| pdf_generated  | BOOLEAN     | NOT NULL           |
| generated_date | TIMESTAMP   | NOT NULL           |
| status         | VARCHAR(20) | NOT NULL           |
| created_at     | TIMESTAMP   | NOT NULL           |
| updated_at     | TIMESTAMP   | NOT NULL           |

Example:

PSL-2026-0001

Indexes:

* UNIQUE(payslip_number)
* INDEX(payroll_id)

---

# TABLE 9: notifications

Purpose:

Notification Center

| Column        | Type         | Constraints        |
| ------------- | ------------ | ------------------ |
| id            | BIGINT       | PK, AUTO_INCREMENT |
| user_id       | BIGINT       | FK → users.id      |
| title         | VARCHAR(255) | NOT NULL           |
| message       | TEXT         | NOT NULL           |
| redirect_type | VARCHAR(30)  | NOT NULL           |
| is_read       | BOOLEAN      | DEFAULT FALSE      |
| expires_at    | TIMESTAMP    | NOT NULL           |
| created_at    | TIMESTAMP    | NOT NULL           |

Redirect Types:

* LEAVE
* PAYROLL
* ATTENDANCE
* PROFILE

Indexes:

* INDEX(user_id)
* INDEX(expires_at)

---

# TABLE 10: otp_verifications

Purpose:

Forgot Password OTP

| Column     | Type        | Constraints        |
| ---------- | ----------- | ------------------ |
| id         | BIGINT      | PK, AUTO_INCREMENT |
| user_id    | BIGINT      | FK → users.id      |
| otp_code   | VARCHAR(10) | NOT NULL           |
| expires_at | TIMESTAMP   | NOT NULL           |
| created_at | TIMESTAMP   | NOT NULL           |

Business Rules:

* 6 Digit OTP
* 2 Minute Expiry

---

# TABLE 11: system_settings

Purpose:

Global System Configuration

| Column                  | Type         | Constraints        |
| ----------------------- | ------------ | ------------------ |
| id                      | BIGINT       | PK, AUTO_INCREMENT |
| company_name            | VARCHAR(255) | NOT NULL           |
| company_address         | TEXT         | NOT NULL           |
| company_email           | VARCHAR(255) | NOT NULL           |
| company_phone           | VARCHAR(20)  | NOT NULL           |
| office_start_time       | TIME         | NOT NULL           |
| office_end_time         | TIME         | NOT NULL           |
| checkout_reminder_time  | TIME         | NOT NULL           |
| annual_paid_leave_limit | INT          | NOT NULL           |
| pf_percentage           | DECIMAL(5,2) | NOT NULL           |
| salary_credit_day       | INT          | NOT NULL           |
| created_at              | TIMESTAMP    | NOT NULL           |
| updated_at              | TIMESTAMP    | NOT NULL           |

Business Rule:

Only one record exists in this table.

---

# RELATIONSHIPS

users
│
├── user_profiles (1:1)
├── user_employment (1:1)
├── user_bank_details (1:1)
├── attendance (1:M)
├── leave_requests (1:M)
├── payrolls (1:M)
├── notifications (1:M)
└── otp_verifications (1:M)

payrolls
│
└── payslips (1:1)

---

# DATABASE STATUS

STATUS: FINALIZED

This document is the official database reference for entity creation, repository design, service development, API development, and future maintenance.
