package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.UserStatusRequestDto;
import imusic.backend.dto.response.ref.UserStatusResponseDto;
import imusic.backend.dto.create.ref.UserStatusCreateDto;
import imusic.backend.dto.update.ref.UserStatusUpdateDto;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.UserStatusMapper;
import imusic.backend.repository.ref.UserStatusRepository;
import imusic.backend.service.ref.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStatusServiceImpl implements UserStatusService {

    private final UserStatusRepository statusRepository;
    private final UserStatusMapper statusMapper;

    @Override
    @Cacheable(cacheNames = "userStatuses", key = "'all'")
    public List<UserStatusResponseDto> getAll() {
        return statusRepository.findAll()
                .stream()
                .map(statusMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "userStatuses", key = "#id")
    public UserStatusResponseDto getById(Long id) {
        UserStatus status = statusRepository.findById(id)
                .orElseThrow(() -> new AppException("Статус пользователя не найден, id: " + id));
        return statusMapper.toResponse(status);
    }

    @Override
    @CacheEvict(cacheNames = "userStatuses", allEntries = true)
    @CachePut(cacheNames = "userStatuses", key = "#result.id")
    public UserStatusResponseDto create(UserStatusCreateDto dto) {
        UserStatus status = statusMapper.toEntity(dto);
        return statusMapper.toResponse(statusRepository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "userStatuses", allEntries = true)
    public UserStatusResponseDto update(Long id, UserStatusUpdateDto dto) {
        UserStatus status = statusRepository.findById(id)
                .orElseThrow(() -> new AppException("Статус пользователя не найден, id: " + id));
        status.setName(dto.getName());
        status.setCode(dto.getCode());
        return statusMapper.toResponse(statusRepository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "userStatuses", allEntries = true)
    public void delete(Long id) {
        UserStatus status = statusRepository.findById(id)
                .orElseThrow(() -> new AppException("Статус пользователя не найден, id: " + id));
        statusRepository.delete(status);
    }

    @Override
    @Cacheable(cacheNames = "userStatuses", key = "'userStatuses-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<UserStatusResponseDto> getStatusesWithFilters(UserStatusRequestDto request) {
        List<UserStatus> userStatuseses = statusRepository.findAll();

        userStatuseses = userStatuseses.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        userStatuseses.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), userStatuseses.size());
        if (fromIndex >= userStatuseses.size()) {
            return List.of();
        }

        return userStatuseses.subList(fromIndex, toIndex).stream()
                .map(statusMapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<UserStatus> getSortComparator(String sortBy, String sortDirection) {
        Comparator<UserStatus> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(UserStatus::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private String safeString(String value) {
        return value != null ? value.toLowerCase() : "";
    }
}
