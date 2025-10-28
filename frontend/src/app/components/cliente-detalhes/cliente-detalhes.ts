import { Component, Input, Output, EventEmitter, type SimpleChanges } from "@angular/core"
import { CommonModule } from "@angular/common"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import  { ClienteService } from "../../service/cliente"
import  { CartaoService } from "../../service/cartao"
import  { ExportacaoService } from "../../service/exportacao"

@Component({
  selector: "app-cliente-detalhes",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./cliente-detalhes.html",
  styleUrls: ["./cliente-detalhes.css"],
})
export class ClienteDetalhesComponent {
  @Input() cliente: Cliente | null = null
  @Input() cartao: Cartao | null = null
  @Input() isOpen = false
  @Output() fechar = new EventEmitter<void>()

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private exportacaoService: ExportacaoService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["cliente"] || changes["cartao"]) {
      console.log("[v0] ClienteDetalhes - Cliente recebido:", this.cliente)
      console.log("[v0] ClienteDetalhes - Cartão recebido:", this.cartao)
    }
  }

  fecharModal(): void {
    this.fechar.emit()
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }

  getStatusTexto(status: string): string {
    return this.cartaoService.getStatusTexto(status)
  }

  getStatusClass(status: string): string {
    return this.cartaoService.getStatusBadgeClass(status)
  }

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

  exportarPDF(): void {
    if (!this.cliente) return
    console.log("[v0] Exportando PDF...")
    this.exportacaoService.exportarClienteJSON(this.cliente, this.cartao)
  }

  exportarXML(): void {
    if (!this.cliente) return
    console.log("[v0] Exportando XML...")
    this.exportacaoService.exportarClienteJSON(this.cliente, this.cartao)
  }
}
