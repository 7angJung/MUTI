export interface SignupRequest {
  email: string;
  username: string;  // 백엔드는 username 사용
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface User {
  id: number;
  email: string;
  nickname: string;
  createdAt: string;
}