import AttendanceSummaryCards from '../attendance/AttendanceSummaryCards';
import { formatCurrency } from '../../utils/formatters';
import { ROLES } from '../../utils/constants';

function CountCard({ label, summary }) {
  if (!summary) {
    return null;
  }
  return (
    <div className="rounded-lg border border-slate-200 bg-slate-50 p-4">
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</p>
      <div className="mt-2 grid grid-cols-3 gap-2 text-center text-sm">
        <div>
          <p className="text-lg font-semibold text-slate-900">{summary.totalUsers}</p>
          <p className="text-xs text-slate-500">Total</p>
        </div>
        <div>
          <p className="text-lg font-semibold text-green-700">{summary.activeUsers}</p>
          <p className="text-xs text-slate-500">Active</p>
        </div>
        <div>
          <p className="text-lg font-semibold text-slate-600">{summary.inactiveUsers}</p>
          <p className="text-xs text-slate-500">Inactive</p>
        </div>
      </div>
    </div>
  );
}

function LeaveSummaryCards({ summary }) {
  if (!summary) {
    return null;
  }
  const items = [
    { label: 'Pending', value: summary.pendingLeaveCount, tone: 'text-amber-700' },
    { label: 'Approved', value: summary.approvedLeaveCount, tone: 'text-green-700' },
    { label: 'Rejected', value: summary.rejectedLeaveCount, tone: 'text-red-700' },
    { label: 'Paid used', value: summary.paidLeaveUsed },
    { label: 'Remaining paid', value: summary.remainingPaidLeave, tone: 'text-green-700' },
    { label: 'Unpaid used', value: summary.unpaidLeaveUsed, tone: 'text-orange-700' },
  ];

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {items.map((item) => (
        <div key={item.label} className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{item.label}</p>
          <p className={`mt-1 text-2xl font-semibold ${item.tone || 'text-slate-900'}`}>{item.value}</p>
        </div>
      ))}
    </div>
  );
}

function PayrollDashboardCards({ summary }) {
  if (!summary) {
    return null;
  }
  const items = [
    { label: 'Salary paid', value: formatCurrency(summary.totalSalaryPaid) },
    { label: 'PF deduction', value: formatCurrency(summary.totalPfDeduction) },
    { label: 'Unpaid leave ded.', value: formatCurrency(summary.totalUnpaidLeaveDeduction) },
    { label: 'Generated', value: summary.generatedPayrollCount },
    { label: 'Credited', value: summary.creditedPayrollCount, tone: 'text-green-700' },
    { label: 'Pending credit', value: summary.pendingSalaryCredits, tone: 'text-amber-700' },
  ];

  return (
    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {items.map((item) => (
        <div key={item.label} className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{item.label}</p>
          <p className={`mt-1 text-xl font-semibold ${item.tone || 'text-slate-900'}`}>{item.value}</p>
        </div>
      ))}
    </div>
  );
}

export default function DashboardAnalyticsPanel({ analytics, viewerRole }) {
  if (!analytics) {
    return null;
  }

  return (
    <div className="space-y-8">
      {viewerRole === ROLES.HR && analytics.employeeSummary && (
        <section>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
            Employees
          </h3>
          <CountCard label="Workforce" summary={analytics.employeeSummary} />
        </section>
      )}
      {viewerRole === ROLES.ADMIN && analytics.hrSummary && (
        <section>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
            HR users
          </h3>
          <CountCard label="HR workforce" summary={analytics.hrSummary} />
        </section>
      )}

      {analytics.attendanceSummary && (
        <section>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
            Attendance (current month)
          </h3>
          <AttendanceSummaryCards summary={analytics.attendanceSummary} />
        </section>
      )}

      {analytics.leaveSummary && (
        <section>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
            Leave overview
          </h3>
          <LeaveSummaryCards summary={analytics.leaveSummary} />
        </section>
      )}

      {analytics.payrollSummary && (
        <section>
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wide text-slate-500">
            Payroll (current month)
          </h3>
          <PayrollDashboardCards summary={analytics.payrollSummary} />
        </section>
      )}
    </div>
  );
}
