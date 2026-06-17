import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const settingsService = {
  getSettings() {
    return axiosClient.get('/api/settings').then(unwrap);
  },
  updateCompany(payload) {
    return axiosClient.put('/api/settings/company', payload).then(unwrapWithMessage);
  },
  updateAttendance(payload) {
    return axiosClient.put('/api/settings/attendance', payload).then(unwrapWithMessage);
  },
  updateLeave(payload) {
    return axiosClient.put('/api/settings/leave', payload).then(unwrapWithMessage);
  },
  updatePayroll(payload) {
    return axiosClient.put('/api/settings/payroll', payload).then(unwrapWithMessage);
  },
};

export default settingsService;
