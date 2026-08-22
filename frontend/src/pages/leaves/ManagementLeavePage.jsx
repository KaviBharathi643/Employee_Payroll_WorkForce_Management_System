import { useCallback, useEffect, useMemo, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import LeaveTable from '../../components/leaves/LeaveTable';
import FormSelect from '../../components/forms/FormSelect';
import FormField from '../../components/forms/FormField';
import employeeService from '../../services/employeeService';
import leaveService from '../../services/leaveService';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLES } from '../../utils/constants';

const CONFIG = {
  [ROLES.HR]: {
    title: 'Employee leave requests',
    description: 'Review, approve, reject, or convert employee leave requests.',
    personLabel: 'Employee',
    allLabel: 'All employees',
    loadPeople: async () => {
      const data = await employeeService.listEmployees({ page: 0, size: 200, sortBy: 'fullName' });
      return data.content || [];
    },
  },
  [ROLES.ADMIN]: {
    title: 'HR leave approval',
    description: 'Review, approve, reject, or convert HR leave requests.',
    personLabel: 'HR user',
    allLabel: 'All HR users',
    loadPeople: () => employeeService.listHr(),
  },
};

export default function ManagementLeavePage({ viewerRole, embedded = false }) {
  const config = CONFIG[viewerRole];

  const [range, setRange] = useState({ fromDate: '', toDate: '' });
  const [appliedRange, setAppliedRange] = useState({ fromDate: '', toDate: '' });
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [people, setPeople] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState('');
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadPeople = useCallback(async () => {
    try {
      const data = await CONFIG[viewerRole].loadPeople();
      setPeople(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load user list'));
    }
  }, [viewerRole]);

  const loadReport = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await leaveService.getReport({
        fromDate: appliedRange.fromDate || undefined,
        toDate: appliedRange.toDate || undefined,
        userId: selectedUserId || undefined,
      });
      setRecords(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load leave report'));
    } finally {
      setLoading(false);
    }
  }, [appliedRange.fromDate, appliedRange.toDate, selectedUserId]);

  useEffect(() => {
    loadPeople();
  }, [loadPeople]);

  useEffect(() => {
    loadReport();
  }, [loadReport]);

  const handleRangeSubmit = (event) => {
    if (event) event.preventDefault();
    setAppliedRange(range);
  };

  const handleRangeClear = () => {
    const cleared = { fromDate: '', toDate: '' };
    setRange(cleared);
    setAppliedRange(cleared);
  };

  const personOptions = people.map((person) => ({
    value: String(person.userId),
    label: `${person.fullName} (${person.employeeCode})`,
  }));

  const filteredRecords = useMemo(() => {
    if (statusFilter === 'ALL') return records;
    return records.filter((r) => r.status === statusFilter);
  }, [records, statusFilter]);

  const content = (
    <>
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

      <div className="mb-6 grid gap-4 md:grid-cols-4 lg:grid-cols-5 items-end">
        {/* Status Filter */}
        <FormSelect
          id="statusFilter"
          label="Status"
          required={false}
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          options={[
            { value: 'ALL', label: 'All' },
            { value: 'PENDING', label: 'Pending' },
            { value: 'APPROVED', label: 'Approved' },
            { value: 'REJECTED', label: 'Rejected' },
          ]}
        />

        {/* From Date */}
        <FormField
          id="fromDate"
          label="From date"
          type="date"
          value={range.fromDate}
          onChange={(e) => setRange((prev) => ({ ...prev, fromDate: e.target.value }))}
        />

        {/* To Date */}
        <FormField
          id="toDate"
          label="To date"
          type="date"
          value={range.toDate}
          onChange={(e) => setRange((prev) => ({ ...prev, toDate: e.target.value }))}
        />

        {/* Apply/Clear buttons */}
        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleRangeSubmit}
            disabled={loading}
            className="flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
          >
            Apply
          </button>
          <button
            type="button"
            onClick={handleRangeClear}
            disabled={loading}
            className="flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
          >
            Clear
          </button>
        </div>

        {/* User/Employee Selection */}
        <FormSelect
          id="userId"
          label={config.personLabel}
          required={false}
          value={selectedUserId}
          onChange={(e) => setSelectedUserId(e.target.value)}
          placeholder={config.allLabel}
          options={personOptions}
        />
      </div>

      <LeaveTable
        rows={filteredRecords}
        loading={loading}
        showEmployee={!selectedUserId}
        actionMode="manage"
        emptyMessage="No leave records found."
        onUpdated={(message) => {
          setNotice(message);
          loadReport();
        }}
        onError={setError}
      />
    </>
  );

  if (embedded) {
    return content;
  }

  return (
    <div>
      <PageHeader title={config.title} description={config.description} />
      {content}
    </div>
  );
}
