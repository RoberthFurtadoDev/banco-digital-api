package com.bancodigital.service.impl;

import com.bancodigital.dto.request.UserRequestDTO;
import com.bancodigital.dto.response.UserResponseDTO;
import com.bancodigital.exception.BusinessException;
import com.bancodigital.exception.ResourceNotFoundException;
import com.bancodigital.model.User;
import com.bancodigital.repository.UserRepository;
import com.bancodigital.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        logger.info("Criando novo usuário com email: {}", userRequestDTO.getEmail());
        validateUserUniqueness(userRequestDTO.getCpf(), userRequestDTO.getEmail());

        User user = convertToEntity(userRequestDTO);
        User savedUser = userRepository.save(user);

        logger.info("Usuário criado com sucesso. ID: {}", savedUser.getId());
        return convertToResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        logger.debug("Buscando usuário com ID: {}", id);
        User user = findUserById(id);
        return convertToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.debug("Listando todos os usuários");
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsersPaginated(Pageable pageable) {
        logger.debug("Listando usuários com paginação: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        logger.info("Atualizando usuário com ID: {}", id);
        User user = findUserById(id);

        if (!user.getCpf().equals(userRequestDTO.getCpf()) &&
                userRepository.existsByCpf(userRequestDTO.getCpf())) {
            logger.warn("Tentativa de atualizar usuário {} com CPF duplicado", id);
            throw new BusinessException("CPF já cadastrado");
        }

        if (!user.getEmail().equals(userRequestDTO.getEmail()) &&
                userRepository.existsByEmail(userRequestDTO.getEmail())) {
            logger.warn("Tentativa de atualizar usuário {} com email duplicado", id);
            throw new BusinessException("Email já cadastrado");
        }

        updateUserFields(user, userRequestDTO);
        User updatedUser = userRepository.save(user);

        logger.info("Usuário {} atualizado com sucesso", id);
        return convertToResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Tentando deletar usuário com ID: {}", id);
        User user = findUserById(id);

        if (!user.getAccounts().isEmpty()) {
            logger.warn("Tentativa de deletar usuário {} com contas ativas", id);
            throw new BusinessException("Não é possível excluir usuário com contas ativas");
        }

        userRepository.delete(user);
        logger.info("Usuário {} deletado com sucesso", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    private void validateUserUniqueness(String cpf, String email) {
        if (userRepository.existsByCpf(cpf)) {
            throw new BusinessException("CPF já cadastrado");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado");
        }
    }

    private User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setNome(dto.getNome());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user.setTelefone(dto.getTelefone());
        user.setRole("USER");
        return user;
    }

    private void updateUserFields(User user, UserRequestDTO dto) {
        user.setNome(dto.getNome());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            user.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        user.setTelefone(dto.getTelefone());
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getCpf(),
                user.getEmail(),
                user.getTelefone(),
                user.getCreatedAt()
        );
    }
}
