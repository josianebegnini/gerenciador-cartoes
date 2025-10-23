import { Component, Input, Output, EventEmitter } from "@angular/core"
import { CommonModule } from "@angular/common"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"

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

  fecharModal(): void {
    this.fechar.emit()
  }

  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativo: "Ativo",
      bloqueado: "Bloqueado",
      pendente: "Pendente",
    }
    return statusMap[status] || "Sem Cartão"
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativo: "badge-ativo",
      bloqueado: "badge-bloqueado",
      pendente: "badge-pendente",
    }
    return classes[status] || "badge-sem-cartao"
  }

  formatarCPF(cpf: string): string {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  }

  formatarCEP(cep: string): string {
    return cep.replace(/(\d{5})(\d{3})/, "$1-$2")
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
