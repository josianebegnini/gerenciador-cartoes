import { Injectable } from "@angular/core";
import { Cliente } from "../models/cliente";
import { Cartao } from "../models/cartao";
import { ClienteService } from "./cliente";
import { CartaoService } from "./cartao";
import { jsPDF } from "jspdf";
import { autoTable } from "jspdf-autotable";

@Injectable({
  providedIn: "root",
})
export class ExportacaoService {
  constructor(
    private clienteService: ClienteService,
    private cartaoService: CartaoService
  ) {}

  exportarRelatorioPDF(clientes: any[]): void {
    const doc = new jsPDF();

    doc.setFontSize(16);
    doc.setTextColor("#7B2D26");
    doc.text("Relatório de Clientes - Detalhado", 14, 15);
    doc.setFontSize(11);
    doc.setTextColor(0, 0, 0);

    const head = [["ID", "Nome", "CPF", "Email", "Data Nasc.", "Endereço", "Conta", "Cartão"]];

    const body = clientes.map((c: any) => [
      c.id ?? "",
      c.nome ?? "",
      c.cpf ?? "",
      c.email ?? "",
      c.dataNasc ?? "",
      c.endereco
        ? `${c.endereco.rua}, ${c.endereco.numero} - ${c.endereco.bairro}, ${c.endereco.cidade}`
        : "---",
      c.conta
        ? `${c.conta.agencia} / ${c.conta.tipo} / R$ ${Number(c.conta.saldo).toLocaleString("pt-BR", {
            minimumFractionDigits: 2,
          })}`
        : "---",
      c.cartao ? `${c.cartao.numero} (${c.cartao.tipoCartao}) - ${c.cartao.status}` : "---",
    ]);

    autoTable(doc, {
      startY: 25,
      head,
      body,
      styles: {
        fontSize: 9,
        cellPadding: 3,
      },
      headStyles: {
        fillColor: [123, 45, 38],
        textColor: 255,
        halign: "center",
        fontStyle: "bold",
      },
      alternateRowStyles: { fillColor: [250, 240, 240] },
      margin: { left: 10, right: 10 },
      didParseCell: (data) => {
        const col = data.column.index;
        const text = String(data.cell.text || "").toLowerCase();

        if (col === 7) {
          if (text.includes("ativado")) {
            data.cell.styles.textColor = [0, 128, 0];
            data.cell.styles.fontStyle = "bold";
          } else if (text.includes("cancelado")) {
            data.cell.styles.textColor = [255, 140, 0];
            data.cell.styles.fontStyle = "bold";
          } else if (text.includes("bloqueado")) {
            data.cell.styles.textColor = [178, 34, 34];
            data.cell.styles.fontStyle = "bold";
          } else if (text.includes("desativado")) {
            data.cell.styles.textColor = [105, 105, 105];
            data.cell.styles.fontStyle = "bold";
          }
        }
      },
    });

    const pageHeight = doc.internal.pageSize.height;
    doc.setFontSize(9);
    doc.setTextColor("#B22222");
    doc.text(`Gerado em: ${new Date().toLocaleString("pt-BR")}`, 14, pageHeight - 10);

    doc.save("relatorio-clientes-tabela.pdf");
  }

  exportarRelatorioXML(clientes: any[]): void {
    const xml = clientes
      .map(
        (c) => `
        <cliente>
          <nome>${c.nome}</nome>
          <email>${c.email}</email>
          <cpf>${c.cpf}</cpf>
          <status>${c.cartao?.status ?? "Sem Cartão"}</status>
        </cliente>
      `
      )
      .join("");

    const blob = new Blob([`<clientes>${xml}</clientes>`], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "relatorio-clientes.xml";
    a.click();
    URL.revokeObjectURL(url);
  }

  exportarClienteJSON(cliente: Cliente, cartao: Cartao | null): void {
    const dados = {
      cliente,
      cartao: cartao || null,
    };

    const json = JSON.stringify(dados, null, 2);
    const blob = new Blob([json], { type: "application/json" });
    this.downloadBlob(blob, `cliente_${cliente.id}.json`);
  }

  exportarClientesCSV(clientes: Cliente[]): void {
    const headers = ["ID", "Nome", "CPF", "Email", "Data Nascimento", "Cidade", "CEP"];
    const linhas = clientes.map((c) => [
      c.id || "",
      c.nome,
      this.clienteService.formatarCPF(c.cpf),
      c.email,
      c.dataNasc,
      c.endereco.cidade,
      this.clienteService.formatarCEP(c.endereco.cep),
    ]);

    const csv = [headers.join(","), ...linhas.map((linha) => linha.join(","))].join("\n");

    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    this.downloadBlob(blob, "clientes.csv");
  }

  private downloadBlob(blob: Blob, nomeArquivo: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = nomeArquivo;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
