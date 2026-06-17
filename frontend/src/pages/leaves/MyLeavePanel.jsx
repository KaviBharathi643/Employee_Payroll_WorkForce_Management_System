import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import Modal from '../../components/common/Modal';
import ApplyLeaveForm from '../../components/leaves/ApplyLeaveForm';
import LeaveBalanceCards from '../../components/leaves/LeaveBalanceCards';
import LeaveTable from '../../components/leaves/LeaveTable';
import leaveService from '../../services/leaveService';
import { getErrorMessage } from '../../utils/authErrors';

export default function MyLeavePanel({ canApply = true }) {
  const [balance, setBalance] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [showApply, setShowApply] = useState(false);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [balanceData, historyData] = await Promise.all([
        leaveService.getBalance(),
        leaveService.getMyHistory(),
      ]);
      setBalance(balanceData);
      setHistory(historyData || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load leave data'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleActionNotice = (message) => {
    setNotice(message);
    loadData();
  };

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <h3 className="text-sm font-semibold uppercase tracking-wide text-slate-500">
          Leave balance
        </h3>
        {canApply && (
          <button
            type="button"
            onClick={() => setShowApply(true)}
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
          >
            Apply for leave
          </button>
        )}
      </div>

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

      <div className="mb-6">
        <LeaveBalanceCards balance={balance} />
      </div>

      <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
        My leave history
      </h3>
      <LeaveTable
        rows={history}
        loading={loading}
        actionMode="own"
        onUpdated={handleActionNotice}
        onError={setError}
      />

      {showApply && (
        <Modal title="Apply for leave" onClose={() => setShowApply(false)} wide>
          <ApplyLeaveForm
            onCancel={() => setShowApply(false)}
            onSuccess={(message) => {
              setShowApply(false);
              handleActionNotice(message);
            }}
          />
        </Modal>
      )}
    </div>
  );
}
