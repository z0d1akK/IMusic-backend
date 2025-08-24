package imusic.backend.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.auth.LoginRequest;
import imusic.backend.dto.auth.RegisterRequest;
import imusic.backend.dto.auth.TokenResponse;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.Role;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.UserStatusRepository;
import imusic.backend.security.CustomUserDetails;
import imusic.backend.token.JwtService;
import imusic.backend.security.UserService;

import java.util.List;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(UserRepository userRepository, UserStatusRepository userStatusRepository,
                       RoleRepository roleRepository,
                       UserDetailsService userDetailsService,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService, UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.roleRepository = roleRepository;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AppException("Неверный логин или пароль");
        }

        User user = userService.getUserByUsername(request.getUsername());
        if (user.getStatus().getCode().equals("BLOCKED")) {
            throw new AppException("Ваш аккаунт заблокирован");
        } else if (user.getStatus().getCode().equals("DELETED")) {
            throw new AppException("Ваш аккаунт удален");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new TokenResponse(user.getId(), token, List.of(user.getRole().getCode()));
    }

    public TokenResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().length() < 6) {
            throw new AppException("Имя пользователя должно содержать минимум 6 символов.");
        }
        String passwordRegex = "^(?=.*[a-zа-я])(?=.*[A-ZА-Я])(?=.*\\d)[a-zA-Zа-яА-Я\\d]{8,}$";
        if (request.getPassword() == null || !request.getPassword().matches(passwordRegex)) {
            throw new AppException("Пароль должен содержать минимум 8 символов, включая заглавные буквы, строчные буквы и цифры.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Пароли не совпадают");
        }
        if (request.getEmail() == null || !request.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new AppException("Некорректный формат email.");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new AppException("ФИО не может быть пустым.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException("Логин уже занят");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email уже зарегистрирован");
        }

        Role defaultRole = roleRepository.findByCode("CLIENT")
                .orElseThrow(() -> new AppException("Роль по умолчанию не найдена"));

        UserStatus defaultStatus = userStatusRepository.findByCode("ACTIVE")
                .orElseThrow(() -> new AppException("Статус по умолчанию не найден"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(defaultRole)
                .status(defaultStatus)
                .build();

        userRepository.save(user);

        UserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        return new TokenResponse(user.getId() ,token, List.of(user.getRole().getCode()));
    }

    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userMapper.toResponse(user);
    }
}


