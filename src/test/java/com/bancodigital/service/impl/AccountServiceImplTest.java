package com.bancodigital.service.impl;

import com.bancodigital.dto.request.AccountRequestDTO;
import com.bancodigital.dto.response.AccountResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.exception.ResourceNotFoundException;
import com.bancodigital.model.Account;
import com.bancodigital.model.User;
import com.bancodigital.repository.AccountRepository;
import com.bancodigital.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AccountService")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User user;
    private Account account;
    private AccountRequestDTO accountRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNome("João Silva");
        user.setEmail("joao@email.com");

        account = new Account();
        account.setId(1L);
        account.setNumeroConta("123456");
        account.setAgencia("0001");
        account.setSaldo(BigDecimal.ZERO);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());

        accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setNumeroConta("123456");
        accountRequestDTO.setAgencia("0001");
        accountRequestDTO.setUserId(1L);
    }

    @Test
    @DisplayName("Deve criar conta com sucesso")
    void shouldCreateAccountSuccessfully() {
        when(accountRepository.existsByNumeroConta(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDTO response = accountService.createAccount(accountRequestDTO);

        assertNotNull(response);
        assertEquals("123456", response.getNumeroConta());
        assertEquals(BigDecimal.ZERO, response.getSaldo());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar conta com número duplicado")
    void shouldThrowExceptionWhenAccountNumberExists() {
        when(accountRepository.existsByNumeroConta(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(accountRequestDTO)
        );

        assertEquals("Número de conta já cadastrado", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar conta para usuário inexistente")
    void shouldThrowExceptionWhenUserNotFound() {
        when(accountRepository.existsByNumeroConta(anyString())).thenReturn(false);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        accountRequestDTO.setUserId(999L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.createAccount(accountRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve buscar conta por ID com sucesso")
    void shouldGetAccountByIdSuccessfully() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponseDTO response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123456", response.getNumeroConta());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar conta com saldo positivo")
    void shouldThrowExceptionWhenDeletingAccountWithBalance() {
        account.setSaldo(new BigDecimal("100.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.deleteAccount(1L)
        );

        assertEquals("Não é possível excluir conta com saldo positivo", exception.getMessage());
        verify(accountRepository, never()).delete(any(Account.class));
    }
}
