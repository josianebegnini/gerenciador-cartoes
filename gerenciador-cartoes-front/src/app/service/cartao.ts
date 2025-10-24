import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cartao } from '../models/cartao';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})

export class CartaoService {
  private cartoes: Cartao[] = [
    {
      clienteId: 1,
      numero: '4532 1234 5678 9010',
      cvv: '123',
      dataVencimento: '12/2027',
      tipoCartao: 'Corrente',
      status: 'ativado',
      categoriaCartao: 'Visa'
    },
    {
      clienteId: 2,
      numero: '5425 2334 3010 9876',
      cvv: '456',
      dataVencimento: '08/2026',
      tipoCartao: 'Poupança',
      status: 'ativado',
      categoriaCartao: 'Mastercard'
    },
    {
      clienteId: 3,
      numero: '3782 822463 10005',
      cvv: '789',
      dataVencimento: '03/2028',
      tipoCartao: 'Corrente',
      status: 'ativado',
      categoriaCartao: 'American Express'
    }
  ];

  private cartoesSubject = new BehaviorSubject<Cartao[]>(this.cartoes);
  public cartoes$ = this.cartoesSubject.asObservable();

  constructor() {}

  getCartoes(): Observable<Cartao[]> {
    return this.cartoes$.pipe(delay(100));
  }

  getCartoesPorCliente(clienteId: number): Observable<Cartao[]> {
    return this.cartoes$.pipe(
      map(cartoes => cartoes.filter(c => c.clienteId === clienteId)),
      delay(100)
    );
  }

  getCartao(clienteId: number): Observable<Cartao> {
    const cartao = this.cartoes.find(c => c.clienteId === clienteId);

    if (!cartao) {
      return throwError(() => new Error(`Cartão do cliente ${clienteId} não encontrado`));
    }

    return of(cartao).pipe(delay(100));
  }

  createCartao(cartao: Cartao): Observable<Cartao> {
    const cartaoExistente = this.cartoes.find(c => c.clienteId === cartao.clienteId);

    if (cartaoExistente) {
      return throwError(() => new Error('Cliente já possui um cartão cadastrado'));
    }

    const novoCartao: Cartao = { ...cartao };

    this.cartoes = [...this.cartoes, novoCartao];
    this.cartoesSubject.next(this.cartoes);

    return of(novoCartao).pipe(delay(100));
  }

  updateCartao(clienteId: number, cartaoData: Partial<Cartao>): Observable<Cartao> {
    const index = this.cartoes.findIndex(c => c.clienteId === clienteId);

    if (index === -1) {
      return throwError(() => new Error(`Cartão do cliente ${clienteId} não encontrado`));
    }

    const cartaoAtualizado: Cartao = {
      ...this.cartoes[index],
      ...cartaoData,
      clienteId
    };

    this.cartoes = [
      ...this.cartoes.slice(0, index),
      cartaoAtualizado,
      ...this.cartoes.slice(index + 1)
    ];

    this.cartoesSubject.next(this.cartoes);

    return of(cartaoAtualizado).pipe(delay(100));
  }

  updateStatus(clienteId: number, status: 'desativado' | 'ativado' | 'bloqueado' | 'cancelado'): Observable<Cartao> {
    return this.updateCartao(clienteId, { status });
  }

  updateStatusEmLote(clienteIds: number[], status: 'desativado' | 'ativado' | 'bloqueado' | 'cancelado'): Observable<Cartao[]> {
    const cartoesAtualizados: Cartao[] = [];

    clienteIds.forEach(clienteId => {
      const index = this.cartoes.findIndex(c => c.clienteId === clienteId);

      if (index !== -1) {
        this.cartoes[index] = {
          ...this.cartoes[index],
          status
        };
        cartoesAtualizados.push(this.cartoes[index]);
      }
    });

    this.cartoesSubject.next([...this.cartoes]);

    return of(cartoesAtualizados).pipe(delay(100));
  }

  alternarStatus(clienteId: number): Observable<Cartao> {
    const cartao = this.cartoes.find(c => c.clienteId === clienteId);

    if (!cartao) {
      return throwError(() => new Error(`Cartão do cliente ${clienteId} não encontrado`));
    }

    const statusAtual = cartao.status;
    let novoStatus: 'desativado' | 'ativado' | 'bloqueado' | 'cancelado';

    if (statusAtual === 'ativado') {
      novoStatus = 'bloqueado';
    } else if (statusAtual === 'bloqueado') {
      novoStatus = 'cancelado';
    } else {
      novoStatus = 'ativado';
    }

    return this.updateStatus(clienteId, novoStatus);
  }

  deleteCartao(clienteId: number): Observable<void> {
    const index = this.cartoes.findIndex(c => c.clienteId === clienteId);

    if (index === -1) {
      return throwError(() => new Error(`Cartão do cliente ${clienteId} não encontrado`));
    }

    this.cartoes = [
      ...this.cartoes.slice(0, index),
      ...this.cartoes.slice(index + 1)
    ];

    this.cartoesSubject.next(this.cartoes);

    return of(void 0).pipe(delay(100));
  }

  solicitarSegundaVia(clienteId: number): Observable<Cartao> {
    const cartao = this.cartoes.find(c => c.clienteId === clienteId);

    if (!cartao) {
      return throwError(() => new Error(`Cartão do cliente ${clienteId} não encontrado`));
    }

    const novoNumero = this.gerarNumeroCartao(cartao.categoriaCartao);

    return this.updateCartao(clienteId, {
      numero: novoNumero,
      status: 'ativado',
    });
  }

  private gerarNumeroCartao(formatoCartao: string): string {
    const prefixos: { [key: string]: string } = {
      'Visa': '4532',
      'Mastercard': '5425',
      'American Express': '3782',
      'Elo': '6362'
    };

    const prefixo = prefixos[formatoCartao] || '4532';
    const randomDigits = Math.floor(Math.random() * 1000000000000).toString().padStart(12, '0');

    if (formatoCartao === 'American Express') {
      return `${prefixo} ${randomDigits.substring(0, 6)} ${randomDigits.substring(6, 11)}`;
    }

    return `${prefixo} ${randomDigits.substring(0, 4)} ${randomDigits.substring(4, 8)} ${randomDigits.substring(8, 12)}`;
  }
}
