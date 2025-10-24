import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuLateral } from "../menu-lateral/menu-lateral";
import { ClienteService } from '../../service/cliente';
import { Router } from '@angular/router';
import { Cliente } from '../../models/cliente';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-novo-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuLateral],
  templateUrl: './cadastro-cliente.html',
  styleUrls: ['./cadastro-cliente.css']
})
export class CadastroCliente implements OnDestroy {
  constructor(private clienteService: ClienteService, private router: Router) {}

  cliente: Cliente = {
    nome: '',
    email: '',
    dataNasc: '',
    cpf: '',
    selecionado: false,
    endereco: {
      cidade: '',
      bairro: '',
      rua: '',
      cep: '',
      complemento: '',
      numero: ''
    }
  };

  isLoading = false;
  mensagem = '';
  mensagemTipo: 'success' | 'error' | '' = '';

  private destroy$ = new Subject<void>();

  novoCliente(): void {
    this.router.navigate(['/cadastro-cliente']);
  }

  onNavegarHome(): void {
    this.router.navigate(['/home']);
  }

  onNavegarCartoes(): void {
     this.router.navigate(['cadastro-cartao']);
  }

  onNavegarRelatorios(): void {
  }

  formatarCPF(event: Event): void {
    const input = event.target as HTMLInputElement;
    let valor = input.value.replace(/\D/g, '');

    if (valor.length <= 11) {
      valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
      valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
      valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      this.cliente.cpf = valor;
    }
  }

  formatarCEP(event: Event): void {
    const input = event.target as HTMLInputElement;
    let valor = input.value.replace(/\D/g, '');

    if (valor.length <= 8) {
      valor = valor.replace(/(\d{5})(\d)/, '$1-$2');
      this.cliente.endereco.cep = valor;
    }
  }

  validarFormulario(): boolean {
    if (!this.cliente.nome.trim()) {
      this.mostrarMensagem('Nome é obrigatório', 'error');
      return false;
    }

    if (!this.cliente.cpf.trim()) {
      this.mostrarMensagem('CPF é obrigatório', 'error');
      return false;
    }

    const cpfLimpo = this.cliente.cpf.replace(/\D/g, '');
    if (cpfLimpo.length !== 11) {
      this.mostrarMensagem('CPF inválido', 'error');
      return false;
    }

    if (!this.cliente.email.trim()) {
      this.mostrarMensagem('Email é obrigatório', 'error');
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.cliente.email)) {
      this.mostrarMensagem('Email inválido', 'error');
      return false;
    }

    if (!this.cliente.dataNasc) {
      this.mostrarMensagem('Data de nascimento é obrigatória', 'error');
      return false;
    }

    if (!this.cliente.endereco.rua.trim()) {
      this.mostrarMensagem('Rua é obrigatória', 'error');
      return false;
    }

    if (!this.cliente.endereco.cidade.trim()) {
      this.mostrarMensagem('Cidade é obrigatória', 'error');
      return false;
    }

    if (!this.cliente.endereco.bairro.trim()) {
      this.mostrarMensagem('Bairro é obrigatório', 'error');
      return false;
    }

    if (!this.cliente.endereco.cep.trim()) {
      this.mostrarMensagem('CEP é obrigatório', 'error');
      return false;
    }

    const cepLimpo = this.cliente.endereco.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.mostrarMensagem('CEP inválido', 'error');
      return false;
    }

    if (!this.cliente.endereco.numero.trim()) {
      this.mostrarMensagem('Número é obrigatório', 'error');
      return false;
    }

    return true;
  }

  criarConta(): void {
    if (!this.validarFormulario()) {
      return;
    }

    this.isLoading = true;
    this.mensagem = '';

    this.clienteService.createCliente(this.cliente)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cliente) => {
          console.log('Cliente criado:', cliente);
          this.mostrarMensagem('Cliente cadastrado com sucesso!', 'success');
          this.isLoading = false;

          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 1500);
        },
        error: (error) => {
          console.error('Erro ao criar cliente:', error);
          this.mostrarMensagem('Erro ao cadastrar cliente. Tente novamente.', 'error');
          this.isLoading = false;
        }
      });
  }

  cancelar(): void {
    this.router.navigate(['/home']);
  }

  private mostrarMensagem(texto: string, tipo: 'success' | 'error'): void {
    this.mensagem = texto;
    this.mensagemTipo = tipo;

    setTimeout(() => {
      this.mensagem = '';
      this.mensagemTipo = '';
    }, 5000);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
