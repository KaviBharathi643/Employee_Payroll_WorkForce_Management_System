import { useState } from 'react';
import Alert from '../common/Alert';
import FormField from '../forms/FormField';
import FormSelect from '../forms/FormSelect';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { GENDER_OPTIONS } from '../../utils/constants';
import { formatLabel } from '../../utils/formatters';

function emptyForm() {
  return {
    email: '',
    fullName: '',
    dateOfBirth: '',
    gender: '',
    phone: '',
    address: '',
    department: '',
    designation: '',
    joiningDate: '',
    basicSalary: '',
  };
}

export default function CreateHrForm({ onSuccess, onCancel }) {
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (field) => (event) => {
    setForm((prev) => ({ ...prev, [field]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setNotice('');
    setSubmitting(true);
    try {
      const { message } = await employeeService.createHr({
        ...form,
        basicSalary: Number(form.basicSalary),
      });
      setNotice(message || 'HR created. Credentials emailed to the new HR user.');
      onSuccess();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to create HR user'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2">
      <FormField id="email" label="Email" type="email" value={form.email} onChange={handleChange('email')} />
      <FormField
        id="fullName"
        label="Full name"
        value={form.fullName}
        onChange={handleChange('fullName')}
      />
      <FormField
        id="dateOfBirth"
        label="Date of birth"
        type="date"
        value={form.dateOfBirth}
        onChange={handleChange('dateOfBirth')}
      />
      <FormSelect
        id="gender"
        label="Gender"
        value={form.gender}
        onChange={handleChange('gender')}
        options={GENDER_OPTIONS.map((value) => ({ value, label: formatLabel(value) }))}
      />
      <FormField id="phone" label="Phone" value={form.phone} onChange={handleChange('phone')} />
      <FormField
        id="department"
        label="Department"
        value={form.department}
        onChange={handleChange('department')}
      />
      <FormField
        id="designation"
        label="Designation"
        value={form.designation}
        onChange={handleChange('designation')}
      />
      <FormField
        id="joiningDate"
        label="Joining date"
        type="date"
        value={form.joiningDate}
        onChange={handleChange('joiningDate')}
      />
      <FormField
        id="basicSalary"
        label="Basic salary"
        type="number"
        min="0"
        step="0.01"
        value={form.basicSalary}
        onChange={handleChange('basicSalary')}
      />
      <div className="sm:col-span-2">
        <FormField
          id="address"
          label="Address"
          as="textarea"
          value={form.address}
          onChange={handleChange('address')}
        />
      </div>
      {notice && <div className="sm:col-span-2"><Alert type="success">{notice}</Alert></div>}
      {error && <div className="sm:col-span-2"><Alert>{error}</Alert></div>}
      <div className="flex gap-2 sm:col-span-2">
        <button
          type="submit"
          disabled={submitting}
          className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
        >
          {submitting ? 'Creating...' : 'Create HR'}
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
