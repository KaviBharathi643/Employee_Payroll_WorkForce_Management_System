import DetailGrid from '../common/DetailGrid';
import Modal from '../common/Modal';
import PayrollStatusBadge from './PayrollStatusBadge';
import { formatCurrency, formatDateTime, formatMonthYear } from '../../utils/formatters';

export default function PayrollDetailModal({ payroll, onClose }) {
  if (!payroll) {
    return null;
  }

  const items = [
    { label: 'Employee', value: `${payroll.employeeName} (${payroll.employeeCode})` },
    { label: 'Period', value: formatMonthYear(payroll.payrollYear, payroll.payrollMonth) },
    { label: 'Department', value: payroll.department },
    { label: 'Designation', value: payroll.designation },
    { label: 'Basic salary', value: formatCurrency(payroll.basicSalary) },
    { label: 'Bonus', value: formatCurrency(payroll.bonus) },
    { label: 'PF %', value: payroll.pfPercentage },
    { label: 'PF amount', value: formatCurrency(payroll.pfAmount) },
    { label: 'Unpaid leave days', value: payroll.unpaidLeaveCount },
    { label: 'Unpaid deduction', value: formatCurrency(payroll.unpaidLeaveDeduction) },
    { label: 'Final salary', value: formatCurrency(payroll.finalSalary) },
    { label: 'Generated', value: formatDateTime(payroll.generatedDate) },
    { label: 'Credited', value: formatDateTime(payroll.creditedDate) },
  ];

  return (
    <Modal title="Payroll details" onClose={onClose} wide>
      <div className="mb-4">
        <PayrollStatusBadge status={payroll.status} />
      </div>
      <DetailGrid items={items} />
    </Modal>
  );
}
