import { useCallback, useEffect, useMemo, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import AttendanceSummaryCards from '../../components/attendance/AttendanceSummaryCards';
import AttendanceTable from '../../components/attendance/AttendanceTable';
import DateRangeFilter from '../../components/attendance/DateRangeFilter';
import attendanceService from '../../services/attendanceService';
import { getErrorMessage } from '../../utils/authErrors';
import {
  currentMonthRange,
  formatDateTime,
  isWeekend,
  todayIsoDate,
} from '../../utils/formatters';

function deriveTodayState(todayRecord) {
  if (!todayRecord) {
    return { canCheckIn: true, canCheckOut: false };
  }
  if (!todayRecord.checkOutTime) {
    return { canCheckIn: false, canCheckOut: true };
  }
  return { canCheckIn: false, canCheckOut: false };
}

export default function EmployeeAttendancePage() {
  const defaultRange = useMemo(() => currentMonthRange(), []);
  const [range, setRange] = useState(defaultRange);
  const [records, setRecords] = useState([]);
  const [todayRecord, setTodayRecord] = useState(null);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [acting, setActing] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const weekend = isWeekend();
  const { canCheckIn, canCheckOut } = deriveTodayState(todayRecord);

  const loadAttendance = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const today = todayIsoDate();
      const [history, todayHistory, summaryData] = await Promise.all([
        attendanceService.getMyAttendance(range),
        attendanceService.getMyAttendance({ fromDate: today, toDate: today }),
        attendanceService.getSummary(range),
      ]);
      setRecords(history || []);
      setTodayRecord(todayHistory?.[0] || null);
      setSummary(summaryData);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load attendance'));
    } finally {
      setLoading(false);
    }
  }, [range]);

  useEffect(() => {
    loadAttendance();
  }, [loadAttendance]);

  const handleCheckIn = async () => {
    setActing(true);
    setError('');
    setNotice('');
    try {
      const { message } = await attendanceService.checkIn();
      setNotice(message || 'Check-in successful');
      await loadAttendance();
    } catch (err) {
      setError(getErrorMessage(err, 'Check-in failed'));
    } finally {
      setActing(false);
    }
  };

  const handleCheckOut = async () => {
    setActing(true);
    setError('');
    setNotice('');
    try {
      const { message } = await attendanceService.checkOut();
      setNotice(message || 'Check-out successful');
      await loadAttendance();
    } catch (err) {
      setError(getErrorMessage(err, 'Check-out failed'));
    } finally {
      setActing(false);
    }
  };

  const handleRangeSubmit = (event) => {
    event.preventDefault();
    loadAttendance();
  };

  return (
    <div>
      <PageHeader
        title="Attendance"
        description="Check in and out daily, then review your attendance history."
      />

      <div className="mb-6 rounded-xl border border-slate-200 bg-slate-50 p-5">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-sm font-medium text-slate-900">Today</p>
            {weekend ? (
              <p className="mt-1 text-sm text-slate-600">Weekend — attendance not required.</p>
            ) : todayRecord ? (
              <div className="mt-1 space-y-1 text-sm text-slate-600">
                <p>Check-in: {formatDateTime(todayRecord.checkInTime)}</p>
                <p>Check-out: {formatDateTime(todayRecord.checkOutTime)}</p>
              </div>
            ) : (
              <p className="mt-1 text-sm text-slate-600">No check-in recorded yet.</p>
            )}
          </div>
          <div className="flex gap-2">
            <button
              type="button"
              disabled={acting || weekend || !canCheckIn}
              onClick={handleCheckIn}
              className="rounded-lg bg-green-700 px-4 py-2 text-sm font-medium text-white hover:bg-green-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {acting ? 'Working...' : 'Check in'}
            </button>
            <button
              type="button"
              disabled={acting || weekend || !canCheckOut}
              onClick={handleCheckOut}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {acting ? 'Working...' : 'Check out'}
            </button>
          </div>
        </div>
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
        <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
          Summary
        </h3>
        <AttendanceSummaryCards summary={summary} />
      </div>

      <div className="mb-4">
        <DateRangeFilter
          fromDate={range.fromDate}
          toDate={range.toDate}
          loading={loading}
          onFromChange={(e) => setRange((prev) => ({ ...prev, fromDate: e.target.value }))}
          onToChange={(e) => setRange((prev) => ({ ...prev, toDate: e.target.value }))}
          onSubmit={handleRangeSubmit}
        />
      </div>

      <AttendanceTable rows={records} loading={loading} />
    </div>
  );
}
