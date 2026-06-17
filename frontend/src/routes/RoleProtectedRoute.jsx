import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLE_HOME } from '../utils/constants';

export default function RoleProtectedRoute({ allowedRoles }) {
  const { role, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center text-slate-600">
        Loading...
      </div>
    );
  }

  if (!role || !allowedRoles.includes(role)) {
    const redirect = role ? ROLE_HOME[role] : '/login';
    return <Navigate to={redirect} replace />;
  }

  return <Outlet />;
}
