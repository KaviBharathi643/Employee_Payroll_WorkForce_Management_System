import axiosClient from '../api/axiosClient';
import { downloadBlob, unwrap } from '../api/apiResponse';

const payslipService = {
  list() {
    return axiosClient.get('/api/payslips').then(unwrap);
  },
  getDetails(id) {
    return axiosClient.get(`/api/payslips/${id}`).then(unwrap);
  },
  download(id, fallbackName = 'payslip.pdf') {
    return axiosClient
      .get(`/api/payslips/${id}/download`, { responseType: 'blob' })
      .then((response) => downloadBlob(response, fallbackName));
  },
};

export default payslipService;
