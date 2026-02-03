import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API calls
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  verifyOtp: (data) => api.post('/auth/verify-otp', data),
  login: (data) => api.post('/auth/login', data),
  resetPassword: (data) => api.post('/auth/reset-password', data),
  updatePassword: (data) => api.post('/auth/update-password', data),
  resendOtp: (email) => api.post(`/auth/resend-otp?email=${email}`),
};

// User API calls
export const userAPI = {
  getDashboard: () => api.get('/user/dashboard'),
  getProfile: () => api.get('/user/profile'),
};

// Admin API calls
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getUsers: () => api.get('/admin/users'),
};

export default api;
