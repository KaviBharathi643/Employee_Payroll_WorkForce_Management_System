-- Employee Payroll & Workforce Management System
-- MySQL 8+ schema (reference DDL — matches DATABASE_SCHEMA.md)

CREATE DATABASE IF NOT EXISTS payroll_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payroll_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role)
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    personal_email VARCHAR(150) UNIQUE,
    address TEXT NOT NULL,
    profile_photo_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_profiles_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS user_employment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reporting_manager_id BIGINT,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    joining_date DATE NOT NULL,
    employment_type VARCHAR(20) NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    employment_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_employment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_employment_manager FOREIGN KEY (reporting_manager_id) REFERENCES users(id),
    INDEX idx_user_employment_user_id (user_id),
    INDEX idx_user_employment_department (department),
    INDEX idx_user_employment_status (employment_status)
);

CREATE TABLE IF NOT EXISTS user_bank_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bank_name VARCHAR(150) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_bank_details_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP NULL,
    check_out_time TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_attendance_user_date (user_id, attendance_date),
    INDEX idx_attendance_date (attendance_date)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    duration_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_days DECIMAL(4,1) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    approved_by BIGINT,
    cancelled_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_requests_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_leave_requests_approver FOREIGN KEY (approved_by) REFERENCES users(id),
    INDEX idx_leave_requests_user_id (user_id),
    INDEX idx_leave_requests_status (status),
    INDEX idx_leave_requests_start_date (start_date)
);

CREATE TABLE IF NOT EXISTS payrolls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payroll_year INT NOT NULL,
    payroll_month INT NOT NULL,
    employee_code VARCHAR(20) NOT NULL,
    employee_name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    bonus DECIMAL(10,2) NOT NULL,
    pf_percentage DECIMAL(5,2) NOT NULL,
    pf_amount DECIMAL(10,2) NOT NULL,
    unpaid_leave_count DECIMAL(4,1) NOT NULL,
    unpaid_leave_deduction DECIMAL(10,2) NOT NULL,
    final_salary DECIMAL(10,2) NOT NULL,
    generated_date TIMESTAMP NOT NULL,
    credited_date TIMESTAMP NULL,
    generated_by BIGINT,
    credited_by BIGINT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payrolls_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payrolls_generated_by FOREIGN KEY (generated_by) REFERENCES users(id),
    CONSTRAINT fk_payrolls_credited_by FOREIGN KEY (credited_by) REFERENCES users(id),
    INDEX idx_payrolls_user_id (user_id),
    INDEX idx_payrolls_year (payroll_year),
    INDEX idx_payrolls_month (payroll_month),
    INDEX idx_payrolls_status (status)
);

CREATE TABLE IF NOT EXISTS payslips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payroll_id BIGINT NOT NULL,
    payslip_number VARCHAR(50) NOT NULL UNIQUE,
    pdf_generated BOOLEAN NOT NULL DEFAULT FALSE,
    generated_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payslips_payroll FOREIGN KEY (payroll_id) REFERENCES payrolls(id),
    INDEX idx_payslips_payroll_id (payroll_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    redirect_type VARCHAR(30) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_expires_at (expires_at)
);

CREATE TABLE IF NOT EXISTS otp_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_otp_verifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    company_address TEXT NOT NULL,
    company_email VARCHAR(255) NOT NULL,
    company_phone VARCHAR(20) NOT NULL,
    office_start_time TIME NOT NULL,
    office_end_time TIME NOT NULL,
    checkout_reminder_time TIME NOT NULL,
    annual_paid_leave_limit INT NOT NULL,
    pf_percentage DECIMAL(5,2) NOT NULL,
    salary_credit_day INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
