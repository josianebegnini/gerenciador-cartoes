import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Cartao } from "../models/cartao";
import { SegundaViaCartaoRequestDTO, SegundaViaCartaoResponseDTO, AlterarStatusRequestDTO, } from "../models/cartao-dtos";
import { environment } from "../enviroments/enviroment";

@Injectable({
  providedIn: "root",
})

export class CartaoService {
  private apiUrl = `${environment.apiUrl}/cartoes`;

  constructor(private http: HttpClient) {}

  // ========== OPERAÇÕES HTTP ========== //

 getCartoesByClienteId(clienteId: number, page: number = 0, size: number = 10): Observable<any> {
  const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', 'id,DESC');

  return this.http.get<any>(`${this.apiUrl}/cliente/${clienteId}`, { params });
  }

  createCartao(cartao: Cartao): Observable<Cartao> {

    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    return this.http.post<Cartao>(`${this.apiUrl}/cadastrar-existente`, cartao, { headers });

    //return this.http.post<Cartao>(`${this.apiUrl}/cadastrar-existente`, cartao);
  }

  alterarStatus(dto: AlterarStatusRequestDTO): Observable<Cartao> {
    return this.http.put<Cartao>(`${this.apiUrl}/alterar-status`, dto);
  }

  solicitarSegundaVia(
    cvv: string,
    numero: string,
    motivo: string
  ): Observable<SegundaViaCartaoResponseDTO> {
    const request: SegundaViaCartaoRequestDTO = { cvv, numero, motivo };
    return this.http.post<SegundaViaCartaoResponseDTO>(`${this.apiUrl}/segunda-via`, request);
  }


listarTodos(page: number = 0, size: number = 10): Observable<any> {
  const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sort', 'id,DESC');

  return this.http.get<any>(`${this.apiUrl}`, { params });
}


  // ========== FORMATAÇÃO ========== //

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ");
  }

  formatarDataVencimento(data: string): string {
    if (!data) return "";

    // Remove tudo que não é número
    const numeros = data.replace(/\D/g, "");

    // Formata como MM/AA
    if (numeros.length <= 2) {
      return numeros;
    }

    const mes = numeros.substring(0, 2);
    const ano = numeros.substring(2, 4);

    return `${mes}/${ano}`;
  }

  mascaraNumeroCartao(numero: string): string {
    if (numero.length < 4) return numero;
    const ultimosQuatro = numero.slice(-4);
    return `**** **** **** ${ultimosQuatro}`;
  }

  aplicarMascaraCVV(value: string): string {
    return value.replace(/\D/g, "").slice(0, 4);
  }


formatarLimiteAoDigitar(valor: string): string {
  const valorNumerico = valor.replace(/\D/g, "");

  if (!valorNumerico) return "";

  const numero = Number(valorNumerico) / 100;

  return numero.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

  // ========== OPERAÇÕES DE STATUS ========== //

  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativado: "Ativado",
      bloqueado: "Bloqueado",
      desativado: 'desativado',
      rejeitado: 'rejeitado',
      cancelado: 'cancelado'
    };
    return statusMap[status] || "Sem Cartão";
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "status-ativado",
      bloqueado: "status-bloqueado",
      desativado: 'status-desativado',
      rejeitado: 'status-rejeitado',
      cancelado: 'status-cancelado'
    };
    return classes[status] || "status-desconhecido";
  }

  getStatusBadgeClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "badge-ativado",
      bloqueado: "badge-bloqueado",
      desativado: 'badge-desativado',
      rejeitado: 'badge-rejeitado',
      cancelado: 'badge-cancelado'
    };
    return classes[status] || "badge-sem-cartao";
  }

  obterProximoStatus(statusAtual: string): string {
    const cicloStatus: Record<string, string> = {
      ativado: "bloqueado",
      bloqueado: "ativado",
      desativado: 'desativado',
      rejeitado: 'rejeitado',
      cancelado: 'cancelado'
    };
    return cicloStatus[statusAtual] || "ativado";
  }

  podeAlterarStatus(status: string): boolean {
    return status !== "Sem Cartão";
  }

  // ========== VALIDAÇÃO ========== //

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
    let anoNum = parseInt(ano, 10);

    if (isNaN(mesNum) || isNaN(anoNum)) return false;
    if (mesNum < 1 || mesNum > 12) return false;

    // Corrige ano com dois dígitos (ex: "25" vira "2025")
    if (ano.length === 2) {
      const anoAtual = new Date().getFullYear();
      const prefixo = Math.floor(anoAtual / 100) * 100;
      anoNum += prefixo;
    }

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

  // ========== IDENTIFICAÇÃO DE BANDEIRA ========== //

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
