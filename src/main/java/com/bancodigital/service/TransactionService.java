package com.bancodigital.service;

import com.bancodigital.dto.request.TransactionRequestDTO;
import com.bancodigital.dto.response.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO);

    TransactionResponseDTO getTransactionById(Long id);

    List<TransactionResponseDTO> getAllTransactions();

    Page<TransactionResponseDTO> getAllTransactionsPaginated(Pageable pageable);

    List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId);
}
