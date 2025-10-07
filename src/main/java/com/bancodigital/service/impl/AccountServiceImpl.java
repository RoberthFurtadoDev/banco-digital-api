package com.bancodigital.service.impl;

import com.bancodigital.dto.request.AccountRequestDTO;
import com.bancodigital.dto.response.AccountResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.exception.ResourceNotFoundException;
import com.bancodigital.model.Account;
import com.bancodigital.model.User;
import com.bancodigital.repository.AccountRepository;
import com.bancodigital.repository.UserRepository;
import com.bancodigital.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        logger.info("Criando nova conta: {}", accountRequestDTO.getNumeroConta());

        if (accountRepository.existsByNumeroConta(accountRequestDTO.getNumeroConta())) {
            logger.warn("Tentativa de criar conta com número duplicado: {}", accountRequestDTO.getNumeroConta());
            throw new BusinessException("Número de conta já cadastrado");
        }

        User user = findUserById(accountRequestDTO.getUserId());

        Account account = convertToEntity(accountRequestDTO, user);
        Account savedAccount = accountRepository.save(account);

        logger.info("Conta criada com sucesso. ID: {}", savedAccount.getId());
        return convertToResponseDTO(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long id) {
        logger.debug("Buscando conta com ID: {}", id);
        Account account = findAccountById(id);
        return convertToResponseDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        logger.debug("Listando todas as contas");
        return accountRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponseDTO> getAllAccountsPaginated(Pageable pageable) {
        logger.debug("Listando contas com paginação: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return accountRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByUserId(Long userId) {
        logger.debug("Buscando contas do usuário: {}", userId);
        findUserById(userId);

        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequestDTO) {
        logger.info("Atualizando conta com ID: {}", id);
        Account account = findAccountById(id);

        if (!account.getNumeroConta().equals(accountRequestDTO.getNumeroConta()) &&
                accountRepository.existsByNumeroConta(accountRequestDTO.getNumeroConta())) {
            logger.warn("Tentativa de atualizar conta {} com número duplicado", id);
            throw new BusinessException("Número de conta já cadastrado");
        }

        User user = findUserById(accountRequestDTO.getUserId());

        updateAccountFields(account, accountRequestDTO, user);
        Account updatedAccount = accountRepository.save(account);

        logger.info("Conta {} atualizada com sucesso", id);
        return convertToResponseDTO(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        logger.info("Tentando deletar conta com ID: {}", id);
        Account account = findAccountById(id);

        if (account.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            logger.warn("Tentativa de deletar conta {} com saldo positivo: {}", id, account.getSaldo());
            throw new BusinessException("Não é possível excluir conta com saldo positivo");
        }

        accountRepository.delete(account);
        logger.info("Conta {} deletada com sucesso", id);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    private Account convertToEntity(AccountRequestDTO dto, User user) {
        Account account = new Account();
        account.setNumeroConta(dto.getNumeroConta());
        account.setAgencia(dto.getAgencia());
        account.setSaldo(BigDecimal.ZERO);
        account.setUser(user);
        return account;
    }

    private void updateAccountFields(Account account, AccountRequestDTO dto, User user) {
        account.setNumeroConta(dto.getNumeroConta());
        account.setAgencia(dto.getAgencia());
        account.setUser(user);
    }

    private AccountResponseDTO convertToResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getNumeroConta(),
                account.getAgencia(),
                account.getSaldo(),
                account.getUser().getId(),
                account.getUser().getNome(),
                account.getCreatedAt()
        );
    }
}
