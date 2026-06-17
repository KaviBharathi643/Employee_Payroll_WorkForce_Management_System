# Changelog

All notable changes to the Employee Payroll & Workforce Management System will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Planned Features
- [ ] Refresh Token Implementation
- [ ] Docker Support & Docker Compose
- [ ] Redis Caching for Performance Optimization
- [ ] Comprehensive Audit Logs
- [ ] Multi-Company Support
- [ ] Mobile Application (React Native/Flutter)
- [ ] Advanced Analytics Dashboard
- [ ] Two-Factor Authentication (2FA)
- [ ] Single Sign-On (SSO) Integration
- [ ] Cloud Deployment (AWS/Azure)
- [ ] GraphQL API
- [ ] Internationalization (i18n)
- [ ] Advanced Search & Filtering
- [ ] Bulk Operations

---

## [1.0.0] - 2024-01-15

### Added - Core Features

#### Authentication Module
- [x] User Login with Email/Password
- [x] User Registration
- [x] JWT Token-based Authentication
- [x] Forgot Password with OTP Verification
- [x] Email-based Password Reset
- [x] Session Management
- [x] Role-Based Access Control (RBAC)
  - Admin Role
  - HR Role
  - Employee Role

#### User Management System
- [x] User Profile Management
  - Personal Information Storage
  - Profile Photo Upload
  - Emergency Contact Details
  - Address Information
- [x] Employment Details
  - Employee Code Generation
  - Designation Management
  - Department Assignment
  - Reporting Manager Assignment
  - Employment Type (Full-time, Part-time, Contract, Intern)
- [x] Bank Details Management
  - Bank Account Information
  - IFSC/MICR Code Storage
  - Multiple Account Support (future)

#### Admin Module
- [x] HR User Management
  - Create/Update/Delete HR Users
  - Assign Roles and Permissions
  - View HR Activity Logs
- [x] HR Attendance Management
  - View All HR Attendance Records
  - Manual Attendance Adjustment
  - Attendance Report Generation
- [x] HR Leave Management
  - Approve/Reject Leave Requests
  - Monitor Leave Balances
  - Generate Leave Reports
- [x] HR Payroll Management
  - Monitor Payroll Processing
  - View Payroll History
  - Access Payroll Reports
- [x] Reports & Analytics
  - Admin-level Report Dashboard
  - System-wide Analytics
  - Data Export Capabilities
- [x] System Settings
  - Configure Application Parameters
  - Manage System Rules
  - Update Company Information

#### HR Module
- [x] Employee Management
  - Add New Employees
  - Update Employee Information
  - Manage Employment Details
  - Track Bank Information
  - View Employee Directory
  - Search and Filter Employees
- [x] Attendance Management
  - Track Daily Attendance
  - View Check-in/Check-out Times
  - Generate Attendance Reports
  - Monitor Attendance Trends
  - Identify Absent Employees
- [x] Leave Management
  - Review Leave Requests
  - Approve/Reject Leave Applications
  - Manage Leave Types
  - Track Leave Balances
  - Generate Leave Analytics
- [x] Payroll Management
  - Generate Monthly Payroll
  - Calculate Salary Components
  - Apply Deductions (PF, ESIC, IT)
  - Generate Payslips
  - Email Payslips to Employees
- [x] Reports
  - Attendance Reports
  - Payroll Reports
  - Leave Analysis
  - Department-wise Analytics
- [x] Notifications
  - Send Notifications to Employees
  - System Alerts
  - Email Notifications

#### Employee Module
- [x] Attendance Tracking
  - Daily Check-in/Check-out
  - View Personal Attendance Records
  - Download Attendance Certificate
  - Track Attendance Percentage
- [x] Leave Management
  - Apply for Leave
  - Track Leave Balance
  - View Leave History
  - Check Leave Status
- [x] Payslip Management
  - Download Payslips
  - View Payslip History
  - Email Payslip Option
  - Annual Salary Certificate
- [x] Profile Management
  - View Personal Profile
  - Update Personal Information
  - Update Bank Details
  - Change Password
- [x] Notifications
  - Receive System Notifications
  - View Notification History
  - Mark Notifications as Read

#### Automated Features
- [x] Email Notifications
  - OTP for Password Reset
  - Leave Approval/Rejection Emails
  - Payslip Distribution via Email
  - System Alerts and Updates
- [x] Missing Checkout Penalty
  - Automatic Detection of Missing Check-out
  - Penalty Calculation
  - Penalty Deduction from Salary
  - Penalty Report
- [x] Payroll Generation
  - Automated Monthly Payroll Processing
  - Salary Calculation
  - Deduction Management
  - Payslip Generation
  - Email Distribution
- [x] PDF Reports
  - Attendance Reports
  - Payroll Reports
  - Leave Reports
  - Payslips
  - Salary Certificates
- [x] Scheduler Jobs
  - End-of-Day Attendance Processing
  - Monthly Payroll Generation Reminder
  - Leave Balance Reset
  - Notification Cleanup

#### Database
- [x] Complete Database Schema
  - 12 Core Tables
  - Proper Indexing
  - Foreign Key Relationships
  - Data Integrity Constraints
- [x] User Tables
  - users
  - user_profiles
  - user_employment
  - user_bank_details
  - otp_verifications
- [x] Business Tables
  - attendance
  - leave_requests
  - leave_balances
  - payrolls
  - payslips
  - notifications
- [x] Configuration Tables
  - system_settings

#### API & Backend
- [x] RESTful API Architecture
  - 50+ Endpoints
  - Consistent Response Format
  - Error Handling
  - Pagination Support
  - Sorting and Filtering
- [x] Security
  - JWT Authentication
  - Spring Security Integration
  - Password Encryption (BCrypt)
  - CORS Configuration
  - Request Validation
- [x] Database Layer
  - JPA/Hibernate ORM
  - Spring Data Repositories
  - Transaction Management
  - Connection Pooling
- [x] Business Logic
  - Service Layer Architecture
  - Separation of Concerns
  - Reusable Utilities
  - Error Handling

#### Frontend & UI
- [x] React Application
  - Component-based Architecture
  - React Router for Navigation
  - Context API for State Management
  - Axios for API Communication
- [x] Responsive Design
  - Mobile-friendly Layouts
  - Tailwind CSS Styling
  - Consistent UI/UX
  - Accessible Forms
- [x] Admin Dashboard
  - Overview Statistics
  - User Management Interface
  - Quick Action Buttons
  - Analytics Charts
- [x] HR Dashboard
  - Employee Statistics
  - Pending Approvals
  - Quick Actions
  - Reports Access
- [x] Employee Dashboard
  - Personal Information
  - Attendance Summary
  - Leave Balance
  - Payslip Access
  - Notifications
- [x] Authentication Pages
  - Login Page
  - Registration Page
  - Forgot Password Flow
  - OTP Verification
- [x] Employee Management UI
  - Employee List
  - Employee Detail View
  - Add/Edit Forms
  - Search and Filter
- [x] Attendance UI
  - Check-in/Check-out Interface
  - Attendance Records View
  - Statistics Dashboard
  - Report Generation
- [x] Leave Management UI
  - Leave Application Form
  - Leave Request Tracking
  - Approval Dashboard
  - Leave Balance View
- [x] Payroll UI
  - Payroll Processing
  - Payslip Download
  - Salary Details View
- [x] Report Generation
  - Attendance Reports
  - Payroll Reports
  - Leave Analysis
  - Export to PDF/Excel
- [x] Notification System
  - In-app Notifications
  - Notification Bell Icon
  - Notification Center

#### Documentation
- [x] README.md
  - Project Overview
  - Features List
  - Tech Stack
  - Installation Guide
  - Usage Instructions
- [x] PROJECT_STRUCTURE.md
  - Backend Structure
  - Frontend Structure
  - Directory Organization
  - Key Files Description
- [x] DATABASE_SCHEMA.md
  - Table Descriptions
  - Entity Relationships
  - Column Details
  - Constraints and Indexes
- [x] API_DOCUMENTATION.md
  - API Endpoints
  - Request/Response Formats
  - Authentication Details
  - Error Handling
- [x] CONTRIBUTING.md
  - Contribution Guidelines
  - Development Setup
  - Code Standards
  - Commit Convention
- [x] CHANGELOG.md (This file)

#### Development Tools
- [x] Maven Build Configuration
  - Dependency Management
  - Build Plugins
  - Profile Configuration
- [x] Vite Configuration
  - Fast Build Tool
  - Development Server
  - Production Build
- [x] Git Configuration
  - .gitignore Files
  - Branch Naming Convention
- [x] IDE Configuration
  - VS Code Settings
  - IntelliJ Configuration

### Technical Details

#### Backend Stack
- Java 21 (LTS)
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- Hibernate ORM
- JWT (JJWT 0.12.6)
- MySQL 8.0
- Lombok 1.18.38
- OpenPDF 2.0.3

#### Frontend Stack
- React 19.2
- Vite 8.0
- React Router 7.17
- Axios 1.17
- Tailwind CSS 4.3
- Node.js 18+

#### Database
- MySQL 8.0
- 12 Normalized Tables
- ~10MB estimated size
- Proper indexing for performance
- ACID compliance

### Bug Fixes
- None (Initial release)

### Changed
- None (Initial release)

### Deprecated
- None

### Removed
- None

### Security
- JWT-based Authentication
- Encrypted Password Storage (BCrypt)
- CORS Protection
- SQL Injection Prevention (Parameterized Queries)
- CSRF Protection
- XSS Prevention

### Performance
- Database Indexing
- Connection Pooling
- Query Optimization
- Frontend Build Optimization (Vite)
- Lazy Loading Components

### Infrastructure
- Local Development Setup
- MySQL Database
- Spring Boot Application Server
- React Development Server

---

## Version History

| Version | Release Date | Status | Notes |
|---------|-------------|--------|-------|
| 1.0.0 | 2024-01-15 | Released | Initial Production Release |
| 0.9.0 | 2024-01-10 | Beta | Final Beta Version |
| 0.5.0 | 2024-01-01 | Alpha | Core Features Complete |

---

## Migration Guide

### From Version 0.x to 1.0.0

No breaking changes. Direct upgrade from beta versions is supported.

```bash
# Backend
git pull origin main
cd backend
mvn clean install
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

---

## Known Issues

- [ ] None reported for v1.0.0

---

## Future Roadmap

### Q1 2024
- [ ] Refresh Token Implementation
- [ ] Email Template Improvements
- [ ] Advanced Filters

### Q2 2024
- [ ] Docker Support
- [ ] Redis Caching
- [ ] Mobile App (React Native)

### Q3 2024
- [ ] 2FA Implementation
- [ ] SSO Integration
- [ ] Multi-language Support

### Q4 2024
- [ ] Cloud Deployment
- [ ] Advanced Analytics
- [ ] GraphQL API

---

## Contributors

- Kavi Bharathi - Initial Development

---

## Support

For support, contact: support@company.com

---

**Last Updated**: 2024-01-15

For detailed information about specific features, refer to the [README.md](./README.md) and related documentation files.
