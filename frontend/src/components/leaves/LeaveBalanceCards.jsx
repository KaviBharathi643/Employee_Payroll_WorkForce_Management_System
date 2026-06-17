export default function LeaveBalanceCards({ balance }) {
  const items = [
    { label: 'Annual paid limit', value: balance?.annualPaidLeaveLimit ?? 0 },
    { label: 'Paid leave used', value: balance?.paidLeaveUsed ?? 0, tone: 'text-blue-700' },
    { label: 'Remaining paid', value: balance?.remainingPaidLeave ?? 0, tone: 'text-green-700' },
    { label: 'Unpaid leave used', value: balance?.unpaidLeaveUsed ?? 0, tone: 'text-orange-700' },
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
