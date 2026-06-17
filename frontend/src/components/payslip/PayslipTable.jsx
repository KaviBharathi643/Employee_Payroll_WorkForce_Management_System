import { formatDateTime, formatMonthYear } from '../../utils/formatters';

export default function PayslipTable({
  rows,
  loading = false,
  showEmployee = false,
  onView,
  onDownload,
  emptyMessage,
}) {
  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Payslip #</th>
            {showEmployee && (
              <th className="px-4 py-3 text-left font-medium text-slate-600">Employee</th>
            )}
            <th className="px-4 py-3 text-left font-medium text-slate-600">Period</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Generated</th>
            <th className="px-4 py-3 text-right font-medium text-slate-600">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200 bg-white">
          {loading ? (
            <tr>
              <td
                colSpan={showEmployee ? 5 : 4}
                className="px-4 py-8 text-center text-slate-500"
              >
                Loading payslips...
              </td>
            </tr>
          ) : rows.length === 0 ? (
            <tr>
              <td
                colSpan={showEmployee ? 5 : 4}
                className="px-4 py-8 text-center text-slate-500"
              >
                {emptyMessage || 'No payslips found.'}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={row.payslipId}>
                <td className="px-4 py-3 font-medium text-slate-900">{row.payslipNumber}</td>
                {showEmployee && (
                  <td className="px-4 py-3">
                    <div className="font-medium text-slate-900">{row.employeeName}</div>
                    <div className="text-xs text-slate-500">{row.employeeCode}</div>
                  </td>
                )}
                <td className="px-4 py-3">
                  {formatMonthYear(row.payrollYear, row.payrollMonth)}
                </td>
                <td className="px-4 py-3 text-slate-600">
                  {formatDateTime(row.generatedDate)}
                </td>
                <td className="px-4 py-3 text-right">
                  <div className="flex flex-wrap justify-end gap-2">
                    <button
                      type="button"
                      onClick={() => onView?.(row.payslipId)}
                      className="text-sm font-medium text-slate-900 hover:underline"
                    >
                      View
                    </button>
                    <button
                      type="button"
                      onClick={() => onDownload?.(row)}
                      className="text-sm font-medium text-blue-700 hover:underline"
                    >
                      Download
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
