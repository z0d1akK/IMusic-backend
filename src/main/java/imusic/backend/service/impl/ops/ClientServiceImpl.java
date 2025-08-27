package imusic.backend.service.impl.ops;

import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import imusic.backend.entity.ops.Client;
import imusic.backend.entity.ops.User;
import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.dto.request.ops.ClientRequestDto;
import imusic.backend.dto.response.ops.ClientResponseDto;
import imusic.backend.mapper.ops.ClientMapper;
import imusic.backend.repository.ops.ClientRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.exception.AppException;
import imusic.backend.service.ops.ClientService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;
    private final UserResolver userResolver;
    private final AuthService authService;
    private final RoleResolver roleResolver;
    private final UserStatusResolver userStatusResolver;

    @Override
    @Cacheable(cacheNames = "clients", key = "'all'")
    public List<ClientResponseDto> getAllClients(ClientRequestDto request) {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "clients", key = "#id")
    public ClientResponseDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new AppException("Клиент не найден, ID: " + id));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto getClientProfileByUserId(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Профиль клиента не найден. Обратитесь к менеджеру."));
        return clientMapper.toResponse(client);
    }

    @Override
    @CachePut(cacheNames = "clients", key = "#result.id")
    @CacheEvict(cacheNames = "clients", allEntries = true)
    public ClientResponseDto createClient(ClientCreateDto dto) {
        if (dto.getUserId() == null) {
            throw new AppException("Не выбран пользователь с ролью CLIENT");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new AppException("Пользователь не найден"));
        if (!user.getRole().getCode().equals("CLIENT")) {
            throw new AppException("Выбранный пользователь не имеет роли CLIENT");
        }

        User createdBy = userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver);

        Client client = clientMapper.toEntity(dto, userResolver);
        client.setCreatedBy(createdBy);

        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    @CacheEvict(cacheNames = "clients", allEntries = true)
    public ClientResponseDto updateClient(Long id, ClientUpdateDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new AppException("Клиент не найден, ID: " + id));
        client.setUpdatedAt(LocalDateTime.now());
        clientMapper.updateEntity(dto, userResolver, client);
        return clientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    @CacheEvict(cacheNames = "clients", allEntries = true)
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new AppException("Клиент не найден, ID: " + id));
        clientRepository.delete(client);
    }

    @Override
    public List<ClientResponseDto> getClientsWithFilters(ClientRequestDto request) {
        List<Client> clients = clientRepository.findAll();

        if (request.getName() != null && !request.getName().isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getCompanyName() != null &&
                            c.getCompanyName().toLowerCase().contains(request.getName().toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getEmail() != null &&
                            c.getEmail().toLowerCase().contains(request.getEmail().toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getPhone() != null &&
                            c.getPhone().toLowerCase().contains(request.getPhone().toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getAddress() != null &&
                            c.getAddress().toLowerCase().contains(request.getAddress().toLowerCase()))
                    .collect(Collectors.toList());
        }

        clients.sort(getClientSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), clients.size());

        return clients.subList(fromIndex, toIndex).stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Comparator<Client> getClientSortComparator(String sortBy, String sortDirection) {
        Comparator<Client> comparator = Comparator.comparing(Client::getId); // базовая сортировка по id

        Comparator<Client> secondaryComparator = switch (sortBy != null ? sortBy : "") {
            case "companyName" -> Comparator.comparing(c -> safeString(c.getCompanyName()));
            case "email" -> Comparator.comparing(c -> safeString(c.getEmail()));
            case "phone" -> Comparator.comparing(c -> safeString(c.getPhone()));
            case "address" -> Comparator.comparing(c -> safeString(c.getAddress()));
            case "createdAt" -> Comparator.comparing(Client::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(Client::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            secondaryComparator = secondaryComparator.reversed();
        }

        return comparator.thenComparing(secondaryComparator);
    }

    private String safeString(String value) {
        return value != null ? value.toLowerCase() : "";
    }

}
