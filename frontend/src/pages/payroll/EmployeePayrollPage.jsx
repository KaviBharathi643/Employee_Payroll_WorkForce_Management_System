import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import PayrollDetailModal from '../../components/payroll/PayrollDetailModal';
import PayrollTable from '../../components/payroll/PayrollTable';
import payrollService from '../../services/payrollService';
import { getErrorMessage } from '../../utils/authErrors';

export default function EmployeePayrollPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selected, setSelected] = useState(null);

  const loadHistory = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await payrollService.getHistory();
      setRows(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payroll history'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadHistory();
  }, [loadHistory]);

  const handleView = async (row) => {
    try {
      const detail = await payrollService.getDetails(row.payrollId);
      setSelected(detail);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payroll details'));
    }
  };

  return (
    <div>
      <PageHeader
        title="Payroll history"
        description="View your monthly payroll records and salary breakdown."
      />

      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}

      <PayrollTable
        rows={rows}
        loading={loading}
        showEmployee={false}
        onView={handleView}
      />

      {selected && (
        <PayrollDetailModal payroll={selected} onClose={() => setSelected(null)} />
      )}
    </div>
  );
}
