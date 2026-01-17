import axios from 'axios';

// API base URL - points to the API Gateway
// In production, uses same host as the app; in development, falls back to localhost
const getApiBaseUrl = (): string => {
  // Check for window config (can be set by root-config)
  if (typeof window !== 'undefined' && (window as any).PILOTQUIZ_API_URL) {
    return (window as any).PILOTQUIZ_API_URL;
  }
  // Use same origin with port 8888 for production
  if (typeof window !== 'undefined' && window.location.hostname !== 'localhost') {
    return `http://${window.location.hostname}:8888`;
  }
  // Default to localhost for development
  return 'http://localhost:8888';
};

const API_BASE_URL = getApiBaseUrl();

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('pilotquiz_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear token and redirect to login
      localStorage.removeItem('pilotquiz_token');
      localStorage.removeItem('pilotquiz_user');
      window.dispatchEvent(new CustomEvent('pilotquiz:logout'));
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: async (email: string, password: string) => {
    const response = await api.post('/api/v1/auth/login', { email, password });
    return response.data;
  },

  register: async (data: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
  }) => {
    const response = await api.post('/api/v1/auth/register', data);
    return response.data;
  },
};

// User API
export const userApi = {
  getProfile: async () => {
    const response = await api.get('/api/v1/users/profile');
    return response.data;
  },

  updateProfile: async (data: {
    firstName?: string;
    lastName?: string;
    currentRating?: string;
    targetRating?: string;
  }) => {
    const response = await api.put('/api/v1/users/profile', data);
    return response.data;
  },
};

// Progress API
export const progressApi = {
  getAnalytics: async () => {
    const response = await api.get('/api/v1/analytics');
    return response.data;
  },

  getProgress: async () => {
    const response = await api.get('/api/v1/progress');
    return response.data;
  },

  getAttempts: async (page = 0, size = 10) => {
    const response = await api.get(`/api/v1/attempts?page=${page}&size=${size}`);
    return response.data;
  },
};

export default api;
