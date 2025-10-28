import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cadastro-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-user.html',
  styleUrls: ['./cadastro-user.css']
})
export class CadastroUserComponent {
  cadastroForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', Validators.required],
    });
  }

  senhasDiferentes(): boolean {
    const { senha, confirmarSenha } = this.cadastroForm.value;
    return senha && confirmarSenha && senha !== confirmarSenha;
  }

  onSubmit() {
    if (this.cadastroForm.valid && !this.senhasDiferentes()) {
      console.log('Usuário cadastrado com sucesso!', this.cadastroForm.value);
      this.router.navigate(['/']);
    } else {
      this.cadastroForm.markAllAsTouched();
    }
  }

  voltarLogin() {
    this.router.navigate(['/']);
  }
}
