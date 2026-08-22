import { useEffect, useRef, useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import NotificationBellPanel from '../notifications/NotificationBellPanel';
import NotificationDetailModal from '../notifications/NotificationDetailModal';
import { useAuth } from '../../context/AuthContext';
import { useNotification } from '../../context/NotificationContext';
import { notificationsPathForRole } from '../../utils/notificationNavigation';

export default function PortalShell({ title, navItems }) {
  const { user, role, logout } = useAuth();
  const { bellData, markAsRead, markAllAsRead } = useNotification();
  const [bellOpen, setBellOpen] = useState(false);
  const [markingAll, setMarkingAll] = useState(false);
  const [selected, setSelected] = useState(null);
  const bellRef = useRef(null);

  const notificationsPath = notificationsPathForRole(role);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (bellRef.current && !bellRef.current.contains(event.target)) {
        setBellOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleBellSelect = async (item) => {
    setBellOpen(false);
    try {
      const detail = await markAsRead(item.notificationId);
      setSelected(detail);
    } catch {
      setSelected(null);
    }
  };

  const handleMarkAll = async () => {
    setMarkingAll(true);
    try {
      await markAllAsRead();
      setBellOpen(false);
    } finally {
      setMarkingAll(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b border-secondary/60 bg-card">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4">
          <div>
            <p className="text-xs font-bold uppercase tracking-wide text-primary">{title}</p>
            <h1 className="text-lg font-bold text-primary-dark">Workforce Portal</h1>
          </div>
          <div className="flex items-center gap-4">
            <div className="relative" ref={bellRef}>
              <button
                type="button"
                onClick={() => setBellOpen((open) => !open)}
                className="relative rounded-lg border border-primary px-3 py-1.5 text-sm font-medium text-primary hover:bg-secondary bg-card transition-colors"
                aria-label="Notifications"
              >
                Notifications
                {bellData.unreadCount > 0 && (
                  <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-primary-dark px-1 text-xs font-bold text-white">
                    {bellData.unreadCount > 9 ? '9+' : bellData.unreadCount}
                  </span>
                )}
              </button>
              {bellOpen && (
                <NotificationBellPanel
                  role={role}
                  bellData={bellData}
                  onSelect={handleBellSelect}
                  onMarkAllRead={handleMarkAll}
                  markingAll={markingAll}
                />
              )}
            </div>
            <NavLink
              to={notificationsPath}
              className="hidden rounded-full bg-secondary px-3 py-1 text-sm font-semibold text-primary-dark hover:bg-secondary/80 sm:inline transition-colors"
            >
              {bellData.unreadCount} unread
            </NavLink>
            <span className="text-sm font-semibold text-text-main">{user?.fullName}</span>
            <button
              type="button"
              onClick={logout}
              className="rounded-lg border border-primary px-3 py-1.5 text-sm font-medium text-primary hover:bg-secondary bg-card transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto flex max-w-7xl gap-6 px-4 py-6">
        <aside className="w-56 shrink-0">
          <nav className="space-y-1">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `block rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-secondary text-primary-dark font-semibold'
                      : 'text-text-main hover:bg-secondary/40 hover:text-primary-dark'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </aside>
        <main className="min-h-[70vh] flex-1 rounded-xl bg-card p-6 shadow-sm ring-1 ring-secondary/50">
          <Outlet />
        </main>
      </div>

      {selected && (
        <NotificationDetailModal notification={selected} onClose={() => setSelected(null)} />
      )}
    </div>
  );
}
