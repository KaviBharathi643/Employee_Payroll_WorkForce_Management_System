import PortalShell from '../components/common/PortalShell';

const navItems = [
  { to: '/employee/profile', label: 'My Profile', end: true },
  { to: '/employee/attendance', label: 'Attendance' },
  { to: '/employee/leaves', label: 'Leave' },
  { to: '/employee/payroll', label: 'Payroll' },
  { to: '/employee/payslips', label: 'Payslips' },
  { to: '/employee/notifications', label: 'Notifications' },
];

export default function EmployeeLayout() {
  return <PortalShell title="Employee" navItems={navItems} />;
}
