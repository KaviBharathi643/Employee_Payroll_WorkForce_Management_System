import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import NotificationDetailModal from '../../components/notifications/NotificationDetailModal';
import NotificationList from '../../components/notifications/NotificationList';
import notificationService from '../../services/notificationService';
import { useNotification } from '../../context/NotificationContext';
import { getErrorMessage } from '../../utils/authErrors';

const TABS = [
  { id: 'recent', label: 'Recent (10)' },
  { id: 'history', label: 'History (30 days)' },
];

export default function NotificationsPage() {
  const { bellData, markAsRead, markAllAsRead, refreshBell } = useNotification();

  const [activeTab, setActiveTab] = useState('recent');
  const [recent, setRecent] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [markingAll, setMarkingAll] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [selected, setSelected] = useState(null);

  const loadLists = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [latest, historyData] = await Promise.all([
        notificationService.getLatest(),
        notificationService.getHistory(),
      ]);
      setRecent(latest || []);
      setHistory(historyData || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load notifications'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadLists();
  }, [loadLists]);

  const handleSelect = async (item) => {
    setError('');
    try {
      const detail = await markAsRead(item.notificationId);
      setSelected(detail);
      await loadLists();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to open notification'));
    }
  };

  const handleMarkAll = async () => {
    if (!window.confirm('Mark all notifications as read?')) {
      return;
    }
    setMarkingAll(true);
    setError('');
    try {
      await markAllAsRead();
      setNotice('All notifications marked as read');
      await loadLists();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to mark all as read'));
    } finally {
      setMarkingAll(false);
    }
  };

  const rows = activeTab === 'recent' ? recent : history;

  return (
    <div>
      <PageHeader
        title="Notifications"
        description={`${bellData.unreadCount} unread · Bell refreshes every 60 seconds`}
        actions={
          <button
            type="button"
            disabled={markingAll || bellData.unreadCount === 0}
            onClick={handleMarkAll}
            className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
          >
            {markingAll ? 'Updating...' : 'Mark all as read'}
          </button>
        }
      />

      {notice && (
        <div className="mb-4">
          <Alert type="success">{notice}</Alert>
        </div>
      )}
      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}

      <div className="mb-6 border-b border-slate-200">
        <nav className="-mb-px flex gap-4">
          {TABS.map((tab) => (
            <button
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
              className={`border-b-2 px-1 py-2 text-sm font-medium ${
                activeTab === tab.id
                  ? 'border-slate-900 text-slate-900'
                  : 'border-transparent text-slate-500 hover:text-slate-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      <NotificationList
        items={rows}
        loading={loading}
        emptyMessage={
          activeTab === 'recent'
            ? 'No recent notifications.'
            : 'No notifications in the last 30 days.'
        }
        onSelect={handleSelect}
      />

      {selected && (
        <NotificationDetailModal
          notification={selected}
          onClose={() => {
            setSelected(null);
            refreshBell();
          }}
        />
      )}
    </div>
  );
}
