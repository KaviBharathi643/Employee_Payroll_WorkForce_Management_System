import { formatLabel } from '../../utils/formatters';

const STYLES = {
  GENERATED: 'bg-amber-50 text-amber-800 ring-amber-200',
  CREDITED: 'bg-green-50 text-green-800 ring-green-200',
};

export default function PayrollStatusBadge({ status }) {
  const style = STYLES[status] || 'bg-slate-50 text-slate-700 ring-slate-200';

  return (
    <span className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ${style}`}>
      {formatLabel(status)}
    </span>
  );
}
