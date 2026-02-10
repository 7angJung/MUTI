import api from './api';
import type { SignupRequest, LoginRequest, AuthResponse } from '../types/auth';

export const authService = {
  // 회원가입
  signup: async (data: SignupRequest) => {
    const response = await api.post<AuthResponse>('/api/v1/auth/signup', data);
    return response.data;
  },

  // 로그인
  login: async (data: LoginRequest) => {
    const response = await api.post<AuthResponse>('/api/v1/auth/login', data);
    return response.data;
  },

  // 로그아웃
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  },
};

export default authService;