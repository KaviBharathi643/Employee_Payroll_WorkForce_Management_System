const EMAIL_KEY = 'payroll_recovery_email';
const OTP_KEY = 'payroll_recovery_otp';

export function saveRecoverySession(email, otpCode) {
  sessionStorage.setItem(EMAIL_KEY, email);
  if (otpCode) {
    sessionStorage.setItem(OTP_KEY, otpCode);
  }
}

export function getRecoverySession() {
  return {
    email: sessionStorage.getItem(EMAIL_KEY) || '',
    otpCode: sessionStorage.getItem(OTP_KEY) || '',
  };
}

export function clearRecoverySession() {
  sessionStorage.removeItem(EMAIL_KEY);
  sessionStorage.removeItem(OTP_KEY);
}
