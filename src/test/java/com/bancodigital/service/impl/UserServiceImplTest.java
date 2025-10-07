package com.bancodigital.service.impl;

import com.bancodigital.dto.request.UserRequestDTO;
import com.bancodigital.dto.response.UserResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.exception.ResourceNotFoundException;
import com.bancodigital.model.User;
import com.bancodigital.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UserService")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNome("João Silva");
        user.setCpf("12345678901");
        user.setEmail("joao@email.com");
        user.setSenha("senhaEncriptada");
        user.setTelefone("11987654321");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setNome("João Silva");
        userRequestDTO.setCpf("12345678901");
        userRequestDTO.setEmail("joao@email.com");
        userRequestDTO.setSenha("senha123");
        userRequestDTO.setTelefone("11987654321");
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncriptada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("joao@email.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com CPF duplicado")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(userRepository.existsByCpf(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(userRequestDTO)
        );

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com Email duplicado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(userRequestDTO)
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João Silva", response.getNome());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void shouldGetAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setNome("Maria Silva");
        user2.setEmail("maria@email.com");
        user2.setCpf("98765432100");
        user2.setTelefone("11987654322");
        user2.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserResponseDTO> response = userService.getAllUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("novaSenhaEncriptada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.updateUser(1L, userRequestDTO);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar usuário sem contas com sucesso")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).delete(user);
    }
}
