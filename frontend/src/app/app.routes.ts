import type { Routes } from "@angular/router"
import { Home } from "./components/home/home"
import { CadastroCliente } from "./components/cadastro-cliente/cadastro-cliente"
import { ClienteDetalhesComponent } from "./components/cliente-detalhes/cliente-detalhes"
import { CadastroCartaoComponent } from "./components/cadastro-cartao/cadastro-cartao"
import { RelatorioComponent } from "./components/relatorio/relatorio"
import { LoginComponent } from "./components/login/login"
import { authGuard } from "./auth/auth-guard"

export const routes: Routes = [
  { path: "login", component: LoginComponent },

  { path: "", redirectTo: "/home", pathMatch: "full" },

  { path: "home", component: Home, canActivate: [authGuard] },
  { path: "cadastro-cliente", component: CadastroCliente, canActivate: [authGuard] },
  { path: "cadastro-cliente/:id", component: CadastroCliente, canActivate: [authGuard] },
  { path: "cliente-detalhes/:id", component: ClienteDetalhesComponent, canActivate: [authGuard] },
  { path: "cadastro-cartao", component: CadastroCartaoComponent, canActivate: [authGuard] },
  { path: "relatorio", component: RelatorioComponent, canActivate: [authGuard] },

  { path: "**", redirectTo: "/home" },
]
