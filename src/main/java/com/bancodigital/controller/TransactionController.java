package com.bancodigital.controller;

import com.bancodigital.dto.request.TransactionRequestDTO;
import com.bancodigital.dto.response.TransactionResponseDTO;
import com.bancodigital.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de transações financeiras (depósito, saque e transferência)")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(
            summary = "Criar nova transação",
            description = "Realiza uma nova transação financeira: DEPOSITO, SAQUE ou TRANSFERENCIA. " +
                    "Para transferências, é obrigatório informar o accountDestinoId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Conta origem ou destino não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO response = transactionService.createTransaction(transactionRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar transação por ID",
            description = "Retorna os detalhes de uma transação específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @Parameter(description = "ID da transação", required = true)
            @PathVariable Long id) {
        TransactionResponseDTO response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as transações",
            description = "Retorna uma lista com todas as transações realizadas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        List<TransactionResponseDTO> response = transactionService.getAllTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    @Operation(
            summary = "Listar transações com paginação",
            description = "Retorna uma lista paginada de transações. Exemplo: /api/transactions/paginated?page=0&size=10&sort=createdAt,desc"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de transações retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactionsPaginated(
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<TransactionResponseDTO> response = transactionService.getAllTransactionsPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    @Operation(
            summary = "Consultar extrato da conta",
            description = "Retorna todas as transações (depósitos, saques e transferências) de uma conta específica, " +
                    "ordenadas da mais recente para a mais antiga"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccountId(
            @Parameter(description = "ID da conta bancária", required = true)
            @PathVariable Long accountId) {
        List<TransactionResponseDTO> response = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(response);
    }
}
