package com.bancodigital.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private Long userId;
    private String nome;
    private String email;
    private String role;

    public AuthResponseDTO(String token, Long userId, String nome, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }
}
