import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthAlert from '../../components/auth/AuthAlert';
import AuthBackLink from '../../components/auth/AuthBackLink';
import AuthSubmitButton from '../../components/auth/AuthSubmitButton';
import AuthTextField from '../../components/auth/AuthTextField';
import authService from '../../services/authService';
import { getErrorMessage } from '../../utils/authErrors';
import { OTP_EXPIRY_MINUTES, OTP_LENGTH } from '../../utils/constants';
import { saveRecoverySession } from '../../utils/passwordResetStorage';

export default function ForgotPasswordPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState('email');
  const [email, setEmail] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [info, setInfo] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSendOtp = async (event) => {
    event.preventDefault();
    setError('');
    setInfo('');
    setSubmitting(true);
    try {
      const { message } = await authService.forgotPassword(email.trim());
      setInfo(message || 'OTP Sent Successfully');
      setStep('otp');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to send OTP'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleResendOtp = async () => {
    setError('');
    setInfo('');
    setSubmitting(true);
    try {
      const { message } = await authService.forgotPassword(email.trim());
      setInfo(message || 'OTP resent successfully');
      setOtpCode('');
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to resend OTP'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleVerifyOtp = async (event) => {
    event.preventDefault();
    setError('');
    setInfo('');
    setSubmitting(true);
    try {
      const payload = { email: email.trim(), otpCode: otpCode.trim() };
      const { message } = await authService.verifyOtp(payload);
      saveRecoverySession(payload.email, payload.otpCode);
      navigate('/reset-password', {
        replace: true,
        state: {
          email: payload.email,
          otpCode: payload.otpCode,
          notice: message || 'OTP Verified',
        },
      });
    } catch (err) {
      setError(getErrorMessage(err, 'Invalid or expired OTP'));
    } finally {
      setSubmitting(false);
    }
  };

  if (step === 'email') {
    return (
      <div>
        <h2 className="mb-2 text-2xl font-semibold text-slate-900">Forgot password</h2>
        <p className="mb-6 text-sm text-slate-600">
          Enter your account email. We will send a 6-digit OTP valid for {OTP_EXPIRY_MINUTES} minutes.
        </p>
        <form onSubmit={handleSendOtp} className="space-y-4">
          <AuthTextField
            id="email"
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
          />
          {info && <AuthAlert type="success">{info}</AuthAlert>}
          {error && <AuthAlert>{error}</AuthAlert>}
          <AuthSubmitButton loading={submitting} loadingText="Sending OTP...">
            Send OTP
          </AuthSubmitButton>
        </form>
        <AuthBackLink />
      </div>
    );
  }

  return (
    <div>
      <h2 className="mb-2 text-2xl font-semibold text-slate-900">Verify OTP</h2>
      <p className="mb-6 text-sm text-slate-600">
        Enter the 6-digit code sent to <span className="font-medium text-slate-900">{email}</span>.
      </p>
      <form onSubmit={handleVerifyOtp} className="space-y-4">
        <AuthTextField
          id="otpCode"
          label="OTP code"
          value={otpCode}
          onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, '').slice(0, OTP_LENGTH))}
          autoComplete="one-time-code"
          placeholder="000000"
          maxLength={OTP_LENGTH}
          pattern={`\\d{${OTP_LENGTH}}`}
          inputMode="numeric"
          hint={`Expires in ${OTP_EXPIRY_MINUTES} minutes`}
        />
        {info && <AuthAlert type="success">{info}</AuthAlert>}
        {error && <AuthAlert>{error}</AuthAlert>}
        <AuthSubmitButton loading={submitting} loadingText="Verifying...">
          Verify OTP
        </AuthSubmitButton>
      </form>
      <div className="mt-4 flex items-center justify-between text-sm">
        <button
          type="button"
          onClick={() => {
            setStep('email');
            setOtpCode('');
            setError('');
            setInfo('');
          }}
          className="font-medium text-slate-700 hover:underline"
        >
          Change email
        </button>
        <button
          type="button"
          onClick={handleResendOtp}
          disabled={submitting}
          className="font-medium text-slate-900 hover:underline disabled:opacity-60"
        >
          Resend OTP
        </button>
      </div>
      <AuthBackLink />
    </div>
  );
}
