// ========== ENTIDADE PRINCIPAL ========== //

export interface Cartao {
  id?: number;
  clienteId: number;
  numero: string;
  cvv: string;
  dataVencimento: string;
  dataCriacao?: string;
  status:  'ativado' | 'bloqueado' | 'desativado' | 'rejeitado' | 'cancelado' ;
  motivoStatus?: string;
  tipoCartao: string;
  tipoEmissao: string;
  limite: number;
}
