import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLE_HOME } from '../utils/constants';

export default function GuestRoute() {
  const { isAuthenticated, loading, role } = useAuth();

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center text-slate-600">
        Loading...
      </div>
    );
  }

  if (isAuthenticated && role) {
    return <Navigate to={ROLE_HOME[role]} replace />;
  }

  return <Outlet />;
}
