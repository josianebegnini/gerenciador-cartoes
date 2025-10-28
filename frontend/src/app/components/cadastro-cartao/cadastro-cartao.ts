import { Component, type OnDestroy } from "@angular/core"
import { Router } from "@angular/router"
import { Subject, takeUntil } from "rxjs"
import { ClienteService } from "../../service/cliente"
import { CartaoService } from "../../service/cartao"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import { MenuLateral } from "../menu-lateral/menu-lateral"
import { FormsModule } from "@angular/forms"
import { CommonModule } from "@angular/common"

@Component({
  selector: "app-cadastro-cartao",
  templateUrl: "./cadastro-cartao.html",
  styleUrls: ["./cadastro-cartao.css"],
  standalone: true,
  imports: [MenuLateral, FormsModule, CommonModule],
})
export class CadastroCartaoComponent implements OnDestroy {
  private destroy$ = new Subject<void>()

  cpfBusca = ""
  clienteEncontrado: Cliente | null = null
  buscandoCliente = false
  erroCliente = ""

  cartao: Cartao = {
    clienteId: 0,
    numero: "",
    cvv: "",
    dataVencimento: "",
    tipoCartao: "debito",
    status: "desativado",
    categoriaCartao: "fisico",
  }

  carregando = false
  mensagemSucesso = ""
  mensagemErro = ""

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private router: Router,
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  novoCliente(): void {
    this.router.navigate(["/cadastro-cliente"])
  }

  onNavegarHome(): void {
    this.router.navigate(["/home"])
  }

  onNavegarCartoes(): void {
    this.router.navigate(["cadastro-cartao"])
  }

  onNavegarRelatorios(): void {
    this.router.navigate(["/relatorio"])
  }

  onNavegarLogout(): void {
    this.router.navigate(["/login"])
  }

  formatarCPF(event: Event): void {
    const input = event.target as HTMLInputElement
    this.cpfBusca = this.clienteService.aplicarMascaraCPF(input.value)
  }

  formatarNumeroCartao(event: Event): void {
    const input = event.target as HTMLInputElement
    this.cartao.numero = this.cartaoService.mascaraNumeroCartao(input.value)
  }

  formatarCVV(event: Event): void {
    const input = event.target as HTMLInputElement
    this.cartao.cvv = this.cartaoService.aplicarMascaraCVV(input.value)
  }

  formatarDataVencimento(event: Event): void {
    const input = event.target as HTMLInputElement
    this.cartaoService.validarDataVencimento(input.value)
  }

  buscarClientePorCPF(): void {
    const valido = this.clienteService.validarCPF(this.cpfBusca)

    if (!valido) {
      console.error('CPF inválido', this.cpfBusca)
      this.erroCliente = "CPF inválido"
      return
    }

    this.buscandoCliente = true
    this.erroCliente = ""
    this.clienteEncontrado = null

    const cpfLimpo = this.cpfBusca.replace(/\D/g, "")

    this.clienteService
      .getClientes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          const cliente = clientes.find((c: { cpf: string }) => c.cpf.replace(/\D/g, "") === cpfLimpo)

          if (cliente) {
            this.clienteEncontrado = cliente
            this.cartao.clienteId = cliente.id!
            this.erroCliente = ""
          } else {
            this.erroCliente = "Cliente não encontrado. Verifique o CPF ou cadastre o cliente primeiro."
          }

          this.buscandoCliente = false
        },
        error: (error) => {
          console.error("[v0] Erro ao buscar cliente:", error)
          this.erroCliente = "Erro ao buscar cliente. Tente novamente."
          this.buscandoCliente = false
        },
      })
  }

  validarFormulario(): boolean {
    if (!this.clienteEncontrado) {
      this.mensagemErro = "Por favor, busque e selecione um cliente válido"
      return false
    }

    const validacao = this.cartaoService.validarCartao(this.cartao)

    if (!validacao.valido) {
      this.mensagemErro = validacao.erros[0]
      return false
    }

    return true
  }

  criarCartao(): void {
    this.mensagemErro = ""
    this.mensagemSucesso = ""

    if (!this.validarFormulario()) {
      return
    }

    this.carregando = true

    this.cartaoService
      .createCartao(this.cartao)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.mensagemSucesso = "Cartão cadastrado com sucesso!"
          this.carregando = false

          setTimeout(() => {
            this.router.navigate(["/home"])
          }, 1000)
        },
        error: (error) => {
          console.error("[v0] Erro ao criar cartão:", error)
          this.mensagemErro = error.error?.message || "Erro ao cadastrar cartão. Tente novamente."
          this.carregando = false
        },
      })
  }

  cancelar(): void {
    this.cpfBusca = ""
    this.clienteEncontrado = null
    this.erroCliente = ""
    this.cartao = {
      clienteId: 0,
      numero: "",
      cvv: "",
      dataVencimento: "",
      tipoCartao: "debito",
      status: "desativado",
      categoriaCartao: "fisico",
    }
    this.mensagemErro = ""
    this.mensagemSucesso = ""
    this.router.navigate(["/home"])
  }
}
