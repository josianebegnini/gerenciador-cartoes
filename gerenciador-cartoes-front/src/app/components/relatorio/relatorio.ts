import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../service/cliente';
import { CartaoService } from '../../service/cartao';
import { Cliente } from '../../models/cliente';
import { Cartao } from '../../models/cartao';
import { Subject, combineLatest, takeUntil } from 'rxjs';
import { MenuLateral } from '../menu-lateral/menu-lateral';
import { jsPDF } from 'jspdf';

interface ClienteComCartao extends Cliente {
  cartao?: Cartao;
}

@Component({
  selector: 'app-relatorio',
  standalone: true,
  imports: [CommonModule, FormsModule, MenuLateral],
  templateUrl: './relatorio.html',
  styleUrls: ['./relatorio.css']
})
export class RelatorioComponent implements OnInit, OnDestroy {
  filtroCpf = '';
  filtroNome = '';
  filtroStatus = 'todos';

  clientes: ClienteComCartao[] = [];
  cartoes: Cartao[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService
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
      .subscribe(([clientes, cartoes]) => {
        this.cartoes = cartoes;
        this.clientes = clientes.map(cliente => ({
          ...cliente,
          cartao: cartoes.find(c => c.clienteId === cliente.id)
        }));
      });
  }

  get clientesFiltrados(): ClienteComCartao[] {
    return this.clientes.filter(c => {
      const cpfMatch = !this.filtroCpf || c.cpf.includes(this.filtroCpf);
      const nomeMatch = !this.filtroNome || c.nome.toLowerCase().includes(this.filtroNome.toLowerCase());
      const statusMatch =
        this.filtroStatus === 'todos' || c.cartao?.status === this.filtroStatus;
      return cpfMatch && nomeMatch && statusMatch;
    });
  }

  getStatusLabel(status: string | undefined): string {
    if (!status) return 'Sem Cartão';
    return status;
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return 'status-sem-cartao';
    const classes: Record<string, string> = {
      'ativado': 'status-ativado',
      'bloqueado': 'status-bloqueado',
      'desativado': 'status-desativado',
      'cancelado': 'status-cancelado'
    };
    return classes[status] || 'status-sem-cartao';
  }

  exportarPDF(): void {
    const doc = new jsPDF();
    doc.text('Relatório de Clientes', 10, 10);
    let y = 20;
    this.clientesFiltrados.forEach(c => {
      doc.text(`${c.nome} | ${c.email} | ${c.cpf} | ${c.cartao?.status ?? 'Sem Cartão'}`, 10, y);
      y += 8;
    });
    doc.save('relatorio-clientes.pdf');
  }

  exportarXML(): void {
    const xml = this.clientesFiltrados.map(c => `
      <cliente>
        <nome>${c.nome}</nome>
        <email>${c.email}</email>
        <cpf>${c.cpf}</cpf>
        <status>${c.cartao?.status ?? 'Sem Cartão'}</status>
      </cliente>
    `).join('');

    const blob = new Blob([`<clientes>${xml}</clientes>`], { type: 'application/xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'relatorio-clientes.xml';
    a.click();
    URL.revokeObjectURL(url);
  }

  onNavegarHome(): void {
    window.location.href = '/home';
  }
}
