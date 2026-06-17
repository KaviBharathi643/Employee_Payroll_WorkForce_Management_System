import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import Modal from '../../components/common/Modal';
import GeneratePayrollForm from '../../components/payroll/GeneratePayrollForm';
import MonthYearFilter from '../../components/payroll/MonthYearFilter';
import PayrollDetailModal from '../../components/payroll/PayrollDetailModal';
import PayrollSummaryCards from '../../components/payroll/PayrollSummaryCards';
import PayrollTable from '../../components/payroll/PayrollTable';
import payrollService from '../../services/payrollService';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLES } from '../../utils/constants';
import { currentPayrollPeriod } from '../../utils/formatters';

const CONFIG = {
  [ROLES.HR]: {
    title: 'Payroll',
    description: 'Generate employee payroll, credit salaries, and review monthly reports.',
    targetLabel: 'employees',
  },
  [ROLES.ADMIN]: {
    title: 'HR Payroll',
    description: 'Generate HR payroll, credit salaries, and review monthly reports.',
    targetLabel: 'HR users',
  },
};

export default function ManagementPayrollPage({ viewerRole }) {
  const config = CONFIG[viewerRole];
  const defaultPeriod = currentPayrollPeriod();

  const [period, setPeriod] = useState(defaultPeriod);
  const [summary, setSummary] = useState(null);
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [showGenerate, setShowGenerate] = useState(false);
  const [selected, setSelected] = useState(null);

  const loadSummary = useCallback(async () => {
    try {
      const data = await payrollService.getSummary();
      setSummary(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payroll summary'));
    }
  }, []);

  const loadReport = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await payrollService.getReport({
        payrollYear: period.payrollYear,
        payrollMonth: period.payrollMonth,
      });
      setRows(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payroll report'));
    } finally {
      setLoading(false);
    }
  }, [period.payrollMonth, period.payrollYear]);

  useEffect(() => {
    loadSummary();
  }, [loadSummary]);

  useEffect(() => {
    loadReport();
  }, [loadReport]);

  const handlePeriodSubmit = (event) => {
    event.preventDefault();
    loadReport();
  };

  const handleView = async (row) => {
    try {
      const detail = await payrollService.getDetails(row.payrollId);
      setSelected(detail);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load payroll details'));
    }
  };

  const refreshAll = (message) => {
    if (message) {
      setNotice(message);
    }
    loadSummary();
    loadReport();
  };

  return (
    <div>
      <PageHeader
        title={config.title}
        description={config.description}
        actions={
          <button
            type="button"
            onClick={() => setShowGenerate(true)}
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
          >
            Generate payroll
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

      <div className="mb-6">
        <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
          Overview
        </h3>
        <PayrollSummaryCards summary={summary} />
      </div>

      <div className="mb-6">
        <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
          Monthly report ({config.targetLabel})
        </h3>
        <MonthYearFilter
          payrollYear={period.payrollYear}
          payrollMonth={period.payrollMonth}
          loading={loading}
          onYearChange={(e) =>
            setPeriod((prev) => ({ ...prev, payrollYear: Number(e.target.value) }))
          }
          onMonthChange={(e) =>
            setPeriod((prev) => ({ ...prev, payrollMonth: Number(e.target.value) }))
          }
          onSubmit={handlePeriodSubmit}
        />
      </div>

      <PayrollTable
        rows={rows}
        loading={loading}
        showEmployee
        showCredit
        emptyMessage="No payroll records for this period. Generate payroll to create records."
        onView={handleView}
        onUpdated={refreshAll}
        onError={setError}
      />

      {showGenerate && (
        <Modal title="Generate payroll" onClose={() => setShowGenerate(false)} wide>
          <GeneratePayrollForm
            onCancel={() => setShowGenerate(false)}
            onSuccess={(message) => {
              setShowGenerate(false);
              refreshAll(message);
            }}
          />
        </Modal>
      )}

      {selected && (
        <PayrollDetailModal payroll={selected} onClose={() => setSelected(null)} />
      )}
    </div>
  );
}
