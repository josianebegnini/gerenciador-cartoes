import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, delay } from 'rxjs/operators';
import { Cliente } from '../models/cliente';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private clientes: Cliente[] = [
    {
      id: 1,
      nome: 'Carlos Andrade',
      email: 'carlos.andrade@example.com',
      dataNasc: '1980-03-15',
      cpf: '123.456.789-01',
      selecionado: false,
      endereco: {
        cidade: 'São Paulo',
        bairro: 'Moema',
        rua: 'Av. Ibirapuera',
        cep: '04548-000',
        complemento: 'Apto 12',
        numero: '1500'
      },
      conta:{
        agencia: '001',
        tipo: 'corrente',
        saldo: 10000.00
      }
    },
    {
      id: 2,
      nome: 'Fernanda Lima',
      email: 'fernanda.lima@example.com',
      dataNasc: '1992-07-22',
      cpf: '987.654.321-00',
      selecionado: false,
      endereco: {
        cidade: 'Rio de Janeiro',
        bairro: 'Copacabana',
        rua: 'Rua Barata Ribeiro',
        cep: '22011-001',
        complemento: 'Cobertura',
        numero: '300'
      },
      conta:{
        agencia: '002',
        tipo: 'corrente',
        saldo: 10000.00
      }
    },
    {
      id: 3,
      nome: 'Lucas Pereira',
      email: 'lucas.pereira@example.com',
      dataNasc: '1988-11-05',
      cpf: '456.789.123-45',
      selecionado: false,
      endereco: {
        cidade: 'Belo Horizonte',
        bairro: 'Savassi',
        rua: 'Rua Pernambuco',
        cep: '30130-150',
        complemento: 'Sala 5',
        numero: '450'
      },
      conta:{
        agencia: '002',
        tipo: 'corrente',
        saldo: 10000.00
      }
    }
  ];

  private clientesSubject = new BehaviorSubject<Cliente[]>(this.clientes);
  public clientes$ = this.clientesSubject.asObservable();

  private nextId = 4;

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

  toggleSelecao(id: number): Observable<Cliente> {
    const cliente = this.clientes.find(c => c.id === id);

    if (!cliente) {
      return throwError(() => new Error(`Cliente com ID ${id} não encontrado`));
    }

    return this.updateCliente(id, { selecionado: !cliente.selecionado });
  }

  desselecionarTodos(): Observable<Cliente[]> {
    this.clientes = this.clientes.map(cliente => ({
      ...cliente,
      selecionado: false
    }));

    this.clientesSubject.next(this.clientes);

    return of(this.clientes).pipe(delay(100));
  }

  getClientesSelecionados(): Observable<Cliente[]> {
    return this.clientes$.pipe(
      map(clientes => clientes.filter(c => c.selecionado)),
      delay(100)
    );
  }
}
