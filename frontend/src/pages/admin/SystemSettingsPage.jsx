import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import FormField from '../../components/forms/FormField';
import settingsService from '../../services/settingsService';
import { getErrorMessage } from '../../utils/authErrors';
import { timeInputToApi, toTimeInputValue } from '../../utils/formatters';

const TABS = [
  { id: 'company', label: 'Company' },
  { id: 'attendance', label: 'Attendance' },
  { id: 'leave', label: 'Leave' },
  { id: 'payroll', label: 'Payroll' },
];

export default function SystemSettingsPage() {
  const [activeTab, setActiveTab] = useState('company');
  const [settings, setSettings] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadSettings = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await settingsService.getSettings();
      setSettings(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load settings'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSettings();
  }, [loadSettings]);

  const handleCompanySubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await settingsService.updateCompany({
        companyName: settings.companyName,
        companyAddress: settings.companyAddress,
        companyEmail: settings.companyEmail,
        companyPhone: settings.companyPhone,
      });
      setSettings(data);
      setNotice(message || 'Company settings updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update company settings'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleAttendanceSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await settingsService.updateAttendance({
        officeStartTime: timeInputToApi(
          toTimeInputValue(settings.officeStartTime) || settings.officeStartTime,
        ),
        officeEndTime: timeInputToApi(
          toTimeInputValue(settings.officeEndTime) || settings.officeEndTime,
        ),
        checkoutReminderHours: Number(settings.checkoutReminderHours),
      });
      setSettings(data);
      setNotice(message || 'Attendance settings updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update attendance settings'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleLeaveSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await settingsService.updateLeave({
        annualPaidLeaveLimit: Number(settings.annualPaidLeaveLimit),
      });
      setSettings(data);
      setNotice(message || 'Leave settings updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update leave settings'));
    } finally {
      setSubmitting(false);
    }
  };

  const handlePayrollSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await settingsService.updatePayroll({
        pfPercentage: Number(settings.pfPercentage),
        salaryCreditDay: Number(settings.salaryCreditDay),
      });
      setSettings(data);
      setNotice(message || 'Payroll settings updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update payroll settings'));
    } finally {
      setSubmitting(false);
    }
  };

  const updateField = (field) => (event) => {
    setSettings((prev) => ({ ...prev, [field]: event.target.value }));
  };

  if (loading) {
    return <p className="text-sm text-slate-600">Loading settings...</p>;
  }

  if (!settings) {
    return <Alert>{error || 'Settings not available'}</Alert>;
  }

  return (
    <div>
      <PageHeader
        title="System settings"
        description="Configure company, attendance, leave, and payroll policies."
      />

      {notice && (
        <div className="mb-4">
          <Alert type="success">{notice}</Alert>
        </div>
      )}
      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}

      <div className="mb-6 border-b border-slate-200">
        <nav className="-mb-px flex flex-wrap gap-4">
          {TABS.map((tab) => (
            <button
              key={tab.id}
              type="button"
              onClick={() => {
                setActiveTab(tab.id);
                setError('');
                setNotice('');
              }}
              className={`border-b-2 px-1 py-2 text-sm font-medium ${
                activeTab === tab.id
                  ? 'border-slate-900 text-slate-900'
                  : 'border-transparent text-slate-500 hover:text-slate-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {activeTab === 'company' && (
        <form onSubmit={handleCompanySubmit} className="grid max-w-2xl gap-4">
          <FormField
            id="companyName"
            label="Company name"
            value={settings.companyName || ''}
            onChange={updateField('companyName')}
          />
          <FormField
            id="companyAddress"
            label="Company address"
            as="textarea"
            value={settings.companyAddress || ''}
            onChange={updateField('companyAddress')}
          />
          <FormField
            id="companyEmail"
            label="Company email"
            type="email"
            value={settings.companyEmail || ''}
            onChange={updateField('companyEmail')}
          />
          <FormField
            id="companyPhone"
            label="Company phone"
            value={settings.companyPhone || ''}
            onChange={updateField('companyPhone')}
          />
          <button
            type="submit"
            disabled={submitting}
            className="w-fit rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
          >
            {submitting ? 'Saving...' : 'Save company settings'}
          </button>
        </form>
      )}

      {activeTab === 'attendance' && (
        <form onSubmit={handleAttendanceSubmit} className="grid max-w-2xl gap-4 sm:grid-cols-2">
          <FormField
            id="officeStartTime"
            label="Office start time"
            type="time"
            value={toTimeInputValue(settings.officeStartTime)}
            onChange={updateField('officeStartTime')}
          />
          <FormField
            id="officeEndTime"
            label="Office end time"
            type="time"
            value={toTimeInputValue(settings.officeEndTime)}
            onChange={updateField('officeEndTime')}
          />
          <FormField
            id="checkoutReminderHours"
            label="Checkout reminder (hours after office end)"
            type="number"
            min="0"
            value={settings.checkoutReminderHours ?? ''}
            onChange={updateField('checkoutReminderHours')}
          />
          <div className="sm:col-span-2">
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
            >
              {submitting ? 'Saving...' : 'Save attendance settings'}
            </button>
          </div>
        </form>
      )}

      {activeTab === 'leave' && (
        <form onSubmit={handleLeaveSubmit} className="grid max-w-md gap-4">
          <FormField
            id="annualPaidLeaveLimit"
            label="Annual paid leave limit"
            type="number"
            min="1"
            value={settings.annualPaidLeaveLimit ?? ''}
            onChange={updateField('annualPaidLeaveLimit')}
          />
          <button
            type="submit"
            disabled={submitting}
            className="w-fit rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
          >
            {submitting ? 'Saving...' : 'Save leave settings'}
          </button>
        </form>
      )}

      {activeTab === 'payroll' && (
        <form onSubmit={handlePayrollSubmit} className="grid max-w-md gap-4 sm:grid-cols-2">
          <FormField
            id="pfPercentage"
            label="PF percentage"
            type="number"
            min="0"
            max="100"
            step="0.01"
            value={settings.pfPercentage ?? ''}
            onChange={updateField('pfPercentage')}
          />
          <FormField
            id="salaryCreditDay"
            label="Salary credit day (1–31)"
            type="number"
            min="1"
            max="31"
            value={settings.salaryCreditDay ?? ''}
            onChange={updateField('salaryCreditDay')}
          />
          <div className="sm:col-span-2">
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
            >
              {submitting ? 'Saving...' : 'Save payroll settings'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
