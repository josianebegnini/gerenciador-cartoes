import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { LoginComponent } from './components/login/login';
import { CadastroUserComponent } from './components/cadastro-user/cadastro-user';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: Home },
  { path: 'cadastro-user', component: CadastroUserComponent },
  { path: '**', redirectTo: '' }
];
