package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.PaymentStatusRequestDto;
import imusic.backend.dto.response.ref.PaymentStatusResponseDto;
import imusic.backend.dto.create.ref.PaymentStatusCreateDto;
import imusic.backend.dto.update.ref.PaymentStatusUpdateDto;
import imusic.backend.entity.ref.PaymentStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.PaymentStatusMapper;
import imusic.backend.repository.ref.PaymentStatusRepository;
import imusic.backend.service.ref.PaymentStatusService;
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
public class PaymentStatusServiceImpl implements PaymentStatusService {

    private final PaymentStatusRepository repository;
    private final PaymentStatusMapper mapper;

    @Override
    @Cacheable(cacheNames = "paymentStatuses", key = "'all'")
    public List<PaymentStatusResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "paymentStatuses", key = "#id")
    public PaymentStatusResponseDto getById(Long id) {
        PaymentStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус оплаты не найден, id: " + id));
        return mapper.toResponse(status);
    }

    @Override
    @CacheEvict(cacheNames = "paymentStatuses", allEntries = true)
    @CachePut(cacheNames = "paymentStatuses", key = "#result.id")
    public PaymentStatusResponseDto create(PaymentStatusCreateDto dto) {
        PaymentStatus status = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "paymentStatuses", allEntries = true)
    public PaymentStatusResponseDto update(Long id, PaymentStatusUpdateDto dto) {
        PaymentStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус оплаты не найден, id: " + id));
        status.setName(dto.getName());
        status.setCode(dto.getCode());
        return mapper.toResponse(repository.save(status));
    }

    @Override
    @CacheEvict(cacheNames = "paymentStatuses", allEntries = true)
    public void delete(Long id) {
        PaymentStatus status = repository.findById(id)
                .orElseThrow(() -> new AppException("Статус оплаты не найден, id: " + id));
        repository.delete(status);
    }

    @Override
    @Cacheable(cacheNames = "paymentStatuses", key = "'paymentStatuses-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<PaymentStatusResponseDto> getStatusesWithFilters(PaymentStatusRequestDto request) {
        List<PaymentStatus> statuses = repository.findAll();

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

    private Comparator<PaymentStatus> getSortComparator(String sortBy, String sortDirection) {
        Comparator<PaymentStatus> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(PaymentStatus::getId);
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
