import type { Routes } from "@angular/router"
import { Home } from "./components/home/home"
import { CadastroCliente } from "./components/cadastro-cliente/cadastro-cliente"
import { ClienteDetalhesComponent } from "./components/cliente-detalhes/cliente-detalhes"
import { CadastroCartaoComponent } from "./components/cadastro-cartao/cadastro-cartao"
import { CadastroUserComponent } from "./components/cadastro-user/cadastro-user"
import { RelatorioComponent } from "./components/relatorio/relatorio"
import { LoginComponent } from "./components/login/login"
import { authGuard } from "./auth/auth-guard"

export const routes: Routes = [
  { path: "login", component: LoginComponent },

  { path: "***", redirectTo: "/login", pathMatch: "full" },
  { path: "home", component: Home},
  { path: "cadastro-user", component: CadastroUserComponent},
  { path: "cadastro-cliente", component: CadastroCliente},
  { path: "cadastro-cliente/:id", component: CadastroCliente},
  { path: "cliente-detalhes/:id", component: ClienteDetalhesComponent},
  { path: "cadastro-cartao", component: CadastroCartaoComponent},
  { path: "relatorio", component: RelatorioComponent}
]
