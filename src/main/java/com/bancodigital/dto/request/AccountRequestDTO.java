package com.bancodigital.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "Número da conta é obrigatório")
    private String numeroConta;

    @NotBlank(message = "Agência é obrigatória")
    private String agencia;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;
}
