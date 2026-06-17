import { ROLES } from './constants';

const PORTAL_BASE = {
  [ROLES.EMPLOYEE]: '/employee',
  [ROLES.HR]: '/hr',
  [ROLES.ADMIN]: '/admin',
};

/**
 * Resolve in-app path for a notification's related entity.
 * @param {string} role
 * @param {string} [relatedEntityType]
 * @param {number | null} [relatedEntityId]
 */
export function resolveNotificationPath(role, relatedEntityType, relatedEntityId) {
  const base = PORTAL_BASE[role];
  if (!base || !relatedEntityType) {
    return `${base || '/employee'}/notifications`;
  }

  const paths = {
    LEAVE: `${base}/leaves`,
    PAYROLL: `${base}/payroll`,
    ATTENDANCE: `${base}/attendance`,
    PROFILE: `${base}/profile`,
    PAYSLIP: role === ROLES.EMPLOYEE ? `${base}/payslips` : `${base}/payroll`,
  };

  const path = paths[relatedEntityType] || `${base}/notifications`;
  if (relatedEntityId != null && relatedEntityType !== 'PROFILE') {
    return path;
  }
  return path;
}

export function notificationsPathForRole(role) {
  const base = PORTAL_BASE[role];
  return base ? `${base}/notifications` : '/login';
}
