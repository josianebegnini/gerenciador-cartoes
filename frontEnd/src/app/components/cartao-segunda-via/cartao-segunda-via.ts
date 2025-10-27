import { Component, Input, Output, EventEmitter } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { CartaoService } from "../../service/cartao"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import type { SegundaViaCartaoRequestDTO, SegundaViaCartaoResponseDTO } from "../../models/cartao-dtos"
import { Subject, takeUntil } from "rxjs"

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
  @Output() confirmar = new EventEmitter<SegundaViaCartaoRequestDTO>()

  motivo = ""
  mensagemErro = ""
  isSubmitting = false

  constructor(private cartaoService: CartaoService) {}

  fecharModal(): void {
    this.motivo = ""
    this.mensagemErro = ""
    this.isSubmitting = false
    this.fechar.emit()
  }

  confirmarSolicitacao(): void {
    if (!this.motivo.trim()) {
      this.mensagemErro = "Por favor, informe o motivo da solicitação."
      return
    }

    if (!this.cliente?.id || !this.cartao?.numero) {
      this.mensagemErro = "Dados do cliente ou cartão não disponíveis."
      return
    }

    const solicitacao: SegundaViaCartaoRequestDTO = {
      clienteId: this.cliente.id,
      numeroCartao: this.cartao.numero,
      motivo: this.motivo.trim(),
    }

    this.confirmar.emit(solicitacao)

    setTimeout(() => {
      this.motivo = ""
      this.mensagemErro = ""
      this.isSubmitting = false
    }, 500)
  }

  formatarNumeroCartao(numero: string): string {
    return this.cartaoService.formatarNumeroCartao(numero)
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }
}
