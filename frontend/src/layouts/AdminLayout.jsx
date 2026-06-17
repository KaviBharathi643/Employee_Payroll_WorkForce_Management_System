import PortalShell from '../components/common/PortalShell';

const navItems = [
  { to: '/admin', label: 'Dashboard', end: true },
  { to: '/admin/hr', label: 'HR Management' },
  { to: '/admin/attendance', label: 'HR Attendance' },
  { to: '/admin/leaves', label: 'HR Leave' },
  { to: '/admin/payroll', label: 'HR Payroll' },
  { to: '/admin/reports', label: 'Reports' },
  { to: '/admin/settings', label: 'System Settings' },
  { to: '/admin/profile', label: 'My Profile' },
  { to: '/admin/notifications', label: 'Notifications' },
];

export default function AdminLayout() {
  return <PortalShell title="Admin" navItems={navItems} />;
}
