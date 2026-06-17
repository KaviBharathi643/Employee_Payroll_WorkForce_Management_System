import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const authService = {
  login(credentials) {
    return axiosClient.post('/api/auth/login', credentials).then(unwrap);
  },
  forgotPassword(email) {
    return axiosClient.post('/api/auth/forgot-password', { email }).then(unwrapWithMessage);
  },
  verifyOtp(payload) {
    return axiosClient.post('/api/auth/verify-otp', payload).then(unwrapWithMessage);
  },
  resetPassword(payload) {
    return axiosClient.post('/api/auth/reset-password', payload).then(unwrapWithMessage);
  },
  me() {
    return axiosClient.get('/api/auth/me').then(unwrap);
  },
};

export default authService;
