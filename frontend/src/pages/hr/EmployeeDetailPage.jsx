import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Alert from '../../components/common/Alert';
import DetailGrid from '../../components/common/DetailGrid';
import PageHeader from '../../components/common/PageHeader';
import FormField from '../../components/forms/FormField';
import FormSelect from '../../components/forms/FormSelect';
import ProfileAvatar from '../../components/profile/ProfileAvatar';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { EMPLOYMENT_TYPE_OPTIONS } from '../../utils/constants';
import { formatCurrency, formatDate, formatLabel } from '../../utils/formatters';

function emptyEditForm(employee) {
  return {
    fullName: employee?.fullName || '',
    phone: employee?.phone || '',
    address: employee?.address || '',
    department: employee?.department || '',
    designation: employee?.designation || '',
    basicSalary: employee?.basicSalary ?? '',
    employmentType: employee?.employmentType || 'FULL_TIME',
  };
}

export default function EmployeeDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState(null);
  const [editForm, setEditForm] = useState(emptyEditForm());
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadEmployee = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await employeeService.getEmployee(id);
      setEmployee(data);
      setEditForm(emptyEditForm(data));
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load employee'));
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadEmployee();
  }, [loadEmployee]);

  const handleUpdate = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    setNotice('');
    try {
      const { message, data } = await employeeService.updateEmployee(id, {
        ...editForm,
        basicSalary: Number(editForm.basicSalary),
      });
      setEmployee(data);
      setEditForm(emptyEditForm(data));
      setEditing(false);
      setNotice(message || 'Employee updated');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to update employee'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!window.confirm('Deactivate this employee? They will no longer be able to sign in.')) {
      return;
    }
    setSubmitting(true);
    setError('');
    try {
      const { message } = await employeeService.deactivateEmployee(id);
      setNotice(message || 'Employee deactivated');
      await loadEmployee();
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to deactivate employee'));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <p className="text-sm text-slate-600">Loading employee...</p>;
  }

  if (!employee) {
    return <Alert>{error || 'Employee not found'}</Alert>;
  }

  const readOnlyItems = [
    { label: 'Employee code', value: employee.employeeCode },
    { label: 'Email', value: employee.email },
    { label: 'Date of birth', value: formatDate(employee.dateOfBirth) },
    { label: 'Gender', value: formatLabel(employee.gender) },
    { label: 'Phone', value: employee.phone },
    { label: 'Address', value: employee.address },
    { label: 'Department', value: employee.department },
    { label: 'Designation', value: employee.designation },
    { label: 'Joining date', value: formatDate(employee.joiningDate) },
    { label: 'Employment type', value: formatLabel(employee.employmentType) },
    { label: 'Basic salary', value: formatCurrency(employee.basicSalary) },
    { label: 'Employment status', value: formatLabel(employee.employmentStatus) },
    { label: 'Account status', value: formatLabel(employee.userStatus) },
    { label: 'Bank name', value: employee.bankName },
    {
      label: 'Account number',
      value: employee.maskedAccountNumber || employee.accountNumber,
    },
    { label: 'IFSC code', value: employee.ifscCode },
  ];

  return (
    <div>
      <div className="mb-4">
        <Link to="/hr/employees" className="text-sm font-medium text-slate-600 hover:underline">
          ← Back to employees
        </Link>
      </div>

      <PageHeader
        title={employee.fullName}
        description={`${employee.employeeCode} · ${employee.email}`}
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
            {employee.employmentStatus === 'ACTIVE' && (
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
          userId={employee.userId}
          fullName={employee.fullName}
          profilePhotoUrl={employee.profilePhotoUrl}
          size="sm"
        />
        <p className="text-sm text-slate-600">
          Profile photo is managed by the employee on their own profile page.
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
          <FormSelect
            id="employmentType"
            label="Employment type"
            value={editForm.employmentType}
            onChange={(e) =>
              setEditForm((prev) => ({ ...prev, employmentType: e.target.value }))
            }
            options={EMPLOYMENT_TYPE_OPTIONS.map((value) => ({
              value,
              label: formatLabel(value),
            }))}
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
                setEditForm(emptyEditForm(employee));
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

      {employee.employmentStatus !== 'ACTIVE' && (
        <p className="mt-4 text-sm text-slate-500">
          This employee is inactive.{' '}
          <button
            type="button"
            onClick={() => navigate('/hr/employees')}
            className="font-medium text-slate-900 hover:underline"
          >
            Return to list
          </button>
        </p>
      )}
    </div>
  );
}
