import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import { BehaviorSubject, type Observable, tap } from "rxjs"
import type { LoginRequest, LoginResponse, User } from "../models/auth.models"

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = "http://localhost:8085/api/auth"

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage())
  public currentUser$ = this.currentUserSubject.asObservable()

  constructor(private http: HttpClient) {}

  // ========== OPERAÇÕES DE AUTENTICAÇÃO ==========

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        this.setSession(response)
      }),
    )
  }

  register(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/register`, credentials).pipe(
      tap((response) => {
        this.setSession(response)
      }),
    )
  }

  logout(): void {
    localStorage.removeItem("token")
    localStorage.removeItem("user")
    this.currentUserSubject.next(null)
  }

  // ========== GERENCIAMENTO DE SESSÃO ==========

  private setSession(authResult: LoginResponse): void {
    localStorage.setItem("token", authResult.token)

    const user: User = {
      username: authResult.username,
      roles: authResult.roles,
    }

    localStorage.setItem("user", JSON.stringify(user))
    this.currentUserSubject.next(user)
  }

  private getUserFromStorage(): User | null {
    const userJson = localStorage.getItem("user")
    if (userJson) {
      try {
        return JSON.parse(userJson)
      } catch {
        return null
      }
    }
    return null
  }

  // ========== VERIFICAÇÕES DE AUTENTICAÇÃO ==========

  isLoggedIn(): boolean {
    return !!this.getToken()
  }

  getToken(): string | null {
    return localStorage.getItem("token")
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser()
    return user?.roles?.includes(role) ?? false
  }

  // ========== VALIDAÇÃO ==========

  validarCredenciais(username: string, password: string): { valido: boolean; erros: string[] } {
    const erros: string[] = []

    if (!username || !username.trim()) {
      erros.push("Usuário é obrigatório")
    }

    if (!password || !password.trim()) {
      erros.push("Senha é obrigatória")
    } else if (password.length < 6) {
      erros.push("Senha deve ter no mínimo 6 caracteres")
    }

    return {
      valido: erros.length === 0,
      erros,
    }
  }
}
