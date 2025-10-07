package com.bancodigital.service.impl;

import com.bancodigital.config.JwtUtil;
import com.bancodigital.dto.request.LoginRequestDTO;
import com.bancodigital.dto.response.AuthResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.model.User;
import com.bancodigital.repository.UserRepository;
import com.bancodigital.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Tentativa de login para o email: {}", loginRequestDTO.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getSenha()
                    )
            );

            User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            String token = jwtUtil.generateToken(user);

            logger.info("Login realizado com sucesso para o usuário: {}", user.getId());

            return new AuthResponseDTO(
                    token,
                    user.getId(),
                    user.getNome(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (AuthenticationException e) {
            logger.warn("Falha no login para o email: {}", loginRequestDTO.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }
    }
}
