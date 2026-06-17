export default function DetailGrid({ items }) {
  return (
    <dl className="grid gap-4 sm:grid-cols-2">
      {items.map((item) => (
        <div key={item.label} className="rounded-lg border border-slate-200 px-4 py-3">
          <dt className="text-xs font-medium uppercase tracking-wide text-slate-500">
            {item.label}
          </dt>
          <dd className="mt-1 text-sm font-medium text-slate-900">{item.value ?? '—'}</dd>
        </div>
      ))}
    </dl>
  );
}
