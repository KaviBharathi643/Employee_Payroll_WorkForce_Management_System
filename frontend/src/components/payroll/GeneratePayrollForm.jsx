import { useState } from 'react';
import Alert from '../common/Alert';
import FormField from '../forms/FormField';
import FormSelect from '../forms/FormSelect';
import payrollService from '../../services/payrollService';
import { getErrorMessage } from '../../utils/authErrors';
import { MONTH_OPTIONS } from '../../utils/constants';
import { currentPayrollPeriod } from '../../utils/formatters';

export default function GeneratePayrollForm({ onSuccess, onCancel }) {
  const period = currentPayrollPeriod();
  const [form, setForm] = useState({
    payrollYear: String(period.payrollYear),
    payrollMonth: String(period.payrollMonth),
    bonusAmount: '',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);

  const yearOptions = [];
  const currentYear = new Date().getFullYear();
  for (let year = currentYear; year >= currentYear - 5; year -= 1) {
    yearOptions.push({ value: String(year), label: String(year) });
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const payload = {
        payrollYear: Number(form.payrollYear),
        payrollMonth: Number(form.payrollMonth),
        bonusAmount: form.bonusAmount ? Number(form.bonusAmount) : undefined,
      };
      const { message, data } = await payrollService.generate(payload);
      setResult(data);
      onSuccess(message || 'Payroll generated', data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to generate payroll'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
      <FormSelect
        id="payrollYear"
        label="Payroll year"
        value={form.payrollYear}
        onChange={(e) => setForm((prev) => ({ ...prev, payrollYear: e.target.value }))}
        options={yearOptions}
      />
      <FormSelect
        id="payrollMonth"
        label="Payroll month"
        value={form.payrollMonth}
        onChange={(e) => setForm((prev) => ({ ...prev, payrollMonth: e.target.value }))}
        options={MONTH_OPTIONS}
      />
      <FormField
        id="bonusAmount"
        label="Bonus amount (optional)"
        type="number"
        min="0"
        step="0.01"
        required={false}
        value={form.bonusAmount}
        onChange={(e) => setForm((prev) => ({ ...prev, bonusAmount: e.target.value }))}
        hint="Applied to each eligible employee in this run"
      />
      {result?.skippedEmployees?.length > 0 && (
        <div className="sm:col-span-2 rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm text-amber-900">
          <p className="font-medium">
            Skipped {result.skippedCount} employee(s) — processed {result.processedCount}
          </p>
          <ul className="mt-2 list-inside list-disc">
            {result.skippedEmployees.map((item) => (
              <li key={`${item.employeeCode}-${item.reason}`}>
                {item.employeeName} ({item.employeeCode}): {item.reason}
              </li>
            ))}
          </ul>
        </div>
      )}
      {error && (
        <div className="sm:col-span-2">
          <Alert>{error}</Alert>
        </div>
      )}
      <div className="flex gap-2 sm:col-span-2">
        <button
          type="submit"
          disabled={submitting}
          className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
        >
          {submitting ? 'Generating...' : 'Generate payroll'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
        >
          Close
        </button>
      </div>
    </form>
  );
}
