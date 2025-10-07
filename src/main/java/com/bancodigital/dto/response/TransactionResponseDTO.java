package com.bancodigital.dto.response;

import com.bancodigital.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Long id;
    private TransactionType tipo;
    private BigDecimal valor;
    private String descricao;
    private Long accountId;
    private String numeroConta;
    private Long accountDestinoId;
    private LocalDateTime createdAt;
}
