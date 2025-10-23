import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ClienteService } from '../../service/cliente';
import { CartaoService } from '../../service/cartao';
import { Cliente } from '../../models/cliente';
import { Cartao } from '../../models/cartao';
import { MenuLateral } from "../menu-lateral/menu-lateral";
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cadastro-cartao',
  templateUrl: './cadastro-cartao.html',
  styleUrls: ['./cadastro-cartao.css'],
  standalone: true,
  imports: [MenuLateral, FormsModule, CommonModule]
})
export class CadastroCartaoComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  cpfBusca: string = '';
  clienteEncontrado: Cliente | null = null;
  buscandoCliente: boolean = false;
  erroCliente: string = '';

  cartao: Cartao = {
    clienteId: 0,
    numero: '',
    cvv: '',
    dataVencimento: '',
    tipoConta: 'debito',
    status: 'pendente',
    formatoCartao: 'fisico'
  };

  carregando: boolean = false;
  mensagemSucesso: string = '';
  mensagemErro: string = '';

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private router: Router
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

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
      this.cpfBusca = valor;
    }
  }

  buscarClientePorCPF(): void {
    if (!this.cpfBusca || this.cpfBusca.length < 14) {
      this.erroCliente = 'Por favor, digite um CPF válido';
      return;
    }

    this.buscandoCliente = true;
    this.erroCliente = '';
    this.clienteEncontrado = null;

    const cpfLimpo = this.cpfBusca.replace(/\D/g, '');

    this.clienteService.getClientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          const cliente = clientes.find(c => c.cpf.replace(/\D/g, '') === cpfLimpo);

          if (cliente) {
            this.clienteEncontrado = cliente;
            this.cartao.clienteId = cliente.id!;
            this.erroCliente = '';
          } else {
            this.erroCliente = 'Cliente não encontrado. Verifique o CPF ou cadastre o cliente primeiro.';
          }

          this.buscandoCliente = false;
        },
        error: (error) => {
          console.error('Erro ao buscar cliente:', error);
          this.erroCliente = 'Erro ao buscar cliente. Tente novamente.';
          this.buscandoCliente = false;
        }
      });
  }

  formatarNumeroCartao(event: Event): void {
    const input = event.target as HTMLInputElement;
    let valor = input.value.replace(/\D/g, '');

    if (valor.length <= 16) {
      valor = valor.replace(/(\d{4})(?=\d)/g, '$1 ');
      this.cartao.numero = valor;
    }
  }

  formatarCVV(event: Event): void {
    const input = event.target as HTMLInputElement;
    let valor = input.value.replace(/\D/g, '');

    if (valor.length <= 4) {
      this.cartao.cvv = valor;
    }
  }

  formatarDataVencimento(event: Event): void {
    const input = event.target as HTMLInputElement;
    let valor = input.value.replace(/\D/g, '');

    if (valor.length <= 4) {
      if (valor.length >= 2) {
        valor = valor.replace(/(\d{2})(\d)/, '$1/$2');
      }
      this.cartao.dataVencimento = valor;
    }
  }

  validarFormulario(): boolean {
    if (!this.clienteEncontrado) {
      this.mensagemErro = 'Por favor, busque e selecione um cliente válido';
      return false;
    }

    if (!this.cartao.numero || this.cartao.numero.replace(/\D/g, '').length !== 16) {
      this.mensagemErro = 'Número do cartão deve ter 16 dígitos';
      return false;
    }

    if (!this.cartao.cvv || this.cartao.cvv.length < 3) {
      this.mensagemErro = 'CVV deve ter 3 ou 4 dígitos';
      return false;
    }

    if (!this.cartao.dataVencimento || this.cartao.dataVencimento.length !== 5) {
      this.mensagemErro = 'Data de vencimento inválida (MM/AA)';
      return false;
    }

    const mes = parseInt(this.cartao.dataVencimento.substring(0, 2));
    if (mes < 1 || mes > 12) {
      this.mensagemErro = 'Mês inválido na data de vencimento';
      return false;
    }

    if (!this.cartao.tipoConta) {
      this.mensagemErro = 'Selecione o tipo de conta';
      return false;
    }

    if (!this.cartao.formatoCartao) {
      this.mensagemErro = 'Selecione o formato do cartão';
      return false;
    }

    return true;
  }

  criarCartao(): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    if (!this.validarFormulario()) {
      return;
    }

    this.carregando = true;

    this.cartaoService.createCartao(this.cartao)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.mensagemSucesso = 'Cartão cadastrado com sucesso!';
          this.carregando = false;

          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 1000);
        },
        error: (error) => {
          console.error('Erro ao criar cartão:', error);
          this.mensagemErro = 'Erro ao cadastrar cartão. Tente novamente.';
          this.carregando = false;
        }
      });
  }

  cancelar(): void {
    this.cpfBusca = '';
    this.clienteEncontrado = null;
    this.erroCliente = '';
    this.cartao = {
      clienteId: 0,
      numero: '',
      cvv: '',
      dataVencimento: '',
      tipoConta: 'debito',
      status: 'pendente',
      formatoCartao: 'fisico'
    };
    this.mensagemErro = '';
    this.mensagemSucesso = '';
    this.router.navigate(['/home']);
  }
}
