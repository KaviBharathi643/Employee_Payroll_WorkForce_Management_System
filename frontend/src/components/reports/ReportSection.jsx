export default function ReportSection({ title, description, children, onDownload, downloading }) {
  return (
    <section className="rounded-xl border border-slate-200 p-5">
      <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h3 className="text-base font-semibold text-slate-900">{title}</h3>
          {description && <p className="mt-1 text-sm text-slate-600">{description}</p>}
        </div>
        <button
          type="button"
          disabled={downloading}
          onClick={onDownload}
          className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
        >
          {downloading ? 'Generating PDF...' : 'Download PDF'}
        </button>
      </div>
      {children}
    </section>
  );
}
