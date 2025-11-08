package imusic.backend.service.impl.ops;

import imusic.backend.dto.auth.ChangeLoginRequest;
import imusic.backend.dto.auth.ChangePasswordRequest;
import imusic.backend.dto.request.ops.UserRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.Role;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.ClientRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.ops.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleResolver roleResolver;
    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final UserStatusResolver userStatusResolver;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    @Cacheable(cacheNames = "users", key = "'all'")
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Cacheable(cacheNames = "users" , key = "#code")
    public List<UserResponseDto> getUsersByRoleCode(String code) {
        Role role = roleRepository.findByCode(code)
                .orElseThrow(() -> new AppException("Роль пользователя не найдена по коду: " + code));
        return userRepository.findAll()
                .stream()
                .filter(u -> Objects.equals(u.getRole(), role))
                .filter(u -> Objects.equals(u.isDeleted(), false))
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> findAvailableClientUsers(String query) {
        List<User> allClients = userRepository.findAll().stream()
                .filter(user -> user.getRole().getCode().equals("CLIENT"))
                .filter(user -> Objects.equals(user.isDeleted(), false))
                .toList();
        List<Long> usedUserIds = clientRepository.findAll()
                .stream()
                .map(client -> client.getUser().getId())
                .toList();

        return allClients.stream()
                .filter(user -> !usedUserIds.contains(user.getId()))
                .filter(user ->
                        query == null || query.isBlank() ||
                                user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))
                )
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));

        if (dto.getPassword() == null) {
            dto.setPassword(existing.getPassword());
        }

        userMapper.updateEntity(dto, roleResolver, userStatusResolver, existing);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        existing.setBlocked(Boolean.TRUE.equals(dto.getIsBlocked()));
        existing.setDeleted(Boolean.TRUE.equals(dto.getIsDeleted()));
        updateStatusBasedOnFlags(existing);

        return userMapper.toResponse(userRepository.save(existing));
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserResponseDto updateUserProfile(Long id, UserUpdateDto dto) {
        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(),roleResolver,userStatusResolver);

        boolean isAdmin = currentUser.getRole().getCode().equals("ADMIN");

        if (!currentUser.getId().equals(id) && !isAdmin) {
            throw new AccessDeniedException("Нет доступа к чужому профилю");
        }

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setAvatarPath(dto.getAvatarPath());

        return userMapper.toResponse(userRepository.save(existing));
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));
        user.setBlocked(true);
        updateStatusBasedOnFlags(user);
        userRepository.save(user);
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));
        user.setBlocked(false);
        updateStatusBasedOnFlags(user);
        userRepository.save(user);
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ключ: " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException("Текущий пароль неверный.");
        }

        String passwordRegex = "^(?=.*[a-zа-я])(?=.*[A-ZА-Я])(?=.*\\d)[a-zA-Zа-яА-Я\\d]{8,}$";
        if (request.getNewPassword() == null || !request.getNewPassword().matches(passwordRegex)) {
            throw new AppException("Новый пароль должен содержать минимум 8 символов, включая заглавные, строчные буквы и цифры.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Новый пароль и подтверждение не совпадают.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void changeLogin(Long id, ChangeLoginRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ключ: " + id));

        if (!user.getUsername().equals(request.getCurrentLogin())) {
            throw new AppException("Текущий логин неверный.");
        }

        if (request.getNewLogin() == null || request.getNewLogin().length() < 6) {
            throw new AppException("Новый логин должен содержать минимум 6 символов.");
        }

        if (userRepository.existsByUsername(request.getNewLogin())) {
            throw new AppException("Новый логин уже используется.");
        }
        user.setUsername(request.getNewLogin());
        userRepository.save(user);
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void uploadAvatar(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));

        if (file == null || file.isEmpty()) {
            throw new AppException("Файл аватара не может быть пустым");
        }

        try {
            String imageName = "user_" + id + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path imagePath = Paths.get("uploads/avatars", imageName);
            Files.createDirectories(imagePath.getParent());
            file.transferTo(imagePath);
            user.setAvatarPath("/avatars/" + imageName);
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("Не удалось загрузить аватар: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден, ID: " + id));
        user.setDeleted(true);
        updateStatusBasedOnFlags(user);
        userRepository.save(user);
    }

    @Override
    public PageResponseDto<UserResponseDto> getPagedUsers(UserRequestDto request) {
        List<User> users = userRepository.findAll();

        if (request.getUsername() != null)
            users = users.stream().filter(u -> u.getUsername().toLowerCase().contains(request.getUsername().toLowerCase())).collect(Collectors.toList());
        if (request.getEmail() != null)
            users = users.stream().filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(request.getEmail().toLowerCase())).collect(Collectors.toList());
        if (request.getRoleId() != null)
            users = users.stream().filter(u -> u.getRole() != null && u.getRole().getId().equals(request.getRoleId())).collect(Collectors.toList());
        if (request.getStatusId() != null)
            users = users.stream().filter(u -> u.getStatus() != null && u.getStatus().getId().equals(request.getStatusId())).collect(Collectors.toList());
        if (request.getStatusId() != null)
            users = users.stream().filter(u -> Objects.equals(u.getStatus().getId(), request.getStatusId())).collect(Collectors.toList());

        users.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int totalElements = users.size();
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), totalElements);

        List<UserResponseDto> content = users.subList(fromIndex, toIndex)
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return new PageResponseDto<>(content, request.getPage(), request.getSize(), totalElements, totalPages);
    }


    private Comparator<User> getSortComparator(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Comparator<User> comparator = switch (sortBy) {
            case "username" -> Comparator.comparing(u -> safeString(u.getUsername()), String.CASE_INSENSITIVE_ORDER);
            case "fullName" -> Comparator.comparing(u -> safeString(u.getFullName()), String.CASE_INSENSITIVE_ORDER);
            case "email" -> Comparator.comparing(u -> safeString(u.getEmail()), String.CASE_INSENSITIVE_ORDER);
            case "roleName" -> Comparator.comparing(
                    u -> u.getRole() != null ? safeString(u.getRole().getName()) : "",
                    String.CASE_INSENSITIVE_ORDER);
            case "statusName" -> Comparator.comparing(
                    u -> u.getStatus() != null ? safeString(u.getStatus().getName()) : "",
                    String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(User::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }


    private String safeString(String value) {
        return value != null ? value.toLowerCase() : "";
    }

    private void updateStatusBasedOnFlags(User user) {
        if (Boolean.TRUE.equals(user.isDeleted())) {
            user.setStatus(userStatusResolver.resolve(3L)); // Deleted
        } else if (Boolean.TRUE.equals(user.isBlocked())) {
            user.setStatus(userStatusResolver.resolve(2L)); // Blocked
        } else {
            user.setStatus(userStatusResolver.resolve(1L)); // Active
        }
    }
}
