import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http"
import type { Observable } from "rxjs"
import type { Cartao } from "../models/cartao"
import type {
  SegundaViaCartaoRequestDTO,
  SegundaViaCartaoResponseDTO,
  AlterarStatusRequestDTO,
} from "../models/cartao-dtos"
import { environment } from "../enviroments/enviroment"

@Injectable({
  providedIn: "root",
})
export class CartaoService {
  private apiUrl = `${environment.apiUrl}/cartoes`

  constructor(private http: HttpClient) {}

  // ========== OPERAÇÕES HTTP ========== //

  getCartoesByClienteId(clienteId: number, page = 0, size = 10): Observable<any> {
    const params = new HttpParams().set("page", page.toString()).set("size", size.toString()).set("sort", "id,DESC")

    return this.http.get<any>(`${this.apiUrl}/cliente/${clienteId}`, { params })
  }

  createCartao(cartao: Cartao): Observable<Cartao> {
    const headers = new HttpHeaders({ "Content-Type": "application/json" })
    return this.http.post<Cartao>(`${this.apiUrl}/cadastrar-existente`, cartao, { headers })
  }

  alterarStatus(dto: AlterarStatusRequestDTO): Observable<Cartao> {
    const dtoCorrigido: AlterarStatusRequestDTO = {
      numero: dto.numero.trim(),
      cvv: dto.cvv.trim(),
      novoStatus: dto.novoStatus.toUpperCase().trim(),
    }

    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json",
    })

    return this.http.put<Cartao>(`${this.apiUrl}/alterar-status`, dtoCorrigido, { headers })
  }

  solicitarSegundaVia(cvv: string, numero: string, motivo: string): Observable<SegundaViaCartaoResponseDTO> {
    const request: SegundaViaCartaoRequestDTO = { cvv, numero, motivo }
    return this.http.post<SegundaViaCartaoResponseDTO>(`${this.apiUrl}/segunda-via`, request)
  }

  listarTodos(page = 0, size = 10): Observable<any> {
    const params = new HttpParams().set("page", page.toString()).set("size", size.toString()).set("sort", "id,DESC")

    return this.http.get<any>(`${this.apiUrl}`, { params })
  }

  // ========== FORMATAÇÃO ========== //

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ")
  }

  formatarDataVencimento(data: string): string {
    if (!data) return ""

    const numeros = data.replace(/\D/g, "")

    if (numeros.length <= 2) {
      return numeros
    }

    const mes = numeros.substring(0, 2)
    const ano = numeros.substring(2, 4)

    return `${mes}/${ano}`
  }

  mascaraNumeroCartao(numero: string): string {
    if (numero.length < 4) return numero
    const ultimosQuatro = numero.slice(-4)
    return `**** **** **** ${ultimosQuatro}`
  }

  aplicarMascaraCVV(value: string): string {
    return value.replace(/\D/g, "").slice(0, 4)
  }

  formatarLimiteAoDigitar(valor: string): string {
    const valorNumerico = valor.replace(/\D/g, "")

    if (!valorNumerico) return ""

    const numero = Number(valorNumerico) / 100

    return numero.toLocaleString("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })
  }

  // ========== OPERAÇÕES DE STATUS ========== //

  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ATIVADO: "Ativado",
      BLOQUEADO: "Bloqueado",
      DESATIVADO: "Desativado",
      REJEITADO: "Rejeitado",
      CANCELADO: "Cancelado",
    }
    return statusMap[status] || "Sem Cartão"
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ATIVADO: "status-ativado",
      BLOQUEADO: "status-bloqueado",
      DESATIVADO: "status-desativado",
      REJEITADO: "status-rejeitado",
      CANCELADO: "status-cancelado",
    }
    return classes[status] || "status-desconhecido"
  }

  getStatusBadgeClass(status: string): string {
    const classes: Record<string, string> = {
      ATIVADO: "badge-ativado",
      BLOQUEADO: "badge-bloqueado",
      DESATIVADO: "badge-desativado",
      REJEITADO: "badge-rejeitado",
      CANCELADO: "badge-cancelado",
    }
    return classes[status] || "badge-sem-cartao"
  }

  obterProximoStatus(statusAtual: string): string {
    // Backend has specific transition rules:
    // DESATIVADO -> ATIVADO (only allowed transition from DESATIVADO)
    // ATIVADO -> BLOQUEADO (only allowed transition from ATIVADO)
    // BLOQUEADO -> não pode alterar (apenas para listar/visualizar)
    // REJEITADO e CANCELADO são status finais (não podem alterar)

    const transicoes: Record<string, string> = {
      DESATIVADO: "ATIVADO", // Só pode ativar cartões desativados
      ATIVADO: "BLOQUEADO", // Só pode bloquear cartões ativados
      BLOQUEADO: "DESATIVADO", // Não suportado pelo backend (CART004)
      REJEITADO: "", // Status final
      CANCELADO: "", // Status final
    }

    return transicoes[statusAtual] || ""
  }

  podeAlterarStatus(status: string): boolean {
    // Apenas DESATIVADO e ATIVADO podem ser alterados para novo status
    const statusesAlteraveis = ["DESATIVADO", "ATIVADO"]
    return statusesAlteraveis.includes(status)
  }

  // ========== VALIDAÇÃO ========== //

  validarNumeroCartao(numero: string): boolean {
    const numeroLimpo = numero.replace(/\s/g, "")
    if (!/^\d{16}$/.test(numeroLimpo)) return false

    let soma = 0
    let alternar = false

    for (let i = numeroLimpo.length - 1; i >= 0; i--) {
      let digito = Number.parseInt(numeroLimpo.charAt(i), 10)
      if (alternar) {
        digito *= 2
        if (digito > 9) digito -= 9
      }
      soma += digito
      alternar = !alternar
    }

    return soma % 10 === 0
  }

  validarDataVencimento(data: string): boolean {
    if (!data) return false

    const [mes, ano] = data.split("/")
    if (!mes || !ano) return false

    const mesNum = Number.parseInt(mes, 10)
    let anoNum = Number.parseInt(ano, 10)

    if (isNaN(mesNum) || isNaN(anoNum)) return false
    if (mesNum < 1 || mesNum > 12) return false

    if (ano.length === 2) {
      const anoAtual = new Date().getFullYear()
      const prefixo = Math.floor(anoAtual / 100) * 100
      anoNum += prefixo
    }

    const hoje = new Date()
    const anoAtual = hoje.getFullYear()
    const mesAtual = hoje.getMonth() + 1

    if (anoNum < anoAtual) return false
    if (anoNum === anoAtual && mesNum < mesAtual) return false

    return true
  }

  validarCVV(cvv: string): boolean {
    return /^\d{3,4}$/.test(cvv)
  }

  validarCartao(cartao: Cartao): { valido: boolean; erros: string[] } {
    const erros: string[] = []

    if (!cartao.numero || !this.validarNumeroCartao(cartao.numero)) {
      erros.push("Número do cartão inválido")
    }

    if (!cartao.dataVencimento || !this.validarDataVencimento(cartao.dataVencimento)) {
      erros.push("Data de vencimento inválida")
    }

    if (!cartao.tipoCartao) {
      erros.push("Tipo de cartão é obrigatório")
    }

    if (!cartao.clienteId) {
      erros.push("Cliente é obrigatório")
    }

    return {
      valido: erros.length === 0,
      erros,
    }
  }

  // ========== IDENTIFICAÇÃO DE BANDEIRA ========== //

  identificarBandeira(numero: string): string {
    const numeroLimpo = numero.replace(/\s/g, "")

    if (/^4/.test(numeroLimpo)) return "Visa"
    if (/^5[1-5]/.test(numeroLimpo) || /^2(22[1-9]|2[3-9][0-9]|[3-6][0-9]{2}|7[0-1][0-9]|720)/.test(numeroLimpo))
      return "Mastercard"
    if (/^3[47]/.test(numeroLimpo)) return "American Express"
    if (/^6011|^622(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[0-1][0-9]|92[0-5])|^64[4-9]|^65/.test(numeroLimpo))
      return "Discover"
    if (/^636368|^438935|^504175|^451416|^636297|^5067|^4576|^4011/.test(numeroLimpo)) return "Elo"

    return "Desconhecida"
  }
}
