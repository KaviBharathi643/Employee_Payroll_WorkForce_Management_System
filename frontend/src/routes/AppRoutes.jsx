import { Navigate, Route, Routes } from 'react-router-dom';
import AdminLayout from '../layouts/AdminLayout';
import AuthLayout from '../layouts/AuthLayout';
import EmployeeLayout from '../layouts/EmployeeLayout';
import HRLayout from '../layouts/HRLayout';
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage';
import LoginPage from '../pages/auth/LoginPage';
import ResetPasswordPage from '../pages/auth/ResetPasswordPage';
import HrDetailPage from '../pages/admin/HrDetailPage';
import HrManagementPage from '../pages/admin/HrManagementPage';
import SystemSettingsPage from '../pages/admin/SystemSettingsPage';
import DashboardPage from '../pages/dashboard/DashboardPage';
import ReportsPage from '../pages/reports/ReportsPage';
import EmployeeDetailPage from '../pages/hr/EmployeeDetailPage';
import EmployeesPage from '../pages/hr/EmployeesPage';
import EmployeeAttendancePage from '../pages/attendance/EmployeeAttendancePage';
import ManagementAttendancePage from '../pages/attendance/ManagementAttendancePage';
import HrAttendancePage from "../pages/attendance/HrAttendancePage";
import EmployeeLeavePage from '../pages/leaves/EmployeeLeavePage';
import HrLeavePage from '../pages/leaves/HrLeavePage';
import ManagementLeavePage from '../pages/leaves/ManagementLeavePage';
import PayslipsPage from '../pages/payslip/PayslipsPage';
import EmployeePayrollPage from '../pages/payroll/EmployeePayrollPage';
import ManagementPayrollPage from '../pages/payroll/ManagementPayrollPage';
import NotificationsPage from '../pages/notifications/NotificationsPage';
import MyProfilePage from '../pages/profile/MyProfilePage';
import { ROLES } from '../utils/constants';
import GuestRoute from './GuestRoute';
import ProtectedRoute from './ProtectedRoute';
import RoleProtectedRoute from './RoleProtectedRoute';

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<GuestRoute />}>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<RoleProtectedRoute allowedRoles={[ROLES.EMPLOYEE]} />}>
          <Route path="/employee" element={<EmployeeLayout />}>
            <Route index element={<Navigate to="/employee/profile" replace />} />
            <Route path="profile" element={<MyProfilePage />} />
            <Route path="attendance" element={<EmployeeAttendancePage />} />
            <Route path="leaves" element={<EmployeeLeavePage />} />
            <Route path="payroll" element={<EmployeePayrollPage />} />
            <Route path="payslips" element={<PayslipsPage />} />
            <Route path="notifications" element={<NotificationsPage />} />
          </Route>
        </Route>

        <Route element={<RoleProtectedRoute allowedRoles={[ROLES.HR]} />}>
          <Route path="/hr" element={<HRLayout />}>
            <Route index element={<DashboardPage viewerRole={ROLES.HR} />} />
            <Route path="employees" element={<EmployeesPage />} />
            <Route path="employees/:id" element={<EmployeeDetailPage />} />
            <Route
              path="attendance"
              element={<HrAttendancePage />}
            />
            <Route path="leaves" element={<HrLeavePage />} />
            <Route
              path="payroll"
              element={<ManagementPayrollPage viewerRole={ROLES.HR} />}
            />
            <Route path="reports" element={<ReportsPage viewerRole={ROLES.HR} />} />
            <Route path="profile" element={<MyProfilePage />} />
            <Route path="notifications" element={<NotificationsPage />} />
          </Route>
        </Route>

        <Route element={<RoleProtectedRoute allowedRoles={[ROLES.ADMIN]} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<DashboardPage viewerRole={ROLES.ADMIN} />} />
            <Route path="hr" element={<HrManagementPage />} />
            <Route path="hr/:id" element={<HrDetailPage />} />
            <Route
              path="attendance"
              element={<ManagementAttendancePage viewerRole={ROLES.ADMIN} />}
            />
            <Route
              path="leaves"
              element={<ManagementLeavePage viewerRole={ROLES.ADMIN} />}
            />
            <Route
              path="payroll"
              element={<ManagementPayrollPage viewerRole={ROLES.ADMIN} />}
            />
            <Route path="reports" element={<ReportsPage viewerRole={ROLES.ADMIN} />} />
            <Route path="settings" element={<SystemSettingsPage />} />
            <Route path="profile" element={<MyProfilePage />} />
            <Route path="notifications" element={<NotificationsPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
