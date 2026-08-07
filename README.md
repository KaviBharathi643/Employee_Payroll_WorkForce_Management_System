# Employee Payroll & Workforce Management System

#LIVE LINK : https://gallant-alignment-production-8b39.up.railway.app/login

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-19.2-61DAFB?style=flat-square&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)
![Vite](https://img.shields.io/badge/Vite-8.0-646CFF?style=flat-square&logo=vite)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)

A comprehensive, production-ready full-stack application for managing employee payroll, attendance, leave requests, and workforce operations. Built with modern technologies and best practices for scalability and maintainability.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Roles and Permissions](#roles-and-permissions)
- [Database Design](#database-design)
- [Installation](#installation)
- [Usage](#usage)
- [API Overview](#api-overview)
- [Screenshots](#screenshots)
- [Project Structure](#project-structure)
- [Default Credentials](#default-credentials)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Employee Payroll & Workforce Management System is an enterprise-grade HRMS solution designed to streamline HR operations, employee management, and payroll processing. The system provides role-based access control with separate interfaces for Admins, HR professionals, and Employees.

With features like automated payroll generation, attendance tracking, leave management, and comprehensive reporting capabilities, this system significantly reduces manual HR operations and ensures data consistency across the organization.

## Features

### 🔐 Authentication & Security

- **JWT Authentication**: Secure token-based authentication
- **Forgot Password with OTP**: Email-based password recovery
- **Role-Based Access Control (RBAC)**: Three-tier access levels (Admin, HR, Employee)
- **Spring Security**: Enterprise-grade security framework
- **Password Encryption**: BCrypt hashing for password storage

### 👔 Admin Module

- **HR Management**: Create, update, and manage HR users
- **HR Attendance**: Track and manage HR attendance records
- **HR Leave**: Manage HR leave requests and approvals
- **HR Payroll**: Monitor and control payroll processes
- **Reports**: Comprehensive admin-level reporting
- **System Settings**: Configure application settings and parameters

### 👨‍💼 HR Module

- **Employee Management**: CRUD operations for employee records
  - Add new employees
  - Update employee information
  - Track employment details
  - Manage bank information
- **Attendance Management**: Track employee check-in and check-out
  - Daily attendance records
  - Missing checkout penalties
  - Attendance analytics
- **Leave Approval**: Review and approve/reject leave requests
  - Different leave types
  - Leave balance tracking
  - Approval workflows
- **Payroll Generation**: Create and process payroll
  - Salary calculations
  - Deductions and allowances
  - Automated processing
- **Reports**: Generate payroll and attendance reports
  - Attendance reports
  - Payroll reports
  - Custom reports

### 👥 Employee Module

- **Attendance**: View personal attendance records
  - Daily check-in/check-out
  - Attendance history
  - Attendance summary
- **Leave Application**: Submit and track leave requests
  - Apply for leave
  - Track status
  - View leave balance
- **Payslips**: Download and view payslips
  - Monthly payslips
  - Payslip history
  - PDF download capability
- **Profile Management**: Update personal information
  - Profile details
  - Bank information
  - Contact details
- **Notifications**: Receive system notifications
  - Leave status updates
  - Payroll notifications
  - System announcements

### ⚙️ Automated Features

- **Email Notifications**: Automated email alerts for important events
- **Missing Checkout Penalty**: Automatic penalty calculation for missing check-outs
- **Payroll Generation**: Automated monthly payroll processing
- **PDF Reports**: Generate downloadable PDF reports
- **Scheduler Jobs**: Background tasks for automated operations

## Tech Stack

### Frontend
- **React 19.2**: Modern React with latest features
- **Vite 8.0**: Ultra-fast build tool
- **Axios**: Promise-based HTTP client
- **React Router**: Client-side routing
- **Context API**: State management
- **Tailwind CSS**: Utility-first CSS framework

### Backend
- **Java 21**: Latest Java LTS version
- **Spring Boot 3.3.5**: Enterprise application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: ORM and database access
- **Spring Mail**: Email service integration
- **JWT (JJWT)**: JSON Web Tokens for authentication
- **Hibernate**: Object-relational mapping
- **Lombok**: Reduce boilerplate code
- **OpenPDF**: PDF generation

### Database
- **MySQL 8.0**: Relational database

### Tools & Infrastructure
- **Maven**: Dependency management and build tool
- **Git**: Version control
- **Docker**: Containerization (future)
- **Redis**: Caching (future)

## System Architecture

The project is built on a decoupled three-tier software architecture:

### 1. Overall System Architecture
![Overall 3-Tier System Architecture](./docs/diagrams/sys_architecture.png)

### 2. System Use Case Boundaries
![System Use Case Boundaries](./docs/diagrams/sys_usecase.png)

### 3. Data Flow Diagram (Level 1)
![DFD Level 1](./docs/diagrams/sys_dfd_l1.png)

### 4. Leave Request Workflow (Activity Diagram)
![Leave Request Workflow](./docs/diagrams/sys_leave_workflow.png)


## Roles and Permissions

| Feature | Admin | HR | Employee |
|---------|-------|----|----|
| User Management | ✅ | ❌ | ❌ |
| Employee Management | ✅ | ✅ | ❌ |
| Attendance Management | ✅ | ✅ | 🔍 View Only |
| Leave Management | ✅ | ✅ | ✅ |
| Leave Approval | ✅ | ✅ | ❌ |
| Payroll Generation | ✅ | ✅ | ❌ |
| Payroll Viewing | ✅ | ✅ | 🔍 Personal Only |
| Reports | ✅ | ✅ | ❌ |
| System Settings | ✅ | ❌ | ❌ |
| Notifications | ✅ | ✅ | ✅ |

## Database Design

### Entity Relationship Diagram (ERD)

![Database ERD](./docs/diagrams/db_erd.png)


### Core Tables

| Table | Purpose |
|-------|---------|
| `users` | User accounts and authentication |
| `user_profiles` | User personal information |
| `user_employment` | Employment details |
| `user_bank_details` | Bank account information |
| `attendance` | Daily attendance records |
| `leave_requests` | Leave applications |
| `leave_balances` | Leave balance tracking |
| `payrolls` | Payroll records |
| `payslips` | Individual payslips |
| `notifications` | System notifications |
| `otp_verifications` | OTP for password reset |
| `system_settings` | Application configuration |

## Installation

### Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+**
- **Node.js 18+** and npm/yarn
- **MySQL 8.0+**
- **Git**

### Backend Setup

1. **Clone the Repository**
```bash
git clone https://github.com/KaviBharathi643/Employee_Payroll_WorkForce_Management_System.git
cd Employee_Payroll_WorkForce_Management_System/backend
```

2. **Configure Database**

Create a MySQL database:
```sql
CREATE DATABASE payroll_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/payroll_db
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: create
```

3. **Build and Run**
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to Frontend Directory**
```bash
cd Employee_Payroll_WorkForce_Management_System/frontend
```

2. **Install Dependencies**
```bash
npm install
```

3. **Configure API Endpoint**

Update `src/api/axiosClient.js`:
```javascript
const BASE_URL = 'http://localhost:8080/api';
```

4. **Start Development Server**
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## Usage

### Starting the Application

1. **Start MySQL Server**
```bash
# On Windows
net start MySQL80

# On macOS
brew services start mysql

# On Linux
sudo systemctl start mysql
```

2. **Start Backend**
```bash
cd backend
mvn spring-boot:run
```

3. **Start Frontend**
```bash
cd frontend
npm run dev
```

4. **Access the Application**
- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (if enabled)

### Building for Production

**Backend:**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/payroll-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Serve the dist folder with your web server
```

## API Overview

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/forgot-password` - Initiate password reset
- `POST /api/auth/reset-password` - Reset password with OTP
- `POST /api/auth/verify-otp` - Verify OTP

### Employee Endpoints

- `GET /api/employees` - List all employees
- `GET /api/employees/{id}` - Get employee details
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

### Attendance Endpoints

- `GET /api/attendance` - Get attendance records
- `POST /api/attendance/check-in` - Check in
- `POST /api/attendance/check-out` - Check out
- `GET /api/attendance/{id}` - Get attendance by ID

### Leave Endpoints

- `GET /api/leaves` - Get leave requests
- `POST /api/leaves` - Create leave request
- `PUT /api/leaves/{id}` - Update leave request
- `PUT /api/leaves/{id}/approve` - Approve leave
- `PUT /api/leaves/{id}/reject` - Reject leave

### Payroll Endpoints

- `GET /api/payrolls` - Get payroll records
- `POST /api/payrolls/generate` - Generate payroll
- `GET /api/payslips` - Get payslips
- `GET /api/payslips/{id}` - Get payslip details

### Reports Endpoints

- `GET /api/reports/attendance` - Attendance report
- `GET /api/reports/payroll` - Payroll report
- `GET /api/reports/leave` - Leave report

### Settings Endpoints

- `GET /api/settings` - Get system settings
- `PUT /api/settings` - Update settings

## Screenshots

### 📄 Project Report
A complete, detailed **20-page project report** with architecture flowcharts, database ERD, use cases, Level-1 DFD, and module walkthroughs is available as a PDF:
👉 **[Download Project Report (PDF)](./Employee_Payroll_Workforce_Management_System_Project_Report.pdf)**

---

### Authentication

#### Login Page
![Login Page](./docs/screenshots/ui_login.png)

#### Forgot Password & Reset Password with OTP
![Reset Password with OTP](./docs/screenshots/ui_reset_password.png)

### Admin Dashboard

#### Dashboard Overview
![Admin Dashboard](./docs/screenshots/ui_admin_dashboard.png)

### HR Module

#### HR Dashboard & Metrics
![HR Dashboard](./docs/screenshots/ui_hr_dashboard.png)

#### Employee Onboarding
![Employee Onboarding](./docs/screenshots/ui_create_employee.png)

#### Attendance Management Filters
![Attendance Management](./docs/screenshots/ui_hr_attendance.png)

### Employee Module

#### Profile Details Update
![My Profile](./docs/screenshots/ui_my_profile.png)

#### Leave Application
![Leave Application](./docs/screenshots/ui_apply_leave.png)


## Project Structure

For detailed project structure explanation, see [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md)

### Backend Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/company/payroll/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── repository/      # Data Access Layer
│   │   │   ├── service/         # Business Logic
│   │   │   ├── security/        # Security Configuration
│   │   │   ├── util/            # Utility Classes
│   │   │   └── PayrollApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── schema.sql
│   └── test/
│       ├── java/
│       └── resources/
├── pom.xml
└── .gitignore
```

### Frontend Structure
```
frontend/
├── src/
│   ├── components/
│   │   ├── attendance/
│   │   ├── auth/
│   │   ├── employees/
│   │   ├── leaves/
│   │   ├── payroll/
│   │   ├── payslip/
│   │   ├── profile/
│   │   ├── reports/
│   │   └── common/
│   ├── pages/
│   ├── services/
│   ├── hooks/
│   ├── context/
│   ├── layouts/
│   ├── routes/
│   ├── utils/
│   ├── api/
│   ├── assets/
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── public/
├── package.json
├── vite.config.js
└── .gitignore
```

## Default Credentials

> ⚠️ **IMPORTANT**: Change these credentials immediately in production!

### Admin Account
- **Email**: `admin@company.com`
- **Password**: `Admin@123456`

### Sample HR Account
- **Email**: `hr@company.com`
- **Password**: `Hr@123456`

### Sample Employee Account
- **Email**: `employee@company.com`
- **Password**: `Employee@123456`

## Future Enhancements

- [x] JWT Authentication
- [ ] Refresh Tokens Implementation
- [ ] Docker Support & Docker Compose
- [ ] Redis Caching for Performance
- [ ] Comprehensive Audit Logs
- [ ] Multi-Company Support
- [ ] Mobile Application (React Native/Flutter)
- [ ] Advanced Analytics & Dashboard
- [ ] Two-Factor Authentication (2FA)
- [ ] Single Sign-On (SSO)
- [ ] Cloud Deployment (AWS/Azure)
- [ ] GraphQL API
- [ ] Internationalization (i18n)
- [ ] Advanced Search & Filtering
- [ ] Bulk Operations

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests (if configured)
```bash
cd frontend
npm test
```

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

### Quick Start for Contributors
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Troubleshooting

### Common Issues

**Port Already in Use**
```bash
# Backend (8080)
lsof -ti:8080 | xargs kill -9

# Frontend (5173)
lsof -ti:5173 | xargs kill -9
```

**Database Connection Error**
- Ensure MySQL is running
- Verify credentials in `application.yml`
- Check database exists and is accessible

**CORS Errors**
- Ensure backend is properly configured with CORS settings
- Check frontend API endpoint configuration

## Documentation

- [Database Schema](./DATABASE_SCHEMA.md) - Detailed database design
- [API Documentation](./API_DOCUMENTATION.md) - Complete API reference
- [Project Structure](./PROJECT_STRUCTURE.md) - Project organization
- [Changelog](./CHANGELOG.md) - Version history and updates
- [Contributing Guide](./CONTRIBUTING.md) - How to contribute

## Support

For support, email support@company.com or open an issue on GitHub.

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

## Authors

- **Kavi Bharathi** - Initial work and full-stack development

## Acknowledgments

- Spring Boot Team for the amazing framework
- React community for the excellent library
- All contributors and supporters of this project

---

**Made with ❤️ by Kavi Bharathi**
