import { formatDate, formatLabel } from '../../utils/formatters';
import LeaveActionButtons from './LeaveActionButtons';
import LeaveStatusBadge from './LeaveStatusBadge';

export default function LeaveTable({
  rows,
  loading = false,
  showEmployee = false,
  actionMode = 'own',
  emptyMessage,
  onUpdated,
  onError,
}) {
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr>
            {showEmployee && (
              <th className="px-4 py-3 text-left font-medium text-slate-600">Employee</th>
            )}
            <th className="px-4 py-3 text-left font-medium text-slate-600">Type</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Duration</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Dates</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Days</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Status</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Reason</th>
            <th className="px-4 py-3 text-right font-medium text-slate-600">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200 bg-white">
          {loading ? (
            <tr>
              <td
                colSpan={showEmployee ? 8 : 7}
                className="px-4 py-8 text-center text-slate-500"
              >
                Loading leave records...
              </td>
            </tr>
          ) : rows.length === 0 ? (
            <tr>
              <td
                colSpan={showEmployee ? 8 : 7}
                className="px-4 py-8 text-center text-slate-500"
              >
                {emptyMessage || 'No leave records found.'}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={row.leaveId}>
                {showEmployee && (
                  <td className="px-4 py-3">
                    <div className="font-medium text-slate-900">{row.employeeName}</div>
                    <div className="text-xs text-slate-500">{row.employeeCode}</div>
                  </td>
                )}
                <td className="px-4 py-3">{formatLabel(row.leaveType)}</td>
                <td className="px-4 py-3">{formatLabel(row.durationType)}</td>
                <td className="px-4 py-3 text-slate-600">
                  {formatDate(row.startDate)}
                  {row.endDate !== row.startDate && (
                    <>
                      <br />
                      <span className="text-xs">to {formatDate(row.endDate)}</span>
                    </>
                  )}
                </td>
                <td className="px-4 py-3">{row.leaveDays ?? '—'}</td>
                <td className="px-4 py-3">
                  <LeaveStatusBadge status={row.status} />
                </td>
                <td className="max-w-xs px-4 py-3 text-slate-600">{row.reason}</td>
                <td className="px-4 py-3 text-right">
                  <LeaveActionButtons
                    leave={row}
                    mode={actionMode}
                    onUpdated={onUpdated}
                    onError={onError}
                  />
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
