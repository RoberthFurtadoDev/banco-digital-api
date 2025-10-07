package com.bancodigital.service;

import com.bancodigital.dto.request.AccountRequestDTO;
import com.bancodigital.dto.response.AccountResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO);

    AccountResponseDTO getAccountById(Long id);

    List<AccountResponseDTO> getAllAccounts();

    Page<AccountResponseDTO> getAllAccountsPaginated(Pageable pageable);

    List<AccountResponseDTO> getAccountsByUserId(Long userId);

    AccountResponseDTO updateAccount(Long id, AccountRequestDTO accountRequestDTO);

    void deleteAccount(Long id);
}
