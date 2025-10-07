package com.bancodigital.dto.request;

import com.bancodigital.model.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "Tipo de transação é obrigatório")
    private TransactionType tipo;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    private String descricao;

    @NotNull(message = "ID da conta é obrigatório")
    private Long accountId;

    private Long accountDestinoId;
}
