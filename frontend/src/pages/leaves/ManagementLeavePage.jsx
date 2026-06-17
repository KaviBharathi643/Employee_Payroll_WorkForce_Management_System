import { useCallback, useEffect, useMemo, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import DateRangeFilter from '../../components/attendance/DateRangeFilter';
import LeaveTable from '../../components/leaves/LeaveTable';
import FormSelect from '../../components/forms/FormSelect';
import employeeService from '../../services/employeeService';
import leaveService from '../../services/leaveService';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLES } from '../../utils/constants';
import { currentMonthRange } from '../../utils/formatters';

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
  const defaultRange = useMemo(() => currentMonthRange(), []);

  const [range, setRange] = useState(defaultRange);
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
        fromDate: range.fromDate,
        toDate: range.toDate,
        userId: selectedUserId || undefined,
      });
      setRecords(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load leave report'));
    } finally {
      setLoading(false);
    }
  }, [range.fromDate, range.toDate, selectedUserId]);

  useEffect(() => {
    loadPeople();
  }, [loadPeople]);

  useEffect(() => {
    loadReport();
  }, [loadReport]);

  const handleRangeSubmit = (event) => {
    event.preventDefault();
    loadReport();
  };

  const personOptions = people.map((person) => ({
    value: String(person.userId),
    label: `${person.fullName} (${person.employeeCode})`,
  }));

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

      <div className="mb-6 grid gap-4 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <DateRangeFilter
            fromDate={range.fromDate}
            toDate={range.toDate}
            loading={loading}
            onFromChange={(e) => setRange((prev) => ({ ...prev, fromDate: e.target.value }))}
            onToChange={(e) => setRange((prev) => ({ ...prev, toDate: e.target.value }))}
            onSubmit={handleRangeSubmit}
          />
        </div>
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
        rows={records}
        loading={loading}
        showEmployee={!selectedUserId}
        actionMode="manage"
        emptyMessage="No leave requests in this range."
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
