import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const payrollService = {
  generate(payload) {
    return axiosClient.post('/api/payrolls/generate', payload).then(unwrapWithMessage);
  },
  credit(id) {
    return axiosClient.post(`/api/payrolls/${id}/credit`).then(unwrapWithMessage);
  },
  list() {
    return axiosClient.get('/api/payrolls').then(unwrap);
  },
  getHistory() {
    return axiosClient.get('/api/payrolls/history').then(unwrap);
  },
  getSummary() {
    return axiosClient.get('/api/payrolls/summary').then(unwrap);
  },
  getReport(params) {
    return axiosClient.get('/api/payrolls/report', { params }).then(unwrap);
  },
  getDetails(id) {
    return axiosClient.get(`/api/payrolls/${id}`).then(unwrap);
  },
};

export default payrollService;
