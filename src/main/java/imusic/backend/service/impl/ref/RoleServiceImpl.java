package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.RoleRequestDto;
import imusic.backend.dto.response.ref.RoleResponseDto;
import imusic.backend.dto.create.ref.RoleCreateDto;
import imusic.backend.dto.update.ref.RoleUpdateDto;
import imusic.backend.entity.ref.Role;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.RoleMapper;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.service.ref.RoleService;
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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Cacheable(cacheNames = "roles", key = "'all'")
    public List<RoleResponseDto> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "roles", key = "#id")
    public RoleResponseDto getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("Роль не найдена, id: " + id));
        return roleMapper.toResponse(role);
    }

    @Override
    @CacheEvict(cacheNames = "roles", allEntries = true)
    @CachePut(cacheNames = "roles", key = "#result.id")
    public RoleResponseDto create(RoleCreateDto dto) {
        Role role = roleMapper.toEntity(dto);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(cacheNames = "roles", allEntries = true)
    public RoleResponseDto update(Long id, RoleUpdateDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("Роль не найдена, id: " + id));
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @CacheEvict(cacheNames = "roles", allEntries = true)
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException("Роль не найдена, id: " + id));
        roleRepository.delete(role);
    }

    @Override
    @Cacheable(cacheNames = "roles", key = "'roles-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<RoleResponseDto> getRolesWithFilters(RoleRequestDto request) {
        List<Role> roles = roleRepository.findAll();
        roles = roles.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        roles.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), roles.size());
        if (fromIndex >= roles.size()) {
            return List.of();
        }

        return roles.subList(fromIndex, toIndex).stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<Role> getSortComparator(String sortBy, String sortDirection) {
        Comparator<Role> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(Role::getId);
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