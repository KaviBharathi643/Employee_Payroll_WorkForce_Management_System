import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import PayslipDetailModal from '../../components/payslip/PayslipDetailModal';
import PayslipTable from '../../components/payslip/PayslipTable';
import payslipService from '../../services/payslipService';
import { getErrorMessage } from '../../utils/authErrors';
import { useAuth } from '../../context/AuthContext';
import { ROLES } from '../../utils/constants';

export default function PayslipsPage({ title = 'Payslips', description }) {
  const { role } = useAuth();
  const showEmployee = role === ROLES.HR || role === ROLES.ADMIN;

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [downloading, setDownloading] = useState(false);

  const loadPayslips = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await payslipService.list();
      setRows(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payslips'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadPayslips();
  }, [loadPayslips]);

  const handleDownload = async (row) => {
    setDownloading(true);
    setError('');
    try {
      const filename = row.payslipNumber ? `${row.payslipNumber}.pdf` : 'payslip.pdf';
      await payslipService.download(row.payslipId, filename);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to download payslip'));
    } finally {
      setDownloading(false);
    }
  };

  const defaultDescription = showEmployee
    ? 'View and download payslips for your team.'
    : 'View and download your payslip PDFs.';

  return (
    <div>
      <PageHeader title={title} description={description || defaultDescription} />

      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}
      {downloading && (
        <p className="mb-4 text-sm text-slate-600">Preparing download...</p>
      )}

      <PayslipTable
        rows={rows}
        loading={loading}
        showEmployee={showEmployee}
        onView={setSelectedId}
        onDownload={handleDownload}
      />

      {selectedId && (
        <PayslipDetailModal payslipId={selectedId} onClose={() => setSelectedId(null)} />
      )}
    </div>
  );
}
