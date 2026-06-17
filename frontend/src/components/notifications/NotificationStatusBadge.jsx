import { formatLabel } from '../../utils/formatters';

const STYLES = {
  UNREAD: 'bg-blue-50 text-blue-800 ring-blue-200',
  READ: 'bg-slate-50 text-slate-600 ring-slate-200',
};

export default function NotificationStatusBadge({ status }) {
  const style = STYLES[status] || STYLES.READ;

  return (
    <span className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ${style}`}>
      {formatLabel(status)}
    </span>
  );
}
