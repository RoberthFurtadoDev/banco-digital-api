package com.bancodigital.repository;

import com.bancodigital.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumeroConta(String numeroConta);

    List<Account> findByUserId(Long userId);

    boolean existsByNumeroConta(String numeroConta);
}
