export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
  ultimosDigitos: string;
  status: 'ativo' | 'bloqueado' | 'pendente';
  selecionado: boolean;
}
