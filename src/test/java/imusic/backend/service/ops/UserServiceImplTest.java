package imusic.backend.service.ops;

import imusic.backend.dto.auth.ChangeLoginRequest;
import imusic.backend.dto.auth.ChangePasswordRequest;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.Role;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.ClientRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleResolver roleResolver;

    @Mock
    private UserStatusResolver userStatusResolver;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setId(1L);
        role.setCode("CLIENT");
        role.setName("Client");

        UserStatus status = new UserStatus();
        status.setId(1L);
        status.setCode("ACTIVE");
        status.setName("Active");

        user = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPass")
                .fullName("User One")
                .email("user1@test.com")
                .role(role)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .username("user1")
                .fullName("User One")
                .email("user1@test.com")
                .roleId(1L)
                .roleName("Client")
                .statusId(1L)
                .statusName("Active")
                .build();
    }

    @Test
    @DisplayName("Get all users")
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponseDto);

        List<UserResponseDto> result = service.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get user by ID success")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponseDto);

        UserResponseDto result = service.getUserById(1L);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
    }

    @Test
    @DisplayName("Get user by invalid ID throws exception")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getUserById(1L));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    @DisplayName("Update user")
    void testUpdateUser() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .username("newUser")
                .password("newPass")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any())).thenReturn(userResponseDto);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        UserResponseDto result = service.updateUser(1L, dto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Delete user")
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.deleteUser(1L);

        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(user.isDeleted());
    }

    @Test
    @DisplayName("Change password success")
    void testChangePassword() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("pass");
        req.setNewPassword("NewPass123");
        req.setConfirmPassword("NewPass123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass123")).thenReturn("encodedNewPass");

        assertDoesNotThrow(() -> service.changePassword(1L, req));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Change login success")
    void testChangeLogin() {
        ChangeLoginRequest req = new ChangeLoginRequest();
        req.setCurrentLogin("user1");
        req.setNewLogin("userNew");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("userNew")).thenReturn(false);

        assertDoesNotThrow(() -> service.changeLogin(1L, req));
        assertEquals("userNew", user.getUsername());
    }
}
