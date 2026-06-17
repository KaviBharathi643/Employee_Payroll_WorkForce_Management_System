export const ROLES = {
  ADMIN: 'ADMIN',
  HR: 'HR',
  EMPLOYEE: 'EMPLOYEE',
};

export const ROLE_HOME = {
  [ROLES.ADMIN]: '/admin',
  [ROLES.HR]: '/hr',
  [ROLES.EMPLOYEE]: '/employee',
};

export const TOKEN_KEY = 'payroll_auth_token';
export const NOTIFICATION_POLL_MS = 60_000;
export const OTP_LENGTH = 6;
export const OTP_EXPIRY_MINUTES = 2;
export const MIN_PASSWORD_LENGTH = 8;

export const GENDER_OPTIONS = ['MALE', 'FEMALE', 'OTHER'];
export const EMPLOYMENT_TYPE_OPTIONS = ['FULL_TIME', 'INTERN'];
export const LEAVE_TYPE_OPTIONS = ['CASUAL', 'SICK', 'UNPAID'];
export const LEAVE_DURATION_OPTIONS = ['HALF_DAY', 'FULL_DAY', 'MULTI_DAY'];
export const ATTENDANCE_STATUSES = ['PRESENT', 'ABSENT', 'LEAVE', 'HALF_DAY_LEAVE'];
export const LEAVE_STATUSES = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];
export const PAYROLL_STATUSES = ['GENERATED', 'CREDITED'];
export const NOTIFICATION_STATUSES = ['READ', 'UNREAD'];
export const EMPLOYMENT_STATUS_FILTER_OPTIONS = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'ALL', label: 'All' },
];

export const MONTH_OPTIONS = [
  { value: '1', label: 'January' },
  { value: '2', label: 'February' },
  { value: '3', label: 'March' },
  { value: '4', label: 'April' },
  { value: '5', label: 'May' },
  { value: '6', label: 'June' },
  { value: '7', label: 'July' },
  { value: '8', label: 'August' },
  { value: '9', label: 'September' },
  { value: '10', label: 'October' },
  { value: '11', label: 'November' },
  { value: '12', label: 'December' },
];
