package com.bancodigital.repository;

import com.bancodigital.model.Transaction;
import com.bancodigital.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Transaction> findByAccountIdAndTipo(Long accountId, TransactionType tipo);

    List<Transaction> findByAccountIdAndCreatedAtBetween(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
