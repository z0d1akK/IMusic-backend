package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.OrderStatusRequestDto;
import imusic.backend.dto.response.ref.OrderStatusResponseDto;
import imusic.backend.dto.create.ref.OrderStatusCreateDto;
import imusic.backend.dto.update.ref.OrderStatusUpdateDto;
import imusic.backend.entity.ref.OrderStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.OrderStatusMapper;
import imusic.backend.repository.ref.OrderStatusRepository;
import imusic.backend.service.ref.OrderStatusService;
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
public class OrderStatusServiceImpl implements OrderStatusService {

    private final OrderStatusRepository repository;
    private final OrderStatusMapper mapper;

    @Override
    @Cacheable(cacheNames = "orderStatuses", key = "'all'")
    public List<OrderStatusResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "orderStatuses", key = "#id")
    public OrderStatusResponseDto getById(Long id) {
        OrderStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус заказа не найден, id: " + id));
        return mapper.toResponse(status);
    }

    @Override
    @CacheEvict(cacheNames = "orderStatuses", allEntries = true)
    @CachePut(cacheNames = "orderStatuses", key = "#result.id")
    public OrderStatusResponseDto create(OrderStatusCreateDto dto) {
        OrderStatus status = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "orderStatuses", allEntries = true)
    public OrderStatusResponseDto update(Long id, OrderStatusUpdateDto dto) {
        OrderStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус заказа не найден, id: " + id));
        status.setName(dto.getName());
        status.setCode(dto.getCode());
        return mapper.toResponse(repository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "orderStatuses", allEntries = true)
    public void delete(Long id) {
        OrderStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус заказа не найден, id: " + id));
        repository.delete(status);
    }

    @Override
    @Cacheable(cacheNames = "orderStatuses", key = "'orderStatuses-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<OrderStatusResponseDto> getStatusesWithFilters(OrderStatusRequestDto request) {
        List<OrderStatus> statuses = repository.findAll();

        statuses = statuses.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        statuses.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), statuses.size());
        if (fromIndex >= statuses.size()) {
            return List.of();
        }

        return statuses.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<OrderStatus> getSortComparator(String sortBy, String sortDirection) {
        Comparator<OrderStatus> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(OrderStatus::getId);
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
