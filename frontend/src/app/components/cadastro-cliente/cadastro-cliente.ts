import { Component, OnDestroy } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MenuLateral } from "../menu-lateral/menu-lateral";
import { ClienteService } from "../../service/cliente";
import { Router } from "@angular/router";
import { Cliente } from "../../models/cliente";
import { Subject, takeUntil } from "rxjs";

@Component({
  selector: "app-novo-cliente",
  standalone: true,
  imports: [CommonModule, FormsModule, MenuLateral],
  templateUrl: "./cadastro-cliente.html",
  styleUrls: ["./cadastro-cliente.css"],
})
export class CadastroCliente implements OnDestroy {
  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  cliente: Cliente = {
    nome: "",
    email: "",
    dataNasc: "",
    cpf: "",
    selecionado: false,
    endereco: {
      cidade: "",
      bairro: "",
      rua: "",
      cep: "",
      complemento: "",
      numero: "",
    },
    conta: {
      agencia: "",
      tipo: "",
      saldo: 0,
    },
  };

  isLoading = false;
  mensagem = "";
  mensagemTipo: "success" | "error" | "" = "";

  private destroy$ = new Subject<void>();

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

  formatarCPF(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cliente.cpf = this.clienteService.formatarCPFAoDigitar(input.value);
  }

  formatarSaldo(event: Event): void {
    const input = event.target as HTMLInputElement;
    const valorFormatado = this.clienteService.formatarSaldoAoDigitar(input.value);
    input.value = valorFormatado;

    const valorNumerico = input.value.replace(/\D/g, "");
    this.cliente.conta!.saldo = Number.parseFloat(valorNumerico) / 100;
  }

  formatarCEP(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cliente.endereco.cep = this.clienteService.formatarCEPAoDigitar(input.value);
  }

  criarConta(): void {
    const validacao = this.clienteService.validarCliente(this.cliente);

    if (!validacao.valido) {
      this.mostrarMensagem(validacao.erros[0], "error");
      return;
    }

    this.isLoading = true;
    this.mensagem = "";

    this.clienteService
      .createCliente(this.cliente)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cliente) => {
          console.log("[v0] Cliente criado:", cliente);
          this.mostrarMensagem("Cliente cadastrado com sucesso!", "success");
          this.isLoading = false;

          setTimeout(() => {
            this.router.navigate(["/home"]);
          }, 1500);
        },
        error: (error) => {
          console.error("[v0] Erro ao criar cliente:", error);
          this.mostrarMensagem("Erro ao cadastrar cliente. Tente novamente.", "error");
          this.isLoading = false;
        },
      });
  }

  cancelar(): void {
    this.router.navigate(["/home"]);
  }

  private mostrarMensagem(texto: string, tipo: "success" | "error"): void {
    this.mensagem = texto;
    this.mensagemTipo = tipo;

    setTimeout(() => {
      this.mensagem = "";
      this.mensagemTipo = "";
    }, 5000);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
