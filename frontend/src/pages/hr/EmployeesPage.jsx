import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Alert from '../../components/common/Alert';
import Modal from '../../components/common/Modal';
import PageHeader from '../../components/common/PageHeader';
import Pagination from '../../components/common/Pagination';
import CreateEmployeeForm from '../../components/employees/CreateEmployeeForm';
import FormField from '../../components/forms/FormField';
import employeeService from '../../services/employeeService';
import { getErrorMessage } from '../../utils/authErrors';
import { formatLabel } from '../../utils/formatters';

export default function EmployeesPage() {
  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [filters, setFilters] = useState({
    search: '',
    department: '',
    designation: '',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCreate, setShowCreate] = useState(false);

  const loadEmployees = useCallback(
    async (pageToLoad = page) => {
      setLoading(true);
      setError('');
      try {
        const data = await employeeService.listEmployees({
          page: pageToLoad,
          size: 10,
          sortBy: 'fullName',
          sortDir: 'asc',
          search: filters.search || undefined,
          department: filters.department || undefined,
          designation: filters.designation || undefined,
        });
        setRows(data.content || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load employees'));
      } finally {
        setLoading(false);
      }
    },
    [filters.department, filters.designation, filters.search, page],
  );

  useEffect(() => {
    loadEmployees();
  }, [loadEmployees]);

  const handleFilterSubmit = (event) => {
    event.preventDefault();
    setPage(0);
    loadEmployees(0);
  };

  return (
    <div>
      <PageHeader
        title="Employees"
        description="Manage employee accounts and employment details."
        actions={
          <button
            type="button"
            onClick={() => setShowCreate(true)}
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
          >
            Add employee
          </button>
        }
      />

      <form onSubmit={handleFilterSubmit} className="mb-6 grid gap-3 sm:grid-cols-4">
        <FormField
          id="search"
          label="Search"
          required={false}
          placeholder="Name, email, code..."
          value={filters.search}
          onChange={(e) => setFilters((prev) => ({ ...prev, search: e.target.value }))}
        />
        <FormField
          id="department"
          label="Department"
          required={false}
          value={filters.department}
          onChange={(e) => setFilters((prev) => ({ ...prev, department: e.target.value }))}
        />
        <FormField
          id="designation"
          label="Designation"
          required={false}
          value={filters.designation}
          onChange={(e) => setFilters((prev) => ({ ...prev, designation: e.target.value }))}
        />
        <div className="flex items-end">
          <button
            type="submit"
            className="w-full rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100"
          >
            Apply filters
          </button>
        </div>
      </form>

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
                  Loading employees...
                </td>
              </tr>
            ) : rows.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-slate-500">
                  No employees found.
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
                      to={`/hr/employees/${row.userId}`}
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

      <Pagination
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        onPageChange={setPage}
      />

      {showCreate && (
        <Modal title="Create employee" onClose={() => setShowCreate(false)} wide>
          <CreateEmployeeForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => {
              setShowCreate(false);
              loadEmployees();
            }}
          />
        </Modal>
      )}
    </div>
  );
}
