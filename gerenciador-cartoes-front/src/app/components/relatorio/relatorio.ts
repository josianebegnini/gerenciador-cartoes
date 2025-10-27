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
import { Router } from '@angular/router';
import autoTable from 'jspdf-autotable';


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

//PDF
exportarPDF(): void {
  const clientes = this.clientesFiltrados;
  const doc = new jsPDF();


  doc.setFontSize(16);
  doc.setTextColor('#7B2D26');
  doc.text('Relatório de Clientes - Detalhado', 14, 15);
  doc.setFontSize(11);
  doc.setTextColor(0, 0, 0);


  const head = [[
    'ID', 'Nome', 'CPF', 'Email', 'Data Nasc.',
    'Endereço', 'Conta', 'Cartão'
  ]];


  const body = clientes.map((c: any) => [
    c.id ?? '',
    c.nome ?? '',
    c.cpf ?? '',
    c.email ?? '',
    c.dataNasc ?? '',
    c.endereco
      ? `${c.endereco.rua}, ${c.endereco.numero} - ${c.endereco.bairro}, ${c.endereco.cidade}`
      : '---',
    c.conta
      ? `${c.conta.agencia} / ${c.conta.tipo} / R$ ${Number(c.conta.saldo).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`
      : '---',
    c.cartao
      ? `${c.cartao.numero} (${c.cartao.tipoCartao}) - ${c.cartao.status}`
      : '---'
  ]);


  autoTable(doc, {
    startY: 25,
    head,
    body,
    styles: {
      fontSize: 9,
      cellPadding: 3,
    },
    headStyles: {
      fillColor: [123, 45, 38], // #7B2D26 cor do projeto
      textColor: 255,
      halign: 'center',
      fontStyle: 'bold'
    },
    alternateRowStyles: { fillColor: [250, 240, 240] },
    margin: { left: 10, right: 10 },


    didParseCell: function (data) {
      const col = data.column.index;
      const text = String(data.cell.text || '').toLowerCase();


      if (col === 7) {
        if (text.includes('ativado')) {
          data.cell.styles.textColor = [0, 128, 0];
          data.cell.styles.fontStyle = 'bold';
        } else if (text.includes('cancelado')) {
          data.cell.styles.textColor = [255, 140, 0];
          data.cell.styles.fontStyle = 'bold';
        } else if (text.includes('bloqueado')) {
          data.cell.styles.textColor = [178, 34, 34];
          data.cell.styles.fontStyle = 'bold';
        } else if (text.includes('desativado')) {
          data.cell.styles.textColor = [105, 105, 105];
          data.cell.styles.fontStyle = 'bold';
        }
      }
    },
  });


  const pageHeight = doc.internal.pageSize.height;
  doc.setFontSize(9);
  doc.setTextColor('#B22222');
  doc.text(`Gerado em: ${new Date().toLocaleString('pt-BR')}`, 14, pageHeight - 10);

  doc.save('relatorio-clientes-tabela.pdf');
}


//XML
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
    this.router.navigate(['/relatorio']);
  }

   onNavegarLogout(): void {
    this.router.navigate(['/login']);
  }

}
