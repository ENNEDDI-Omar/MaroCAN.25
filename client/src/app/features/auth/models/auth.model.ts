export interface User {
  id?: number;
  username: string;
  email: string;
  profileImageUrl?: string;
  roles?: string[];
}


export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}


export interface AuthResponse {
  message: string;
  accessToken: string;
  refreshToken: string;
  success: boolean;
}


export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  accessToken: string | null;
  refreshToken: string | null;
}
