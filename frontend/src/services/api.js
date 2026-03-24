import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
const OAUTH_BASE_URL = import.meta.env.VITE_OAUTH_BASE_URL || 'http://localhost:8080';
const IS_BROWSER = typeof window !== 'undefined';
const REFRESH_SKIP_PATHS = [
  '/auth/login',
  '/auth/refresh',
  '/auth/logout',
  '/auth/register',
  '/auth/verify-otp',
  '/auth/reset-password',
  '/auth/update-password',
];

export const USER_KEY = 'user';

let accessToken = null;

export const OAUTH_PROVIDERS = [
  { id: 'google', label: 'Google', iconClass: 'bi bi-google' },
  { id: 'github', label: 'GitHub', iconClass: 'bi bi-github' },
  { id: 'apple', label: 'Apple', iconClass: 'bi bi-apple' },
  { id: 'linkedin', label: 'LinkedIn', iconClass: 'bi bi-linkedin' },
];

export const getOAuthAuthorizationUrl = (providerId) =>
  `${OAUTH_BASE_URL}/oauth2/authorization/${providerId}`;

const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

const readStoredUser = () => {
  if (!IS_BROWSER) {
    return null;
  }

  const raw = localStorage.getItem(USER_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem(USER_KEY);
    return null;
  }
};

export const clearAuthStorage = () => {
  accessToken = null;
  if (IS_BROWSER) {
    localStorage.removeItem(USER_KEY);
  }
};

export const saveAuthPayload = (payload) => {
  if (!payload?.accessToken) {
    clearAuthStorage();
    return;
  }

  accessToken = payload.accessToken;

  const user = {
    id: payload.id,
    name: payload.name,
    email: payload.email,
    enabled: payload.enabled,
    roles: payload.roles || [],
  };
  if (IS_BROWSER) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }
};

let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

const notifyTokenRefreshed = (newToken) => {
  const subscribers = [...refreshSubscribers];
  refreshSubscribers = [];
  subscribers.forEach((callback) => callback(newToken));
};

const shouldSkipRefresh = (url = '') => {
  if (!url) {
    return false;
  }
  return REFRESH_SKIP_PATHS.some((path) => url.includes(path));
};

const hasApiEnvelope = (payload) =>
  payload &&
  typeof payload === 'object' &&
  Object.prototype.hasOwnProperty.call(payload, 'success') &&
  Object.prototype.hasOwnProperty.call(payload, 'data');

const unwrapApiPayload = (payload) => {
  if (!hasApiEnvelope(payload)) {
    return payload;
  }
  return payload.success ? payload.data : payload;
};

api.interceptors.request.use(
  (config) => {
    if (accessToken) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => {
    response.data = unwrapApiPayload(response.data);
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    if (!originalRequest || status !== 401 || originalRequest._retry || shouldSkipRefresh(originalRequest.url)) {
      if (status === 401 && shouldSkipRefresh(originalRequest?.url)) {
        clearAuthStorage();
      }
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        subscribeTokenRefresh((newToken) => {
          if (!newToken) {
            reject(error);
            return;
          }
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          resolve(api(originalRequest));
        });
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const refreshResponse = await api.post('/auth/refresh');
      const refreshPayload = refreshResponse.data;
      if (!refreshPayload?.accessToken) {
        throw new Error('Refresh response missing access token.');
      }

      saveAuthPayload(refreshPayload);
      notifyTokenRefreshed(refreshPayload.accessToken);

      originalRequest.headers = originalRequest.headers || {};
      originalRequest.headers.Authorization = `Bearer ${refreshPayload.accessToken}`;
      return api(originalRequest);
    } catch (refreshError) {
      clearAuthStorage();
      notifyTokenRefreshed(null);
      if (IS_BROWSER && window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  verifyOtp: (data) => api.post('/auth/verify-otp', data),
  login: (data) => api.post('/auth/login', data),
  refresh: () => api.post('/auth/refresh'),
  resetPassword: (data) => api.post('/auth/reset-password', data),
  updatePassword: (data) => api.post('/auth/update-password', data),
  resendOtp: (email) => api.post(`/auth/resend-otp?email=${encodeURIComponent(email)}`),
  logout: () => api.post('/auth/logout'),
};

export const userAPI = {
  getDashboard: () => api.get('/user/dashboard'),
  getProfile: () => api.get('/user/profile'),
  changePassword: (data) => api.post('/user/change-password', data),
};

export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getUsers: (params = {}) => api.get('/admin/users', { params }),
};

export const getStoredUser = readStoredUser;

export default api;
