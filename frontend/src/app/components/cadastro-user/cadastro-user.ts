import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-cadastro-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-user.html',
  styleUrls: ['./cadastro-user.css']
})

export class CadastroUserComponent {
  cadastroForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.cadastroForm = this.fb.group({
      nomeCompleto: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', Validators.required],
    });
  }

  // ========== Verifica se as senhas são diferentes ========== //
  senhasDiferentes(): boolean {
    const senha = this.cadastroForm.get('senha')?.value;
    const confirmar = this.cadastroForm.get('confirmarSenha')?.value;
    return senha && confirmar && senha !== confirmar;
  }

  // ========== Envia os dados para o backend ========== //
  onSubmit(): void {
    if (this.cadastroForm.invalid || this.senhasDiferentes()) {
      this.cadastroForm.markAllAsTouched();
      return;
    }

    const dados = this.cadastroForm.value;

    // Requisição de registro usando AuthService
    this.authService.register({
      username: dados.nomeCompleto,
      password: dados.senha,
      email: dados.email
    }).subscribe({
      next: (res) => {
        console.log('✅ Cadastro realizado com sucesso:', res);
        alert('Conta criada com sucesso! Faça login para continuar.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('❌ Erro no cadastro:', err);
        alert('Erro ao cadastrar. Tente novamente mais tarde.');
      }
    });
  }

  // ========== NAVEGAÇÃO ========== //
  voltarLogin(): void {
    this.router.navigate(['/login']);
  }
}
