import { Conta } from "./conta";
import { Endereco } from "./endereco";

export interface Cliente {
  id?: number;
  nome: string;
  email: string;
  dataNasc: string;
  cpf: string;
  selecionado: boolean;
  endereco: Endereco;
  conta: Conta;
}
