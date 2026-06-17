import { useEffect, useState } from 'react';
import Alert from '../common/Alert';
import FormField from '../forms/FormField';
import FormSelect from '../forms/FormSelect';
import leaveService from '../../services/leaveService';
import { getErrorMessage } from '../../utils/authErrors';
import { LEAVE_DURATION_OPTIONS, LEAVE_TYPE_OPTIONS } from '../../utils/constants';
import { formatLabel, todayIsoDate } from '../../utils/formatters';

function emptyForm() {
  const today = todayIsoDate();
  return {
    leaveType: 'CASUAL',
    durationType: 'FULL_DAY',
    startDate: today,
    endDate: today,
    reason: '',
  };
}

export default function ApplyLeaveForm({ onSuccess, onCancel }) {
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (form.durationType === 'HALF_DAY' || form.durationType === 'FULL_DAY') {
      setForm((prev) => {
        if (prev.endDate === prev.startDate) {
          return prev;
        }
        return { ...prev, endDate: prev.startDate };
      });
    }
  }, [form.durationType, form.startDate]);

  const handleChange = (field) => (event) => {
    const value = event.target.value;
    setForm((prev) => {
      const next = { ...prev, [field]: value };
      if (field === 'startDate' && (prev.durationType === 'HALF_DAY' || prev.durationType === 'FULL_DAY')) {
        next.endDate = value;
      }
      return next;
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const { message } = await leaveService.applyLeave(form);
      onSuccess(message || 'Leave applied successfully');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to apply for leave'));
    } finally {
      setSubmitting(false);
    }
  };

  const isSingleDay = form.durationType === 'HALF_DAY' || form.durationType === 'FULL_DAY';

  return (
    <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
      <FormSelect
        id="leaveType"
        label="Leave type"
        value={form.leaveType}
        onChange={handleChange('leaveType')}
        options={LEAVE_TYPE_OPTIONS.map((value) => ({ value, label: formatLabel(value) }))}
      />
      <FormSelect
        id="durationType"
        label="Duration"
        value={form.durationType}
        onChange={handleChange('durationType')}
        options={LEAVE_DURATION_OPTIONS.map((value) => ({ value, label: formatLabel(value) }))}
      />
      <FormField
        id="startDate"
        label="Start date"
        type="date"
        value={form.startDate}
        onChange={handleChange('startDate')}
      />
      <FormField
        id="endDate"
        label="End date"
        type="date"
        value={form.endDate}
        onChange={handleChange('endDate')}
        hint={isSingleDay ? 'Same as start date for half/full day' : undefined}
      />
      <div className="sm:col-span-2">
        <FormField
          id="reason"
          label="Reason"
          as="textarea"
          value={form.reason}
          onChange={handleChange('reason')}
        />
      </div>
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
          {submitting ? 'Submitting...' : 'Submit request'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}
