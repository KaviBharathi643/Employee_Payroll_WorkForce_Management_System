import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import notificationService from '../services/notificationService';
import { NOTIFICATION_POLL_MS } from '../utils/constants';
import { useAuth } from './AuthContext';

const NotificationContext = createContext(null);

export function NotificationProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [bellData, setBellData] = useState({ latestNotifications: [], unreadCount: 0 });
  const [loading, setLoading] = useState(false);

  const refreshBell = useCallback(async () => {
    if (!isAuthenticated) {
      setBellData({ latestNotifications: [], unreadCount: 0 });
      return;
    }
    setLoading(true);
    try {
      const data = await notificationService.getBell();
      setBellData(data);
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    refreshBell();
  }, [refreshBell]);

  useEffect(() => {
    if (!isAuthenticated) {
      return undefined;
    }
    const id = setInterval(refreshBell, NOTIFICATION_POLL_MS);
    return () => clearInterval(id);
  }, [isAuthenticated, refreshBell]);

  const markAsRead = useCallback(async (notificationId) => {
    const data = await notificationService.markAsRead(notificationId);
    await refreshBell();
    return data;
  }, [refreshBell]);

  const markAllAsRead = useCallback(async () => {
    await notificationService.markAllAsRead();
    await refreshBell();
  }, [refreshBell]);

  const value = useMemo(
    () => ({
      bellData,
      loading,
      refreshBell,
      markAsRead,
      markAllAsRead,
    }),
    [bellData, loading, refreshBell, markAsRead, markAllAsRead],
  );

  return (
    <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>
  );
}

export function useNotification() {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
}
