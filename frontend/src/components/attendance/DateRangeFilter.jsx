import FormField from '../forms/FormField';

export default function DateRangeFilter({ fromDate, toDate, onFromChange, onToChange, onSubmit, loading }) {
  return (
    <form onSubmit={onSubmit} className="grid gap-3 sm:grid-cols-3">
      <FormField
        id="fromDate"
        label="From date"
        type="date"
        value={fromDate}
        onChange={onFromChange}
      />
      <FormField
        id="toDate"
        label="To date"
        type="date"
        value={toDate}
        onChange={onToChange}
      />
      <div className="flex items-end">
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
        >
          {loading ? 'Loading...' : 'Apply range'}
        </button>
      </div>
    </form>
  );
}
