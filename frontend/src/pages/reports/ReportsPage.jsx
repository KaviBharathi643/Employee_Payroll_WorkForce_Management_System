import { useState } from 'react';
import Alert from '../../components/common/Alert';
import PageHeader from '../../components/common/PageHeader';
import DateRangeFilter from '../../components/attendance/DateRangeFilter';
import MonthYearFilter from '../../components/payroll/MonthYearFilter';
import ReportSection from '../../components/reports/ReportSection';
import FormField from '../../components/forms/FormField';
import FormSelect from '../../components/forms/FormSelect';
import reportService from '../../services/reportService';
import { getErrorMessage } from '../../utils/authErrors';
import {
  EMPLOYMENT_STATUS_FILTER_OPTIONS,
  LEAVE_STATUSES,
  LEAVE_TYPE_OPTIONS,
  ROLES,
} from '../../utils/constants';
import { currentMonthRange, currentPayrollPeriod, formatLabel } from '../../utils/formatters';

const CONFIG = {
  [ROLES.HR]: {
    title: 'Reports',
    description: 'Download PDF reports for employees, attendance, leave, and payroll.',
    employeeLabel: 'employee',
  },
  [ROLES.ADMIN]: {
    title: 'Reports',
    description: 'Download PDF reports for HR users, attendance, leave, and payroll.',
    employeeLabel: 'HR',
  },
};

function cleanParams(params) {
  return Object.fromEntries(
    Object.entries(params).filter(([, value]) => value != null && value !== ''),
  );
}

export default function ReportsPage({ viewerRole }) {
  const config = CONFIG[viewerRole];
  const defaultRange = currentMonthRange();
  const defaultPeriod = currentPayrollPeriod();

  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [downloading, setDownloading] = useState(null);

  const [employeeFilters, setEmployeeFilters] = useState({
    employmentStatus: 'ACTIVE',
    department: '',
    designation: '',
    employeeCode: '',
  });
  const [attendanceRange, setAttendanceRange] = useState(defaultRange);
  const [attendanceFilters, setAttendanceFilters] = useState({
    department: '',
    designation: '',
    employeeCode: '',
  });
  const [leaveRange, setLeaveRange] = useState(defaultRange);
  const [leaveFilters, setLeaveFilters] = useState({
    department: '',
    designation: '',
    employeeCode: '',
    leaveType: '',
    leaveStatus: '',
  });
  const [payrollPeriod, setPayrollPeriod] = useState(defaultPeriod);

  const runDownload = async (key, action) => {
    setDownloading(key);
    setError('');
    setNotice('');
    try {
      await action();
      setNotice('PDF download started');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to download report'));
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div>
      <PageHeader title={config.title} description={config.description} />

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

      <div className="space-y-6">
        <ReportSection
          title={`${formatLabel(config.employeeLabel)} report`}
          description={`PDF listing of ${config.employeeLabel} records with optional filters.`}
          downloading={downloading === 'employees'}
          onDownload={() =>
            runDownload('employees', () =>
              reportService.downloadEmployeeReport(cleanParams(employeeFilters)),
            )
          }
        >
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
            <FormSelect
              id="employmentStatus"
              label="Employment status"
              required={false}
              value={employeeFilters.employmentStatus}
              onChange={(e) =>
                setEmployeeFilters((prev) => ({ ...prev, employmentStatus: e.target.value }))
              }
              options={EMPLOYMENT_STATUS_FILTER_OPTIONS}
            />
            <FormField
              id="empDepartment"
              label="Department"
              required={false}
              value={employeeFilters.department}
              onChange={(e) =>
                setEmployeeFilters((prev) => ({ ...prev, department: e.target.value }))
              }
            />
            <FormField
              id="empDesignation"
              label="Designation"
              required={false}
              value={employeeFilters.designation}
              onChange={(e) =>
                setEmployeeFilters((prev) => ({ ...prev, designation: e.target.value }))
              }
            />
            <FormField
              id="empCode"
              label="Employee code"
              required={false}
              value={employeeFilters.employeeCode}
              onChange={(e) =>
                setEmployeeFilters((prev) => ({ ...prev, employeeCode: e.target.value }))
              }
            />
          </div>
        </ReportSection>

        <ReportSection
          title="Attendance report"
          description="PDF attendance records for the selected date range."
          downloading={downloading === 'attendance'}
          onDownload={() =>
            runDownload('attendance', () =>
              reportService.downloadAttendanceReport(
                cleanParams({
                  fromDate: attendanceRange.fromDate,
                  toDate: attendanceRange.toDate,
                  ...attendanceFilters,
                }),
              ),
            )
          }
        >
          <div className="space-y-4">
            <DateRangeFilter
              fromDate={attendanceRange.fromDate}
              toDate={attendanceRange.toDate}
              onFromChange={(e) =>
                setAttendanceRange((prev) => ({ ...prev, fromDate: e.target.value }))
              }
              onToChange={(e) =>
                setAttendanceRange((prev) => ({ ...prev, toDate: e.target.value }))
              }
              onSubmit={(e) => e.preventDefault()}
            />
            <div className="grid gap-3 sm:grid-cols-3">
              <FormField
                id="attDepartment"
                label="Department"
                required={false}
                value={attendanceFilters.department}
                onChange={(e) =>
                  setAttendanceFilters((prev) => ({ ...prev, department: e.target.value }))
                }
              />
              <FormField
                id="attDesignation"
                label="Designation"
                required={false}
                value={attendanceFilters.designation}
                onChange={(e) =>
                  setAttendanceFilters((prev) => ({ ...prev, designation: e.target.value }))
                }
              />
              <FormField
                id="attCode"
                label="Employee code"
                required={false}
                value={attendanceFilters.employeeCode}
                onChange={(e) =>
                  setAttendanceFilters((prev) => ({ ...prev, employeeCode: e.target.value }))
                }
              />
            </div>
          </div>
        </ReportSection>

        <ReportSection
          title="Leave report"
          description="PDF leave requests for the selected date range."
          downloading={downloading === 'leaves'}
          onDownload={() =>
            runDownload('leaves', () =>
              reportService.downloadLeaveReport(
                cleanParams({
                  fromDate: leaveRange.fromDate,
                  toDate: leaveRange.toDate,
                  ...leaveFilters,
                }),
              ),
            )
          }
        >
          <div className="space-y-4">
            <DateRangeFilter
              fromDate={leaveRange.fromDate}
              toDate={leaveRange.toDate}
              onFromChange={(e) =>
                setLeaveRange((prev) => ({ ...prev, fromDate: e.target.value }))
              }
              onToChange={(e) => setLeaveRange((prev) => ({ ...prev, toDate: e.target.value }))}
              onSubmit={(e) => e.preventDefault()}
            />
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-5">
              <FormField
                id="leaveDepartment"
                label="Department"
                required={false}
                value={leaveFilters.department}
                onChange={(e) =>
                  setLeaveFilters((prev) => ({ ...prev, department: e.target.value }))
                }
              />
              <FormField
                id="leaveDesignation"
                label="Designation"
                required={false}
                value={leaveFilters.designation}
                onChange={(e) =>
                  setLeaveFilters((prev) => ({ ...prev, designation: e.target.value }))
                }
              />
              <FormField
                id="leaveCode"
                label="Employee code"
                required={false}
                value={leaveFilters.employeeCode}
                onChange={(e) =>
                  setLeaveFilters((prev) => ({ ...prev, employeeCode: e.target.value }))
                }
              />
              <FormSelect
                id="leaveType"
                label="Leave type"
                required={false}
                value={leaveFilters.leaveType}
                onChange={(e) =>
                  setLeaveFilters((prev) => ({ ...prev, leaveType: e.target.value }))
                }
                placeholder="Any"
                options={LEAVE_TYPE_OPTIONS.map((value) => ({
                  value,
                  label: formatLabel(value),
                }))}
              />
              <FormSelect
                id="leaveStatus"
                label="Leave status"
                required={false}
                value={leaveFilters.leaveStatus}
                onChange={(e) =>
                  setLeaveFilters((prev) => ({ ...prev, leaveStatus: e.target.value }))
                }
                placeholder="Any"
                options={LEAVE_STATUSES.map((value) => ({
                  value,
                  label: formatLabel(value),
                }))}
              />
            </div>
          </div>
        </ReportSection>

        <ReportSection
          title="Payroll report"
          description="PDF payroll summary for a specific month (required year and month)."
          downloading={downloading === 'payroll'}
          onDownload={() =>
            runDownload('payroll', () =>
              reportService.downloadPayrollReport({
                payrollYear: payrollPeriod.payrollYear,
                payrollMonth: payrollPeriod.payrollMonth,
              }),
            )
          }
        >
          <MonthYearFilter
            payrollYear={payrollPeriod.payrollYear}
            payrollMonth={payrollPeriod.payrollMonth}
            onYearChange={(e) =>
              setPayrollPeriod((prev) => ({ ...prev, payrollYear: Number(e.target.value) }))
            }
            onMonthChange={(e) =>
              setPayrollPeriod((prev) => ({ ...prev, payrollMonth: Number(e.target.value) }))
            }
            onSubmit={(e) => e.preventDefault()}
          />
        </ReportSection>
      </div>
    </div>
  );
}
