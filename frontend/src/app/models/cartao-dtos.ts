export interface CartaoIdentificacaoRequestDTO {
  clienteId: number
  numeroCartao: string
}

export interface CartaoResponseDTO {
  clienteId: number
  numeroCartao: string
  status: string
  mensagem: string
}

export interface SegundaViaCartaoRequestDTO {
  clienteId: number
  numeroCartao: string
  motivo: string
}

export interface SegundaViaCartaoResponseDTO {
  clienteId: number
  numeroCartaoAntigo: string
  numeroCartaoNovo: string
  dataEmissao: string
  mensagem: string
}
