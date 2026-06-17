import { useState } from 'react';
import PageHeader from '../../components/common/PageHeader';
import { ROLES } from '../../utils/constants';
import EmployeeAttendancePage from './EmployeeAttendancePage';
import ManagementAttendancePage from './ManagementAttendancePage';

const TABS = [
  { id: 'mine', label: 'My attendance' },
  { id: 'employees', label: 'Employee attendance' },
];

export default function HrAttendancePage() {
  const [activeTab, setActiveTab] = useState('mine');

  return (
    <div>
      <PageHeader
        title="Attendance"
        description="Check in/out for your own attendance and monitor employee records."
      />

      <div className="mb-6 border-b border-slate-200">
        <nav className="-mb-px flex gap-4">
          {TABS.map((tab) => (
            <button
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
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

      {activeTab === 'mine' ? (
        <EmployeeAttendancePage />
      ) : (
        <ManagementAttendancePage viewerRole={ROLES.HR} embedded />
      )}
    </div>
  );
}
