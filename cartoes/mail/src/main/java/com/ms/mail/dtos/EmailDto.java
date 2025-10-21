package com.ms.mail.dtos;

import java.util.UUID;

public record EmailDto(
        Long userId,
        String emailTo,
        String subject,
        String text
) {
}
