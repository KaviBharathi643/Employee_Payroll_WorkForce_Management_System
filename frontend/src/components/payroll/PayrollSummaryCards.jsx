import { formatCurrency } from '../../utils/formatters';

export default function PayrollSummaryCards({ summary }) {
  const items = [
    { label: 'Total salary paid', value: formatCurrency(summary?.totalSalaryPaid) },
    { label: 'Employees paid', value: summary?.totalEmployeesPaid ?? 0 },
    {
      label: 'Unpaid leave deductions',
      value: formatCurrency(summary?.totalUnpaidLeaveDeductions),
    },
    { label: 'Pending credits', value: summary?.pendingSalaryCredits ?? 0, tone: 'text-amber-700' },
  ];

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {items.map((item) => (
        <div
          key={item.label}
          className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3"
        >
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
            {item.label}
          </p>
          <p className={`mt-1 text-2xl font-semibold ${item.tone || 'text-slate-900'}`}>
            {item.value}
          </p>
        </div>
      ))}
    </div>
  );
}
