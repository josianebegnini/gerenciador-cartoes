import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import type { Observable } from "rxjs"
import type { Cartao } from "../models/cartao"
import {
  CartaoResponseDTO,
  CartaoIdentificacaoRequestDTO,
  SegundaViaCartaoRequestDTO,
  SegundaViaCartaoResponseDTO,
} from "../models/cartao-dtos"

@Injectable({
  providedIn: "root",
})
export class CartaoService {
  private apiUrl = "http://ms-cartao:8081"

  constructor(private http: HttpClient) {}

  getCartoes(): Observable<Cartao[]> {
    return this.http.get<Cartao[]>(this.apiUrl)
  }

  getCartaoByClienteId(clienteId: number): Observable<Cartao> {
    return this.http.get<Cartao>(`${this.apiUrl}/cliente/${clienteId}`)
  }

  createCartao(cartao: Cartao): Observable<Cartao> {
    return this.http.post<Cartao>(this.apiUrl, cartao)
  }

  updateCartao(id: number, cartao: Cartao): Observable<Cartao> {
    return this.http.put<Cartao>(`${this.apiUrl}/${id}`, cartao)
  }

  deleteCartao(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  alternarStatus(clienteId: number): Observable<Cartao> {
    return this.http.patch<Cartao>(`${this.apiUrl}/alternar-status/${clienteId}`, {})
  }

  updateStatusEmLote(clienteIds: number[], novoStatus: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/status-lote`, {
      clienteIds,
      status: novoStatus,
    })
  }

  ativarCartao(clienteId: number, numeroCartao: string): Observable<CartaoResponseDTO> {
    const request: CartaoIdentificacaoRequestDTO = {
      clienteId,
      numeroCartao,
    }
    return this.http.put<CartaoResponseDTO>(`${this.apiUrl}/ativar`, request)
  }

  solicitarSegundaVia(
    clienteId: number,
    numeroCartao: string,
    motivo: string,
  ): Observable<SegundaViaCartaoResponseDTO> {
    const request: SegundaViaCartaoRequestDTO = {
      clienteId,
      numeroCartao,
      motivo,
    }
    return this.http.post<SegundaViaCartaoResponseDTO>(`${this.apiUrl}/segunda-via`, request)
  }

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ")
  }

  formatarDataVencimento(data: string): string {
    // Formato esperado: MM/YYYY
    if (!data) return ""

    const partes = data.split("/")
    if (partes.length === 2) {
      return data
    }

    // Se vier no formato YYYY-MM-DD, converte
    if (data.includes("-")) {
      const [ano, mes] = data.split("-")
      return `${mes}/${ano}`
    }

    return data
  }

  mascaraNumeroCartao(numero: string): string {
    if (numero.length < 4) return numero
    const ultimosQuatro = numero.slice(-4)
    return `**** **** **** ${ultimosQuatro}`
  }

  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativado: "Ativado",
      bloqueado: "Bloqueado",
      cancelado: "Cancelado",
      desativado: "Desativado",
    }
    return statusMap[status] || "Sem Cartão"
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "status-ativado",
      bloqueado: "status-bloqueado",
      cancelado: "status-cancelado",
      desativado: "status-desativado",
    }
    return classes[status] || "status-desconhecido"
  }

  getStatusBadgeClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "badge-ativado",
      bloqueado: "badge-bloqueado",
      cancelado: "badge-cancelado",
      desativado: "badge-desativado",
    }
    return classes[status] || "badge-sem-cartao"
  }

  obterProximoStatus(statusAtual: string): string {
    const cicloStatus: Record<string, string> = {
      ativado: "bloqueado",
      bloqueado: "ativado",
      desativado: "ativado",
      cancelado: "cancelado", // Cancelado não pode mudar
    }
    return cicloStatus[statusAtual] || "ativado"
  }

  podeAlterarStatus(status: string): boolean {
    return status !== "cancelado"
  }

  validarNumeroCartao(numero: string): boolean {
    const numeroLimpo = numero.replace(/\s/g, "")

    if (!/^\d{16}$/.test(numeroLimpo)) {
      return false
    }

    // Algoritmo de Luhn
    let soma = 0
    let alternar = false

    for (let i = numeroLimpo.length - 1; i >= 0; i--) {
      let digito = Number.parseInt(numeroLimpo.charAt(i), 10)

      if (alternar) {
        digito *= 2
        if (digito > 9) {
          digito -= 9
        }
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
    const anoNum = Number.parseInt(ano, 10)

    if (mesNum < 1 || mesNum > 12) return false

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

  identificarBandeira(numero: string): string {
    const numeroLimpo = numero.replace(/\s/g, "")

    if (/^4/.test(numeroLimpo)) return "Visa"
    if (/^5[1-5]/.test(numeroLimpo)) return "Mastercard"
    if (/^3[47]/.test(numeroLimpo)) return "American Express"
    if (/^6(?:011|5)/.test(numeroLimpo)) return "Discover"
    if (/^35/.test(numeroLimpo)) return "JCB"

    return "Desconhecida"
  }

  getTipoCartaoLabel(tipo: string): string {
    const tipos: Record<string, string> = {
      debito: "Débito",
      credito: "Crédito",
      multiplo: "Múltiplo",
    }
    return tipos[tipo] || tipo
  }

  aplicarMascaraNumeroCartao(valor: string): string {
    let numeroLimpo = valor.replace(/\D/g, "")

    if (numeroLimpo.length <= 16) {
      numeroLimpo = numeroLimpo.replace(/(\d{4})(?=\d)/g, "$1 ")
    }

    return numeroLimpo
  }

  aplicarMascaraCVV(valor: string): string {
    return valor.replace(/\D/g, "").substring(0, 4)
  }

  aplicarMascaraDataVencimento(valor: string): string {
    let numeroLimpo = valor.replace(/\D/g, "")

    if (numeroLimpo.length <= 4) {
      if (numeroLimpo.length >= 2) {
        numeroLimpo = numeroLimpo.replace(/(\d{2})(\d)/, "$1/$2")
      }
    }

    return numeroLimpo
  }
}
