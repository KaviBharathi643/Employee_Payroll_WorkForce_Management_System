import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Alert from '../../components/common/Alert';
import Modal from '../../components/common/Modal';
import PageHeader from '../../components/common/PageHeader';
import CreateHrForm from '../../components/employees/CreateHrForm';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { formatLabel } from '../../utils/formatters';

export default function HrManagementPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreate, setShowCreate] = useState(false);

  const loadHrUsers = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await employeeService.listHr();
      setRows(data || []);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load HR users'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadHrUsers();
  }, [loadHrUsers]);

  return (
    <div>
      <PageHeader
        title="HR Management"
        description="Create and manage HR accounts."
        actions={
          <button
            type="button"
            onClick={() => setShowCreate(true)}
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
          >
            Add HR
          </button>
        }
      />

      {error && (
        <div className="mb-4">
          <Alert>{error}</Alert>
        </div>
      )}

      <div className="overflow-x-auto rounded-lg border border-slate-200">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr>
              <th className="px-4 py-3 text-left font-medium text-slate-600">Code</th>
              <th className="px-4 py-3 text-left font-medium text-slate-600">Name</th>
              <th className="px-4 py-3 text-left font-medium text-slate-600">Email</th>
              <th className="px-4 py-3 text-left font-medium text-slate-600">Department</th>
              <th className="px-4 py-3 text-left font-medium text-slate-600">Status</th>
              <th className="px-4 py-3 text-right font-medium text-slate-600">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 bg-white">
            {loading ? (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-slate-500">
                  Loading HR users...
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-slate-500">
                  No HR users found.
                </td>
              </tr>
            ) : (
              rows.map((row) => (
                <tr key={row.userId}>
                  <td className="px-4 py-3 font-medium text-slate-900">{row.employeeCode}</td>
                  <td className="px-4 py-3">{row.fullName}</td>
                  <td className="px-4 py-3 text-slate-600">{row.email}</td>
                  <td className="px-4 py-3">{row.department}</td>
                  <td className="px-4 py-3">{formatLabel(row.employmentStatus)}</td>
                  <td className="px-4 py-3 text-right">
                    <Link
                      to={`/admin/hr/${row.userId}`}
                      className="font-medium text-slate-900 hover:underline"
                    >
                      View
                    </Link>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showCreate && (
        <Modal title="Create HR user" onClose={() => setShowCreate(false)} wide>
          <CreateHrForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => {
              setShowCreate(false);
              loadHrUsers();
            }}
          />
        </Modal>
      )}
    </div>
  );
}
