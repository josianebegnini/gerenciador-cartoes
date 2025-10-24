import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { CadastroCliente } from './components/cadastro-cliente/cadastro-cliente';
import { CadastroCartaoComponent } from './components/cadastro-cartao/cadastro-cartao';
import { LoginComponent } from './components/login/login';
import { CadastroUserComponent } from './components/cadastro-user/cadastro-user';
import { RelatorioComponent } from './components/relatorio/relatorio';


export const routes: Routes = [
   { path: '', component: LoginComponent },
  { path: 'home', component: Home },
  { path: 'cadastro-cliente', component: CadastroCliente },
   { path: 'relatorio', component: RelatorioComponent },
  { path: 'cadastro-cartao', component: CadastroCartaoComponent },
  { path: 'cadastro-user', component: CadastroUserComponent },
  { path: '**', redirectTo: '' }
];
