import PortalShell from '../components/common/PortalShell';

const navItems = [
  { to: '/hr', label: 'Dashboard', end: true },
  { to: '/hr/employees', label: 'Employees' },
  { to: '/hr/attendance', label: 'Attendance' },
  { to: '/hr/leaves', label: 'Leave' },
  { to: '/hr/payroll', label: 'Payroll' },
  { to: '/hr/reports', label: 'Reports' },
  { to: '/hr/profile', label: 'My Profile' },
  { to: '/hr/notifications', label: 'Notifications' },
];

export default function HRLayout() {
  return <PortalShell title="HR" navItems={navItems} />;
}
