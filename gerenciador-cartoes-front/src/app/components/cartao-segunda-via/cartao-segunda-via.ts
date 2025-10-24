import { Component, Input, Output, EventEmitter } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"

export interface SolicitacaoSegundaVia {
  clienteId: number
  cartaoId: number
  motivo: string
  dataSolicitacao: string
}

@Component({
  selector: "app-segunda-via-popup",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./cartao-segunda-via.html",
  styleUrls: ["./cartao-segunda-via.css"],
})
export class SegundaViaPopupComponent {
  @Input() cliente: Cliente | null = null
  @Input() cartao: Cartao | null = null
  @Input() isOpen = false
  @Output() fechar = new EventEmitter<void>()
  @Output() confirmar = new EventEmitter<SolicitacaoSegundaVia>()

  motivo = ""
  isSubmitting = false

  fecharModal(): void {
    this.motivo = ""
    this.fechar.emit()
  }

  confirmarSolicitacao(): void {
    if (!this.motivo.trim()) {
      alert("Por favor, informe o motivo da solicitação.")
      return
    }

    if (!this.cliente || !this.cartao) {
      alert("Dados do cliente ou cartão não disponíveis.")
      return
    }

    this.isSubmitting = true

    const solicitacao: SolicitacaoSegundaVia = {
      clienteId: this.cliente!.id!,
      cartaoId: Number(this.cartao.numero),
      motivo: this.motivo.trim(),
      dataSolicitacao: new Date().toISOString(),
    }

    console.log("[v0] Solicitação de segunda via:", solicitacao)

    this.confirmar.emit(solicitacao)

    setTimeout(() => {
      this.motivo = ""
      this.isSubmitting = false
    }, 500)
  }

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ")
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }
}
