export interface Cartao {
  clienteId: number;
  numero: string;
  cvv: string;
  dataVencimento: string;
  tipoConta: string;
  status: 'ativo' | 'bloqueado' | 'pendente' | '';
  formatoCartao: string;
}
