package imusic.backend.service.auth;

import imusic.backend.dto.auth.LoginRequest;
import imusic.backend.dto.auth.RegisterRequest;
import imusic.backend.dto.auth.TokenResponse;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.Role;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.repository.ref.UserStatusRepository;
import imusic.backend.security.UserService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserDetailsService userDetailsService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserService userService;
    @Mock private UserMapper userMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login — успешный вход")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("user", "Pass1234");
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        Role role = new Role();
        role.setCode("CLIENT");
        user.setRole(role);
        UserStatus status = new UserStatus();
        status.setCode("ACTIVE");
        user.setStatus(status);

        when(userService.getUserByUsername("user")).thenReturn(user);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(mock(UserDetails.class));
        when(jwtService.generateToken(any())).thenReturn("token");

        TokenResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(List.of("CLIENT"), response.getRoles());
        assertEquals("token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login — блокированный пользователь")
    void testLoginBlockedUser() {
        LoginRequest request = new LoginRequest("user", "pass");
        User user = new User();
        user.setUsername("user");
        Role role = new Role();
        role.setCode("CLIENT");
        user.setRole(role);
        UserStatus status = new UserStatus();
        status.setCode("BLOCKED");
        user.setStatus(status);

        when(userService.getUserByUsername("user")).thenReturn(user);

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("Ваш аккаунт заблокирован", ex.getMessage());
    }

    @Test
    @DisplayName("register — успешная регистрация")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("username","Pass1234","Pass1234","Full Name","user@mail.com");
        Role role = new Role();
        role.setCode("CLIENT");
        UserStatus status = new UserStatus();
        status.setCode("ACTIVE");

        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.existsByEmail("user@mail.com")).thenReturn(false);
        when(roleRepository.findByCode("CLIENT")).thenReturn(Optional.of(role));
        when(userStatusRepository.findByCode("ACTIVE")).thenReturn(Optional.of(status));
        when(passwordEncoder.encode("Pass1234")).thenReturn("encodedPass");
        when(jwtService.generateToken(any())).thenReturn("token");

        TokenResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals(List.of("CLIENT"), response.getRoles());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register — пароль не совпадает")
    void testRegisterPasswordMismatch() {
        RegisterRequest request = new RegisterRequest("username","Pass1234","OtherPass","Full Name","user@mail.com");

        AppException ex = assertThrows(AppException.class, () -> authService.register(request));
        assertEquals("Пароли не совпадают", ex.getMessage());
    }

    @Test
    @DisplayName("getCurrentUser — успешное получение")
    void testGetCurrentUser() {
        User user = new User();
        user.setUsername("user");
        UserResponseDto dto = new UserResponseDto();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(dto);

        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("user");
        when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        UserResponseDto result = authService.getCurrentUser();
        assertNotNull(result);
    }
}
