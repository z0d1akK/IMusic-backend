package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.NotificationTypeRequestDto;
import imusic.backend.dto.response.ref.NotificationTypeResponseDto;
import imusic.backend.dto.create.ref.NotificationTypeCreateDto;
import imusic.backend.dto.update.ref.NotificationTypeUpdateDto;
import imusic.backend.entity.ref.NotificationType;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.NotificationTypeMapper;
import imusic.backend.repository.ref.NotificationTypeRepository;
import imusic.backend.service.ref.NotificationTypeService;
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
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final NotificationTypeRepository repository;
    private final NotificationTypeMapper mapper;

    @Override
    @Cacheable(cacheNames = "notificationTypes", key = "'all'")
    public List<NotificationTypeResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "notificationTypes", key = "#id")
    public NotificationTypeResponseDto getById(Long id) {
        NotificationType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип уведомления не найден, id: " + id));
        return mapper.toResponse(type);
    }

    @Override
    @CacheEvict(cacheNames = "notificationTypes", allEntries = true)
    @CachePut(cacheNames = "notificationTypes", key = "#result.id")
    public NotificationTypeResponseDto create(NotificationTypeCreateDto dto) {
        NotificationType type = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(type));
    }

    @Override
    @CacheEvict(cacheNames = "notificationTypes", allEntries = true)
    public NotificationTypeResponseDto update(Long id, NotificationTypeUpdateDto dto) {
        NotificationType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип уведомления не найден, id: " + id));
        type.setName(dto.getName());
        type.setCode(dto.getCode());
        return mapper.toResponse(repository.save(type));
    }

    @Override
    @CacheEvict(cacheNames = "notificationTypes", allEntries = true)
    public void delete(Long id) {
        NotificationType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип уведомления не найден, id: " + id));
        repository.delete(type);
    }

    @Override
    @Cacheable(cacheNames = "notificationTypes", key = "'notificationTypes-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<NotificationTypeResponseDto> getTypesWithFilters(NotificationTypeRequestDto request) {
        List<NotificationType> types = repository.findAll();

        types = types.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        types.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), types.size());
        if (fromIndex >= types.size()) {
            return List.of();
        }

        return types.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<NotificationType> getSortComparator(String sortBy, String sortDirection) {
        Comparator<NotificationType> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(NotificationType::getId);
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