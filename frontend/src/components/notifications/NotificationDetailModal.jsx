import { useNavigate } from 'react-router-dom';
import DetailGrid from '../common/DetailGrid';
import Modal from '../common/Modal';
import NotificationStatusBadge from './NotificationStatusBadge';
import NotificationTypeBadge from './NotificationTypeBadge';
import { useAuth } from '../../context/AuthContext';
import { formatDateTime } from '../../utils/formatters';
import { resolveNotificationPath } from '../../utils/notificationNavigation';

export default function NotificationDetailModal({ notification, onClose }) {
  const navigate = useNavigate();
  const { role } = useAuth();

  if (!notification) {
    return null;
  }

  const items = [
    { label: 'Type', value: <NotificationTypeBadge type={notification.notificationType} /> },
    { label: 'Status', value: <NotificationStatusBadge status={notification.status} /> },
    { label: 'Created', value: formatDateTime(notification.createdAt) },
    { label: 'Message', value: notification.message },
  ];

  const targetPath = resolveNotificationPath(
    role,
    notification.relatedEntityType,
    notification.relatedEntityId,
  );

  return (
    <Modal title={notification.title} onClose={onClose}>
      <DetailGrid items={items} />
      <div className="mt-6 flex gap-2">
        {notification.relatedEntityType && (
          <button
            type="button"
            onClick={() => {
              navigate(targetPath);
              onClose();
            }}
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
          >
            Go to {notification.relatedEntityType.toLowerCase()} page
          </button>
        )}
        <button
          type="button"
          onClick={onClose}
          className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
        >
          Close
        </button>
      </div>
    </Modal>
  );
}
