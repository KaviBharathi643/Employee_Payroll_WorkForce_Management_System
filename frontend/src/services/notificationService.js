import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const notificationService = {
  getLatest() {
    return axiosClient.get('/api/notifications').then(unwrap);
  },
  getHistory() {
    return axiosClient.get('/api/notifications/history').then(unwrap);
  },
  getBell() {
    return axiosClient.get('/api/notifications/bell').then(unwrap);
  },
  markAsRead(id) {
    return axiosClient.put(`/api/notifications/${id}/read`).then(unwrap);
  },
  markAllAsRead() {
    return axiosClient.put('/api/notifications/read-all').then(unwrapWithMessage);
  },
};

export default notificationService;
