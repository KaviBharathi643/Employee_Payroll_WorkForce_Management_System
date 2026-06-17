import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const leaveService = {
  applyLeave(payload) {
    return axiosClient.post('/api/leaves', payload).then(unwrapWithMessage);
  },
  approveLeave(id) {
    return axiosClient.put(`/api/leaves/${id}/approve`).then(unwrapWithMessage);
  },
  rejectLeave(id) {
    return axiosClient.put(`/api/leaves/${id}/reject`).then(unwrapWithMessage);
  },
  cancelLeave(id) {
    return axiosClient.put(`/api/leaves/${id}/cancel`).then(unwrapWithMessage);
  },
  convertToUnpaid(id) {
    return axiosClient.put(`/api/leaves/${id}/convert-unpaid`).then(unwrapWithMessage);
  },
  getMyHistory() {
    return axiosClient.get('/api/leaves/my-history').then(unwrap);
  },
  getBalance() {
    return axiosClient.get('/api/leaves/balance').then(unwrap);
  },
  getReport(params) {
    return axiosClient.get('/api/leaves/report', { params }).then(unwrap);
  },
};

export default leaveService;
