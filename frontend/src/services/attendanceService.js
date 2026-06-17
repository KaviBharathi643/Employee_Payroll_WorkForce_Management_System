import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const attendanceService = {
  checkIn() {
    return axiosClient.post('/api/attendance/check-in').then(unwrapWithMessage);
  },
  checkOut() {
    return axiosClient.post('/api/attendance/check-out').then(unwrapWithMessage);
  },
  getMyAttendance(params) {
    return axiosClient.get('/api/attendance/me', { params }).then(unwrap);
  },
  getEmployeeAttendance(userId, params) {
    return axiosClient.get(`/api/attendance/employee/${userId}`, { params }).then(unwrap);
  },
  getSummary(params) {
    return axiosClient.get('/api/attendance/summary', { params }).then(unwrap);
  },
  getReport(params) {
    return axiosClient.get('/api/attendance/report', { params }).then(unwrap);
  },
};

export default attendanceService;
