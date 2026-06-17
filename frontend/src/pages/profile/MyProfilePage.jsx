import { useCallback, useEffect, useState } from 'react';
import Alert from '../../components/common/Alert';
import DetailGrid from '../../components/common/DetailGrid';
import PageHeader from '../../components/common/PageHeader';
import FormField from '../../components/forms/FormField';
import FormSelect from '../../components/forms/FormSelect';
import ProfilePhotoSection from '../../components/profile/ProfilePhotoSection';
import { useAuth } from '../../context/AuthContext';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { EMPLOYMENT_TYPE_OPTIONS, GENDER_OPTIONS, ROLES } from '../../utils/constants';
import { formatCurrency, formatDate, formatLabel } from '../../utils/formatters';

const TABS = [
  { id: 'personal', label: 'Personal' },
  { id: 'employment', label: 'Employment' },
  { id: 'bank', label: 'Bank' },
];

function emptyPersonalForm() {
  return {
    fullName: '',
    phone: '',
    address: '',
    dateOfBirth: '',
    gender: '',
  };
}

function emptyBankForm() {
  return {
    bankName: '',
    accountNumber: '',
    ifscCode: '',
  };
}

export default function MyProfilePage() {
  const { role, refreshUser } = useAuth();
  const showBankTab = role === ROLES.EMPLOYEE || role === ROLES.HR;
  const visibleTabs = showBankTab ? TABS : TABS.filter((tab) => tab.id !== 'bank');

  const [activeTab, setActiveTab] = useState('personal');
  const [profile, setProfile] = useState(null);
  const [personalForm, setPersonalForm] = useState(emptyPersonalForm);
  const [bankForm, setBankForm] = useState(emptyBankForm);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const applyProfile = useCallback((data) => {
    setProfile(data);
    setPersonalForm({
      fullName: data.fullName || '',
      phone: data.phone || '',
      address: data.address || '',
      dateOfBirth: data.dateOfBirth || '',
      gender: data.gender || '',
    });
    setBankForm({
      bankName: data.bankName || '',
      accountNumber: data.accountNumber || '',
      ifscCode: data.ifscCode || '',
    });
  }, []);

  const loadProfile = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await employeeService.getMyProfile();
      applyProfile(data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load profile'));
    } finally {
      setLoading(false);
    }
  }, [applyProfile]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handlePersonalSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await employeeService.updateMyProfile(personalForm);
      applyProfile(data);
      await refreshUser();
      setNotice(message || 'Profile updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update profile'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleBankSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await employeeService.updateBankDetails(bankForm);
      applyProfile(data);
      setNotice(message || 'Bank details updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update bank details'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <p className="text-sm text-slate-600">Loading profile...</p>;
  }

  if (!profile) {
    return <Alert>{error || 'Profile not found'}</Alert>;
  }

  const employmentItems = [
    { label: 'Employee code', value: profile.employeeCode },
    { label: 'Email', value: profile.email },
    { label: 'Department', value: profile.department },
    { label: 'Designation', value: profile.designation },
    { label: 'Joining date', value: formatDate(profile.joiningDate) },
    { label: 'Employment type', value: formatLabel(profile.employmentType) },
    { label: 'Basic salary', value: formatCurrency(profile.basicSalary) },
    { label: 'Employment status', value: formatLabel(profile.employmentStatus) },
    { label: 'Account status', value: formatLabel(profile.userStatus) },
  ];

  return (
    <div>
      <PageHeader
        title="My Profile"
        description={`${profile.employeeCode} · ${formatLabel(role)}`}
      />

      <div className="mb-6 border-b border-slate-200">
        <nav className="-mb-px flex gap-4">
          {visibleTabs.map((tab) => (
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

      {activeTab === 'personal' && (
        <div className="space-y-6">
          <ProfilePhotoSection
            profile={profile}
            onUpdated={(data) => {
              applyProfile(data);
              refreshUser();
            }}
          />
          <form onSubmit={handlePersonalSubmit} className="grid gap-4 sm:grid-cols-2">
            <FormField
              id="fullName"
              label="Full name"
              value={personalForm.fullName}
              onChange={(e) =>
                setPersonalForm((prev) => ({ ...prev, fullName: e.target.value }))
              }
            />
            <FormField
              id="phone"
              label="Phone"
              value={personalForm.phone}
              onChange={(e) => setPersonalForm((prev) => ({ ...prev, phone: e.target.value }))}
            />
            <FormField
              id="dateOfBirth"
              label="Date of birth"
              type="date"
              value={personalForm.dateOfBirth}
              onChange={(e) =>
                setPersonalForm((prev) => ({ ...prev, dateOfBirth: e.target.value }))
              }
            />
            <FormSelect
              id="gender"
              label="Gender"
              value={personalForm.gender}
              onChange={(e) => setPersonalForm((prev) => ({ ...prev, gender: e.target.value }))}
              options={GENDER_OPTIONS.map((value) => ({ value, label: formatLabel(value) }))}
            />
            <div className="sm:col-span-2">
              <FormField
                id="address"
                label="Address"
                as="textarea"
                value={personalForm.address}
                onChange={(e) =>
                  setPersonalForm((prev) => ({ ...prev, address: e.target.value }))
                }
              />
            </div>
            <div className="sm:col-span-2">
              <button
                type="submit"
                disabled={submitting}
                className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
              >
                {submitting ? 'Saving...' : 'Save personal info'}
              </button>
            </div>
          </form>
        </div>
      )}

      {activeTab === 'employment' && <DetailGrid items={employmentItems} />}

      {activeTab === 'bank' && showBankTab && (
        <form onSubmit={handleBankSubmit} className="grid max-w-2xl gap-4">
          <FormField
            id="bankName"
            label="Bank name"
            value={bankForm.bankName}
            onChange={(e) => setBankForm((prev) => ({ ...prev, bankName: e.target.value }))}
          />
          <FormField
            id="accountNumber"
            label="Account number"
            value={bankForm.accountNumber}
            onChange={(e) =>
              setBankForm((prev) => ({ ...prev, accountNumber: e.target.value }))
            }
          />
          <FormField
            id="ifscCode"
            label="IFSC code"
            value={bankForm.ifscCode}
            onChange={(e) => setBankForm((prev) => ({ ...prev, ifscCode: e.target.value }))}
          />
          <button
            type="submit"
            disabled={submitting}
            className="w-fit rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
          >
            {submitting ? 'Saving...' : 'Save bank details'}
          </button>
        </form>
      )}
    </div>
  );
}
