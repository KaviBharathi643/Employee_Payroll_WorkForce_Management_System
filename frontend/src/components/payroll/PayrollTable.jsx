import { useState } from 'react';
import payrollService from '../../services/payrollService';
import { getErrorMessage } from '../../utils/authErrors';
import { formatCurrency, formatMonthYear } from '../../utils/formatters';
import PayrollStatusBadge from './PayrollStatusBadge';

export default function PayrollTable({
  rows,
  loading = false,
  showEmployee = true,
  showCredit = false,
  emptyMessage,
  onView,
  onUpdated,
  onError,
}) {
  const [creditingId, setCreditingId] = useState(null);

  const handleCredit = async (payrollId) => {
    if (!window.confirm('Credit salary for this payroll record?')) {
      return;
    }
    setCreditingId(payrollId);
    onError?.('');
    try {
      const { message } = await payrollService.credit(payrollId);
      onUpdated?.(message);
    } catch (err) {
      onError?.(getErrorMessage(err, 'Failed to credit salary'));
    } finally {
      setCreditingId(null);
    }
  };

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-200">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr>
            {showEmployee && (
              <th className="px-4 py-3 text-left font-medium text-slate-600">Employee</th>
            )}
            <th className="px-4 py-3 text-left font-medium text-slate-600">Period</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Final salary</th>
            <th className="px-4 py-3 text-left font-medium text-slate-600">Status</th>
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
                Loading payroll records...
              </td>
            </tr>
          ) : rows.length === 0 ? (
            <tr>
              <td
                colSpan={showEmployee ? 5 : 4}
                className="px-4 py-8 text-center text-slate-500"
              >
                {emptyMessage || 'No payroll records found.'}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={row.payrollId}>
                {showEmployee && (
                  <td className="px-4 py-3">
                    <div className="font-medium text-slate-900">{row.employeeName}</div>
                    <div className="text-xs text-slate-500">{row.employeeCode}</div>
                  </td>
                )}
                <td className="px-4 py-3">
                  {formatMonthYear(row.payrollYear, row.payrollMonth)}
                </td>
                <td className="px-4 py-3 font-medium text-slate-900">
                  {formatCurrency(row.finalSalary)}
                </td>
                <td className="px-4 py-3">
                  <PayrollStatusBadge status={row.status} />
                </td>
                <td className="px-4 py-3 text-right">
                  <div className="flex flex-wrap justify-end gap-2">
                    <button
                      type="button"
                      onClick={() => onView?.(row)}
                      className="text-sm font-medium text-slate-900 hover:underline"
                    >
                      View
                    </button>
                    {showCredit && row.status === 'GENERATED' && (
                      <button
                        type="button"
                        disabled={creditingId === row.payrollId}
                        onClick={() => handleCredit(row.payrollId)}
                        className="rounded-lg bg-green-700 px-2.5 py-1 text-xs font-medium text-white hover:bg-green-800 disabled:opacity-60"
                      >
                        {creditingId === row.payrollId ? 'Crediting...' : 'Credit'}
                      </button>
                    )}
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
