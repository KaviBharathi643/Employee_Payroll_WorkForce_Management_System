import { Link } from 'react-router-dom';
import NotificationList from './NotificationList';
import { notificationsPathForRole } from '../../utils/notificationNavigation';

export default function NotificationBellPanel({
  role,
  bellData,
  onSelect,
  onMarkAllRead,
  markingAll,
}) {
  const notificationsPath = notificationsPathForRole(role);

  return (
    <div className="absolute right-0 top-full z-50 mt-2 w-80 rounded-xl border border-slate-200 bg-white shadow-lg ring-1 ring-slate-200">
      <div className="flex items-center justify-between border-b border-slate-200 px-4 py-3">
        <p className="text-sm font-semibold text-slate-900">Notifications</p>
        <span className="text-xs text-slate-500">{bellData.unreadCount} unread</span>
      </div>
      <div className="max-h-72 overflow-y-auto p-2">
        <NotificationList
          items={bellData.latestNotifications || []}
          emptyMessage="You're all caught up."
          onSelect={onSelect}
        />
      </div>
      <div className="flex items-center justify-between border-t border-slate-200 px-4 py-3">
        <button
          type="button"
          disabled={markingAll || bellData.unreadCount === 0}
          onClick={onMarkAllRead}
          className="text-xs font-medium text-slate-700 hover:underline disabled:opacity-50"
        >
          {markingAll ? 'Updating...' : 'Mark all read'}
        </button>
        <Link
          to={notificationsPath}
          className="text-xs font-medium text-slate-900 hover:underline"
        >
          View all
        </Link>
      </div>
    </div>
  );
}
