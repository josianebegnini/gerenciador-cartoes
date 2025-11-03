import { Component, Input, Output, EventEmitter, type SimpleChanges, type OnChanges } from "@angular/core"
import { CommonModule } from "@angular/common"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import { ClienteService } from "../../service/cliente"
import { CartaoService } from "../../service/cartao"
import { ExportacaoService } from "../../service/exportacao"

@Component({
  selector: "app-cliente-detalhes",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./cliente-detalhes.html",
  styleUrls: ["./cliente-detalhes.css"],
})
export class ClienteDetalhesComponent implements OnChanges {
  @Input() cliente: Cliente | null = null
  @Input() isOpen = false
  @Output() fechar = new EventEmitter<void>()

  cartoesDoClienteSelecionado: Cartao[] = []
  @Input() cartoesDoCliente: Cartao[] = []

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private exportacaoService: ExportacaoService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["cartoesDoCliente"] && this.cartoesDoCliente?.length > 0) {
      this.cartoesDoClienteSelecionado = this.cartoesDoCliente
    }

    if (changes["cliente"]) {
      console.log("ClienteDetalhes - Cliente recebido:", this.cliente)
    }
  }

  // ========== NAVEGAÇÃO ========== //
  fecharModal(): void {
    this.fechar.emit()
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }

  // ========== OPERAÇÕES HTTP ========== //
  getStatusTexto(status: string): string {
    return this.cartaoService.getStatusTexto(status)
  }

  getStatusClass(status: string): string {
    return this.cartaoService.getStatusBadgeClass(status)
  }

  // ========== FORMATAÇÃO ========== //
  formatarCPF(cpf: string): string {
    return this.clienteService.formatarCPF(cpf)
  }

  formatarCEP(cep: string): string {
    return this.clienteService.formatarCEP(cep)
  }

  formatarNumeroCartao(numero: string): string {
    return this.cartaoService.formatarNumeroCartao(numero)
  }

  formatarValorParaReal(valor: number | string): string {
    return this.clienteService.formatarValorParaReal(valor)
  }

  // ========== EXPORTAR RELATORIO CLIENTE ========== //
  exportarPDF(): void {
    if (!this.cliente) return
    this.exportacaoService.exportarClienteJSON(this.cliente, this.cartoesDoClienteSelecionado[0] || null)
  }

  exportarXML(): void {
    if (!this.cliente) return
    this.exportacaoService.exportarClienteJSON(this.cliente, this.cartoesDoClienteSelecionado[0] || null)
  }

  temCartoes(): boolean {
    return this.cartoesDoClienteSelecionado && this.cartoesDoClienteSelecionado.length > 0
  }

  getCartaoCardClass(cartao: Cartao): string {
    return `cartao-card ${this.getStatusClass(cartao.status)}`
  }
}
