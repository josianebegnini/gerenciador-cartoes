import { Component, type OnDestroy, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import { Cliente } from "../../models/cliente"
import { Cartao } from "../../models/cartao"
import { ClienteService } from "../../service/cliente"
import { CartaoService } from "../../service/cartao"
import { Subject, takeUntil, combineLatest, finalize, of, forkJoin, Observable } from "rxjs"
import { ClienteDetalhesComponent } from "../cliente-detalhes/cliente-detalhes"
import { MenuLateral } from "../menu-lateral/menu-lateral"
import { Router } from "@angular/router"
import { SegundaViaPopupComponent } from "../cartao-segunda-via/cartao-segunda-via"
import { AlterarStatusRequestDTO, SegundaViaCartaoRequestDTO } from "../../models/cartao-dtos"

interface ClienteComCartao extends Cliente {
  cartao?: Cartao
  selecionado: boolean
}

type StatusCartao =  'ativado' | 'bloqueado' | 'desativado' | 'rejeitado' | 'cancelado';

export const VALID_STATUSES = ["ativado", "bloqueado", "desativado", "rejeitado", "cancelado"] as const;
export type CartaoStatus = typeof VALID_STATUSES[number];

export function isValidStatus(s: string): s is CartaoStatus {
  return VALID_STATUSES.includes(s as CartaoStatus);
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
  clientes: ClienteComCartao[] = []
  cartoes: Cartao[] = []

  clienteSelecionadoDetalhes: ClienteComCartao | null = null
  cartaoSelecionado: Cartao | null = null
  modalDetalhesAberto = false

  modalSegundaViaAberto = false
  clienteSegundaVia: ClienteComCartao | null = null
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
  this.isLoading = true;

  this.clienteService.getClientes()
    .pipe(
      takeUntil(this.destroy$),
      finalize(() => (this.isLoading = false))
    )
    .subscribe({
      next: (response) => {
        const clientes = response.content || response;
        this.clientes = clientes.map((cliente: any) => ({
          ...cliente,
          cartao: null,
          selecionado: false
        }));

        // Buscar cartões paginados para cada cliente
        this.clientes.forEach(cliente => {
          this.cartaoService.getCartoesByClienteId(cliente.id!, 0, 1).subscribe({
            next: (cartoesPage) => {
              const cartaoEncontrado = cartoesPage.content?.[0] || null;
              cliente.cartao = cartaoEncontrado;
            },
            error: (err) => {
              console.error(`Erro ao buscar cartão do cliente ${cliente.id}:`, err);
            }
          });
        });
      },
      error: (error) => {
        console.error("Erro ao carregar dados:", error);
        this.mostrarErro("Erro ao carregar dados. Tente novamente.");
      }
    });
  }

  // ========== FILTROS ========== //

  get clientesFiltrados(): ClienteComCartao[] {
    return this.clienteService.filtrarClientes(this.clientes, this.filtroCpf, this.filtroNome)
  }

  // ========== SELEÇÃO ========== //

  get temClientesSelecionados(): boolean {
    return this.clientes.some((cliente) => cliente.selecionado === true)
  }

  get quantidadeClientesSelecionados(): number {
    return this.clientes.filter((cliente) => cliente.selecionado === true).length
  }

  // ========== OPERAÇÕES DE STATUS ========== //

  mudarStatus(novoStatus: CartaoStatus): void {
  const clientesSelecionados = this.clientes.filter(cliente => cliente.selecionado === true);
  const clientesComCartao = clientesSelecionados.filter(cliente => cliente.cartao && cliente.id);

  if (clientesComCartao.length === 0) {
    this.mostrarErro("Nenhum cliente com cartão selecionado");
    return;
  }

  const statusTexto = this.cartaoService.getStatusTexto(novoStatus);

  if (!confirm(`Deseja alterar o status de ${clientesComCartao.length} cartão(ões) para ${statusTexto}?`)) {
    return;
  }

  this.isProcessing = true;

  const alteracoes = clientesComCartao.map(cliente => {
    const { cartao } = cliente;
    if (!cartao?.numero || !cartao?.cvv) return of(null); // segurança
    const dto: AlterarStatusRequestDTO = {
      numero: cartao.numero,
      cvv: cartao.cvv,
      novoStatus: novoStatus
    };

    return this.cartaoService.alterarStatus(dto);
  });

  forkJoin(alteracoes)
    .pipe(
      takeUntil(this.destroy$),
      finalize(() => (this.isProcessing = false))
    )
    .subscribe({
      next: () => {
        this.clientes.forEach(cliente => {
          if (cliente.selecionado && cliente.cartao) {
            if (isValidStatus(novoStatus)) {
              cliente.cartao.status = novoStatus;
            } else {
              console.warn("Status inválido recebido:", novoStatus);
            }
          }
          cliente.selecionado = false;
        });
        this.mostrarSucesso("Status alterado com sucesso!");
      },
      error: (error) => {
        console.error("Erro ao alterar status:", error);
        this.mostrarErro("Erro ao alterar status. Tente novamente.");
      }
    });
}

alternarStatus(cliente: ClienteComCartao): void {
  if (!cliente.cartao) {
    this.mostrarErro("Cartão não encontrado para este cliente.");
    return;
  }

  const cartao = cliente.cartao;

  if (!this.cartaoService.podeAlterarStatus(cartao.status)) {
    this.mostrarErro("Cartão cancelado não pode ter status alterado");
    return;
  }

  let novoStatus: CartaoStatus;
  if (cartao.status === "ativado") {
    novoStatus = "bloqueado";
  } else if (cartao.status === "bloqueado") {
    novoStatus = "ativado";
  } else {
    this.mostrarErro("Status atual não permite alternância");
    return;
  }

  const dto: AlterarStatusRequestDTO = {
    numero: cartao.numero,
    cvv: cartao.cvv,
    novoStatus: novoStatus
  };

  this.isProcessing = true;

  this.cartaoService.alterarStatus(dto)
    .pipe(
      takeUntil(this.destroy$),
      finalize(() => (this.isProcessing = false))
    )
    .subscribe({
      next: (cartaoAtualizado) => {
        const statusRecebido = cartaoAtualizado?.status;

        if (isValidStatus(statusRecebido)) {
          cliente.cartao!.status = statusRecebido;
          this.mostrarSucesso("Status alterado com sucesso!");
        } else {
          console.warn("Status inválido recebido:", statusRecebido);
          this.mostrarErro("Status recebido é inválido.");
        }
      },
      error: (error) => {
        console.error("Erro ao alterar status:", error);
        this.mostrarErro("Erro ao alterar status. Tente novamente.");
      }
    });
}

  // ========== MODAL DETALHES ========== //

  verDetalhes(cliente: ClienteComCartao): void {
    this.clienteSelecionadoDetalhes = cliente

    if (cliente.id) {
      const cartaoAtualizado = this.cartoes.find((c) => c.clienteId === cliente.id)
      this.cartaoSelecionado = cartaoAtualizado || null
    } else {
      this.cartaoSelecionado = null
    }

    this.modalDetalhesAberto = true
  }

  fecharDetalhes(): void {
    this.modalDetalhesAberto = false
    this.clienteSelecionadoDetalhes = null
    this.cartaoSelecionado = null
  }

  // ========== SEGUNDA VIA ========== //

  segundaVia(cliente: ClienteComCartao): void {
    if (!cliente.id || !cliente.cartao) {
      this.mostrarErro("Cliente não possui cartão para solicitar 2ª via")
      return
    }

    this.clienteSegundaVia = cliente
    this.cartaoSegundaVia = cliente.cartao
    this.modalSegundaViaAberto = true
  }

  fecharSegundaVia(): void {
    this.modalSegundaViaAberto = false
    this.clienteSegundaVia = null
    this.cartaoSegundaVia = null
  }

  processarSegundaVia(solicitacao: SegundaViaCartaoRequestDTO): void {
    this.isProcessing = true

    this.cartaoService
      .solicitarSegundaVia(solicitacao.cvv, solicitacao.motivo, solicitacao.numero)
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

  excluirCliente(cliente: ClienteComCartao): void {
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

  trackByClienteId(index: number, cliente: ClienteComCartao): number | undefined {
    return cliente.id
  }

}
