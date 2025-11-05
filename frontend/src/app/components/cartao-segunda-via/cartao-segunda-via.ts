import { Component, Input, Output, EventEmitter } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { CartaoService } from "../../service/cartao"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import type { SegundaViaCartaoRequestDTO } from "../../models/cartao-dtos"

@Component({
  selector: "app-segunda-via-popup",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./cartao-segunda-via.html",
  styleUrls: ["./cartao-segunda-via.css"],
})
export class SegundaViaPopupComponent {
  @Input() cliente: Cliente | null = null
  @Input() cartoes: Cartao[] = []
  @Input() isOpen = false

  @Output() fechar = new EventEmitter<void>()
  @Output() confirmar = new EventEmitter<SegundaViaCartaoRequestDTO>()

  cartaoSelecionado: Cartao | null = null
  motivo = ""
  mensagemErro = ""
  isSubmitting = false

  constructor(private cartaoService: CartaoService) {}

  selecionarCartao(cartao: Cartao): void {
    console.log(cartao.numero)
    this.cartaoSelecionado = cartao
    this.mensagemErro = ""
  }

  // ========== FAZ A SOLICITAÇÃO DA SEGUNDA VIA ========== //

  confirmarSolicitacao(): void {
    if (!this.cartaoSelecionado) {
      this.mensagemErro = "Por favor, selecione um cartão."
      return
    }

    if (!this.motivo.trim()) {
      this.mensagemErro = "Por favor, informe o motivo da solicitação."
      return
    }

    if (!this.cliente?.id || !this.cartaoSelecionado?.numero) {
      this.mensagemErro = "Dados do cliente ou cartão não disponíveis."
      return
    }

    const solicitacao: SegundaViaCartaoRequestDTO = {
      cvv: this.cartaoSelecionado.cvv,
      numero: this.cartaoSelecionado.numero,
      motivo: this.motivo.trim(),
    }

    this.confirmar.emit(solicitacao)
    this.resetarEstado()
  }

  // ========== FORMATAÇÃO ========== //

  formatarNumeroCartao(numero: string): string {
    return this.cartaoService.formatarNumeroCartao(numero)
  }

  obterCorStatus(status: string): string {
    const cores: { [key: string]: string } = {
      ativado: "#10b981",
      bloqueado: "#ef4444",
      desativado: "#6b7280",
      rejeitado: "#f59e0b",
      cancelado: "#8b5cf6",
    }
    return cores[status] || "#6b7280"
  }

  // ========== NAVEGAÇÃO ========== //

  fecharModal(): void {
    this.resetarEstado()
    this.fechar.emit()
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }

  // ========== LIMPA FORMS ========== //

  private resetarEstado(): void {
    this.cartaoSelecionado = null
    this.motivo = ""
    this.mensagemErro = ""
    this.isSubmitting = false
  }
}
