import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, delay } from 'rxjs/operators';

export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
  ultimosDigitos: string;
  status: 'ativo' | 'bloqueado' | 'pendente';
  selecionado: boolean;
}

@Injectable({
  providedIn: 'root'
})

export class ClienteService {

  private clientes: Cliente[] = [
    {
      id: 1,
      nome: 'Ana Silva',
      cpf: '123.456.789-00',
      ultimosDigitos: '1234',
      status: 'ativo',
      selecionado: false
    },
    {
      id: 2,
      nome: 'Bruno Costa',
      cpf: '987.554.321-11',
      ultimosDigitos: '5678',
      status: 'bloqueado',
      selecionado: false
    },
    {
      id: 3,
      nome: 'Carlos Oliveira',
      cpf: '456.789.012-22',
      ultimosDigitos: '9012',
      status: 'ativo',
      selecionado: false
    },
    {
      id: 4,
      nome: 'Sofia Mendes',
      cpf: '321.654.987-33',
      ultimosDigitos: '3456',
      status: 'pendente',
      selecionado: false
    }
  ];

  private clientesSubject = new BehaviorSubject<Cliente[]>(this.clientes);
  public clientes$ = this.clientesSubject.asObservable();

  private nextId = 5;

  constructor() {}

  getClientes(): Observable<Cliente[]> {
    return this.clientes$.pipe(delay(100));
  }

  getClienteById(id: number): Observable<Cliente | undefined> {
    return this.clientes$.pipe(
      map(clientes => clientes.find(c => c.id === id)),
      delay(100)
    );
  }

  createCliente(clienteData: Omit<Cliente, 'id'>): Observable<Cliente> {
    const novoCliente: Cliente = {
      ...clienteData,
      id: this.nextId++
    };

    this.clientes = [...this.clientes, novoCliente];
    this.clientesSubject.next(this.clientes);

    return of(novoCliente).pipe(delay(100));
  }

  updateCliente(id: number, clienteData: Partial<Cliente>): Observable<Cliente> {
    const index = this.clientes.findIndex(c => c.id === id);

    if (index === -1) {
      return throwError(() => new Error(`Cliente com ID ${id} não encontrado`));
    }

    const clienteAtualizado: Cliente = {
      ...this.clientes[index],
      ...clienteData,
      id
    };

    this.clientes = [
      ...this.clientes.slice(0, index),
      clienteAtualizado,
      ...this.clientes.slice(index + 1)
    ];

    this.clientesSubject.next(this.clientes);

    return of(clienteAtualizado).pipe(delay(100));
  }

  deleteCliente(id: number): Observable<boolean> {
    const index = this.clientes.findIndex(c => c.id === id);

    if (index === -1) {
      return throwError(() => new Error(`Cliente com ID ${id} não encontrado`));
    }

    this.clientes = [
      ...this.clientes.slice(0, index),
      ...this.clientes.slice(index + 1)
    ];

    this.clientesSubject.next(this.clientes);

    return of(true).pipe(delay(100));
  }

  alternarStatus(id: number): Observable<Cliente> {
    const cliente = this.clientes.find(c => c.id === id);

    if (!cliente) {
      return throwError(() => new Error(`Cliente com ID ${id} não encontrado`));
    }

    const novoStatus = cliente.status === 'ativo' ? 'bloqueado' : 'ativo';
    return this.updateCliente(id, { status: novoStatus });
  }

  updateStatusEmLote(ids: number[], novoStatus: 'ativo' | 'bloqueado' | 'pendente'): Observable<Cliente[]> {
    const clientesAtualizados: Cliente[] = [];

    this.clientes = this.clientes.map(cliente => {
      if (ids.includes(cliente.id)) {
        const clienteAtualizado = { ...cliente, status: novoStatus, selecionado: false };
        clientesAtualizados.push(clienteAtualizado);
        return clienteAtualizado;
      }
      return cliente;
    });

    this.clientesSubject.next(this.clientes);

    return of(clientesAtualizados).pipe(delay(100));
  }

  filtrarClientes(cpf?: string, nome?: string): Observable<Cliente[]> {
    return this.clientes$.pipe(
      map(clientes => {
        return clientes.filter(cliente => {
          const matchCpf = !cpf || cliente.cpf.includes(cpf);
          const matchNome = !nome ||
            cliente.nome.toLowerCase().includes(nome.toLowerCase());
          return matchCpf && matchNome;
        });
      }),
      delay(100)
    );
  }

  getClientesPorStatus(status: 'ativo' | 'bloqueado' | 'pendente'): Observable<Cliente[]> {
    return this.clientes$.pipe(
      map(clientes => clientes.filter(c => c.status === status)),
      delay(100)
    );
  }
}
