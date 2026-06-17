import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Alert from '../../components/common/Alert';
import DetailGrid from '../../components/common/DetailGrid';
import PageHeader from '../../components/common/PageHeader';
import FormField from '../../components/forms/FormField';
import ProfileAvatar from '../../components/profile/ProfileAvatar';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { formatCurrency, formatDate, formatLabel } from '../../utils/formatters';

function emptyEditForm(hr) {
  return {
    fullName: hr?.fullName || '',
    phone: hr?.phone || '',
    address: hr?.address || '',
    department: hr?.department || '',
    designation: hr?.designation || '',
    basicSalary: hr?.basicSalary ?? '',
  };
}

export default function HrDetailPage() {
  const { id } = useParams();
  const [hr, setHr] = useState(null);
  const [editForm, setEditForm] = useState(emptyEditForm());
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadHr = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await employeeService.getHr(id);
      setHr(data);
      setEditForm(emptyEditForm(data));
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load HR user'));
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadHr();
  }, [loadHr]);

  const handleUpdate = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await employeeService.updateHr(id, {
        ...editForm,
        basicSalary: Number(editForm.basicSalary),
      });
      setHr(data);
      setEditForm(emptyEditForm(data));
      setEditing(false);
      setNotice(message || 'HR user updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update HR user'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!window.confirm('Deactivate this HR user? They will no longer be able to sign in.')) {
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      const { message } = await employeeService.deactivateHr(id);
      setNotice(message || 'HR user deactivated');
      await loadHr();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to deactivate HR user'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <p className="text-sm text-slate-600">Loading HR user...</p>;
  }

  if (!hr) {
    return <Alert>{error || 'HR user not found'}</Alert>;
  }

  const readOnlyItems = [
    { label: 'Employee code', value: hr.employeeCode },
    { label: 'Email', value: hr.email },
    { label: 'Date of birth', value: formatDate(hr.dateOfBirth) },
    { label: 'Gender', value: formatLabel(hr.gender) },
    { label: 'Phone', value: hr.phone },
    { label: 'Address', value: hr.address },
    { label: 'Department', value: hr.department },
    { label: 'Designation', value: hr.designation },
    { label: 'Joining date', value: formatDate(hr.joiningDate) },
    { label: 'Employment type', value: formatLabel(hr.employmentType) },
    { label: 'Basic salary', value: formatCurrency(hr.basicSalary) },
    { label: 'Employment status', value: formatLabel(hr.employmentStatus) },
    { label: 'Account status', value: formatLabel(hr.userStatus) },
    { label: 'Bank name', value: hr.bankName },
    { label: 'Account number', value: hr.maskedAccountNumber || hr.accountNumber },
    { label: 'IFSC code', value: hr.ifscCode },
  ];

  return (
    <div>
      <div className="mb-4">
        <Link to="/admin/hr" className="text-sm font-medium text-slate-600 hover:underline">
          ← Back to HR list
        </Link>
      </div>

      <PageHeader
        title={hr.fullName}
        description={`${hr.employeeCode} · ${hr.email}`}
        actions={
          <div className="flex gap-2">
            {!editing && (
              <button
                type="button"
                onClick={() => setEditing(true)}
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100"
              >
                Edit
              </button>
            )}
            {hr.employmentStatus === 'ACTIVE' && (
              <button
                type="button"
                disabled={submitting}
                onClick={handleDeactivate}
                className="rounded-lg border border-red-300 px-4 py-2 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
              >
                Deactivate
              </button>
            )}
          </div>
        }
      />

      <div className="mb-6 flex items-center gap-4">
        <ProfileAvatar
          userId={hr.userId}
          fullName={hr.fullName}
          profilePhotoUrl={hr.profilePhotoUrl}
          size="sm"
        />
        <p className="text-sm text-slate-600">
          Profile photo is managed by the HR user on their own profile page.
        </p>
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

      {editing ? (
        <form onSubmit={handleUpdate} className="grid max-w-3xl gap-4 sm:grid-cols-2">
          <FormField
            id="fullName"
            label="Full name"
            value={editForm.fullName}
            onChange={(e) => setEditForm((prev) => ({ ...prev, fullName: e.target.value }))}
          />
          <FormField
            id="phone"
            label="Phone"
            value={editForm.phone}
            onChange={(e) => setEditForm((prev) => ({ ...prev, phone: e.target.value }))}
          />
          <FormField
            id="department"
            label="Department"
            value={editForm.department}
            onChange={(e) => setEditForm((prev) => ({ ...prev, department: e.target.value }))}
          />
          <FormField
            id="designation"
            label="Designation"
            value={editForm.designation}
            onChange={(e) => setEditForm((prev) => ({ ...prev, designation: e.target.value }))}
          />
          <FormField
            id="basicSalary"
            label="Basic salary"
            type="number"
            min="0"
            step="0.01"
            value={editForm.basicSalary}
            onChange={(e) => setEditForm((prev) => ({ ...prev, basicSalary: e.target.value }))}
          />
          <div className="sm:col-span-2">
            <FormField
              id="address"
              label="Address"
              as="textarea"
              value={editForm.address}
              onChange={(e) => setEditForm((prev) => ({ ...prev, address: e.target.value }))}
            />
          </div>
          <div className="flex gap-2 sm:col-span-2">
            <button
              type="submit"
              disabled={submitting}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-60"
            >
              {submitting ? 'Saving...' : 'Save changes'}
            </button>
            <button
              type="button"
              onClick={() => {
                setEditing(false);
                setEditForm(emptyEditForm(hr));
              }}
              className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
            >
              Cancel
            </button>
          </div>
        </form>
      ) : (
        <DetailGrid items={readOnlyItems} />
      )}
    </div>
  );
}
