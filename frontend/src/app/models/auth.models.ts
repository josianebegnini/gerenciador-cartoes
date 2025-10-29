export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  roles: string[]
}

export interface User {
  username: string
  roles: string[]
}

export interface CadastroRequest {
  username: string;
  password: string;
  email: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword?: string; 
}
