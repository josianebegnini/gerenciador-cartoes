import { Component, type OnInit, type OnDestroy } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { Subject, takeUntil } from "rxjs"
import type { ClienteService, Cliente } from "../../service/cliente"

interface FiltrosRelatorio {
  dataInicio: string
  dataFim: string
  nomeCliente: string
  ultimosDigitos: string
  cpf: string
}

@Component({
  selector: "app-relatorio",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./relatorio.html",
  styleUrls: ["./relatorio.css"],
})

export class RelatorioComponent {//implements OnInit, OnDestroy {
 /* filtros: FiltrosRelatorio = {
    dataInicio: "",
    dataFim: "",
    nomeCliente: "",
    ultimosDigitos: "",
    cpf: "",
  }

  clientes: Cliente[] = []
  clientesFiltrados: Cliente[] = []
  mostrarPreview = false

  private destroy$ = new Subject<void>()

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.carregarClientes()
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  carregarClientes(): void {
    this.clienteService
      .getClientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          this.clientes = clientes
        },
        error: (error) => {
          console.error("Erro ao carregar clientes:", error)
        },
      })
  }

  aplicarFiltros(): void {
    this.clientesFiltrados = this.clientes.filter((cliente) => {
      const matchNome =
        !this.filtros.nomeCliente || cliente.nome.toLowerCase().includes(this.filtros.nomeCliente.toLowerCase())

      const matchDigitos = !this.filtros.ultimosDigitos || cliente.ultimosDigitos.includes(this.filtros.ultimosDigitos)

      const matchCpf = !this.filtros.cpf || cliente.cpf.includes(this.filtros.cpf)

      return matchNome && matchDigitos && matchCpf
    })

    this.mostrarPreview = true
  }

  limparFiltros(): void {
    this.filtros = {
      dataInicio: "",
      dataFim: "",
      nomeCliente: "",
      ultimosDigitos: "",
      cpf: "",
    }
    this.clientesFiltrados = []
    this.mostrarPreview = false
  }

  gerarPDF(): void {
    if (this.clientesFiltrados.length === 0) {
      alert("Nenhum cliente encontrado com os filtros aplicados")
      return
    }

    const printWindow = window.open("", "_blank")
    if (!printWindow) {
      alert("Por favor, permita pop-ups para gerar o PDF")
      return
    }

    const html = this.gerarHTMLRelatorio()
    printWindow.document.write(html)
    printWindow.document.close()

    setTimeout(() => {
      printWindow.print()
    }, 250)
  }

  baixarJSON(): void {
    if (this.clientesFiltrados.length === 0) {
      alert("Nenhum cliente encontrado com os filtros aplicados")
      return
    }

    const relatorio = {
      dataGeracao: new Date().toISOString(),
      filtros: this.filtros,
      totalRegistros: this.clientesFiltrados.length,
      clientes: this.clientesFiltrados.map((cliente) => ({
        id: cliente.id,
        nome: cliente.nome,
        cpf: cliente.cpf,
        ultimosDigitos: cliente.ultimosDigitos,
        status: cliente.status,
      })),
    }

    const blob = new Blob([JSON.stringify(relatorio, null, 2)], {
      type: "application/json",
    })

    const url = window.URL.createObjectURL(blob)
    const link = document.createElement("a")
    link.href = url
    link.download = `relatorio-clientes-${new Date().getTime()}.json`
    link.click()
    window.URL.revokeObjectURL(url)
  }

  private gerarHTMLRelatorio(): string {
    const dataAtual = new Date().toLocaleDateString("pt-BR")
    const horaAtual = new Date().toLocaleTimeString("pt-BR")

    return `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <title>Relatório de Clientes</title>
        <style>
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
          }
          body {
            font-family: Arial, sans-serif;
            padding: 40px;
            color: #111827;
          }
          .header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #10B981;
          }
          .header h1 {
            color: #10B981;
            font-size: 24px;
            margin-bottom: 10px;
          }
          .header p {
            color: #6b7280;
            font-size: 14px;
          }
          .filtros {
            background-color: #f9fafb;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
          }
          .filtros h2 {
            font-size: 16px;
            margin-bottom: 15px;
            color: #374151;
          }
          .filtros-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
          }
          .filtro-item {
            font-size: 13px;
          }
          .filtro-item strong {
            color: #6b7280;
          }
          .resumo {
            background-color: #eff6ff;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            border-left: 4px solid #3b82f6;
          }
          .resumo p {
            font-size: 14px;
            color: #1e40af;
            font-weight: 500;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
          }
          thead {
            background-color: #f9fafb;
          }
          th {
            padding: 12px;
            text-align: left;
            font-size: 12px;
            font-weight: 600;
            color: #6b7280;
            text-transform: uppercase;
            border-bottom: 2px solid #e5e7eb;
          }
          td {
            padding: 12px;
            font-size: 13px;
            border-bottom: 1px solid #e5e7eb;
          }
          tbody tr:hover {
            background-color: #f9fafb;
          }
          .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 500;
          }
          .status-ativo {
            background-color: #d1fae5;
            color: #065f46;
          }
          .status-bloqueado {
            background-color: #fee2e2;
            color: #991b1b;
          }
          .status-pendente {
            background-color: #fef3c7;
            color: #92400e;
          }
          .footer {
            text-align: center;
            color: #9ca3af;
            font-size: 12px;
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
          }
          @media print {
            body {
              padding: 20px;
            }
          }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>Relatório de Clientes</h1>
          <p>Gerado em ${dataAtual} às ${horaAtual}</p>
        </div>

        <div class="filtros">
          <h2>Filtros Aplicados</h2>
          <div class="filtros-grid">
            ${this.filtros.dataInicio ? `<div class="filtro-item"><strong>Data Início:</strong> ${new Date(this.filtros.dataInicio).toLocaleDateString("pt-BR")}</div>` : ""}
            ${this.filtros.dataFim ? `<div class="filtro-item"><strong>Data Fim:</strong> ${new Date(this.filtros.dataFim).toLocaleDateString("pt-BR")}</div>` : ""}
            ${this.filtros.nomeCliente ? `<div class="filtro-item"><strong>Nome:</strong> ${this.filtros.nomeCliente}</div>` : ""}
            ${this.filtros.ultimosDigitos ? `<div class="filtro-item"><strong>Últimos Dígitos:</strong> ${this.filtros.ultimosDigitos}</div>` : ""}
            ${this.filtros.cpf ? `<div class="filtro-item"><strong>CPF:</strong> ${this.filtros.cpf}</div>` : ""}
          </div>
        </div>

        <div class="resumo">
          <p>Total de registros encontrados: ${this.clientesFiltrados.length}</p>
        </div>

        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>CPF</th>
              <th>Últimos Dígitos</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            ${this.clientesFiltrados
              .map(
                (cliente) => `
              <tr>
                <td>#${cliente.id}</td>
                <td>${cliente.nome}</td>
                <td>${cliente.cpf}</td>
                <td>${cliente.ultimosDigitos}</td>
                <td>
                  <span class="status-badge status-${cliente.status}">
                    ${this.getStatusTexto(cliente.status)}
                  </span>
                </td>
              </tr>
            `,
              )
              .join("")}
          </tbody>
        </table>

        <div class="footer">
          <p>Sistema de Gerenciamento de Cartões de Clientes</p>
        </div>
      </body>
      </html>
    `
  }

  private getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativo: "Ativo",
      bloqueado: "Bloqueado",
      pendente: "Pendente",
    }
    return statusMap[status] || status
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativo: "badge-ativo",
      bloqueado: "badge-bloqueado",
      pendente: "badge-pendente",
    }
    return classes[status] || ""
  }*/
}
