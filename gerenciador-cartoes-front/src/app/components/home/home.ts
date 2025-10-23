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
          console.log('[v0] Cartões carregados:', cartoes);
          this.cartoes = cartoes;
          this.clientes = clientes.map(cliente => {
            const cartaoEncontrado = cartoes.find(c => c.clienteId === cliente.id);
            console.log(`[v0] Cliente ${cliente.id} - Cartão encontrado:`, cartaoEncontrado);
            return {
              ...cliente,
              cartao: cartaoEncontrado
            };
          });
          console.log('[v0] Clientes com cartões:', this.clientes);
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

  mudarStatus(novoStatus: 'ativo' | 'bloqueado' | 'pendente'): void {
    const clientesSelecionados = this.clientes.filter(cliente => cliente.selecionado);
    const idsComCartao = clientesSelecionados
      .filter(cliente => cliente.cartao && cliente.id)
      .map(cliente => cliente.id!);

    if (idsComCartao.length === 0) {
      alert('Nenhum cliente com cartão selecionado');
      return;
    }

    const statusTexto = novoStatus === 'ativo' ? 'Ativo' :
                        novoStatus === 'bloqueado' ? 'Bloqueado' : 'Pendente';

    if (confirm(`Deseja alterar o status de ${idsComCartao.length} cartão(ões) para ${statusTexto}?`)) {
      this.cartaoService.updateStatusEmLote(idsComCartao, novoStatus)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('Status alterado com sucesso');
            this.clientes.forEach(cliente => cliente.selecionado = false);
          },
          error: (error) => {
            console.error('Erro ao alterar status:', error);
            alert('Erro ao alterar status dos cartões');
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
        next: () => {
          console.log('Status alterado com sucesso');
        },
        error: (error) => {
          console.error('Erro ao alterar status:', error);
          alert('Erro ao alterar status do cartão');
        }
      });
  }

 verDetalhes(cliente: ClienteComCartao): void {
    console.log('[v0] Abrindo detalhes do cliente:', cliente);
    console.log('[v0] Cartão do cliente:', cliente.cartao);

    this.clienteSelecionadoDetalhes = cliente;

    // Busca o cartão diretamente do array de cartões para garantir que está atualizado
    if (cliente.id) {
      const cartaoAtualizado = this.cartoes.find(c => c.clienteId === cliente.id);
      this.cartaoSelecionado = cartaoAtualizado || null;
      console.log('[v0] Cartão selecionado:', this.cartaoSelecionado);
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

  getStatusLabel(status: string | undefined): string {
    if (!status) return 'Sem Cartão';
    if (status === 'ativo') return 'Ativo';
    if (status === 'bloqueado') return 'Bloqueado';
    if (status === 'pendente') return 'Pendente';
    return 'Sem Cartão';
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return 'status-sem-cartao';
    const classes: Record<string, string> = {
      'ativo': 'status-ativo',
      'bloqueado': 'status-bloqueado',
      'pendente': 'status-pendente'
    };
    return classes[status] || 'status-sem-cartao';
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
          next: () => {
            alert('2ª via solicitada com sucesso!');
          },
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
          next: () => {
            alert('Cliente excluído com sucesso!');
          },
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
    this.router.navigate(['cadastro-cartao']);
  }

  onNavegarRelatorios(): void {
  }
}
