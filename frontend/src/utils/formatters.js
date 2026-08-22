export function formatDate(value) {
  if (!value) {
    return '—';
  }
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

export function formatCurrency(value) {
  if (value == null || value === '') {
    return '—';
  }
  const amount = Number(value);
  if (Number.isNaN(amount)) {
    return value;
  }
  return amount.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

export function formatLabel(value) {
  if (!value) {
    return '—';
  }
  return value
    .toString()
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}

export function formatDateTime(value) {
  if (!value) {
    return '—';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
}

export function toIsoDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function currentMonthRange() {
  const today = new Date();
  const from = new Date(today.getFullYear(), today.getMonth(), 1);
  return {
    fromDate: toIsoDate(from),
    toDate: toIsoDate(today),
  };
}

export function todayIsoDate() {
  return toIsoDate(new Date());
}

export function isWeekend(date = new Date()) {
  const day = date.getDay();
  return day === 0 || day === 6;
}

export function formatMonthYear(year, month) {
  if (!year || !month) {
    return '—';
  }
  const date = new Date(year, month - 1, 1);
  if (Number.isNaN(date.getTime())) {
    return `${month}/${year}`;
  }
  return date.toLocaleDateString(undefined, { month: 'long', year: 'numeric' });
}

export function currentPayrollPeriod() {
  const now = new Date();
  return {
    payrollYear: now.getFullYear(),
    payrollMonth: now.getMonth() + 1,
  };
}

/** @param {string} [value] LocalTime as HH:mm:ss or HH:mm */
export function toTimeInputValue(value) {
  if (!value) {
    return '';
  }
  return value.length >= 5 ? value.slice(0, 5) : value;
}

/** @param {string} value from input type="time" */
export function timeInputToApi(value) {
  if (!value) {
    return value;
  }
  return value.length === 5 ? `${value}:00` : value;
}

/** Format 10-digit phone number as (XXX) XXX-XXXX or standard grouped string */
export function formatPhoneNumber(phone) {
  if (!phone) return '—';
  const cleaned = phone.replace(/\D/g, '');
  if (cleaned.length === 10) {
    return `(${cleaned.slice(0, 3)}) ${cleaned.slice(3, 6)}-${cleaned.slice(6)}`;
  }
  return phone;
}

/** Get up to two capital initials from a full name */
export function getInitials(name) {
  if (!name) return '??';
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((word) => word.charAt(0).toUpperCase())
    .join('');
}

/**
 * Format bytes into human-readable file size string (e.g. "1.5 MB", "420 KB")
 * @param {number} bytes
 * @param {number} [decimals=1]
 */
export function formatFileSize(bytes, decimals = 1) {
  if (!bytes || bytes === 0) return '0 B';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
}


