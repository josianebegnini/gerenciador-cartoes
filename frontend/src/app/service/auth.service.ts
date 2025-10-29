import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { environment } from "../enviroments/enviroment";
import type { LoginRequest, LoginResponse, User, RegisterRequest } from "../models/auth.models";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  // ---------- LOGIN ----------
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res) => this.setSession(res))
    );
  }

  // ---------- REGISTER ----------
  register(data: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/register`, data).pipe(
      tap((res) => this.setSession(res))
    );
  }

  // ---------- LOGOUT ----------
  logout(): void {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    this.currentUserSubject.next(null);
  }

  // ---------- GETTERS ----------
  getToken(): string | null {
    return localStorage.getItem("token");
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.includes(role) ?? false;
  }

  // ---------- VALIDATIONS ----------
  validateCredentials(username: string, password: string): { valid: boolean; errors: string[] } {
    const errors: string[] = [];

    if (!username.trim()) errors.push("Usuário é obrigatório");
    if (!password.trim()) errors.push("Senha é obrigatória");
    else if (password.length < 6) errors.push("Senha deve ter no mínimo 6 caracteres");

    return { valid: errors.length === 0, errors };
  }

  // ---------- PRIVATE ----------
  private setSession(authResult: LoginResponse): void {
    const user: User = { username: authResult.username, roles: authResult.roles };

    localStorage.setItem("token", authResult.token);
    localStorage.setItem("user", JSON.stringify(user));

    this.currentUserSubject.next(user);
  }

  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem("user");
    if (!userJson) return null;
    try {
      return JSON.parse(userJson);
    } catch {
      return null;
    }
  }
}
