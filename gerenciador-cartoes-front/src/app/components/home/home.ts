import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../models/cliente';
import { ClienteService} from '../../service/cliente';
import { Subject, takeUntil } from 'rxjs';
import { ClienteDetalhesComponent } from '../cliente-detalhes/cliente-detalhes';
import { MenuLateral } from '../menu-lateral/menu-lateral';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteDetalhesComponent, MenuLateral],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})

export class Home implements OnInit, OnDestroy{
  filtroCpf = '';
  filtroNome = '';
  clientes: Cliente[] = [];

  clienteSelecionadoDetalhes: Cliente | null = null;
  modalDetalhesAberto: boolean = false;

  private destroy$ = new Subject<void>();

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.carregarClientes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarClientes(): void {
    this.clienteService.getClientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          this.clientes = clientes;
        },
        error: (error) => {
          console.error('Erro ao carregar clientes:', error);
        }
      });
  }

  get clientesFiltrados(): Cliente[] {
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
    const idsSelecionados = this.clientes
      .filter(cliente => cliente.selecionado)
      .map(cliente => cliente.id);

    if (idsSelecionados.length === 0) {
      alert('Nenhum cliente selecionado');
      return;
    }

    const statusTexto = novoStatus === 'ativo' ? 'Ativo' :
                        novoStatus === 'bloqueado' ? 'Bloqueado' : 'Pendente';

    if (confirm(`Deseja alterar o status de ${idsSelecionados.length} cliente(s) para ${statusTexto}?`)) {
      this.clienteService.updateStatusEmLote(idsSelecionados, novoStatus)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (clientesAtualizados) => {
            console.log('Status alterado para:', clientesAtualizados);
          },
          error: (error) => {
            console.error('Erro ao alterar status:', error);
          }
        });
    }
  }

  alternarStatus(cliente: Cliente): void {
    this.clienteService.alternarStatus(cliente.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clienteAtualizado) => {
          console.log('Status alterado:', clienteAtualizado);
        },
        error: (error) => {
          console.error('Erro ao alterar status:', error);
        }
      });
  }

  verDetalhes(cliente: Cliente): void {
    this.clienteSelecionadoDetalhes = cliente;
    this.modalDetalhesAberto = true;
  }

  fecharDetalhes(): void {
    this.modalDetalhesAberto = false;
    this.clienteSelecionadoDetalhes = null;
  }

  getStatusLabel(status: string): string {
    if (status === 'ativo') return 'Ativo';
    if (status === 'bloqueado') return 'Bloqueado';
    return 'Pendente';
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      'ativo': 'status-ativo',
      'bloqueado': 'status-bloqueado',
      'pendente': 'status-pendente'
    };
    return classes[status] || '';
  }

  novoCliente(): void {
    const novoCliente = {
      nome: 'Novo Cliente',
      cpf: '000.000.000-00',
      ultimosDigitos: '0000',
      status: 'pendente' as const,
      selecionado: false
    };

    this.clienteService.createCliente(novoCliente)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cliente) => {
          console.log('Cliente criado:', cliente);
        },
        error: (error) => {
          console.error('Erro ao criar cliente:', error);
        }
      });
  }

  segundaVia(cliente: Cliente): void {
    alert(`Solicitando 2ª via do cartão para ${cliente.nome}`);
  }

  excluirCliente(cliente: Cliente): void {
    if (confirm(`Deseja realmente excluir ${cliente.nome}?`)) {
      this.clienteService.deleteCliente(cliente.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('Cliente excluído com sucesso');
            alert('Cliente excluído com sucesso!');
          },
          error: (error) => {
            console.error('Erro ao excluir cliente:', error);
          }
        });
    }
  }

  onNavegarHome(): void {
    window.location.reload()
  }

  onNavegarCartoes(): void {
    console.log("Navegando para Cartões")
  }

  onNavegarRelatorios(): void {
    console.log("Navegando para Relatórios")
  }
}
