import { Component, OnDestroy, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { ClienteService } from "../../service/cliente";
import { CartaoService } from "../../service/cartao";
import { ExportacaoService } from "../../service/exportacao";
import { Cliente } from "../../models/cliente";
import { Cartao } from "../../models/cartao";
import { Subject, combineLatest, finalize, takeUntil } from "rxjs";
import { MenuLateral } from "../menu-lateral/menu-lateral";

interface ClienteComCartao extends Cliente {
  cartao?: Cartao;
}

@Component({
  selector: "app-relatorio",
  standalone: true,
  imports: [CommonModule, FormsModule, MenuLateral],
  templateUrl: "./relatorio.html",
  styleUrls: ["./relatorio.css"],
})
export class RelatorioComponent implements OnInit, OnDestroy {
  filtroCpf = "";
  filtroNome = "";
  filtroStatus = "todos";

  clientes: ClienteComCartao[] = [];
  cartoes: Cartao[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService,
    private exportacaoService: ExportacaoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

 carregarDados(): void {

   this.clienteService.getClientes()
     .subscribe({
       next: (response) => {
         const clientes = response.content || response;
         this.clientes = clientes.map((cliente: any) => ({
           ...cliente,
           cartao: null,
           selecionado: false
         }));

         // Para cada cliente, buscar os cartões
         this.clientes.forEach(cliente => {
           this.cartaoService.getCartaoByClienteId(cliente.id!).subscribe({
             next: (cartoes) => {
               const cartaoEncontrado = Array.isArray(cartoes) ? cartoes[0] : cartoes;
               cliente.cartao = cartaoEncontrado;
             },
             error: (err) => {
               console.error(`[v0] Erro ao buscar cartão do cliente ${cliente.id}:`, err);
             }
           });
         });
       },
       error: (error) => {
         console.error("[v0] Erro ao carregar dados:", error);
         this.mostrarErro("Erro ao carregar dados. Tente novamente.");
       }
     });
 }

  get clientesFiltrados(): ClienteComCartao[] {
    return this.clienteService.filtrarClientes(
      this.clientes,
      this.filtroCpf,
      this.filtroNome
      //this.filtroStatus
    );
  }

  getStatusLabel(status: string | undefined): string {
    if (!status) return "Sem Cartão";
    return this.cartaoService.getStatusTexto(status);
  }

  getStatusClass(status: string | undefined): string {
    if (!status) return "status-sem-cartao";
    return this.cartaoService.getStatusClass(status);
  }

  exportarPDF(): void {
    this.exportacaoService.exportarRelatorioPDF(this.clientesFiltrados);
  }

  exportarXML(): void {
    this.exportacaoService.exportarRelatorioXML(this.clientesFiltrados);
  }

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

  private mostrarErro(mensagem: string): void {
    alert(mensagem)
  }
}
