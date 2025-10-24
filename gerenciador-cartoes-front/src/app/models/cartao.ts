export interface Cartao {
  id?: number;
  clienteId: number;
  numero: string;
  cvv: string;
  dataVencimento: string;
  status: 'desativado' | 'ativado' | 'bloqueado' | 'cancelado' | '';
  categoriaCartao: string;
  tipoCartao: string;
  motivoSegundaVia?: string;
}
