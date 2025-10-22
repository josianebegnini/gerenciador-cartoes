import { Component, Input, Output, EventEmitter } from "@angular/core"
import { CommonModule } from "@angular/common"
import type { Cliente } from "../../service/cliente"

@Component({
  selector: "app-cliente-detalhes",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./cliente-detalhes.html",
  styleUrls: ["./cliente-detalhes.css"],
})
export class ClienteDetalhesComponent {
  @Input() cliente: Cliente | null = null
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
    return statusMap[status] || status
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativo: "badge-ativo",
      bloqueado: "badge-bloqueado",
      pendente: "badge-pendente",
    }
    return classes[status] || ""
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }
}
