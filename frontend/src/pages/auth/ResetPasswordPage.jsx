import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import AuthAlert from '../../components/auth/AuthAlert';
import AuthBackLink from '../../components/auth/AuthBackLink';
import AuthSubmitButton from '../../components/auth/AuthSubmitButton';
import AuthTextField from '../../components/auth/AuthTextField';
import authService from '../../services/authService';
import { getErrorMessage } from '../../utils/authErrors';
import { MIN_PASSWORD_LENGTH, OTP_LENGTH } from '../../utils/constants';
import {
  clearRecoverySession,
  getRecoverySession,
} from '../../utils/passwordResetStorage';

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const recovery = getRecoverySession();
  const state = location.state || {};

  const [form, setForm] = useState({
    email: state.email || recovery.email || '',
    otpCode: state.otpCode || recovery.otpCode || '',
    newPassword: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!form.email || !form.otpCode) {
      navigate('/forgot-password', { replace: true });
    }
  }, [form.email, form.otpCode, navigate]);

  const handleChange = (field) => (event) => {
    let value = event.target.value;
    if (field === 'otpCode') {
      value = value.replace(/\D/g, '').slice(0, OTP_LENGTH);
    }
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    if (form.newPassword !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    if (form.newPassword.length < MIN_PASSWORD_LENGTH) {
      setError(`Password must be at least ${MIN_PASSWORD_LENGTH} characters`);
      return;
    }

    setSubmitting(true);
    try {
      const { message } = await authService.resetPassword({
        email: form.email.trim(),
        otpCode: form.otpCode.trim(),
        newPassword: form.newPassword,
        confirmPassword: form.confirmPassword,
      });
      clearRecoverySession();
      navigate('/login', {
        replace: true,
        state: { notice: message || 'Password Updated Successfully' },
      });
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to reset password'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h2 className="mb-2 text-2xl font-semibold text-slate-900">Reset password</h2>
      <p className="mb-6 text-sm text-slate-600">Choose a new password for your account.</p>
      {state.notice && <AuthAlert type="success">{state.notice}</AuthAlert>}
      <form onSubmit={handleSubmit} className="mt-4 space-y-4">
        <AuthTextField
          id="email"
          label="Email"
          type="email"
          value={form.email}
          onChange={handleChange('email')}
          autoComplete="email"
        />
        <AuthTextField
          id="otpCode"
          label="OTP code"
          value={form.otpCode}
          onChange={handleChange('otpCode')}
          autoComplete="one-time-code"
          maxLength={OTP_LENGTH}
          pattern={`\\d{${OTP_LENGTH}}`}
          inputMode="numeric"
        />
        <AuthTextField
          id="newPassword"
          label="New password"
          type="password"
          value={form.newPassword}
          onChange={handleChange('newPassword')}
          autoComplete="new-password"
          hint={`Minimum ${MIN_PASSWORD_LENGTH} characters`}
        />
        <AuthTextField
          id="confirmPassword"
          label="Confirm password"
          type="password"
          value={form.confirmPassword}
          onChange={handleChange('confirmPassword')}
          autoComplete="new-password"
        />
        {error && <AuthAlert>{error}</AuthAlert>}
        <AuthSubmitButton loading={submitting} loadingText="Updating password...">
          Reset password
        </AuthSubmitButton>
      </form>
      <AuthBackLink />
    </div>
  );
}
