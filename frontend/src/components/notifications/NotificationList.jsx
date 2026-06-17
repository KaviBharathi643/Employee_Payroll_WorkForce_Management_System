import NotificationStatusBadge from './NotificationStatusBadge';
import NotificationTypeBadge from './NotificationTypeBadge';
import { formatDateTime } from '../../utils/formatters';

export default function NotificationList({
  items,
  loading = false,
  emptyMessage,
  onSelect,
}) {
  if (loading) {
    return <p className="text-sm text-slate-600">Loading notifications...</p>;
  }

  if (!items.length) {
    return (
      <p className="rounded-lg border border-dashed border-slate-200 px-4 py-8 text-center text-sm text-slate-500">
        {emptyMessage || 'No notifications.'}
      </p>
    );
  }

  return (
    <ul className="divide-y divide-slate-200 rounded-lg border border-slate-200">
      {items.map((item) => (
        <li key={item.notificationId}>
          <button
            type="button"
            onClick={() => onSelect(item)}
            className={`flex w-full items-start gap-3 px-4 py-3 text-left transition hover:bg-slate-50 ${
              item.status === 'UNREAD' ? 'bg-blue-50/40' : 'bg-white'
            }`}
          >
            <div className="min-w-0 flex-1">
              <div className="mb-1 flex flex-wrap items-center gap-2">
                <NotificationTypeBadge type={item.notificationType} />
                <NotificationStatusBadge status={item.status} />
              </div>
              <p
                className={`text-sm ${
                  item.status === 'UNREAD' ? 'font-semibold text-slate-900' : 'text-slate-700'
                }`}
              >
                {item.title}
              </p>
              <p className="mt-1 text-xs text-slate-500">{formatDateTime(item.createdAt)}</p>
            </div>
            <span className="shrink-0 text-xs text-slate-400">View</span>
          </button>
        </li>
      ))}
    </ul>
  );
}
