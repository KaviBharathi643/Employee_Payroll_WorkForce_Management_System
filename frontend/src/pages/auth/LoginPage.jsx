import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import AuthAlert from '../../components/auth/AuthAlert';
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
  const [showPassword, setShowPassword] = useState(false);

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
      {/* Fingerprint icon circle */}
      <div className="mx-auto mb-5 flex h-14 w-14 items-center justify-center rounded-full bg-secondary text-primary border border-primary/20 shadow-sm">
        <svg className="h-7 w-7" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 11c0-3.517-1.009-6.799-2.753-9.571m-3.44 2.04l.054-.09A13.916 13.916 0 009 11a13.918 13.918 0 00-6.125 11.533M21.75 12c0 3.517-1.009 6.799-2.753 9.571m-3.44-2.04l.054.09A13.916 13.916 0 0015 11c0-2.78-.812-5.37-2.203-7.565M12 9a3.99 3.99 0 00-3.935 3.242M16.5 12a4.5 4.5 0 01-9 0" />
        </svg>
      </div>

      <h3 className="text-2xl font-bold text-center text-text-main tracking-tight">Welcome back!</h3>
      <p className="text-xs text-center text-text-main/60 mt-1 mb-6">Sign in to continue to your account</p>

      {notice && <AuthAlert type="success">{notice}</AuthAlert>}
      {error && <AuthAlert>{error}</AuthAlert>}

      <form onSubmit={handleSubmit} className="mt-4 space-y-5">
        {/* Email Field */}
        <div>
          <label className="block text-[11px] font-bold text-text-main/70 uppercase tracking-wider mb-2">Email address</label>
          <div className="relative">
            <span className="absolute inset-y-0 left-0 flex items-center pl-3.5 text-text-main/40">
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8">
                <path strokeLinecap="round" strokeLinejoin="round" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.206" />
              </svg>
            </span>
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
              autoComplete="email"
              placeholder="Enter your email"
              className="w-full rounded-xl border border-primary/20 bg-background pl-11 pr-4 py-3 text-sm text-text-main placeholder-text-main/40 outline-none transition-all focus:border-primary focus:bg-card focus:ring-2 focus:ring-primary/10"
            />
          </div>
        </div>

        {/* Password Field */}
        <div>
          <label className="block text-[11px] font-bold text-text-main/70 uppercase tracking-wider mb-2">Password</label>
          <div className="relative">
            <span className="absolute inset-y-0 left-0 flex items-center pl-3.5 text-text-main/40">
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </span>
            <input
              type={showPassword ? 'text' : 'password'}
              required
              value={form.password}
              onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))}
              autoComplete="current-password"
              placeholder="Enter your password"
              className="w-full rounded-xl border border-primary/20 bg-background pl-11 pr-11 py-3 text-sm text-text-main placeholder-text-main/40 outline-none transition-all focus:border-primary focus:bg-card focus:ring-2 focus:ring-primary/10"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 flex items-center pr-3 text-text-main/40 hover:text-text-main/70"
            >
              {showPassword ? (
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                </svg>
              ) : (
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.8">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
          </div>
        </div>

        {/* Options Row */}
        <div className="flex items-center justify-between text-xs sm:text-sm">
          <label className="flex items-center gap-2 text-text-main/70 select-none cursor-pointer">
            <input
              type="checkbox"
              className="h-4 w-4 rounded border-primary/30 text-primary focus:ring-primary cursor-pointer"
            />
            <span className="text-text-main/70">Remember me</span>
          </label>
          <Link to="/forgot-password" className="font-semibold text-primary hover:text-primary-dark">
            Forgot password?
          </Link>
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={submitting}
          className="w-full flex items-center justify-between rounded-xl bg-primary px-5 py-3 text-sm font-semibold text-white hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary/20 disabled:opacity-60 transition-all shadow-md shadow-primary/10 cursor-pointer"
        >
          <span>{submitting ? 'Signing in...' : 'Sign in'}</span>
          <div className="flex h-6 w-6 items-center justify-center rounded-full bg-white/20">
            <svg className="h-3 w-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
              <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </button>
      </form>

      {/* Enterprise Security Footer */}
      <div className="mt-8 pt-6 border-t border-primary/10 flex flex-col items-center">
        <div className="flex h-7 w-7 items-center justify-center rounded-full bg-secondary border border-primary/10 text-primary shadow-sm mb-2">
          <svg className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
        </div>
        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider text-center">Your data is protected with enterprise-grade security</p>
      </div>
    </div>
  );
}

// Touch to update git timestamp