import { formatCurrency } from '../../utils/formatters';
import { ROLES } from '../../utils/constants';

function CountCard({ label, summary }) {
  if (!summary) {
    return null;
  }
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:shadow-md">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</p>
          <p className="mt-2 text-3xl font-extrabold text-slate-950">{summary.totalUsers}</p>
          <p className="text-xs text-slate-400">Total Registered</p>
        </div>
        <div className="rounded-lg bg-blue-50 p-3 text-blue-600">
          <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
      </div>
      <div className="mt-4 flex gap-4 border-t border-slate-100 pt-3 text-sm">
        <div className="flex items-center gap-1.5">
          <span className="h-2.5 w-2.5 rounded-full bg-green-500"></span>
          <span className="font-semibold text-green-700">{summary.activeUsers}</span>
          <span className="text-xs text-slate-500">Active</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="h-2.5 w-2.5 rounded-full bg-slate-300"></span>
          <span className="font-semibold text-slate-600">{summary.inactiveUsers}</span>
          <span className="text-xs text-slate-500">Inactive</span>
        </div>
      </div>
    </div>
  );
}

function TodayAttendanceSummary({ summary }) {
  if (!summary) return null;

  const present = summary.presentCount ?? 0;
  const absent = summary.absentCount ?? 0;
  const leave = summary.leaveCount ?? 0;
  const halfDay = summary.halfDayLeaveCount ?? 0;
  const missingCheckout = summary.missingCheckoutCount ?? 0;

  const total = present + absent + leave + halfDay + missingCheckout;
  const presentPercent = total > 0 ? Math.round((present / total) * 100) : 0;
  const absentPercent = total > 0 ? Math.round((absent / total) * 100) : 0;

  // Detect if today is weekend (Saturday = 6, Sunday = 0)
  const isTodayWeekend = [0, 6].includes(new Date().getDay()) || total === 0;

  return (
    <div className="grid gap-6 md:grid-cols-3">
      {/* Visual Metric: Present Rate */}
      <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex flex-col justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Present Rate</p>
          {isTodayWeekend ? (
            <div className="mt-4 flex flex-col gap-1">
              <span className="text-4xl font-extrabold text-slate-500">Weekend</span>
              <span className="text-xs text-slate-400">Workforce off-duty today</span>
            </div>
          ) : (
            <div className="mt-4 flex items-baseline gap-2">
              <span className="text-4xl font-extrabold text-green-600">{presentPercent}%</span>
              <span className="text-xs text-slate-400">of workforce today</span>
            </div>
          )}
        </div>
        <div className="mt-4">
          <div className="h-2 w-full rounded-full bg-slate-100 overflow-hidden">
            {isTodayWeekend ? (
              <div className="h-full bg-slate-300 rounded-full w-full"></div>
            ) : (
              <div className="h-full bg-green-500 rounded-full transition-all duration-500" style={{ width: `${presentPercent}%` }}></div>
            )}
          </div>
          <div className="mt-2 flex justify-between text-xs text-slate-500">
            {isTodayWeekend ? (
              <span>No attendance required</span>
            ) : (
              <>
                <span>{present} Present</span>
                <span>{total} Total expected</span>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Grid of detail counts */}
      <div className="md:col-span-2 grid grid-cols-2 sm:grid-cols-3 gap-4">
        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm flex flex-col justify-between border-l-4 border-l-green-500">
          <p className="text-xs font-medium uppercase text-slate-500">Present</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-2xl font-bold text-green-700">{present}</span>
            <span className="text-xs text-slate-400">{isTodayWeekend ? '0%' : `${presentPercent}%`}</span>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm flex flex-col justify-between border-l-4 border-l-red-500">
          <p className="text-xs font-medium uppercase text-slate-500">Absent</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-2xl font-bold text-red-700">{absent}</span>
            <span className="text-xs text-slate-400">{isTodayWeekend ? '0%' : `${absentPercent}%`}</span>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm flex flex-col justify-between border-l-4 border-l-blue-500">
          <p className="text-xs font-medium uppercase text-slate-500">On Leave</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-2xl font-bold text-blue-700">{leave}</span>
            <span className="text-xs text-slate-400">Full day</span>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm flex flex-col justify-between border-l-4 border-l-amber-500">
          <p className="text-xs font-medium uppercase text-slate-500">Half-Day Leave</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-2xl font-bold text-amber-700">{halfDay}</span>
            <span className="text-xs text-slate-400">Shift split</span>
          </div>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm flex flex-col justify-between border-l-4 border-l-orange-500">
          <p className="text-xs font-medium uppercase text-slate-500">Missing Checkout</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-2xl font-bold text-orange-700">{missingCheckout}</span>
            <span className="text-xs text-red-400">Alerts active</span>
          </div>
        </div>
      </div>
    </div>
  );
}

function LeaveSummaryCards({ summary }) {
  if (!summary) return null;

  const pending = summary.pendingLeaveCount ?? 0;
  const approved = summary.approvedLeaveCount ?? 0;
  const rejected = summary.rejectedLeaveCount ?? 0;

  const paidUsed = summary.paidLeaveUsed ?? 0;
  const remainingPaid = summary.remainingPaidLeave ?? 0;
  const unpaidUsed = summary.unpaidLeaveUsed ?? 0;

  const totalPaidPool = paidUsed + remainingPaid;
  const utilizationRate = totalPaidPool > 0 ? Math.round((paidUsed / totalPaidPool) * 100) : 0;

  return (
    <div className="space-y-6">
      {/* Row 1: Requests Status */}
      <div className="grid gap-4 sm:grid-cols-3">
        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm border-l-4 border-l-amber-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Pending Requests</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-amber-700">{pending}</span>
            <span className="rounded bg-amber-50 px-2 py-0.5 text-xs font-medium text-amber-800">Requires Action</span>
          </div>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm border-l-4 border-l-green-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Approved Requests</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-green-700">{approved}</span>
            <span className="rounded bg-green-50 px-2 py-0.5 text-xs font-medium text-green-800">Processed</span>
          </div>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm border-l-4 border-l-red-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Rejected Requests</p>
          <div className="mt-2 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-red-700">{rejected}</span>
            <span className="rounded bg-red-50 px-2 py-0.5 text-xs font-medium text-red-800">Declined</span>
          </div>
        </div>
      </div>

      {/* Row 2: Paid Leave Utilization Pool */}
      <div className="grid gap-6 md:grid-cols-3">
        {/* Paid Pool Utilization Progress Card */}
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex flex-col justify-between border-l-4 border-l-indigo-500 md:col-span-2">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Paid Leave Pool Utilization</p>
            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-indigo-700">{utilizationRate}%</span>
              <span className="text-xs text-slate-400">allocated pool consumed</span>
            </div>
          </div>
          <div className="mt-4">
            <div className="h-2 w-full rounded-full bg-slate-100 overflow-hidden">
              <div className="h-full bg-indigo-600 rounded-full transition-all duration-500" style={{ width: `${utilizationRate}%` }}></div>
            </div>
            <div className="mt-2 flex justify-between text-xs text-slate-500">
              <span>{paidUsed} Days Used</span>
              <span>{totalPaidPool} Days Total Pool</span>
            </div>
          </div>
        </div>

        {/* Unpaid / Extra stats */}
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex flex-col justify-between border-l-4 border-l-orange-500">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Unpaid Leave Used</p>
            <p className="mt-3 text-3xl font-extrabold text-orange-700">{unpaidUsed}</p>
          </div>
          <p className="mt-2 text-xs text-slate-400">Total unpaid days taken across active workforce</p>
        </div>
      </div>
    </div>
  );
}

function PayrollDashboardCards({ summary }) {
  if (!summary) return null;

  const salaryPaid = summary.totalSalaryPaid ?? 0;
  const pfDeduction = summary.totalPfDeduction ?? 0;
  const unpaidDed = summary.totalUnpaidLeaveDeduction ?? 0;

  const generated = summary.generatedPayrollCount ?? 0;
  const credited = summary.creditedPayrollCount ?? 0;
  const pending = summary.pendingSalaryCredits ?? 0;

  const completionRate = generated > 0 ? Math.round((credited / generated) * 100) : 0;

  return (
    <div className="space-y-6">
      {/* Financial Amounts Cards */}
      <div className="grid gap-4 sm:grid-cols-3">
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm border-l-4 border-l-emerald-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Salary Credited</p>
          <p className="mt-2 text-2xl font-bold text-emerald-700">{formatCurrency(salaryPaid)}</p>
          <p className="mt-1 text-xs text-slate-400">Net salary successfully paid</p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm border-l-4 border-l-slate-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">PF Deduction</p>
          <p className="mt-2 text-2xl font-bold text-slate-700">{formatCurrency(pfDeduction)}</p>
          <p className="mt-1 text-xs text-slate-400">Provident fund contributions</p>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm border-l-4 border-l-orange-500">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Unpaid Leave Ded.</p>
          <p className="mt-2 text-2xl font-bold text-orange-700">{formatCurrency(unpaidDed)}</p>
          <p className="mt-1 text-xs text-slate-400">Deductions for unpaid leave days</p>
        </div>
      </div>

      {/* Payout Completion Status Grid */}
      <div className="grid gap-6 md:grid-cols-3">
        {/* Payout progress tracking */}
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex flex-col justify-between border-l-4 border-l-teal-500 md:col-span-2">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Payout Distribution Status</p>
            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-teal-700">{completionRate}%</span>
              <span className="text-xs text-slate-400">completed</span>
            </div>
          </div>
          <div className="mt-4">
            <div className="h-2 w-full rounded-full bg-slate-100 overflow-hidden">
              <div className="h-full bg-teal-600 rounded-full transition-all duration-500" style={{ width: `${completionRate}%` }}></div>
            </div>
            <div className="mt-2 flex justify-between text-xs text-slate-500">
              <span>{credited} Credited</span>
              <span>{generated} Total Generated</span>
            </div>
          </div>
        </div>

        {/* Uncredited / Pending counts */}
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm flex flex-col justify-between border-l-4 border-l-amber-500">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Pending Credit</p>
            <p className="mt-3 text-3xl font-extrabold text-amber-700">{pending}</p>
          </div>
          <p className="mt-2 text-xs text-slate-400">Payroll generated but not yet paid</p>
        </div>
      </div>
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
            Attendance (Today)
          </h3>
          <TodayAttendanceSummary summary={analytics.attendanceSummary} />
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
