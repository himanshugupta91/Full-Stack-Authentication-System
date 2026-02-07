import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
// Create axios instance with default config
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request Interceptor
 * Automatically adds the Bearer token from localStorage to every request.
 */
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

/**
 * Response Interceptor
 * Handles global responses. If a 401 Unauthorized is received, logs the user out.
 */
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

/**
 * Authentication API Service
 * Handles all public authentication-related endpoints.
 */
export const authAPI = {
  /** Register a new user account */
  register: (data) => api.post('/auth/register', data),

  /** Verify email using OTP */
  verifyOtp: (data) => api.post('/auth/verify-otp', data),

  /** Login with email and password */
  login: (data) => api.post('/auth/login', data),

  /** Request a password reset link via email */
  resetPassword: (data) => api.post('/auth/reset-password', data),

  /** Update password using a reset token */
  updatePassword: (data) => api.post('/auth/update-password', data),

  /** Resend OTP for verification */
  resendOtp: (email) => api.post(`/auth/resend-otp?email=${email}`),
};

/**
 * User API Service
 * Handles protected user-related endpoints.
 */
export const userAPI = {
  /** Get user dashboard data */
  getDashboard: () => api.get('/user/dashboard'),

  /** Get user profile details */
  getProfile: () => api.get('/user/profile'),

  /** Change current user's password */
  changePassword: (data) => api.post('/user/change-password', data),
};

/**
 * Admin API Service
 * Handles protected admin-only endpoints.
 */
export const adminAPI = {
  /** Get admin dashboard statistics */
  getDashboard: () => api.get('/admin/dashboard'),

  /** Get list of all registered users */
  getUsers: () => api.get('/admin/users'),
};

export default api;
