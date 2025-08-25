package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.PaymentMethodRequestDto;
import imusic.backend.dto.response.ref.PaymentMethodResponseDto;
import imusic.backend.dto.create.ref.PaymentMethodCreateDto;
import imusic.backend.dto.update.ref.PaymentMethodUpdateDto;
import imusic.backend.entity.ref.PaymentMethod;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.PaymentMethodMapper;
import imusic.backend.repository.ref.PaymentMethodRepository;
import imusic.backend.service.ref.PaymentMethodService;
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
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    @Override
    @Cacheable(cacheNames = "paymentMethods", key = "'all'")
    public List<PaymentMethodResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "paymentMethods", key = "#id")
    public PaymentMethodResponseDto getById(Long id) {
        PaymentMethod method = repository.findById(id)
                .orElseThrow(() -> new AppException("Метод оплаты не найден, id: " + id));
        return mapper.toResponse(method);
    }

    @Override
    @CacheEvict(cacheNames = "paymentMethods", allEntries = true)
    @CachePut(cacheNames = "paymentMethods", key = "#result.id")
    public PaymentMethodResponseDto create(PaymentMethodCreateDto dto) {
        PaymentMethod method = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(method));
    }

    @Override
    @CacheEvict(cacheNames = "paymentMethods", allEntries = true)
    public PaymentMethodResponseDto update(Long id, PaymentMethodUpdateDto dto) {
        PaymentMethod method = repository.findById(id)
                .orElseThrow(() -> new AppException("Метод оплаты не найден, id: " + id));
        method.setName(dto.getName());
        method.setCode(dto.getCode());
        return mapper.toResponse(repository.save(method));
    }

    @Override
    @CacheEvict(cacheNames = "paymentMethods", allEntries = true)
    public void delete(Long id) {
        PaymentMethod method = repository.findById(id)
                .orElseThrow(() -> new AppException("Метод оплаты не найден, id: " + id));
        repository.delete(method);
    }

    @Override
    @Cacheable(cacheNames = "paymentMethods", key = "'paymentMethods-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<PaymentMethodResponseDto> getStatusesWithFilters(PaymentMethodRequestDto request) {
        List<PaymentMethod> methods = repository.findAll();

        methods = methods.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        methods.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), methods.size());
        if (fromIndex >= methods.size()) {
            return List.of();
        }

        return methods.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<PaymentMethod> getSortComparator(String sortBy, String sortDirection) {
        Comparator<PaymentMethod> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(PaymentMethod::getId);
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
