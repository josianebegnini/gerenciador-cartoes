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

  // ========== LIMPANDO CONTENTEUDO DO FORM ========== //

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
    },
  };

  // ========== VÁRIAVEIS DE CARREGAMENTO DE DADOS ========== //

  isLoading = false;
  mensagem = "";
  mensagemTipo: "success" | "error" | "" = "";

  private destroy$ = new Subject<void>();

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

  cancelar(): void {
    this.router.navigate(["/home"]);
  }

  // ========== FORMATAÇÃO ========== //

  formatarCPF(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cliente.cpf = this.clienteService.formatarCPFAoDigitar(input.value);
  }

  formatarCEP(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.cliente.endereco.cep = this.clienteService.formatarCEPAoDigitar(input.value);
  }

  // ========== CRIAÇÃO DE CONTA/CLIENTE ========== //

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
          this.mostrarMensagem("Cliente cadastrado com sucesso!", "success");
          this.isLoading = false;

          setTimeout(() => {
            this.router.navigate(["/home"]);
          }, 1500);
        },
        error: (error) => {
          console.error("Erro ao criar cliente:", error);
          this.mostrarMensagem("Erro ao cadastrar cliente. Tente novamente.", "error");
          this.isLoading = false;
        },
      });
  }

  // ========== GERAÇÃO DE MENSAGEM PARA CONTROLE DE REQUEST ========== //

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
