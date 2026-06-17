import axiosClient from '../api/axiosClient';
import { unwrap, unwrapWithMessage } from '../api/apiResponse';

const employeeService = {
  getMyProfile() {
    return axiosClient.get('/api/employees/profile').then(unwrap);
  },
  updateMyProfile(payload) {
    return axiosClient.put('/api/employees/profile', payload).then(unwrapWithMessage);
  },
  updateBankDetails(payload) {
    return axiosClient.put('/api/employees/bank-details', payload).then(unwrapWithMessage);
  },
  listEmployees(params) {
    return axiosClient.get('/api/employees', { params }).then(unwrap);
  },
  getEmployee(id) {
    return axiosClient.get(`/api/employees/${id}`).then(unwrap);
  },
  createEmployee(payload) {
    return axiosClient.post('/api/employees', payload).then(unwrapWithMessage);
  },
  updateEmployee(id, payload) {
    return axiosClient.put(`/api/employees/${id}`, payload).then(unwrapWithMessage);
  },
  deactivateEmployee(id) {
    return axiosClient.patch(`/api/employees/${id}/deactivate`).then(unwrapWithMessage);
  },
  listHr() {
    return axiosClient.get('/api/employees/hr').then(unwrap);
  },
  getHr(id) {
    return axiosClient.get(`/api/employees/hr/${id}`).then(unwrap);
  },
  createHr(payload) {
    return axiosClient.post('/api/employees/hr', payload).then(unwrapWithMessage);
  },
  updateHr(id, payload) {
    return axiosClient.put(`/api/employees/hr/${id}`, payload).then(unwrapWithMessage);
  },
  deactivateHr(id) {
    return axiosClient.patch(`/api/employees/hr/${id}/deactivate`).then(unwrapWithMessage);
  },
};

export default employeeService;
