# API Documentation

Complete REST API endpoint documentation for the Employee Payroll & Workforce Management System.

## Table of Contents

- [Base URL](#base-url)
- [Authentication](#authentication)
- [Response Format](#response-format)
- [Status Codes](#status-codes)
- [Authentication Endpoints](#authentication-endpoints)
- [Employee Endpoints](#employee-endpoints)
- [Attendance Endpoints](#attendance-endpoints)
- [Leave Endpoints](#leave-endpoints)
- [Payroll Endpoints](#payroll-endpoints)
- [Payslip Endpoints](#payslip-endpoints)
- [Report Endpoints](#report-endpoints)
- [Notification Endpoints](#notification-endpoints)
- [Settings Endpoints](#settings-endpoints)
- [Error Handling](#error-handling)

## Base URL

```
http://localhost:8080/api
```

For production:
```
https://api.company.com/api
```

## Authentication

All endpoints except `/auth/login` and `/auth/register` require JWT authentication.

### JWT Token Format

Include the token in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

### Token Expiration

- Default token expiration: 24 hours
- Refresh tokens: 30 days (future implementation)

## Response Format

All responses follow a standard format:

### Success Response (200, 201)

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Operation successful",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response

```json
{
  "success": false,
  "statusCode": 400,
  "message": "Validation error",
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Paginated Response

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Data retrieved successfully",
  "data": [
    { "id": 1, "name": "Employee 1" },
    { "id": 2, "name": "Employee 2" }
  ],
  "pagination": {
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 204 | No Content | Request successful, no content to return |
| 400 | Bad Request | Invalid request parameters |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 500 | Server Error | Internal server error |

## Authentication Endpoints

### 1. User Login

Login with email and password.

```
POST /auth/login
Content-Type: application/json

{
  "email": "user@company.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "user@company.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "EMPLOYEE"
    }
  }
}
```

**Errors:** 400, 401, 500

---

### 2. User Registration

Register a new user account.

```
POST /auth/register
Content-Type: application/json

{
  "email": "newuser@company.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response (201):**
```json
{
  "success": true,
  "statusCode": 201,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "newuser@company.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

**Errors:** 400, 409, 500

---

### 3. Forgot Password

Initiate password reset process.

```
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "user@company.com"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "OTP sent to your email",
  "data": {
    "expiryTime": "2024-01-15T11:30:00Z"
  }
}
```

---

### 4. Verify OTP

Verify OTP sent to email.

```
POST /auth/verify-otp
Content-Type: application/json

{
  "email": "user@company.com",
  "otp": "123456"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "resetToken": "reset-token-xyz"
  }
}
```

---

### 5. Reset Password

Reset password with OTP verification.

```
POST /auth/reset-password
Content-Type: application/json

{
  "resetToken": "reset-token-xyz",
  "newPassword": "NewPassword@123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Password reset successfully"
}
```

---

## Employee Endpoints

### 1. Get All Employees

Get list of all employees with pagination.

```
GET /employees?pageNumber=1&pageSize=10&sortBy=firstName&sortOrder=ASC
Authorization: Bearer <token>
```

**Query Parameters:**
- `pageNumber`: Page number (default: 1)
- `pageSize`: Records per page (default: 10)
- `sortBy`: Sort field (default: id)
- `sortOrder`: ASC or DESC (default: ASC)
- `search`: Search by name or email

**Response (200):**
```json
{
  "success": true,
  "statusCode": 200,
  "data": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@company.com",
      "designation": "Senior Developer",
      "department": "IT",
      "status": "ACTIVE"
    }
  ],
  "pagination": {
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```

---

### 2. Get Employee by ID

Get detailed employee information.

```
GET /employees/{id}
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@company.com",
    "phoneNumber": "9876543210",
    "designation": "Senior Developer",
    "department": "IT",
    "joinDate": "2022-01-15",
    "reportingTo": "Jane Smith",
    "baseSalary": 75000,
    "profile": {
      "gender": "MALE",
      "dateOfBirth": "1995-05-20",
      "address": "123 Main St, City"
    },
    "bankDetails": {
      "accountNumber": "****5678",
      "bankName": "HDFC Bank",
      "ifscCode": "HDFC0001234"
    }
  }
}
```

---

### 3. Create Employee

Create a new employee record.

```
POST /employees
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@company.com",
  "phoneNumber": "9876543210",
  "designation": "Project Manager",
  "department": "HR",
  "joinDate": "2024-02-01",
  "baseSalary": 85000,
  "profile": {
    "gender": "FEMALE",
    "dateOfBirth": "1990-03-15",
    "address": "456 Oak St, City"
  },
  "bankDetails": {
    "accountNumber": "9876543210123456",
    "bankName": "HDFC Bank",
    "ifscCode": "HDFC0001234"
  }
}
```

**Response (201):**
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Employee created successfully",
  "data": {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane@company.com",
    "designation": "Project Manager"
  }
}
```

---

### 4. Update Employee

Update employee information.

```
PUT /employees/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Jane",
  "designation": "Senior Project Manager",
  "department": "Projects"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Employee updated successfully",
  "data": {
    "id": 2,
    "firstName": "Jane",
    "designation": "Senior Project Manager"
  }
}
```

---

### 5. Delete Employee

Delete an employee record.

```
DELETE /employees/{id}
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "message": "Employee deleted successfully"
}
```

---

## Attendance Endpoints

### 1. Get Attendance Records

Get attendance records for a user or date range.

```
GET /attendance?startDate=2024-01-01&endDate=2024-01-31&userId=1
Authorization: Bearer <token>
```

**Query Parameters:**
- `startDate`: Start date (YYYY-MM-DD)
- `endDate`: End date (YYYY-MM-DD)
- `userId`: Employee ID (optional, defaults to current user)
- `pageNumber`: Page number (default: 1)
- `pageSize`: Records per page (default: 10)

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "attendanceDate": "2024-01-15",
      "checkInTime": "09:00:00",
      "checkOutTime": "17:30:00",
      "workingHours": 8.5,
      "status": "PRESENT"
    }
  ]
}
```

---

### 2. Check-In

Record employee check-in.

```
POST /attendance/check-in
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": 28.6139,
  "longitude": 77.2090,
  "deviceInfo": "Mobile App v1.0"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Checked in successfully",
  "data": {
    "checkInTime": "2024-01-15T09:00:00Z",
    "message": "Welcome to work!"
  }
}
```

---

### 3. Check-Out

Record employee check-out.

```
POST /attendance/check-out
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": 28.6139,
  "longitude": 77.2090,
  "deviceInfo": "Mobile App v1.0"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Checked out successfully",
  "data": {
    "checkOutTime": "2024-01-15T17:30:00Z",
    "workingHours": 8.5,
    "message": "Have a good evening!"
  }
}
```

---

### 4. Get Attendance Stats

Get attendance summary for a period.

```
GET /attendance/stats?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "totalWorkingDays": 22,
    "presentDays": 20,
    "absentDays": 1,
    "halfDays": 1,
    "averageWorkingHours": 8.5,
    "attendancePercentage": 90.9
  }
}
```

---

## Leave Endpoints

### 1. Apply for Leave

Submit a leave request.

```
POST /leaves
Authorization: Bearer <token>
Content-Type: application/json

{
  "leaveType": "CASUAL",
  "startDate": "2024-02-01",
  "endDate": "2024-02-03",
  "reason": "Personal work",
  "attachment": "file_url"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Leave request submitted successfully",
  "data": {
    "id": 1,
    "leaveType": "CASUAL",
    "startDate": "2024-02-01",
    "endDate": "2024-02-03",
    "numberOfDays": 3,
    "status": "PENDING"
  }
}
```

---

### 2. Get Leave Requests

Get all leave requests.

```
GET /leaves?status=PENDING&pageNumber=1&pageSize=10
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "employeeName": "John Doe",
      "leaveType": "CASUAL",
      "startDate": "2024-02-01",
      "endDate": "2024-02-03",
      "numberOfDays": 3,
      "status": "PENDING",
      "reason": "Personal work"
    }
  ],
  "pagination": {
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 15,
    "totalPages": 2
  }
}
```

---

### 3. Approve Leave

Approve a leave request (HR/Admin only).

```
PUT /leaves/{id}/approve
Authorization: Bearer <token>
Content-Type: application/json

{
  "remarks": "Approved"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Leave approved successfully",
  "data": {
    "id": 1,
    "status": "APPROVED",
    "approvalDate": "2024-01-15T10:00:00Z"
  }
}
```

---

### 4. Reject Leave

Reject a leave request (HR/Admin only).

```
PUT /leaves/{id}/reject
Authorization: Bearer <token>
Content-Type: application/json

{
  "remarks": "Cannot approve at this time"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Leave rejected successfully",
  "data": {
    "id": 1,
    "status": "REJECTED"
  }
}
```

---

### 5. Get Leave Balance

Get employee leave balance.

```
GET /leaves/balance?fiscalYear=2024
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "fiscalYear": 2024,
    "balances": [
      {
        "leaveType": "CASUAL",
        "totalDays": 12,
        "usedDays": 3,
        "remainingDays": 9
      },
      {
        "leaveType": "SICK",
        "totalDays": 8,
        "usedDays": 1,
        "remainingDays": 7
      }
    ]
  }
}
```

---

## Payroll Endpoints

### 1. Generate Payroll

Generate payroll for a month (Admin/HR only).

```
POST /payrolls/generate
Authorization: Bearer <token>
Content-Type: application/json

{
  "payrollMonth": 1,
  "payrollYear": 2024,
  "employees": [1, 2, 3]
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Payroll generated successfully",
  "data": {
    "payrollId": 1,
    "month": "January 2024",
    "totalEmployees": 3,
    "totalGrossSalary": 250000,
    "totalNetSalary": 220000,
    "status": "GENERATED"
  }
}
```

---

### 2. Get Payroll Records

Get payroll records.

```
GET /payrolls?month=1&year=2024&status=GENERATED
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "employeeName": "John Doe",
      "month": "January 2024",
      "grossSalary": 75000,
      "deductions": 8500,
      "netSalary": 66500,
      "status": "GENERATED"
    }
  ]
}
```

---

### 3. Get Payroll Details

Get detailed payroll information.

```
GET /payrolls/{id}
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "month": "January 2024",
    "workingDays": 22,
    "presentDays": 20,
    "absentDays": 2,
    "basicSalary": 50000,
    "hra": 10000,
    "dearness": 5000,
    "allowances": 10000,
    "pfDeduction": 1800,
    "esicDeduction": 0,
    "incomeTax": 5700,
    "otherDeductions": 0,
    "grossSalary": 75000,
    "netSalary": 66500
  }
}
```

---

## Payslip Endpoints

### 1. Get Payslips

Get employee payslips.

```
GET /payslips?month=1&year=2024&pageNumber=1
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "payslipNumber": "PS-2024-01-001",
      "month": "January 2024",
      "grossSalary": 75000,
      "netSalary": 66500,
      "isEmailSent": true,
      "pdfUrl": "/payslips/ps-2024-01-001.pdf"
    }
  ],
  "pagination": {
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 12,
    "totalPages": 2
  }
}
```

---

### 2. Download Payslip

Download payslip as PDF.

```
GET /payslips/{id}/download
Authorization: Bearer <token>
```

**Response:** PDF file

---

### 3. Email Payslip

Send payslip via email.

```
POST /payslips/{id}/email
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "message": "Payslip sent to email successfully"
}
```

---

## Report Endpoints

### 1. Attendance Report

Generate attendance report.

```
GET /reports/attendance?startDate=2024-01-01&endDate=2024-01-31&format=PDF
Authorization: Bearer <token>
```

**Response:** PDF or JSON report

---

### 2. Payroll Report

Generate payroll report.

```
GET /reports/payroll?month=1&year=2024&format=EXCEL
Authorization: Bearer <token>
```

**Response:** Excel or PDF report

---

### 3. Leave Report

Generate leave report.

```
GET /reports/leave?year=2024&format=PDF
Authorization: Bearer <token>
```

**Response:** PDF report

---

## Notification Endpoints

### 1. Get Notifications

Get user notifications.

```
GET /notifications?isRead=false&pageNumber=1&pageSize=10
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Leave Approved",
      "message": "Your leave request for Jan 15-17 has been approved",
      "type": "LEAVE_APPROVED",
      "isRead": false,
      "createdAt": "2024-01-15T10:00:00Z"
    }
  ]
}
```

---

### 2. Mark as Read

Mark notification as read.

```
PUT /notifications/{id}/read
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

---

## Settings Endpoints

### 1. Get Settings

Get system settings (Admin only).

```
GET /settings
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "key": "company_name",
      "value": "ABC Corporation",
      "type": "STRING"
    },
    {
      "key": "financial_year_start",
      "value": "4",
      "type": "INTEGER"
    }
  ]
}
```

---

### 2. Update Settings

Update system settings (Admin only).

```
PUT /settings/{key}
Authorization: Bearer <token>
Content-Type: application/json

{
  "value": "XYZ Corporation"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Setting updated successfully"
}
```

---

## Error Handling

### Common Error Responses

**Authentication Error (401):**
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Invalid or expired token",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Authorization Error (403):**
```json
{
  "success": false,
  "statusCode": 403,
  "message": "You don't have permission to access this resource",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Validation Error (400):**
```json
{
  "success": false,
  "statusCode": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Not Found Error (404):**
```json
{
  "success": false,
  "statusCode": 404,
  "message": "Employee not found with ID: 999",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Server Error (500):**
```json
{
  "success": false,
  "statusCode": 500,
  "message": "An internal server error occurred",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## Rate Limiting

- **Rate Limit**: 100 requests per minute per user
- **Header**: `X-RateLimit-Remaining: 95`

---

## Pagination

For endpoints that return lists, pagination parameters are:

- `pageNumber`: 1-based page number (default: 1)
- `pageSize`: Records per page (default: 10, max: 100)

---

## Sorting

Where applicable, use:

- `sortBy`: Field name to sort by
- `sortOrder`: ASC or DESC

---

For more information, refer to the [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) and [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md).
