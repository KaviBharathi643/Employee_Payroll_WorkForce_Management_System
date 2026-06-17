export default function AttendanceSummaryCards({ summary }) {
  const items = [
    { label: 'Present', value: summary?.presentCount ?? 0, tone: 'text-green-700' },
    { label: 'Absent', value: summary?.absentCount ?? 0, tone: 'text-red-700' },
    { label: 'Leave', value: summary?.leaveCount ?? 0, tone: 'text-blue-700' },
    { label: 'Half-day leave', value: summary?.halfDayLeaveCount ?? 0, tone: 'text-amber-700' },
    {
      label: 'Missing checkout',
      value: summary?.missingCheckoutCount ?? 0,
      tone: 'text-orange-700',
    },
  ];

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
      {items.map((item) => (
        <div
          key={item.label}
          className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3"
        >
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
            {item.label}
          </p>
          <p className={`mt-1 text-2xl font-semibold ${item.tone}`}>{item.value}</p>
        </div>
      ))}
    </div>
  );
}
