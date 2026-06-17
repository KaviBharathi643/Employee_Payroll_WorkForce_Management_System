import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import DashboardAnalyticsPanel from '../../components/reports/DashboardAnalyticsPanel';
import reportService from '../../services/reportService';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLES } from '../../utils/constants';

const CONFIG = {
  [ROLES.HR]: {
    title: 'HR Dashboard',
    description: 'Workforce analytics for employees, attendance, leave, and payroll.',
  },
  [ROLES.ADMIN]: {
    title: 'Admin Dashboard',
    description: 'Analytics for HR users, attendance, leave, and payroll.',
  },
};

export default function DashboardPage({ viewerRole }) {
  const config = CONFIG[viewerRole];
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await reportService.getDashboard();
      setAnalytics(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load dashboard'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  return (
    <div>
      <PageHeader title={config.title} description={config.description} />

      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}

      {loading ? (
        <p className="text-sm text-slate-600">Loading dashboard...</p>
      ) : (
        <DashboardAnalyticsPanel analytics={analytics} viewerRole={viewerRole} />
      )}
    </div>
  );
}
