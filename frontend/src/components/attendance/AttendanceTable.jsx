import { formatDate, formatDateTime } from '../../utils/formatters';
import AttendanceStatusBadge from './AttendanceStatusBadge';

export default function AttendanceTable({ rows, showEmployee = false, loading = false, emptyMessage }) {
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Date</th>
            {showEmployee && (
              <th className="px-4 py-3 text-left font-medium text-slate-600">Employee</th>
            )}
            <th className="px-4 py-3 text-left font-medium text-slate-600">Check-in</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Check-out</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Status</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200 bg-white">
          {loading ? (
            <tr>
              <td
                colSpan={showEmployee ? 5 : 4}
                className="px-4 py-8 text-center text-slate-500"
              >
                Loading attendance...
              </td>
            </tr>
          ) : rows.length === 0 ? (
            <tr>
              <td
                colSpan={showEmployee ? 5 : 4}
                className="px-4 py-8 text-center text-slate-500"
              >
                {emptyMessage || 'No attendance records found.'}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={row.attendanceId}>
                <td className="px-4 py-3 font-medium text-slate-900">
                  {formatDate(row.attendanceDate)}
                </td>
                {showEmployee && (
                  <td className="px-4 py-3">
                    <div className="font-medium text-slate-900">{row.employeeName}</div>
                    <div className="text-xs text-slate-500">{row.employeeCode}</div>
                  </td>
                )}
                <td className="px-4 py-3 text-slate-600">{formatDateTime(row.checkInTime)}</td>
                <td className="px-4 py-3 text-slate-600">{formatDateTime(row.checkOutTime)}</td>
                <td className="px-4 py-3">
                  <AttendanceStatusBadge status={row.status} />
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
