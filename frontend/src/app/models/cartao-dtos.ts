// ========== DTOS PARA OPERAÇÕES HTTP ========== //

export interface AlterarStatusRequestDTO {
  numero: string
  cvv: string
  novoStatus: string
}

export interface SegundaViaCartaoRequestDTO {
  cvv: string
  numero: string
  motivo: string
}

export interface SegundaViaCartaoResponseDTO {
  numero: number
  cvv: string
  motivoSegundaVia: string
  dataVencimento: string
  status: string
}

export interface CartaoRequestDTO {
  id?: number;
  clienteId: number;
  numero: string;
  cvv: string;
  dataVencimento: string;
  dataCriacao?: string;
  status:  "ATIVADO" | "BLOQUEADO" | "DESATIVADO" | "REJEITADO" | "CANCELADO";
  motivoStatus?: string;
  tipoCartao: string;
  tipoEmissao: string;
  limite: number;
}
