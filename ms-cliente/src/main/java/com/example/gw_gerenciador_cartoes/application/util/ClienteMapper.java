package com.example.gw_gerenciador_cartoes.application.util;

import com.example.gw_gerenciador_cartoes.application.dto.ClienteDTO;
import com.example.gw_gerenciador_cartoes.domain.model.Cliente;
import com.example.gw_gerenciador_cartoes.domain.model.Endereco;
import org.springframework.beans.BeanUtils;

public class ClienteMapper {

    public static Cliente FromDTOtoEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        BeanUtils.copyProperties(dto, cliente);

        Endereco endereco = new Endereco();
        BeanUtils.copyProperties(dto.enderecoDTO(), endereco);
        cliente.setEndereco(endereco);

        return cliente;
    }
}