package com.bancodigital.controller;

import com.bancodigital.dto.request.AccountRequestDTO;
import com.bancodigital.dto.response.AccountResponseDTO;
import com.bancodigital.service.AccountService;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gerenciamento de contas bancárias")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(
            summary = "Criar nova conta bancária",
            description = "Cadastra uma nova conta bancária vinculada a um usuário existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO response = accountService.createAccount(accountRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar conta por ID",
            description = "Retorna os detalhes de uma conta bancária específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<AccountResponseDTO> getAccountById(
            @Parameter(description = "ID da conta bancária", required = true)
            @PathVariable Long id) {
        AccountResponseDTO response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as contas",
            description = "Retorna uma lista com todas as contas bancárias cadastradas no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> response = accountService.getAllAccounts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    @Operation(
            summary = "Listar contas com paginação",
            description = "Retorna uma lista paginada de contas. Exemplo: /api/accounts/paginated?page=0&size=10&sort=numeroConta,asc"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de contas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<AccountResponseDTO>> getAllAccountsPaginated(
            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<AccountResponseDTO> response = accountService.getAllAccountsPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Listar contas por usuário",
            description = "Retorna todas as contas bancárias vinculadas a um usuário específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas do usuário retornadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByUserId(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        List<AccountResponseDTO> response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar conta bancária",
            description = "Atualiza os dados de uma conta bancária existente (número da conta, agência ou usuário vinculado)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta ou usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @Parameter(description = "ID da conta bancária", required = true)
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO response = accountService.updateAccount(id, accountRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar conta bancária",
            description = "Remove uma conta bancária do sistema. A conta não pode ter saldo positivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Conta possui saldo positivo"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "ID da conta bancária", required = true)
            @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
