import { Component, type OnDestroy, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import { ClienteService } from "../../service/cliente"
import { CartaoService } from "../../service/cartao"
import { Subject, takeUntil, finalize, of, forkJoin } from "rxjs"
import { ClienteDetalhesComponent } from "../cliente-detalhes/cliente-detalhes"
import { MenuLateral } from "../menu-lateral/menu-lateral"
import { Router } from "@angular/router"
import { SegundaViaPopupComponent } from "../cartao-segunda-via/cartao-segunda-via"
import type { AlterarStatusRequestDTO, SegundaViaCartaoRequestDTO } from "../../models/cartao-dtos"

interface ClienteComCartoes extends Cliente {
  cartoes: Cartao[]
  selecionado: boolean
  expandido: boolean
}

type StatusCartao = "ATIVADO" | "BLOQUEADO" | "DESATIVADO" | "REJEITADO" | "CANCELADO"

export const VALID_STATUSES = ["ATIVADO", "BLOQUEADO", "DESATIVADO", "REJEITADO", "CANCELADO"] as const
export type CartaoStatus = (typeof VALID_STATUSES)[number]

export function isValidStatus(s: string): s is CartaoStatus {
  return VALID_STATUSES.includes(s as CartaoStatus)
}

@Component({
  selector: "app-home",
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteDetalhesComponent, MenuLateral, SegundaViaPopupComponent],
  templateUrl: "./home.html",
  styleUrls: ["./home.css"],
})
export class Home implements OnInit, OnDestroy {
  isLoading = false
  isProcessing = false

  filtroCpf = ""
  filtroNome = ""
  clientes: ClienteComCartoes[] = []
  cartoes: Cartao[] = []

  clienteSelecionadoDetalhes: ClienteComCartoes | null = null
  cartaoSelecionado: Cartao | null = null
  modalDetalhesAberto = false

  modalSegundaViaAberto = false
  clienteSegundaVia: ClienteComCartoes | null = null
  cartaoSegundaVia: Cartao | null = null

  private destroy$ = new Subject<void>()

  constructor(
    public clienteService: ClienteService,
    private cartaoService: CartaoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregarDados()
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  // ========== CARREGAMENTO DE DADOS ========== //

  carregarDados(): void {
    this.isLoading = true

    forkJoin({
      clientes: this.clienteService.getClientes(),
      cartoes: this.cartaoService.listarTodos(0, 1000),
    })
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isLoading = false)),
      )
      .subscribe({
        next: ({ clientes, cartoes }) => {
          const listaClientes = clientes.content || clientes
          const listaCartoes = cartoes.content || []

          this.clientes = listaClientes.map((cliente: any) => {
            const cartoesDoCliente = listaCartoes.filter((cartao: any) => cartao.clienteId === cliente.id)

            return {
              ...cliente,
              cartoes: cartoesDoCliente,
              selecionado: false,
              expandido: false,
            } as ClienteComCartoes
          })
        },
        error: (error) => {
          console.error("Erro ao carregar dados:", error)
          this.mostrarErro("Erro ao carregar dados. Tente novamente.")
        },
      })
  }

  // ========== FILTROS ========== //

  get clientesFiltrados(): ClienteComCartoes[] {
    return this.clienteService.filtrarClientes(this.clientes, this.filtroCpf, this.filtroNome) as ClienteComCartoes[]
  }

  // ========== SELEÇÃO ========== //

  get temClientesSelecionados(): boolean {
    return this.clientes.some((cliente) => cliente.selecionado === true)
  }

  get quantidadeClientesSelecionados(): number {
    return this.clientes.filter((cliente) => cliente.selecionado === true).length
  }

  alternarExpansao(cliente: ClienteComCartoes): void {
    cliente.expandido = !cliente.expandido
  }

  temCartoes(cliente: ClienteComCartoes): boolean {
    return cliente.cartoes && cliente.cartoes.length > 0
  }

  // ========== OPERAÇÕES DE STATUS ========== //

  mudarStatus(novoStatus: CartaoStatus): void {
    const clientesSelecionados = this.clientes.filter((cliente) => cliente.selecionado === true)
    const clientesComCartao = clientesSelecionados.filter((cliente) => cliente.cartoes && cliente.cartoes.length > 0)

    if (clientesComCartao.length === 0) {
      this.mostrarErro("Nenhum cliente com cartão selecionado")
      return
    }

    const statusTexto = this.cartaoService.getStatusTexto(novoStatus)

    if (!confirm(`Deseja alterar o status de ${clientesComCartao.length} cartão(ões) para ${statusTexto}?`)) {
      return
    }

    this.isProcessing = true

    const alteracoes = clientesComCartao.flatMap((cliente) => {
      return cliente.cartoes.map((cartao) => {
        if (!cartao?.numero || !cartao?.cvv) return of(null)
        const dto: AlterarStatusRequestDTO = {
          numero: cartao.numero,
          cvv: cartao.cvv,
          novoStatus: novoStatus.toUpperCase(),
        }

        return this.cartaoService.alterarStatus(dto)
      })
    })

    forkJoin(alteracoes)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isProcessing = false)),
      )
      .subscribe({
        next: () => {
          this.clientes.forEach((cliente) => {
            if (cliente.selecionado && cliente.cartoes) {
              cliente.cartoes.forEach((cartao) => {
                if (isValidStatus(novoStatus)) {
                  cartao.status = novoStatus
                }
              })
            }
            cliente.selecionado = false
          })
          this.mostrarSucesso("Status alterado com sucesso!")
        },
        error: (error) => {
          console.error("Erro ao alterar status:", error)
          this.mostrarErro("Erro ao alterar status. Tente novamente.")
        },
      })
  }

  alternarStatusCartao(cartao: Cartao): void {
    if (!cartao) {
      this.mostrarErro("Cartão não encontrado.")
      return
    }

    if (!this.cartaoService.podeAlterarStatus(cartao.status)) {
      const statusAtual = this.cartaoService.getStatusTexto(cartao.status)
      this.mostrarErro(
        `Não é possível alterar cartão com status '${statusAtual}'.\nApenas cartões ATIVADO ou DESATIVADO podem ser alterados.`,
      )
      return
    }

    const proximoStatus = this.cartaoService.obterProximoStatus(cartao.status)

    if (!proximoStatus) {
      const statusAtual = this.cartaoService.getStatusTexto(cartao.status)
      this.mostrarErro(`Transição não permitida de '${statusAtual}'. Verifique as regras de status.`)
      return
    }

    if (!isValidStatus(proximoStatus)) {
      this.mostrarErro("Status inválido para transição")
      return
    }

    const dto: AlterarStatusRequestDTO = {
      numero: cartao.numero,
      cvv: cartao.cvv,
      novoStatus: proximoStatus.toUpperCase(),
    }

    this.isProcessing = true

    this.cartaoService
      .alterarStatus(dto)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isProcessing = false)),
      )
      .subscribe({
        next: (cartaoAtualizado) => {
          const statusRecebido = cartaoAtualizado?.status

          if (isValidStatus(statusRecebido)) {
            cartao.status = statusRecebido
            this.mostrarSucesso("Status alterado com sucesso!")
          } else {
            console.warn("[v0] Status inválido recebido:", statusRecebido)
            this.mostrarErro("Status recebido é inválido.")
          }
        },
        error: (error) => {
          console.error("[v0] Erro ao alterar status:", error)
          this.mostrarErro("Erro ao alterar status. Tente novamente.")
        },
      })
  }

  alterarStatusMultiploCartoes(cliente: ClienteComCartoes, novoStatus: CartaoStatus): void {
    if (!cliente.cartoes || cliente.cartoes.length === 0) {
      this.mostrarErro("Cliente não possui cartões")
      return
    }

    const statusTexto = this.cartaoService.getStatusTexto(novoStatus)
    if (!confirm(`Deseja alterar o status de ${cliente.cartoes.length} cartão(ões) para ${statusTexto}?`)) {
      return
    }

    this.isProcessing = true

    const alteracoes = cliente.cartoes.map((cartao) => {
      if (!cartao?.numero || !cartao?.cvv) return of(null)
      const dto: AlterarStatusRequestDTO = {
        numero: cartao.numero,
        cvv: cartao.cvv,
        novoStatus: novoStatus.toUpperCase(),
      }

      return this.cartaoService.alterarStatus(dto)
    })

    forkJoin(alteracoes)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isProcessing = false)),
      )
      .subscribe({
        next: () => {
          cliente.cartoes.forEach((cartao) => {
            if (isValidStatus(novoStatus)) {
              cartao.status = novoStatus
            }
          })
          this.mostrarSucesso("Status de todos os cartões alterado com sucesso!")
        },
        error: (error) => {
          console.error("Erro ao alterar status:", error)
          this.mostrarErro("Erro ao alterar status. Tente novamente.")
        },
      })
  }

  // ========== MODAL DETALHES ========== //

  verDetalhes(cliente: ClienteComCartoes): void {
    this.clienteSelecionadoDetalhes = cliente
    this.modalDetalhesAberto = true
  }

  fecharDetalhes(): void {
    this.modalDetalhesAberto = false
    this.clienteSelecionadoDetalhes = null
  }

  // ========== SEGUNDA VIA ========== //

  segundaVia(cliente: ClienteComCartoes, cartao: Cartao): void {
    if (!cliente.id || !cartao) {
      this.mostrarErro("Cliente não possui cartão para solicitar 2ª via")
      return
    }

    this.clienteSegundaVia = cliente
    this.modalSegundaViaAberto = true
  }

  fecharSegundaVia(): void {
    this.modalSegundaViaAberto = false
    this.clienteSegundaVia = null
  }

  processarSegundaVia(solicitacao: SegundaViaCartaoRequestDTO): void {
    this.isProcessing = true

    this.cartaoService
      .solicitarSegundaVia(solicitacao.cvv, solicitacao.numero, solicitacao.motivo)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isProcessing = false)),
      )
      .subscribe({
        next: () => {
          this.mostrarSucesso(`2ª via solicitada com sucesso!\nMotivo: ${solicitacao.motivo}`)
          this.fecharSegundaVia()
          this.carregarDados()
        },
        error: (error) => {
          console.error("Erro ao solicitar 2ª via:", error)
          this.mostrarErro("Erro ao solicitar 2ª via do cartão")
        },
      })
  }

  // ========== EXCLUSÃO ========== //

  excluirCliente(cliente: ClienteComCartoes): void {
    if (!cliente.id) return

    if (!confirm(`Deseja realmente excluir ${cliente.nome}?`)) {
      return
    }

    this.isProcessing = true

    this.clienteService
      .deleteCliente(cliente.id)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isProcessing = false)),
      )
      .subscribe({
        next: () => {
          this.mostrarSucesso("Cliente excluído com sucesso!")
          this.carregarDados()
        },
        error: (error) => {
          console.error("Erro ao excluir cliente:", error)
          this.mostrarErro("Erro ao excluir cliente")
        },
      })
  }

  // ========== NAVEGAÇÃO ========== //

  novoCliente(): void {
    this.router.navigate(["/cadastro-cliente"])
  }

  onNavegarHome(): void {
    this.router.navigate(["/home"])
  }

  onNavegarCartoes(): void {
    this.router.navigate(["/cadastro-cartao"])
  }

  onNavegarRelatorios(): void {
    this.router.navigate(["/relatorio"])
  }

  onNavegarLogout(): void {
    this.router.navigate(["/login"])
  }

  // ========== FORMATAÇÃO (delegada ao service) ========== //

  getStatusClass(status?: string): string {
    return this.cartaoService.getStatusClass(status || "")
  }

  getStatusLabel(status?: string): string {
    return this.cartaoService.getStatusTexto(status || "")
  }

  // ========== UTILITÁRIOS ========== //

  private mostrarSucesso(mensagem: string): void {
    alert(mensagem)
  }

  private mostrarErro(mensagem: string): void {
    alert(mensagem)
  }

  trackByClienteId(index: number, cliente: ClienteComCartoes): number | undefined {
    return cliente.id
  }

  trackByCartaoId(index: number, cartao: Cartao): string | undefined {
    return cartao.numero
  }

  formatarNumeroCartao(numero: string): string {
    return this.cartaoService.formatarNumeroCartao(numero)
  }

  formatarDataVencimento(data: string): string {
    return this.cartaoService.formatarDataVencimento(data)
  }

  mascararNumeroCartao(numero: string): string {
    return this.cartaoService.mascaraNumeroCartao(numero)
  }
}
