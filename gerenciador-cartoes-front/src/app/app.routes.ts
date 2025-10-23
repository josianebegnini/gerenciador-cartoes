import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { CadastroCliente } from './components/cadastro-cliente/cadastro-cliente';
import { CadastroCartaoComponent } from './components/cadastro-cartao/cadastro-cartao';

export const routes: Routes = [
  { path: 'home', component: Home },
  { path: 'cadastro-cliente', component: CadastroCliente },
  { path: 'cadastro-cartao', component: CadastroCartaoComponent },
  { path: '**', redirectTo: 'home' },
];
