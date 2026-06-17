import FormSelect from '../forms/FormSelect';
import { MONTH_OPTIONS } from '../../utils/constants';

export default function MonthYearFilter({
  payrollYear,
  payrollMonth,
  onYearChange,
  onMonthChange,
  onSubmit,
  loading,
}) {
  const yearOptions = [];
  const currentYear = new Date().getFullYear();
  for (let year = currentYear; year >= currentYear - 5; year -= 1) {
    yearOptions.push({ value: String(year), label: String(year) });
  }

  return (
    <form onSubmit={onSubmit} className="grid gap-3 sm:grid-cols-3">
      <FormSelect
        id="payrollYear"
        label="Year"
        value={String(payrollYear)}
        onChange={onYearChange}
        options={yearOptions}
        placeholder="Year"
      />
      <FormSelect
        id="payrollMonth"
        label="Month"
        value={String(payrollMonth)}
        onChange={onMonthChange}
        options={MONTH_OPTIONS}
        placeholder="Month"
      />
      <div className="flex items-end">
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 disabled:opacity-60"
        >
          {loading ? 'Loading...' : 'Load report'}
        </button>
      </div>
    </form>
  );
}
