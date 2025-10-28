import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { CadastroCliente } from './components/cadastro-cliente/cadastro-cliente';
import { CadastroCartaoComponent } from './components/cadastro-cartao/cadastro-cartao';
import { LoginComponent } from './components/login/login';
import { CadastroUserComponent } from './components/cadastro-user/cadastro-user';
import { RelatorioComponent } from './components/relatorio/relatorio';
import { authGuard } from './auth/auth-guard';


export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: 'home', component: Home, canActivate: [authGuard]  },
  { path: 'cadastro-cliente', component: CadastroCliente, canActivate: [authGuard]  },
  { path: 'relatorio', component: RelatorioComponent, canActivate: [authGuard]  },
  { path: 'cadastro-cartao', component: CadastroCartaoComponent, canActivate: [authGuard]  },
  { path: 'cadastro-user', component: CadastroUserComponent, canActivate: [authGuard]  },
  { path: '**', redirectTo: '/login', pathMatch: 'full' },
  { path: "**", redirectTo: "/home" },
];
