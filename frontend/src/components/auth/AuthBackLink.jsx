import { Link } from 'react-router-dom';

export default function AuthBackLink({ to = '/login', children = 'Back to login' }) {
  return (
    <p className="mt-6 text-center text-sm text-slate-600">
      <Link to={to} className="font-medium text-slate-900 hover:underline">
        {children}
      </Link>
    </p>
  );
}
