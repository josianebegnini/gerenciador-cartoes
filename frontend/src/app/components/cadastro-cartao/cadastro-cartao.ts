import { Component, OnDestroy } from "@angular/core";
import { Router } from "@angular/router";
import { Subject, takeUntil } from "rxjs";
import { ClienteService } from "../../service/cliente";
import { CartaoService } from "../../service/cartao";
import { Cliente } from "../../models/cliente";
import { Cartao } from "../../models/cartao";
import { MenuLateral } from "../menu-lateral/menu-lateral";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { CartaoRequestDTO } from "../../models/cartao-dtos";

@Component({
  selector: "app-cadastro-cartao",
  templateUrl: "./cadastro-cartao.html",
  styleUrls: ["./cadastro-cartao.css"],
  standalone: true,
  imports: [MenuLateral, FormsModule, CommonModule],
})

export class CadastroCartaoComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  // ========== VÁRIAVEIS DE BUSCA DE CLIENTE ========== //

  cpfBusca = "";
  clienteEncontrado: Cliente | null = null;
  buscandoCliente = false;
  erroCliente = "";
  isLoading = false;

  // ========== LIMPANDO CONTENTEUDO DO FORM ========== //

  cartao: Cartao = {
    clienteId: 0,
    numero: "",
    cvv: "",
    dataVencimento: "",
    status: 'ativado',
    tipoCartao: "",
    tipoEmissao: "",
    limite: 0
  };

  carregando = false;
  mensagemSucesso = "";
  mensagemErro = "";

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private router: Router
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ========== NAVEGAÇÃO ========== //

  novoCliente(): void {
    this.router.navigate(["/cadastro-cliente"]);
  }

  onNavegarHome(): void {
    this.router.navigate(["/home"]);
  }

  onNavegarCartoes(): void {
    this.router.navigate(["/cadastro-cartao"]);
  }

  onNavegarRelatorios(): void {
    this.router.navigate(["/relatorio"]);
  }

  onNavegarLogout(): void {
    this.router.navigate(["/login"]);
  }

  // ========== FORMATAÇÃO ========== //

  formatarCPF(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cpfBusca = this.clienteService.aplicarMascaraCPF(input.value);
  }

  /*formatarNumeroCartao(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cartao.numero = this.cartaoService.mascaraNumeroCartao(input.value);
  }

  formatarCVV(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cartao.cvv = this.cartaoService.aplicarMascaraCVV(input.value);
  }*/

  formatarDataVencimento(event: Event): void {
    const input = event.target as HTMLInputElement;
    const formatado = this.cartaoService.formatarDataVencimento(input.value);
    input.value = formatado;
    this.cartao.dataVencimento = formatado;
  }

  formatarLimite(event: Event): void {
  const input = event.target as HTMLInputElement;
  const valorDigitado = input.value;

  const valorNumerico = valorDigitado.replace(/\D/g, "");

  if (!valorNumerico) {
    input.value = "";
    this.cartao.limite = 0;
    return;
  }

  const valorFloat = Number(valorNumerico) / 100;
  this.cartao.limite = valorFloat;

  input.value = valorFloat.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

  // ========== CARREGAR DADOS DO CLIENTE ========== //

  buscarClientePorCPF(): void {
  const valido = this.clienteService.validarCPF(this.cpfBusca);

  if (!valido) {
    console.error("CPF inválido", this.cpfBusca);
    this.erroCliente = "CPF inválido";
    return;
  }

  this.buscandoCliente = true;
  this.erroCliente = "";
  this.clienteEncontrado = null;

  const cpfLimpo = this.cpfBusca.replace(/\D/g, "");

  this.clienteService
    .getClientes()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (res) => {
        const clientes = Array.isArray(res) ? res : res.content;

        if (!Array.isArray(clientes)) {
          console.error("Resposta inesperada da API:", res);
          this.erroCliente = "Erro ao buscar cliente. Tente novamente.";
          this.buscandoCliente = false;
          return;
        }

        const cliente = clientes.find((c: { cpf: string }) => c.cpf.replace(/\D/g, "") === cpfLimpo);

        if (cliente) {
          this.clienteEncontrado = cliente;
          this.cartao.clienteId = cliente.id!;
          this.erroCliente = "";
        } else {
          this.erroCliente = "Cliente não encontrado. Verifique o CPF ou cadastre o cliente primeiro.";
        }

        this.buscandoCliente = false;
      },
      error: (error) => {
        console.error("Erro ao buscar cliente:", error);
        this.erroCliente = "Erro ao buscar cliente. Tente novamente.";
        this.buscandoCliente = false;
      },
    });
}

  // ========== VALIDAÇÃO ========== //

  validarFormulario(): boolean {
    if (!this.clienteEncontrado) {
      this.mensagemErro = "Por favor, busque e selecione um cliente válido";
      return false;
    }

    const validacao = this.cartaoService.validarCartao(this.cartao);

    if (!validacao.valido) {
      this.mensagemErro = validacao.erros[0];
      return false;
    }

    return true;
  }


 converterDataVencimentoParaISO(data: string): string {
  const [mes, ano] = data.split("/");

  if (!mes || !ano || mes.length !== 2 || ano.length !== 2) return "";

  const anoCompleto = 2000 + parseInt(ano, 10); // Ex: "25" vira "2025"
  const mesNum = parseInt(mes, 10);

  // Cria a data no primeiro dia do mês
  const dataObj = new Date(anoCompleto, mesNum - 1, 1);

  return dataObj.toISOString(); // Ex: "2025-12-01T00:00:00.000Z"
}


  // ========== CRIAÇÃO DE CARTÃO ========== //

  criarCartao(): void {
  this.mensagemErro = "";
  this.mensagemSucesso = "";

  if (!this.validarFormulario()) {
    return;
  }

  const dto: Cartao = {
    clienteId: this.cartao.clienteId,
    numero: this.cartao.numero.replace(/\s+/g, ""), // remove espaços
    cvv: this.cartao.cvv,
    dataVencimento: this.converterDataVencimentoParaISO(this.cartao.dataVencimento).substring(0, 10), // yyyy-MM-dd
    status: this.cartao.status || "ativado", // enum padronizado
    motivoStatus: "Cadastro inicial",
    tipoCartao: (this.cartao.tipoCartao || "CREDITO").toUpperCase(), // enum padronizado
    tipoEmissao: (this.cartao.tipoEmissao || "VIRTUAL").toUpperCase(), // enum padronizado
    limite: typeof this.cartao.limite === "string"
      ? parseFloat(this.cartao.limite)
      : this.cartao.limite > 0
        ? this.cartao.limite
        : 1000 // valor padrão
  };

  console.log("DTO tratado:", dto);

  this.carregando = true;

  this.cartaoService
    .createCartao(dto)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: () => {
        this.mensagemSucesso = "Cartão cadastrado com sucesso!";
        this.carregando = false;

        setTimeout(() => {
          this.router.navigate(["/home"]);
        }, 1000);
      },
      error: (error) => {
        console.error("Erro ao criar cartão:", error);
        this.mensagemErro = error.error?.message || "Erro ao cadastrar cartão. Tente novamente.";
        this.carregando = false;
      },
    });
}


  // ========== CANCELANDO OPERAÇÃO DE CADASTRO ========== //

  cancelar(): void {
    this.cpfBusca = "";
    this.clienteEncontrado = null;
    this.erroCliente = "";
    this.cartao = {
      clienteId: 0,
      numero: "",
      cvv: "",
      dataVencimento: "",
      status: 'ativado',
      tipoCartao: "",
      tipoEmissao: "",
      limite: 0
    };
    this.mensagemErro = "";
    this.mensagemSucesso = "";
    this.router.navigate(["/home"]);
  }
}
