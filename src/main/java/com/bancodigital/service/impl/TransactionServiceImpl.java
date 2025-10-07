package com.bancodigital.service.impl;

import com.bancodigital.dto.request.TransactionRequestDTO;
import com.bancodigital.dto.response.TransactionResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.exception.ResourceNotFoundException;
import com.bancodigital.model.Account;
import com.bancodigital.model.Transaction;
import com.bancodigital.model.enums.TransactionType;
import com.bancodigital.repository.AccountRepository;
import com.bancodigital.repository.TransactionRepository;
import com.bancodigital.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        logger.info("Criando nova transação do tipo: {} para conta: {}",
                transactionRequestDTO.getTipo(), transactionRequestDTO.getAccountId());

        Account account = findAccountById(transactionRequestDTO.getAccountId());

        validateTransaction(transactionRequestDTO, account);

        Transaction transaction = convertToEntity(transactionRequestDTO, account);

        processTransaction(transaction, account, transactionRequestDTO);

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        logger.info("Transação criada com sucesso. ID: {}", savedTransaction.getId());
        return convertToResponseDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Long id) {
        logger.debug("Buscando transação com ID: {}", id);
        Transaction transaction = findTransactionById(id);
        return convertToResponseDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        logger.debug("Listando todas as transações");
        return transactionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getAllTransactionsPaginated(Pageable pageable) {
        logger.debug("Listando transações com paginação: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return transactionRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        logger.debug("Buscando transações da conta: {}", accountId);
        findAccountById(accountId);

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private void validateTransaction(TransactionRequestDTO dto, Account account) {
        if (dto.getTipo() == TransactionType.SAQUE) {
            if (account.getSaldo().compareTo(dto.getValor()) < 0) {
                logger.warn("Saldo insuficiente para saque. Saldo: {}, Valor solicitado: {}",
                        account.getSaldo(), dto.getValor());
                throw new BusinessException("Saldo insuficiente para realizar o saque");
            }
        }

        if (dto.getTipo() == TransactionType.TRANSFERENCIA) {
            if (dto.getAccountDestinoId() == null) {
                throw new BusinessException("Conta destino é obrigatória para transferências");
            }
            if (account.getSaldo().compareTo(dto.getValor()) < 0) {
                logger.warn("Saldo insuficiente para transferência. Saldo: {}, Valor: {}",
                        account.getSaldo(), dto.getValor());
                throw new BusinessException("Saldo insuficiente para realizar a transferência");
            }
            if (dto.getAccountId().equals(dto.getAccountDestinoId())) {
                throw new BusinessException("Conta origem e destino não podem ser iguais");
            }
        }
    }

    private void processTransaction(Transaction transaction, Account account, TransactionRequestDTO dto) {
        switch (dto.getTipo()) {
            case DEPOSITO:
                logger.debug("Processando depósito de {} na conta {}", dto.getValor(), account.getId());
                account.setSaldo(account.getSaldo().add(dto.getValor()));
                break;

            case SAQUE:
                logger.debug("Processando saque de {} da conta {}", dto.getValor(), account.getId());
                account.setSaldo(account.getSaldo().subtract(dto.getValor()));
                break;

            case TRANSFERENCIA:
                logger.debug("Processando transferência de {} da conta {} para conta {}",
                        dto.getValor(), account.getId(), dto.getAccountDestinoId());
                Account accountDestino = findAccountById(dto.getAccountDestinoId());
                account.setSaldo(account.getSaldo().subtract(dto.getValor()));
                accountDestino.setSaldo(accountDestino.getSaldo().add(dto.getValor()));
                accountRepository.save(accountDestino);

                Transaction transactionDestino = new Transaction();
                transactionDestino.setTipo(TransactionType.DEPOSITO);
                transactionDestino.setValor(dto.getValor());
                transactionDestino.setDescricao("Transferência recebida de conta " + account.getNumeroConta());
                transactionDestino.setAccount(accountDestino);
                transactionDestino.setAccountDestinoId(account.getId());
                transactionRepository.save(transactionDestino);
                break;
        }
    }

    private Transaction findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com ID: " + id));
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + id));
    }

    private Transaction convertToEntity(TransactionRequestDTO dto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setTipo(dto.getTipo());
        transaction.setValor(dto.getValor());
        transaction.setDescricao(dto.getDescricao());
        transaction.setAccount(account);
        transaction.setAccountDestinoId(dto.getAccountDestinoId());
        return transaction;
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getTipo(),
                transaction.getValor(),
                transaction.getDescricao(),
                transaction.getAccount().getId(),
                transaction.getAccount().getNumeroConta(),
                transaction.getAccountDestinoId(),
                transaction.getCreatedAt()
        );
    }
}
