import { formatLabel } from '../../utils/formatters';

const STYLES = {
  LEAVE: 'bg-purple-50 text-purple-800',
  PAYROLL: 'bg-emerald-50 text-emerald-800',
  PAYSLIP: 'bg-teal-50 text-teal-800',
  ATTENDANCE: 'bg-orange-50 text-orange-800',
  PROFILE: 'bg-indigo-50 text-indigo-800',
};

export default function NotificationTypeBadge({ type }) {
  const style = STYLES[type] || 'bg-slate-100 text-slate-700';

  return (
    <span className={`inline-flex rounded px-2 py-0.5 text-xs font-medium ${style}`}>
      {formatLabel(type)}
    </span>
  );
}
