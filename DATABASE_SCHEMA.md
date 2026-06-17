# Database Schema Documentation

This document provides a comprehensive overview of the database design for the Employee Payroll & Workforce Management System.

## Table of Contents

- [Overview](#overview)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [Detailed Table Schema](#detailed-table-schema)
- [Indexes](#indexes)
- [Key Relationships](#key-relationships)
- [Data Constraints](#data-constraints)

## Overview

The database consists of 12 core tables organized into functional domains:

1. **User Management**: users, user_profiles, user_employment, user_bank_details, otp_verifications
2. **Attendance**: attendance
3. **Leave Management**: leave_requests, leave_balances
4. **Payroll**: payrolls, payslips
5. **Notifications**: notifications
6. **System Configuration**: system_settings

Total Database Size: ~10MB (estimated with sample data)

## Entity Relationship Diagram

```
┌─────────────────────┐
│      users          │
│ ─────────────────── │
│ user_id (PK)        │
│ email (UNIQUE)      │
│ password            │
│ role                │
│ status              │
│ created_at          │
└──────────┬──────────┘
           │
           ├─────────────────────────────────────────┐
           │                                         │
           │                    ┌────────────────────┴──────────┐
           │                    │                               │
    ┌──────▼──────────┐  ┌──────▼──────────┐  ┌───────▼────────┐
    │ user_profiles   │  │ user_employment │  │user_bank_detail│
    │ ──────────────  │  │ ─────────────── │  │──────────────  │
    │ profile_id (PK) │  │ emp_id (PK)     │  │ bank_id (PK)   │
    │ user_id (FK)    │  │ user_id (FK)    │  │ user_id (FK)   │
    │ first_name      │  │ designation     │  │ account_number │
    │ last_name       │  │ department      │  │ bank_name      │
    │ phone           │  │ join_date       │  │ ifsc_code      │
    │ dob             │  │ reporting_to    │  │ account_holder │
    └─────────────────┘  └─────────────────┘  └────────────────┘
    
           │
           │
    ┌──────┴──────────────────────────────────────────────────────┐
    │                                                              │
    │                   ┌──────────────────┐                       │
    │                   │   attendance     │                       │
    │                   │ ────────────────│                       │
    │                   │ attendance_id (PK)                      │
    │                   │ user_id (FK)     │                      │
    │                   │ check_in_time    │                      │
    │                   │ check_out_time   │                      │
    │                   │ status           │                      │
    │                   │ penalty_applied  │                      │
    │                   └──────────────────┘                       │
    │                                                              │
    │                   ┌──────────────────┐                       │
    │                   │ leave_requests   │                       │
    │                   │ ────────────────│                       │
    │                   │ leave_id (PK)    │                       │
    │                   │ user_id (FK)     │                       │
    │                   │ leave_type       │                       │
    │                   │ start_date       │                       │
    │                   │ end_date         │                       │
    │                   │ approver_id (FK) │                       │
    │                   │ status           │                       │
    │                   └──────────────────┘                       │
    │                           │                                  │
    │                           │                                  │
    │                   ┌───────▼─────────┐                       │
    │                   │leave_balances   │                       │
    │                   │ ────────────────│                       │
    │                   │ balance_id (PK) │                       │
    │                   │ user_id (FK)    │                       │
    │                   │ leave_type      │                       │
    │                   │ total_days      │                       │
    │                   │ used_days       │                       │
    │                   │ remaining_days  │                       │
    │                   └─────────────────┘                       │
    │                                                              │
    │                   ┌──────────────────┐                       │
    │                   │   payrolls       │                       │
    │                   │ ────────────────│                       │
    │                   │ payroll_id (PK)  │                       │
    │                   │ user_id (FK)     │                       │
    │                   │ salary           │                       │
    │                   │ deductions       │                       │
    │                   │ allowances       │                       │
    │                   │ net_salary       │                       │
    │                   │ month_year       │                       │
    │                   └────────┬─────────┘                       │
    │                            │                                 │
    │                            │                                 │
    │                    ┌───────▼─────────┐                      │
    │                    │   payslips      │                      │
    │                    │ ───────────────│                      │
    │                    │ payslip_id (PK) │                      │
    │                    │ payroll_id (FK) │                      │
    │                    │ user_id (FK)    │                      │
    │                    │ gross_salary    │                      │
    │                    │ net_salary      │                      │
    │                    │ pdf_path        │                      │
    │                    │ created_at      │                      │
    │                    └─────────────────┘                      │
    │                                                              │
    │                   ┌──────────────────┐                       │
    │                   │ notifications    │                       │
    │                   │ ────────────────│                       │
    │                   │ notify_id (PK)   │                       │
    │                   │ user_id (FK)     │                       │
    │                   │ message          │                       │
    │                   │ type             │                       │
    │                   │ read_status      │                       │
    │                   │ created_at       │                       │
    │                   └──────────────────┘                       │
    │                                                              │
    │              ┌────────────────────────┐                      │
    │              │ otp_verifications      │                      │
    │              │ ──────────────────────│                      │
    │              │ otp_id (PK)            │                      │
    │              │ user_id (FK)           │                      │
    │              │ otp_code               │                      │
    │              │ expiry_time            │                      │
    │              │ is_verified            │                      │
    │              └────────────────────────┘                      │
    │                                                              │
    └──────────────────────────────────────────────────────────────┘

┌─────────────────────────┐
│  system_settings        │
│ ───────────────────────│
│ setting_id (PK)        │
│ setting_key            │
│ setting_value          │
│ description            │
└─────────────────────────┘
```

## Detailed Table Schema

### 1. USERS Table

**Purpose**: Store user account information

```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'HR', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
    status ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    is_two_factor_enabled BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    login_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- `user_id`: Unique identifier
- `email`: Email address (must be unique)
- `password`: BCrypt hashed password
- `role`: User role (ADMIN, HR, EMPLOYEE)
- `status`: Account status
- `is_two_factor_enabled`: 2FA flag for future use
- `last_login`: Last login timestamp
- `login_attempts`: Failed login count
- `created_at`: Account creation time
- `updated_at`: Last modification time

---

### 2. USER_PROFILES Table

**Purpose**: Store personal information of users

```sql
CREATE TABLE user_profiles (
    profile_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100) NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NULL,
    date_of_birth DATE NULL,
    phone_number VARCHAR(20) NOT NULL,
    alternate_phone VARCHAR(20) NULL,
    personal_email VARCHAR(255) NULL,
    address_line_1 VARCHAR(255) NULL,
    address_line_2 VARCHAR(255) NULL,
    city VARCHAR(50) NULL,
    state VARCHAR(50) NULL,
    country VARCHAR(50) NULL,
    postal_code VARCHAR(10) NULL,
    profile_photo_url VARCHAR(500) NULL,
    marital_status ENUM('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED') NULL,
    emergency_contact_name VARCHAR(100) NULL,
    emergency_contact_phone VARCHAR(20) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Personal identification details
- Contact information
- Address information
- Emergency contact information

---

### 3. USER_EMPLOYMENT Table

**Purpose**: Store employment details

```sql
CREATE TABLE user_employment (
    emp_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    designation VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    employment_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN') NOT NULL,
    join_date DATE NOT NULL,
    reporting_to BIGINT NULL,
    base_salary DECIMAL(15, 2) NOT NULL,
    ctc DECIMAL(15, 2) NULL,
    blood_group VARCHAR(10) NULL,
    pan_number VARCHAR(20) NULL,
    aadhar_number VARCHAR(20) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reporting_to) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_employee_code (employee_code),
    INDEX idx_department (department),
    INDEX idx_join_date (join_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- `employee_code`: Unique employee identifier
- `designation`: Job designation
- `department`: Department assignment
- `employment_type`: Type of employment
- `join_date`: Date of joining
- `reporting_to`: Manager user ID
- `base_salary`: Monthly salary
- `ctc`: Cost to Company (annual)

---

### 4. USER_BANK_DETAILS Table

**Purpose**: Store bank account information

```sql
CREATE TABLE user_bank_details (
    bank_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    account_holder_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT', 'OTHER') NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    branch_name VARCHAR(100) NOT NULL,
    ifsc_code VARCHAR(11) NOT NULL,
    micr_code VARCHAR(9) NULL,
    swift_code VARCHAR(20) NULL,
    is_primary BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_account_number (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Bank account details for salary transfer
- IFSC/MICR codes for validation

---

### 5. ATTENDANCE Table

**Purpose**: Track daily attendance records

```sql
CREATE TABLE attendance (
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP NULL,
    check_out_time TIMESTAMP NULL,
    working_hours DECIMAL(5, 2) NULL,
    attendance_status ENUM('PRESENT', 'ABSENT', 'HALF_DAY', 'LEAVE', 'HOLIDAY', 'WEEKEND') NOT NULL,
    is_checkout_missing BOOLEAN DEFAULT FALSE,
    penalty_applied DECIMAL(10, 2) DEFAULT 0,
    penalty_reason VARCHAR(255) NULL,
    remarks VARCHAR(500) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_check_in_time (check_in_time),
    UNIQUE KEY unique_user_date (user_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- `check_in_time`: Daily check-in time
- `check_out_time`: Daily check-out time
- `working_hours`: Calculated working hours
- `is_checkout_missing`: Flag for missing checkout
- `penalty_applied`: Automatic penalty amount

---

### 6. LEAVE_REQUESTS Table

**Purpose**: Track employee leave requests

```sql
CREATE TABLE leave_requests (
    leave_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    leave_type ENUM('CASUAL', 'SICK', 'PERSONAL', 'PAID', 'UNPAID', 'MATERNITY', 'PATERNITY') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_days INT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    attachment_url VARCHAR(500) NULL,
    approver_id BIGINT NULL,
    approval_date TIMESTAMP NULL,
    approval_remarks VARCHAR(500) NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date),
    INDEX idx_approver_id (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Leave request details
- Approval workflow
- Status tracking

---

### 7. LEAVE_BALANCES Table

**Purpose**: Track leave balance for each employee

```sql
CREATE TABLE leave_balances (
    balance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    leave_type ENUM('CASUAL', 'SICK', 'PERSONAL', 'PAID', 'UNPAID', 'MATERNITY', 'PATERNITY') NOT NULL,
    fiscal_year INT NOT NULL,
    total_days INT NOT NULL,
    used_days INT NOT NULL DEFAULT 0,
    remaining_days INT NOT NULL,
    carryover_days INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_fiscal_year (fiscal_year),
    UNIQUE KEY unique_user_leave_year (user_id, leave_type, fiscal_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Leave balance tracking per employee
- Fiscal year based balances

---

### 8. PAYROLLS Table

**Purpose**: Store monthly payroll records

```sql
CREATE TABLE payrolls (
    payroll_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    payroll_month INT NOT NULL,
    payroll_year INT NOT NULL,
    working_days INT NOT NULL,
    present_days INT NOT NULL,
    absent_days INT NOT NULL,
    paid_leave_days INT NOT NULL,
    unpaid_leave_days INT NOT NULL,
    gross_salary DECIMAL(15, 2) NOT NULL,
    basic_salary DECIMAL(15, 2) NOT NULL,
    hra DECIMAL(15, 2) DEFAULT 0,
    dearness_allowance DECIMAL(15, 2) DEFAULT 0,
    other_allowances DECIMAL(15, 2) DEFAULT 0,
    pf_deduction DECIMAL(15, 2) DEFAULT 0,
    esic_deduction DECIMAL(15, 2) DEFAULT 0,
    income_tax DECIMAL(15, 2) DEFAULT 0,
    other_deductions DECIMAL(15, 2) DEFAULT 0,
    total_deductions DECIMAL(15, 2) NOT NULL,
    net_salary DECIMAL(15, 2) NOT NULL,
    status ENUM('DRAFT', 'GENERATED', 'APPROVED', 'PROCESSED', 'PAID') NOT NULL DEFAULT 'DRAFT',
    payment_date TIMESTAMP NULL,
    remarks VARCHAR(500) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_payroll_month_year (payroll_month, payroll_year),
    INDEX idx_status (status),
    UNIQUE KEY unique_user_payroll (user_id, payroll_month, payroll_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Complete payroll calculation
- Salary components and deductions
- Status tracking

---

### 9. PAYSLIPS Table

**Purpose**: Store generated payslips

```sql
CREATE TABLE payslips (
    payslip_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payroll_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    payslip_number VARCHAR(50) NOT NULL UNIQUE,
    payslip_month INT NOT NULL,
    payslip_year INT NOT NULL,
    gross_salary DECIMAL(15, 2) NOT NULL,
    net_salary DECIMAL(15, 2) NOT NULL,
    pdf_path VARCHAR(500) NULL,
    pdf_generated_at TIMESTAMP NULL,
    is_email_sent BOOLEAN DEFAULT FALSE,
    email_sent_at TIMESTAMP NULL,
    download_count INT DEFAULT 0,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (payroll_id) REFERENCES payrolls(payroll_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_payroll_id (payroll_id),
    INDEX idx_payslip_month_year (payslip_month, payslip_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Generated payslip details
- PDF storage path
- Email status tracking

---

### 10. NOTIFICATIONS Table

**Purpose**: Store system notifications

```sql
CREATE TABLE notifications (
    notify_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    notification_type ENUM('LEAVE_APPROVED', 'LEAVE_REJECTED', 'PAYSLIP_GENERATED', 'ATTENDANCE_ALERT', 'SYSTEM_ALERT', 'EMAIL_NOTIFICATION') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    related_entity_id BIGINT NULL,
    related_entity_type VARCHAR(50) NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    action_url VARCHAR(500) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Notification details
- Read/unread status
- Related entity tracking

---

### 11. OTP_VERIFICATIONS Table

**Purpose**: Store OTP codes for password reset

```sql
CREATE TABLE otp_verifications (
    otp_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    attempt_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_expiry_time (expiry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- OTP code management
- Expiry time tracking
- Verification status

---

### 12. SYSTEM_SETTINGS Table

**Purpose**: Store application configuration

```sql
CREATE TABLE system_settings (
    setting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(1000) NOT NULL,
    setting_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'DECIMAL') DEFAULT 'STRING',
    description VARCHAR(255) NULL,
    is_editable BOOLEAN DEFAULT TRUE,
    updated_by BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_setting_key (setting_key),
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Columns**:
- Application configuration settings
- Key-value pairs
- Audit trail

---

## Indexes

Performance indexes are created on frequently queried columns:

```sql
-- Primary Keys (Automatic)
-- user_id, profile_id, emp_id, bank_id, attendance_id, leave_id, balance_id, payroll_id, payslip_id, notify_id, otp_id, setting_id

-- Foreign Keys
ALTER TABLE user_profiles ADD FOREIGN KEY (user_id) REFERENCES users(user_id);
ALTER TABLE user_employment ADD FOREIGN KEY (user_id) REFERENCES users(user_id);
-- ... (all foreign keys)

-- Search Indexes
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_leave_status ON leave_requests(status);
CREATE INDEX idx_payroll_month_year ON payrolls(payroll_month, payroll_year);

-- Composite Indexes (for unique constraints)
CREATE UNIQUE INDEX unique_user_date ON attendance(user_id, attendance_date);
CREATE UNIQUE INDEX unique_user_payroll ON payrolls(user_id, payroll_month, payroll_year);
```

## Key Relationships

### One-to-Many Relationships

- **users** → **user_profiles**: One user has one profile
- **users** → **user_employment**: One user has one employment record
- **users** → **user_bank_details**: One user has one bank detail (can extend to many)
- **users** → **attendance**: One user has many attendance records
- **users** → **leave_requests**: One user has many leave requests
- **users** → **leave_balances**: One user has leave balances for different types
- **users** → **payrolls**: One user has many payrolls (monthly)
- **users** → **payslips**: One user has many payslips
- **users** → **notifications**: One user has many notifications
- **users** → **otp_verifications**: One user has many OTP records

### Many-to-One Relationships

- **leave_requests** → **users** (approver_id): Multiple leave requests can be approved by one HR
- **user_employment** → **users** (reporting_to): Multiple employees report to one manager

### One-to-One Relationships

- **payrolls** → **payslips**: One payroll generates one payslip per employee

## Data Constraints

### Unique Constraints

- `users.email`: Email must be unique
- `user_employment.employee_code`: Employee code must be unique
- `attendance.user_id, attendance_date`: One attendance record per user per day
- `payrolls.user_id, payroll_month, payroll_year`: One payroll per user per month
- `payslips.payslip_number`: Payslip number must be unique
- `system_settings.setting_key`: Setting key must be unique

### Not Null Constraints

Critical fields that cannot be NULL:
- `users.email`, `users.password`, `users.role`
- `user_profiles.first_name`, `user_profiles.last_name`
- `user_employment.employee_code`, `user_employment.designation`
- `attendance.attendance_date`, `attendance.attendance_status`
- `leave_requests.leave_type`, `leave_requests.start_date`, `leave_requests.reason`
- `payrolls.payroll_month`, `payrolls.payroll_year`, `payrolls.gross_salary`

### Default Values

- `users.status`: 'ACTIVE'
- `users.created_at`: Current timestamp
- `attendance.is_checkout_missing`: FALSE
- `payrolls.status`: 'DRAFT'
- `notifications.is_read`: FALSE

### Referential Integrity

- All foreign keys have `ON DELETE CASCADE` or `ON DELETE SET NULL`
- Ensures data consistency when records are deleted
- Prevents orphaned records

## Performance Considerations

1. **Indexing Strategy**
   - Indexed on frequently queried columns (user_id, status, dates)
   - Composite indexes for unique constraints
   - Partial indexes for boolean flags

2. **Query Optimization**
   - Denormalized fields (e.g., `working_days`, `net_salary`) for quick calculations
   - Calculated fields stored in database for reporting performance

3. **Archival Strategy**
   - `is_archived` field in payslips for historical data
   - Soft delete using `status` field in users table

4. **Scalability**
   - InnoDB engine for ACID compliance
   - UTF8MB4 encoding for international character support
   - Partitioning possible on `attendance` and `payrolls` by date

## Sample Data Insertion

See [schema.sql](../backend/src/main/resources/schema.sql) for complete DDL and sample data insertion scripts.

## Backup & Recovery

- Regular backups recommended
- Point-in-time recovery enabled
- Transaction logs maintained

## Monitoring & Maintenance

- Monitor table sizes monthly
- Update statistics for query optimizer
- Archive old data (attendance > 2 years)
- Regular integrity checks

---

For more information, refer to the [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for how these tables are used in API endpoints.
