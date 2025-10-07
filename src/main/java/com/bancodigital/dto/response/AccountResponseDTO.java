package com.bancodigital.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {

    private Long id;
    private String numeroConta;
    private String agencia;
    private BigDecimal saldo;
    private Long userId;
    private String nomeUsuario;
    private LocalDateTime createdAt;
}
