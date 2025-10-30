import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Cartao } from "../models/cartao";
import {
  CartaoResponseDTO,
  CartaoIdentificacaoRequestDTO,
  SegundaViaCartaoRequestDTO,
  SegundaViaCartaoResponseDTO,
} from "../models/cartao-dtos";
import { environment } from "../enviroments/enviroment";

@Injectable({
  providedIn: "root",
})
export class CartaoService {
  private apiUrl = `${environment.apiUrl}/cartoes`;

  constructor(private http: HttpClient) {}

  // ========== OPERAÇÕES HTTP ==========

 getCartoesByClienteId(clienteId: number, page: number = 0, size: number = 10): Observable<any> {
  const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', 'id,DESC');

  return this.http.get<any>(`${this.apiUrl}/cliente/${clienteId}`, { params });
}

  /*createCartao(cartao: Cartao): Observable<Cartao> {
    return this.http.post<Cartao>(this.apiUrl, cartao);
  }

  updateCartao(id: number, cartao: Cartao): Observable<Cartao> {
    return this.http.put<Cartao>(`${this.apiUrl}/${id}`, cartao);
  }

  deleteCartao(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }*/

  ativarCartao(clienteId: number, numeroCartao: string): Observable<CartaoResponseDTO> {
    const request: CartaoIdentificacaoRequestDTO = { clienteId, numeroCartao };
    return this.http.put<CartaoResponseDTO>(`${this.apiUrl}/ativar`, request);
  }

  bloquearCartao(clienteId: number, numeroCartao: string): Observable<CartaoResponseDTO> {
    const request: CartaoIdentificacaoRequestDTO = { clienteId, numeroCartao };
    return this.http.put<CartaoResponseDTO>(`${this.apiUrl}/bloquear`, request);
  }

  solicitarSegundaVia(
    clienteId: number,
    numeroCartao: string,
    motivo: string
  ): Observable<SegundaViaCartaoResponseDTO> {
    const request: SegundaViaCartaoRequestDTO = { clienteId, numeroCartao, motivo };
    return this.http.post<SegundaViaCartaoResponseDTO>(`${this.apiUrl}/segunda-via`, request);
  }

  // ========== FORMATAÇÃO ==========

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ");
  }

  formatarDataVencimento(data: string): string {
    if (!data) return "";
    const partes = data.split("/");
    if (partes.length === 2) return data;
    if (data.includes("-")) {
      const [ano, mes] = data.split("-");
      return `${mes}/${ano}`;
    }
    return data;
  }

  mascaraNumeroCartao(numero: string): string {
    if (numero.length < 4) return numero;
    const ultimosQuatro = numero.slice(-4);
    return `**** **** **** ${ultimosQuatro}`;
  }

  aplicarMascaraCVV(value: string): string {
    return value.replace(/\D/g, "").slice(0, 4);
  }

  // ========== STATUS ==========
  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativado: "Ativado",
      bloqueado: "Bloqueado"
    };
    return statusMap[status] || "Sem Cartão";
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "status-ativado",
      bloqueado: "status-bloqueado",
      cancelado: "status-cancelado",
      desativado: "status-desativado",
    };
    return classes[status] || "status-desconhecido";
  }

  getStatusBadgeClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "badge-ativado",
      bloqueado: "badge-bloqueado",
      cancelado: "badge-cancelado",
      desativado: "badge-desativado",
    };
    return classes[status] || "badge-sem-cartao";
  }

  obterProximoStatus(statusAtual: string): string {
    const cicloStatus: Record<string, string> = {
      ativado: "bloqueado",
      bloqueado: "ativado",
      desativado: "ativado",
      cancelado: "cancelado",
    };
    return cicloStatus[statusAtual] || "ativado";
  }

  podeAlterarStatus(status: string): boolean {
    return status !== " ";
  }

  // ========== VALIDAÇÃO ==========

  validarNumeroCartao(numero: string): boolean {
    const numeroLimpo = numero.replace(/\s/g, "");
    if (!/^\d{16}$/.test(numeroLimpo)) return false;

    let soma = 0;
    let alternar = false;

    for (let i = numeroLimpo.length - 1; i >= 0; i--) {
      let digito = parseInt(numeroLimpo.charAt(i), 10);
      if (alternar) {
        digito *= 2;
        if (digito > 9) digito -= 9;
      }
      soma += digito;
      alternar = !alternar;
    }

    return soma % 10 === 0;
  }

  validarDataVencimento(data: string): boolean {
    if (!data) return false;
    const [mes, ano] = data.split("/");
    if (!mes || !ano) return false;

    const mesNum = parseInt(mes, 10);
    const anoNum = parseInt(ano, 10);
    if (mesNum < 1 || mesNum > 12) return false;

    const hoje = new Date();
    const anoAtual = hoje.getFullYear();
    const mesAtual = hoje.getMonth() + 1;

    if (anoNum < anoAtual) return false;
    if (anoNum === anoAtual && mesNum < mesAtual) return false;

    return true;
  }

  validarCVV(cvv: string): boolean {
    return /^\d{3,4}$/.test(cvv);
  }

  validarCartao(cartao: Cartao): { valido: boolean; erros: string[] } {
    const erros: string[] = [];

    if (!cartao.numero || !this.validarNumeroCartao(cartao.numero)) {
      erros.push("Número do cartão inválido");
    }

    if (!cartao.dataVencimento || !this.validarDataVencimento(cartao.dataVencimento)) {
      erros.push("Data de vencimento inválida");
    }

    if (!cartao.tipoCartao) {
      erros.push("Tipo de cartão é obrigatório");
    }

    if (!cartao.clienteId) {
      erros.push("Cliente é obrigatório");
    }

    return {
      valido: erros.length === 0,
      erros,
    };
  }

  // ========== IDENTIFICAÇÃO DE BANDEIRA ==========

  identificarBandeira(numero: string): string {
    const numeroLimpo = numero.replace(/\s/g, "");

    if (/^4/.test(numeroLimpo)) return "Visa";
    if (/^5[1-5]/.test(numeroLimpo) || /^2(22[1-9]|2[3-9][0-9]|[3-6][0-9]{2}|7[0-1][0-9]|720)/.test(numeroLimpo)) return "Mastercard";
    if (/^3[47]/.test(numeroLimpo)) return "American Express";
    if (/^6011|^622(12[6-9]|1[3-9][0-9]|[2-8][0-9]{2}|9[0-1][0-9]|92[0-5])|^64[4-9]|^65/.test(numeroLimpo)) return "Discover";
    if (/^636368|^438935|^504175|^451416|^636297|^5067|^4576|^4011/.test(numeroLimpo)) return "Elo";

    return "Desconhecida";
  }
}
