package com.bancodigital.service;

import com.bancodigital.dto.request.LoginRequestDTO;
import com.bancodigital.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
