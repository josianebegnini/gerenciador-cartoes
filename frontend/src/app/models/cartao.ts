export interface Cartao {
  id?: number;
  clienteId: number;
  numero: string;
  cvv: string;
  dataVencimento: string;
  status:  'ativado' | 'bloqueado' | '';
  categoriaCartao: string;
  tipoCartao: string;
  motivoSegundaVia?: string;
}
