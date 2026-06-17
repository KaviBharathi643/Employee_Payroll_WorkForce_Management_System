import axiosClient from '../api/axiosClient';
import { downloadBlob, unwrap } from '../api/apiResponse';

const reportService = {
  getDashboard() {
    return axiosClient.get('/api/reports/dashboard').then(unwrap);
  },
  downloadEmployeeReport(params) {
    return axiosClient
      .get('/api/reports/employees', { params, responseType: 'blob' })
      .then((response) => downloadBlob(response, 'employee-report.pdf'));
  },
  downloadAttendanceReport(params) {
    return axiosClient
      .get('/api/reports/attendance', { params, responseType: 'blob' })
      .then((response) => downloadBlob(response, 'attendance-report.pdf'));
  },
  downloadLeaveReport(params) {
    return axiosClient
      .get('/api/reports/leaves', { params, responseType: 'blob' })
      .then((response) => downloadBlob(response, 'leave-report.pdf'));
  },
  downloadPayrollReport(params) {
    return axiosClient
      .get('/api/reports/payroll', { params, responseType: 'blob' })
      .then((response) => downloadBlob(response, 'payroll-report.pdf'));
  },
};

export default reportService;
