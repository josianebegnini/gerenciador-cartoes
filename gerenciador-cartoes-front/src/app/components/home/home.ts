import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../models/cliente';
import { Cartao } from '../../models/cartao';
import { ClienteService } from '../../service/cliente';
import { CartaoService } from '../../service/cartao';
import { Subject, takeUntil, combineLatest } from 'rxjs';
import { ClienteDetalhesComponent } from '../cliente-detalhes/cliente-detalhes';
import { MenuLateral } from '../menu-lateral/menu-lateral';
import { Router } from '@angular/router';

interface ClienteComCartao extends Cliente {
  cartao?: Cartao;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteDetalhesComponent, MenuLateral],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit, OnDestroy {
  filtroCpf = '';
  filtroNome = '';
  clientes: ClienteComCartao[] = [];
  cartoes: Cartao[] = [];

  clienteSelecionadoDetalhes: ClienteComCartao | null = null;
  cartaoSelecionado: Cartao | null = null;
  modalDetalhesAberto: boolean = false;

  private destroy$ = new Subject<void>();

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarDados(): void {
    combineLatest([
      this.clienteService.getClientes(),
      this.cartaoService.getCartoes()
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ([clientes, cartoes]) => {
          this.cartoes = cartoes;
          this.clientes = clientes.map(cliente => {
            const cartaoEncontrado = cartoes.find(c => c.clienteId === cliente.id);
            return {
              ...cliente,
              cartao: cartaoEncontrado
            };
          });
        },
        error: (error) => {
          console.error('Erro ao carregar dados:', error);
        }
      });
  }

  get clientesFiltrados(): ClienteComCartao[] {
    return this.clientes.filter(cliente => {
      const matchCpf = !this.filtroCpf || cliente.cpf.includes(this.filtroCpf);
      const matchNome = !this.filtroNome ||
        cliente.nome.toLowerCase().includes(this.filtroNome.toLowerCase());
      return matchCpf && matchNome;
    });
  }

  get temClientesSelecionados(): boolean {
    return this.clientes.some(cliente => cliente.selecionado);
  }

  get quantidadeClientesSelecionados(): number {
    return this.clientes.filter(cliente => cliente.selecionado).length;
  }

  mudarStatus(novoStatus: 'desativado' | 'ativado' | 'bloqueado' | 'cancelado'): void {
    const clientesSelecionados = this.clientes.filter(cliente => cliente.selecionado);
    const idsComCartao = clientesSelecionados
      .filter(cliente => cliente.cartao && cliente.id)
      .map(cliente => cliente.id!);

    if (idsComCartao.length === 0) {
      alert('Nenhum cliente com cartão selecionado');
      return;
    }

    let statusTexto = '';
    switch (novoStatus) {
      case 'ativado':
        statusTexto = 'ativado';
        break;
      case 'bloqueado':
        statusTexto = 'bloqueado';
        break;
      case 'cancelado':
        statusTexto = 'cancelado';
        break;
      case 'desativado':
        statusTexto = 'desativado';
        break;
    }

    if (confirm(`Deseja alterar o status de ${idsComCartao.length} cartão(ões) para ${statusTexto}?`)) {
      this.cartaoService.updateStatusEmLote(idsComCartao, novoStatus)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.clientes.forEach(cliente => {
              if (cliente.selecionado && cliente.cartao) {
                cliente.cartao.status = novoStatus;
              }
              cliente.selecionado = false;
            });
          },
          error: (error) => {
            console.error('Erro ao alterar status:', error);
          }
        });
    }
  }

  alternarStatus(cliente: ClienteComCartao): void {
    if (!cliente.id || !cliente.cartao) {
      alert('Cliente não possui cartão');
      return;
    }

    this.cartaoService.alternarStatus(cliente.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => console.log('Status alterado com sucesso'),
        error: (error) => console.error('Erro ao alterar status:', error)
      });
  }

  verDetalhes(cliente: ClienteComCartao): void {
    this.clienteSelecionadoDetalhes = cliente;

    if (cliente.id) {
      const cartaoAtualizado = this.cartoes.find(c => c.clienteId === cliente.id);
      this.cartaoSelecionado = cartaoAtualizado || null;
    } else {
      this.cartaoSelecionado = null;
    }

    this.modalDetalhesAberto = true;
  }

  fecharDetalhes(): void {
    this.modalDetalhesAberto = false;
    this.clienteSelecionadoDetalhes = null;
    this.cartaoSelecionado = null;
  }

  novoCliente(): void {
    this.router.navigate(['/cadastro-cliente']);
  }

  segundaVia(cliente: ClienteComCartao): void {
    if (!cliente.id || !cliente.cartao) {
      alert('Cliente não possui cartão para solicitar 2ª via');
      return;
    }

    if (confirm(`Deseja solicitar 2ª via do cartão para ${cliente.nome}?`)) {
      this.cartaoService.solicitarSegundaVia(cliente.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => alert('2ª via solicitada com sucesso!'),
          error: (error) => {
            console.error('Erro ao solicitar 2ª via:', error);
            alert('Erro ao solicitar 2ª via do cartão');
          }
        });
    }
  }

  excluirCliente(cliente: ClienteComCartao): void {
    if (!cliente.id) return;

    if (confirm(`Deseja realmente excluir ${cliente.nome}?`)) {
      this.clienteService.deleteCliente(cliente.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => alert('Cliente excluído com sucesso!'),
          error: (error) => {
            console.error('Erro ao excluir cliente:', error);
            alert('Erro ao excluir cliente');
          }
        });
    }
  }

  onNavegarHome(): void {
    this.router.navigate(['/home']);
  }

  onNavegarCartoes(): void {
    this.router.navigate(['/cadastro-cartao']);
  }

  onNavegarRelatorios(): void {
    this.router.navigate(['/relatorio']);
  }

  // 🧩 Adicionados para corrigir o erro no HTML:
  getStatusClass(status?: string): string {
    switch (status) {
      case 'ativado':
        return 'status-ativado';
      case 'bloqueado':
        return 'status-bloqueado';
      case 'cancelado':
        return 'status-cancelado';
      case 'desativado':
        return 'status-desativado';
      default:
        return 'status-desconhecido';
    }
  }

  getStatusLabel(status?: string): string {
    switch (status) {
      case 'ativado':
        return 'Ativado';
      case 'bloqueado':
        return 'Bloqueado';
      case 'cancelado':
        return 'Cancelado';
      case 'desativado':
        return 'Desativado';
      default:
        return 'Sem cartão';
    }
  }
}
