import { Component, Input, Output, EventEmitter, SimpleChanges } from "@angular/core"
import { CommonModule } from "@angular/common"
import type { Cliente } from "../../models/cliente"
import type { Cartao } from "../../models/cartao"
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: "app-cliente-detalhes",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./cliente-detalhes.html",
  styleUrls: ["./cliente-detalhes.css"],
})
export class ClienteDetalhesComponent {
  @Input() cliente: Cliente | null = null
  @Input() cartao: Cartao | null = null
  @Input() isOpen = false
  @Output() fechar = new EventEmitter<void>()


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cliente'] || changes['cartao']) {
      console.log('[v0] ClienteDetalhes - Cliente recebido:', this.cliente);
      console.log('[v0] ClienteDetalhes - Cartão recebido:', this.cartao);
    }
  }

  fecharModal(): void {
    this.fechar.emit()
  }

  getStatusTexto(status: string): string {
    const statusMap: Record<string, string> = {
      ativado: "ativado",
      bloqueado: "bloqueado",
      cancelado: "cancelado",
      desativado: "desativado",
    }
    return statusMap[status] || "Sem Cartão"
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      ativado: "badge-ativado",
      bloqueado: "badge-bloqueado",
      cancelado: "badge-cancelado",
      desativado: "badge-desativado",
    }
    return classes[status] || "badge-sem-cartao"
  }

  formatarCPF(cpf: string): string {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  }

  formatarCEP(cep: string): string {
    return cep.replace(/(\d{5})(\d{3})/, "$1-$2")
  }

  formatarNumeroCartao(numero: string): string {
    return numero.replace(/(\d{4})(?=\d)/g, "$1 ")
  }

  onOverlayClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fecharModal()
    }
  }

  formatarValorParaReal(valor: number | string): string {
    let numero = Number(valor);

    if (isNaN(numero)) {
      return 'R$ 0,00';
    }

    return numero.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
  }

  //PDF
  exportarPDF(): void {
    if (!this.cliente) return;
    console.log('Exportando PDF...');

    const doc = new jsPDF();
    let y = 15;

    doc.setFontSize(16);
    doc.text("Relatório do Cliente", 14, y);
    y += 10;

    doc.setFontSize(12);
    doc.text(`Nome: ${this.cliente.nome}`, 14, y); y += 7;
    doc.text(`CPF: ${this.formatarCPF(this.cliente.cpf)}`, 14, y); y += 7;
    doc.text(`Email: ${this.cliente.email}`, 14, y); y += 7;
    doc.text(`Data de Nascimento: ${this.cliente.dataNasc}`, 14, y); y += 7;
    doc.text(`ID: ${this.cliente.id}`, 14, y); y += 10;

    doc.text("Endereço:", 14, y); y += 7;
    const end = this.cliente.endereco;
    doc.text(`Rua: ${end.rua}, Nº ${end.numero}`, 14, y); y += 7;
    doc.text(`Bairro: ${end.bairro}`, 14, y); y += 7;
    doc.text(`Cidade: ${end.cidade}`, 14, y); y += 7;
    doc.text(`CEP: ${this.formatarCEP(end.cep)}`, 14, y); y += 10;

    if (this.cartao) {
      doc.text("Cartão:", 14, y); y += 7;
      doc.text(`Número: ${this.formatarNumeroCartao(this.cartao.numero)}`, 14, y); y += 7;
      doc.text(`Vencimento: ${this.cartao.dataVencimento}`, 14, y); y += 7;
      doc.text(`Tipo de Conta: ${this.cartao.tipoCartao}`, 14, y); y += 7;
      doc.text(`Status: ${this.getStatusTexto(this.cartao.status)}`, 14, y);
    } else {
      doc.text("Cartão: Não associado", 14, y);
    }

    doc.save(`cliente_${this.cliente.id}.pdf`);
  }

  //XML
  exportarXML(): void {
    if (!this.cliente) return;
    console.log('Exportando XML...');

    const cartaoXML = this.cartao
      ? `
      <cartao>
        <numero>${this.cartao.numero}</numero>
        <dataVencimento>${this.cartao.dataVencimento}</dataVencimento>
        <tipoCartao>${this.cartao.tipoCartao}</tipoCartao>
        <status>${this.cartao.status}</status>
      </cartao>`
      : `<cartao>Nenhum</cartao>`;

    const xml = `<?xml version="1.0" encoding="UTF-8"?>
<cliente>
  <id>${this.cliente.id}</id>
  <nome>${this.cliente.nome}</nome>
  <cpf>${this.cliente.cpf}</cpf>
  <email>${this.cliente.email}</email>
  <dataNasc>${this.cliente.dataNasc}</dataNasc>
  <endereco>
    <cep>${this.cliente.endereco.cep}</cep>
    <rua>${this.cliente.endereco.rua}</rua>
    <numero>${this.cliente.endereco.numero}</numero>
    <bairro>${this.cliente.endereco.bairro}</bairro>
    <cidade>${this.cliente.endereco.cidade}</cidade>
    <complemento>${this.cliente.endereco.complemento || ""}</complemento>
  </endereco>
  ${cartaoXML}
</cliente>`;

    const blob = new Blob([xml], { type: "application/xml" });
    saveAs(blob, `cliente_${this.cliente.id}.xml`);
  }
}

function saveAs(blob: Blob, arg1: string) {
  throw new Error("Function not implemented.")
}