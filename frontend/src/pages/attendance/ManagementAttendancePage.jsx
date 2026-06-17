import { useCallback, useEffect, useMemo, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import AttendanceSummaryCards from '../../components/attendance/AttendanceSummaryCards';
import AttendanceTable from '../../components/attendance/AttendanceTable';
import DateRangeFilter from '../../components/attendance/DateRangeFilter';
import FormSelect from '../../components/forms/FormSelect';
import attendanceService from '../../services/attendanceService';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLES } from '../../utils/constants';
import { currentMonthRange } from '../../utils/formatters';

const CONFIG = {
  [ROLES.HR]: {
    title: 'Employee Attendance',
    description: 'Monitor employee attendance, missing checkouts, and date-range reports.',
    personLabel: 'Employee',
    allLabel: 'All employees',
    loadPeople: async () => {
      const data = await employeeService.listEmployees({ page: 0, size: 200, sortBy: 'fullName' });
      return data.content || [];
    },
  },
  [ROLES.ADMIN]: {
    title: 'HR Attendance',
    description: 'Monitor HR attendance records and generate attendance reports.',
    personLabel: 'HR user',
    allLabel: 'All HR users',
    loadPeople: () => employeeService.listHr(),
  },
};

export default function ManagementAttendancePage({ viewerRole, embedded = false }) {
  const config = CONFIG[viewerRole];
  const defaultRange = useMemo(() => currentMonthRange(), []);

  const [range, setRange] = useState(defaultRange);
  const [people, setPeople] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState('');
  const [records, setRecords] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadPeople = useCallback(async () => {
    try {
      const data = await CONFIG[viewerRole].loadPeople();
      setPeople(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load user list'));
    }
  }, [viewerRole]);

  const loadAttendance = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const params = {
        fromDate: range.fromDate,
        toDate: range.toDate,
        userId: selectedUserId || undefined,
      };
      const [report, summaryData] = await Promise.all([
        attendanceService.getReport(params),
        attendanceService.getSummary(params),
      ]);
      setRecords(report || []);
      setSummary(summaryData);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load attendance report'));
    } finally {
      setLoading(false);
    }
  }, [range.fromDate, range.toDate, selectedUserId]);

  useEffect(() => {
    loadPeople();
  }, [loadPeople]);

  useEffect(() => {
    loadAttendance();
  }, [loadAttendance]);

  const handleRangeSubmit = (event) => {
    event.preventDefault();
    loadAttendance();
  };

  const personOptions = people.map((person) => ({
    value: String(person.userId),
    label: `${person.fullName} (${person.employeeCode})`,
  }));

  return (
    <div>
      {!embedded && <PageHeader title={config.title} description={config.description} />}

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

      <div className="mb-6">
        <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
          Summary
        </h3>
        <AttendanceSummaryCards summary={summary} />
      </div>

      <AttendanceTable
        rows={records}
        showEmployee={!selectedUserId}
        loading={loading}
        emptyMessage="No attendance records in this range."
      />
    </div>
  );
}
