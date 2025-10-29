import { Component, OnDestroy, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { ClienteService } from "../../service/cliente";
import { CartaoService } from "../../service/cartao";
import { ExportacaoService } from "../../service/exportacao";
import { Cliente } from "../../models/cliente";
import { Cartao } from "../../models/cartao";
import { Subject, combineLatest, takeUntil } from "rxjs";
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
    combineLatest([
      this.clienteService.getClientes(),
      this.cartaoService.getCartoes(),
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ([clientes, cartoes]) => {
          this.cartoes = cartoes;
          this.clientes = clientes.map((cliente: { id: number }) => ({
            ...cliente,
            cartao: cartoes.find((c) => c.clienteId === cliente.id),
          }));
        },
        error: (error) => {
          console.error("[v0] Erro ao carregar dados:", error);
        },
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
}
