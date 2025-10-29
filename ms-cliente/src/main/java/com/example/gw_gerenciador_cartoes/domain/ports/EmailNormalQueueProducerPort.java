package com.example.gw_gerenciador_cartoes.domain.ports;

import com.example.gw_gerenciador_cartoes.infra.dto.EmailMessageDTO;

public interface EmailNormalQueueProducerPort {
    void sendEmailNormalQueue(EmailMessageDTO emailMessageDTO);
}
