import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu-lateral',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-lateral.html',
  styleUrls: ['./menu-lateral.css']
})
export class MenuLateral {
  @Output() novoCliente = new EventEmitter<void>();
  @Output() navegarHome = new EventEmitter<void>();
  @Output() navegarCartoes = new EventEmitter<void>();
  @Output() navegarRelatorios = new EventEmitter<void>();
  @Output() irLogin = new EventEmitter<void>();

  onNovoCliente(): void {
    this.novoCliente.emit();
  }

  onNavegarHome(): void {
    this.navegarHome.emit();
  }

  onNavegarCartoes(): void {
    this.navegarCartoes.emit();
  }

  onNavegarRelatorios(): void {
    this.navegarRelatorios.emit();
  }

  onNavegarLogout(): void {
    this.irLogin.emit();
  }
}
