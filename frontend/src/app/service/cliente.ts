import { Injectable } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import type { Observable } from "rxjs"
import type { Cliente } from "../models/cliente"

@Injectable({
  providedIn: "root",
})
export class ClienteService {
  private apiUrl = "http://localhost:8085/api/clientes"

  constructor(private http: HttpClient) {}

  getClientes(page = 0, size = 100, sort = "id", direction = "asc", nome?: string, cpf?: string): Observable<any> {
    let params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString())
      .set("sort", sort)
      .set("direction", direction)

    if (nome) {
      params = params.set("nome", nome)
    }
    if (cpf) {
      params = params.set("cpf", cpf)
    }

    return this.http.get<any>(this.apiUrl, { params })
  }

  getClienteById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`)
  }

  createCliente(cliente: Cliente): Observable<Cliente> {
    const clienteParaEnviar = this.prepararClienteParaEnvio(cliente)
    return this.http.post<Cliente>(this.apiUrl, clienteParaEnviar)
  }

  updateCliente(id: number, cliente: Cliente): Observable<Cliente> {
    const clienteParaEnviar = this.prepararClienteParaEnvio(cliente)
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, clienteParaEnviar)
  }

  deleteCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }

  // ========== FORMATAÇÃO ==========

  formatarCPF(cpf: string): string {
    const cpfLimpo = this.limparCPF(cpf)
    return cpfLimpo.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  }

  limparCPF(cpf: string): string {
    return cpf.replace(/\D/g, "")
  }

  formatarCPFAoDigitar(valor: string): string {
    let cpfLimpo = valor.replace(/\D/g, "")

    if (cpfLimpo.length <= 11) {
      cpfLimpo = cpfLimpo.replace(/(\d{3})(\d)/, "$1.$2")
      cpfLimpo = cpfLimpo.replace(/(\d{3})(\d)/, "$1.$2")
      cpfLimpo = cpfLimpo.replace(/(\d{3})(\d{1,2})$/, "$1-$2")
    }

    return cpfLimpo
  }

  aplicarMascaraCPF(value: string): string {
    return this.formatarCPFAoDigitar(value)
  }

  formatarCEP(cep: string): string {
    const cepLimpo = this.limparCEP(cep)
    return cepLimpo.replace(/(\d{5})(\d{3})/, "$1-$2")
  }

  limparCEP(cep: string): string {
    return cep.replace(/\D/g, "")
  }

  formatarCEPAoDigitar(valor: string): string {
    let cepLimpo = valor.replace(/\D/g, "")

    if (cepLimpo.length <= 8) {
      cepLimpo = cepLimpo.replace(/(\d{5})(\d)/, "$1-$2")
    }

    return cepLimpo
  }

  formatarValorParaReal(valor: number | string): string {
    const numero = Number(valor)

    if (isNaN(numero)) {
      return "R$ 0,00"
    }

    return numero.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    })
  }

  formatarSaldoAoDigitar(valor: string): string {
    const valorNumerico = valor.replace(/\D/g, "")
    const numero = Number.parseFloat(valorNumerico) / 100

    return numero.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    })
  }

  // ========== VALIDAÇÃO ==========

  validarCPF(cpf: string): boolean {
    const cpfLimpo = this.limparCPF(cpf)

    if (cpfLimpo.length !== 11) {
      return false
    }

    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1{10}$/.test(cpfLimpo)) {
      return false
    }

    // Validação dos dígitos verificadores
    let soma = 0
    let resto

    for (let i = 1; i <= 9; i++) {
      soma += Number.parseInt(cpfLimpo.substring(i - 1, i)) * (11 - i)
    }

    resto = (soma * 10) % 11
    if (resto === 10 || resto === 11) resto = 0
    if (resto !== Number.parseInt(cpfLimpo.substring(9, 10))) return false

    soma = 0
    for (let i = 1; i <= 10; i++) {
      soma += Number.parseInt(cpfLimpo.substring(i - 1, i)) * (12 - i)
    }

    resto = (soma * 10) % 11
    if (resto === 10 || resto === 11) resto = 0
    if (resto !== Number.parseInt(cpfLimpo.substring(10, 11))) return false

    return true
  }

  validarEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  }

  validarCEP(cep: string): boolean {
    const cepLimpo = this.limparCEP(cep)
    return cepLimpo.length === 8
  }

  validarDataNascimento(data: string): boolean {
    if (!data) return false

    const dataNasc = new Date(data)
    const hoje = new Date()

    // Verifica se a data é válida
    if (isNaN(dataNasc.getTime())) return false

    // Verifica se a data não é futura
    if (dataNasc > hoje) return false

    // Verifica idade mínima (18 anos)
    const idade = hoje.getFullYear() - dataNasc.getFullYear()
    const mesAtual = hoje.getMonth()
    const mesNasc = dataNasc.getMonth()

    if (mesAtual < mesNasc || (mesAtual === mesNasc && hoje.getDate() < dataNasc.getDate())) {
      return idade - 1 >= 18
    }

    return idade >= 18
  }

  validarCliente(cliente: Cliente): { valido: boolean; erros: string[] } {
    const erros: string[] = []

    if (!cliente.nome || !cliente.nome.trim()) {
      erros.push("Nome é obrigatório")
    }

    if (!cliente.cpf || !cliente.cpf.trim()) {
      erros.push("CPF é obrigatório")
    } else if (!this.validarCPF(cliente.cpf)) {
      erros.push("CPF inválido")
    }

    if (!cliente.email || !cliente.email.trim()) {
      erros.push("Email é obrigatório")
    } else if (!this.validarEmail(cliente.email)) {
      erros.push("Email inválido")
    }

    if (!cliente.dataNasc) {
      erros.push("Data de nascimento é obrigatória")
    } else if (!this.validarDataNascimento(cliente.dataNasc)) {
      erros.push("Data de nascimento inválida ou cliente menor de 18 anos")
    }

    if (!cliente.endereco.cep || !cliente.endereco.cep.trim()) {
      erros.push("CEP é obrigatório")
    } else if (!this.validarCEP(cliente.endereco.cep)) {
      erros.push("CEP inválido")
    }

    if (!cliente.endereco || !cliente.endereco.cidade.trim()) {
      erros.push("Endereço é obrigatório")
    }

    if (!cliente.endereco.cidade || !cliente.endereco.cidade.trim()) {
      erros.push("Cidade é obrigatória")
    }

    return {
      valido: erros.length === 0,
      erros,
    }
  }

  // ========== FILTROS ==========

  filtrarClientes(clientes: any[], filtroCpf: string, filtroNome: string): any[] {
    return clientes.filter((cliente) => {
      const cpfMatch = !filtroCpf || this.limparCPF(cliente.cpf).includes(this.limparCPF(filtroCpf))
      const nomeMatch = !filtroNome || cliente.nome.toLowerCase().includes(filtroNome.toLowerCase())
      return cpfMatch && nomeMatch
    })
  }

  // ========== PREPARAÇÃO DE DADOS ==========

  private prepararClienteParaEnvio(cliente: Cliente): any {
    return {
      ...cliente,
      cpf: this.limparCPF(cliente.cpf),
      cep: this.limparCEP(cliente.endereco.cep),
    }
  }
}
