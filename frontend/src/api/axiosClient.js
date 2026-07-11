import axios from 'axios';
import { clearToken, getToken } from '../utils/storage';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '';
console.log("API URL:", import.meta.env.VITE_API_BASE_URL);

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const PUBLIC_AUTH_PATHS = ['/login', '/forgot-password', '/reset-password'];

function isPublicAuthPath(pathname) {
  return PUBLIC_AUTH_PATHS.some((path) => pathname === path || pathname.startsWith(`${path}/`));
}

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      if (!isPublicAuthPath(window.location.pathname)) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);

export default axiosClient;
