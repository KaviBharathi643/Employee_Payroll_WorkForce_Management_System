# Project Structure

This document provides a detailed overview of the project structure, explaining the purpose of each directory and file.

## Root Level

```
Employee_Payroll_WorkForce_Management_System/
├── backend/                 # Spring Boot backend application
├── frontend/               # React frontend application
├── docs/                   # Project documentation
├── README.md              # Main project README
├── LICENSE                # MIT License
├── CONTRIBUTING.md        # Contribution guidelines
├── CHANGELOG.md           # Version history
├── PROJECT_STRUCTURE.md   # This file
├── DATABASE_SCHEMA.md     # Database design documentation
└── API_DOCUMENTATION.md   # API endpoints documentation
```

## Backend Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/company/payroll/
│   │   │       ├── config/                  # Configuration classes
│   │   │       │   ├── CorsConfig.java     # CORS configuration
│   │   │       │   ├── SecurityConfig.java # Spring Security setup
│   │   │       │   └── JwtConfig.java      # JWT configuration
│   │   │       ├── controller/              # REST Controllers
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── EmployeeController.java
│   │   │       │   ├── AttendanceController.java
│   │   │       │   ├── LeaveController.java
│   │   │       │   ├── PayrollController.java
│   │   │       │   ├── PayslipController.java
│   │   │       │   ├── ReportController.java
│   │   │       ├── dto/                     # Data Transfer Objects
│   │   │       │   ├── AuthDTO.java
│   │   │       │   ├── EmployeeDTO.java
│   │   │       │   ├── AttendanceDTO.java
│   │   │       │   ├── LeaveRequestDTO.java
│   │   │       │   ├── PayrollDTO.java
│   │   │       │   └── PayslipDTO.java
│   │   │       ├── entity/                  # JPA Entities
│   │   │       │   ├── User.java
│   │   │       │   ├── UserProfile.java
│   │   │       │   ├── UserEmployment.java
│   │   │       │   ├── UserBankDetails.java
│   │   │       │   ├── Attendance.java
│   │   │       │   ├── LeaveRequest.java
│   │   │       │   ├── LeaveBalance.java
│   │   │       │   ├── Payroll.java
│   │   │       │   ├── Payslip.java
│   │   │       │   ├── Notification.java
│   │   │       │   ├── OtpVerification.java
│   │   │       │   └── SystemSetting.java
│   │   │       ├── repository/              # Data Access Layer
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── AttendanceRepository.java
│   │   │       │   ├── LeaveRequestRepository.java
│   │   │       │   ├── PayrollRepository.java
│   │   │       │   ├── PayslipRepository.java
│   │   │       │   ├── NotificationRepository.java
│   │   │       │   ├── SystemSettingRepository.java
│   │   │       │   └── OtpVerificationRepository.java
│   │   │       ├── service/                 # Business Logic
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── EmployeeService.java
│   │   │       │   ├── AttendanceService.java
│   │   │       │   ├── LeaveService.java
│   │   │       │   ├── PayrollService.java
│   │   │       │   ├── PayslipService.java
│   │   │       │   ├── ReportService.java
│   │   │       │   ├── NotificationService.java
│   │   │       │   ├── EmailService.java
│   │   │       │   └── SettingsService.java
│   │   │       ├── security/                # Security Components
│   │   │       │   ├── JwtProvider.java
│   │   │       │   ├── JwtFilter.java
│   │   │       │   ├── UserPrincipal.java
│   │   │       │   └── SecurityUtils.java
│   │   │       ├── util/                    # Utility Classes
│   │   │       │   ├── ResponseUtil.java
│   │   │       │   ├── ValidationUtil.java
│   │   │       │   ├── DateUtil.java
│   │   │       │   └── PdfUtil.java
│   │   │       ├── exception/               # Exception Handlers
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── EntityNotFoundException.java
│   │   │       │   └── ValidationException.java
│   │   │       ├── enums/                   # Enums
│   │   │       │   ├── Role.java
│   │   │       │   ├── LeaveType.java
│   │   │       │   └── AttendanceStatus.java
│   │   │       └── PayrollApplication.java  # Main Spring Boot Application
│   │   └── resources/
│   │       ├── application.yml              # Default configuration
│   │       ├── application-dev.yml         # Development configuration
│   │       ├── application-prod.yml        # Production configuration (not committed)
│   │       ├── schema.sql                  # Database schema
│   │       └── templates/                  # Email templates
│   │           ├── forgot-password.html
│   │           └── payslip-email.html
│   └── test/
│       ├── java/
│       │   └── com/company/payroll/
│       │       ├── controller/
│       │       ├── service/
│       │       └── repository/
│       └── resources/
│           └── application-test.yml
├── pom.xml                                   # Maven configuration
├── .gitignore                               # Git ignore rules
└── README.md                                # Backend-specific README

### Backend Directory Details

#### `/config`
Configuration classes for Spring Boot application:
- **CorsConfig**: Handles CORS settings for cross-origin requests
- **SecurityConfig**: Spring Security bean configuration
- **JwtConfig**: JWT token configuration

#### `/controller`
REST API endpoints that handle HTTP requests:
- Maps URL paths to service methods
- Validates request data
- Returns JSON responses

#### `/dto`
Data Transfer Objects for API communication:
- Used for request/response payloads
- Decouples API contracts from internal entities
- Reduces unnecessary data exposure

#### `/entity`
JPA Entity classes representing database tables:
- Annotated with `@Entity` and mapped to tables
- Define relationships between tables
- Include validation annotations

#### `/repository`
Spring Data JPA repositories for database operations:
- Extend `JpaRepository` for CRUD operations
- Define custom query methods
- Handle database interactions

#### `/service`
Business logic layer:
- Implements core application logic
- Orchestrates repository and external service calls
- Handles transactions and error management

#### `/security`
Security-related components:
- JWT token generation and validation
- Security filters for request interception
- User principal and authentication details

#### `/util`
Utility classes for common operations:
- Response formatting
- Data validation
- PDF generation
- Date/time operations

#### `/exception`
Custom exception classes and global handlers:
- Centralized error handling
- Consistent error responses
- Business-specific exceptions

#### `/enums`
Enumeration classes for constants:
- User roles (Admin, HR, Employee)
- Leave types (Sick, Casual, etc.)
- Attendance statuses
```

## Frontend Structure

```
frontend/
├── src/
│   ├── components/                          # Reusable React components
│   │   ├── attendance/
│   │   │   ├── AttendanceList.jsx
│   │   │   ├── CheckInOut.jsx
│   │   │   └── AttendanceStats.jsx
│   │   ├── auth/
│   │   │   ├── LoginForm.jsx
│   │   │   ├── RegisterForm.jsx
│   │   │   ├── ForgotPasswordForm.jsx
│   │   │   └── OtpVerification.jsx
│   │   ├── employees/
│   │   │   ├── EmployeeList.jsx
│   │   │   ├── EmployeeForm.jsx
│   │   │   ├── EmployeeDetail.jsx
│   │   │   └── EmployeeCard.jsx
│   │   ├── leaves/
│   │   │   ├── LeaveRequest.jsx
│   │   │   ├── LeaveApproval.jsx
│   │   │   ├── LeaveList.jsx
│   │   │   └── LeaveBalance.jsx
│   │   ├── payroll/
│   │   │   ├── PayrollGeneration.jsx
│   │   │   ├── PayrollList.jsx
│   │   │   └── PayrollDetail.jsx
│   │   ├── payslip/
│   │   │   ├── PayslipList.jsx
│   │   │   ├── PayslipDetail.jsx
│   │   │   └── PayslipDownload.jsx
│   │   ├── reports/
│   │   │   ├── AttendanceReport.jsx
│   │   │   ├── PayrollReport.jsx
│   │   │   └── ReportGenerator.jsx
│   │   ├── profile/
│   │   │   ├── ProfileView.jsx
│   │   │   ├── ProfileEdit.jsx
│   │   │   └── BankDetails.jsx
│   │   ├── notifications/
│   │   │   ├── NotificationCenter.jsx
│   │   │   ├── NotificationList.jsx
│   │   │   └── NotificationBell.jsx
│   │   └── common/
│   │       ├── Header.jsx
│   │       ├── Sidebar.jsx
│   │       ├── Footer.jsx
│   │       ├── Loading.jsx
│   │       ├── ErrorMessage.jsx
│   │       ├── SuccessMessage.jsx
│   │       └── ConfirmModal.jsx
│   ├── pages/                               # Page components (screens)
│   │   ├── admin/
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── AdminUsers.jsx
│   │   │   ├── AdminSettings.jsx
│   │   │   └── AdminReports.jsx
│   │   ├── hr/
│   │   │   ├── HRDashboard.jsx
│   │   │   ├── EmployeeManagement.jsx
│   │   │   ├── AttendanceAdmin.jsx
│   │   │   ├── LeaveApprovalList.jsx
│   │   │   ├── PayrollAdmin.jsx
│   │   │   └── HRReports.jsx
│   │   ├── attendance/
│   │   │   ├── AttendancePage.jsx
│   │   │   └── CheckInPage.jsx
│   │   ├── auth/
│   │   │   ├── LoginPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── ForgotPasswordPage.jsx
│   │   │   └── ResetPasswordPage.jsx
│   │   ├── dashboard/
│   │   │   ├── EmployeeDashboard.jsx
│   │   │   └── Dashboard.jsx
│   │   ├── leaves/
│   │   │   ├── LeavePage.jsx
│   │   │   ├── LeaveApplicationPage.jsx
│   │   │   └── LeaveHistoryPage.jsx
│   │   ├── payslip/
│   │   │   ├── PayslipPage.jsx
│   │   │   └── PayslipDetailPage.jsx
│   │   ├── profile/
│   │   │   ├── ProfilePage.jsx
│   │   │   └── EditProfilePage.jsx
│   │   ├── reports/
│   │   │   └── ReportsPage.jsx
│   │   ├── notifications/
│   │   │   └── NotificationsPage.jsx
│   │   └── common/
│   │       ├── NotFoundPage.jsx
│   │       ├── UnauthorizedPage.jsx
│   │       └── ErrorPage.jsx
│   ├── services/                            # API service calls
│   │   ├── attendanceService.js
│   │   ├── authService.js
│   │   ├── employeeService.js
│   │   ├── leaveService.js
│   │   ├── notificationService.js
│   │   ├── payrollService.js
│   │   ├── payslipService.js
│   │   ├── profileService.js
│   │   ├── reportService.js
│   │   └── settingsService.js
│   ├── hooks/                               # Custom React hooks
│   │   ├── useAuth.js                       # Authentication hook
│   │   ├── useNotification.js               # Notification hook
│   │   ├── useFetch.js                      # Data fetching hook
│   │   ├── useForm.js                       # Form handling hook
│   │   └── useLocalStorage.js               # Local storage hook
│   ├── context/                             # React Context for state
│   │   ├── AuthContext.jsx
│   │   ├── NotificationContext.jsx
│   │   └── AppContext.jsx
│   ├── layouts/                             # Layout components
│   │   ├── AdminLayout.jsx
│   │   ├── AuthLayout.jsx
│   │   ├── EmployeeLayout.jsx
│   │   ├── HRLayout.jsx
│   │   └── MainLayout.jsx
│   ├── routes/                              # Routing configuration
│   │   ├── AppRoutes.jsx                    # Main routes
│   │   ├── ProtectedRoute.jsx               # Auth-protected routes
│   │   ├── RoleProtectedRoute.jsx           # Role-based protected routes
│   │   └── GuestRoute.jsx                   # Guest-only routes
│   ├── api/                                 # API client configuration
│   │   ├── axiosClient.js                  # Axios instance setup
│   │   └── apiResponse.js                  # API response interceptors
│   ├── utils/                               # Utility functions
│   │   ├── validators.js
│   │   ├── formatters.js
│   │   ├── dateUtils.js
│   │   ├── constants.js
│   │   └── helpers.js
│   ├── assets/                              # Static assets
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   ├── App.jsx                              # Root component
│   ├── main.jsx                             # Entry point
│   └── index.css                            # Global styles
├── public/                                  # Static files
│   ├── index.html
│   ├── favicon.ico
│   └── vite.svg
├── package.json                             # NPM dependencies
├── vite.config.js                           # Vite configuration
├── eslint.config.js                         # ESLint configuration
├── .gitignore                               # Git ignore rules
└── README.md                                # Frontend-specific README

### Frontend Directory Details

#### `/components`
Reusable React components organized by feature:
- Each component handles a specific feature
- Components are modular and reusable
- Include JSX, styling, and logic

#### `/pages`
Full-page components representing different screens:
- Map to routes in the application
- Compose multiple components
- Handle page-level state and effects

#### `/services`
API communication layer:
- Encapsulate API calls using Axios
- Handle request/response logic
- Provide clean interfaces for components

#### `/hooks`
Custom React hooks for logic reuse:
- Extract component logic
- Share state between components
- Simplify component code

#### `/context`
React Context for global state management:
- Authentication context (user, token)
- Notification context (alerts, messages)
- Application-wide state

#### `/layouts`
Layout wrapper components:
- Provide consistent layout for different user roles
- Include header, sidebar, footer
- Route-specific layouts

#### `/routes`
Routing configuration:
- Define application routes
- Implement route protection
- Handle role-based access

#### `/api`
API client configuration:
- Axios instance with base URL
- Request/response interceptors
- Error handling
```

## Documentation Folder

```
docs/
├── DATABASE_SCHEMA.md       # Database design documentation
├── FINAL_ARCHITECTURE_AUDIT.md
├── FINAL_PROJECT_BLUEPRINT.md
├── FRONTEND_API_CONTRACT.md
├── PROJECT_FREEZE_REPORT.md
├── PROJECT_MASTER_SPEC.md
├── PROJECT_STRUCTURE.md
├── SYSTEM_ARCHITECTURE.md
└── pseudocode/
    ├── ATTENDANCE_MANAGEMENT_PSEUDOCODE.md
    ├── AUTHENTICATION_PSEUDOCODE.md
    ├── EMPLOYEE_MANAGEMENT_PSEUDOCODE.md
    ├── LEAVE_MANAGEMENT_PSEUDOCODE.md
    ├── NOTIFICATION_PSEUDOCODE.md
    ├── PAYROLL_MANAGEMENT_PSEUDOCODE.md
    ├── PAYSLIP_MANAGEMENT_PSEUDOCODE.md
    ├── REPORTS_PSEUDOCODE.md
    └── SYSTEM_SETTINGS_PSEUDOCODE.md
```

## Key Files at Root Level

| File | Purpose |
|------|---------|
| `README.md` | Main project documentation and getting started guide |
| `LICENSE` | MIT License for the project |
| `CONTRIBUTING.md` | Guidelines for contributing to the project |
| `CHANGELOG.md` | Version history and release notes |
| `PROJECT_STRUCTURE.md` | This file - detailed structure explanation |
| `DATABASE_SCHEMA.md` | Database design and relationships |
| `API_DOCUMENTATION.md` | API endpoints and usage |

## Data Flow

### Authentication Flow
```
Frontend (LoginForm) 
  → Axios API Call to /api/auth/login 
  → Spring Boot AuthController 
  → AuthService (validate credentials) 
  → JwtProvider (generate token) 
  → Response with JWT token 
  → Frontend (store token in context/localStorage)
```

### Attendance Check-In Flow
```
Frontend (CheckInOut component) 
  → Axios API Call to /api/attendance/check-in 
  → AttendanceController 
  → AttendanceService (create record) 
  → AttendanceRepository (persist) 
  → NotificationService (send email) 
  → EmailService (send via Spring Mail) 
  → Database (MySQL) 
  → Response to Frontend
```

### Payroll Generation Flow
```
Frontend (PayrollGeneration component) 
  → Axios API Call to /api/payrolls/generate 
  → PayrollController 
  → PayrollService (calculate payroll) 
  → EmployeeService (get employee data) 
  → AttendanceService (get attendance) 
  → LeaveService (get leave records) 
  → PayrollRepository (persist) 
  → PayslipService (create payslips) 
  → PdfUtil (generate PDF) 
  → NotificationService (send emails) 
  → Response with status
```

## Technology Stack by Layer

### Presentation Layer (Frontend)
- React 19.2
- Vite
- Axios
- React Router
- Context API
- Tailwind CSS

### API Layer (Backend)
- Spring Boot 3.3.5
- Spring Web (REST Controllers)
- Spring Security
- JWT (JJWT)

### Business Logic Layer
- Spring Service
- Spring Mail
- PDF Generation (OpenPDF)
- Business Rules

### Data Access Layer
- Spring Data JPA
- Hibernate
- MySQL Connector

### Database
- MySQL 8.0

## Build & Deployment

### Development
```
Frontend: npm run dev (Vite dev server)
Backend: mvn spring-boot:run (Spring Boot dev server)
Database: MySQL running locally
```

### Production
```
Frontend: npm run build (generates dist folder)
Backend: mvn package (generates JAR file)
Database: MySQL on production server
```

## Best Practices

1. **Separation of Concerns**: Each layer has clear responsibilities
2. **DRY Principle**: Components and services are reusable
3. **Configuration Management**: Different configs for dev/prod
4. **Security**: JWT, Spring Security, Password encryption
5. **Error Handling**: Global exception handlers and try-catch blocks
6. **Logging**: Proper logging at each layer
7. **Documentation**: Self-documenting code with comments
8. **Testing**: Unit and integration tests at key layers
