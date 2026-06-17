import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import AuthAlert from '../../components/auth/AuthAlert';
import AuthSubmitButton from '../../components/auth/AuthSubmitButton';
import AuthTextField from '../../components/auth/AuthTextField';
import { useAuth } from '../../context/AuthContext';
import { getErrorMessage } from '../../utils/authErrors';
import { ROLE_HOME } from '../../utils/constants';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const notice = location.state?.notice;
  const redirectTo = location.state?.from?.pathname;

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const result = await login(form);
      const destination = redirectTo || result.home || ROLE_HOME[result.role];
      navigate(destination, { replace: true });
    } catch (err) {
      setError(getErrorMessage(err, 'Login failed'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h2 className="mb-2 text-2xl font-semibold text-slate-900">Sign in</h2>
      <p className="mb-6 text-sm text-slate-600">Employee Payroll & Workforce Management</p>
      {notice && <AuthAlert type="success">{notice}</AuthAlert>}
      <form onSubmit={handleSubmit} className="mt-4 space-y-4">
        <AuthTextField
          id="email"
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
          autoComplete="email"
        />
        <AuthTextField
          id="password"
          label="Password"
          type="password"
          value={form.password}
          onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))}
          autoComplete="current-password"
        />
        {error && <AuthAlert>{error}</AuthAlert>}
        <AuthSubmitButton loading={submitting} loadingText="Signing in...">
          Sign in
        </AuthSubmitButton>
      </form>
      <p className="mt-4 text-center text-sm text-slate-600">
        <Link to="/forgot-password" className="font-medium text-slate-900 hover:underline">
          Forgot password?
        </Link>
      </p>
    </div>
  );
}
