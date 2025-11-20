package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.dto.response.ops.ClientResponseDto;
import imusic.backend.entity.ops.Client;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.Role;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ClientMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.ClientRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl service;

    @Mock private ClientRepository clientRepository;
    @Mock private UserRepository userRepository;
    @Mock private ClientMapper clientMapper;
    @Mock private UserMapper userMapper;
    @Mock private UserResolver userResolver;
    @Mock private AuthService authService;
    @Mock private RoleResolver roleResolver;
    @Mock private UserStatusResolver userStatusResolver;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getAllClients — возвращает список клиентов")
    void testGetAllClients() {
        Client client = new Client();
        client.setId(1L);

        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(1L);

        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponse(client)).thenReturn(dto);

        List<ClientResponseDto> result = service.getAllClients(null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("getClientById — возвращает DTO")
    void testGetClientById() {
        Client client = new Client();
        client.setId(5L);

        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(5L);

        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(dto);

        ClientResponseDto result = service.getClientById(5L);

        assertEquals(5L, result.getId());
    }

    @Test
    @DisplayName("getClientById — выбрасывает AppException, если не найден")
    void testGetClientByIdNotFound() {
        when(clientRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> service.getClientById(10L));
    }

    @Test
    @DisplayName("getClientProfileByUserId — возвращает профиль")
    void testGetClientProfileByUserId() {
        Client client = new Client();
        client.setId(7L);

        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(7L);

        when(clientRepository.findByUserId(2L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(dto);

        ClientResponseDto result = service.getClientProfileByUserId(2L);

        assertEquals(7L, result.getId());
    }

    @Test
    @DisplayName("createClient — создаёт клиента")
    void testCreateClient() {
        ClientCreateDto dto = new ClientCreateDto();
        dto.setUserId(2L);
        dto.setCompanyName("Test Company");
        dto.setPhone("12345");
        dto.setAddress("Address");
        dto.setInn("123456");
        dto.setContactPerson("John");

        Role role = new Role();
        role.setCode("CLIENT");
        role.setId(3L);
        role.setName("client");

        User user = new User();
        user.setId(2L);
        user.setRole(role);

        User createdBy = new User();
        createdBy.setId(100L);

        Client client = new Client();
        client.setId(10L);

        ClientResponseDto response = new ClientResponseDto();
        response.setId(10L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(createdBy);
        when(clientMapper.toEntity(dto, userResolver)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponseDto result = service.createClient(dto);

        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("updateClient — обновляет клиента")
    void testUpdateClient() {
        Client client = new Client();
        client.setId(8L);

        ClientUpdateDto dto = new ClientUpdateDto();
        dto.setCompanyName("Updated");

        ClientResponseDto response = new ClientResponseDto();
        response.setId(8L);

        when(clientRepository.findById(8L)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateEntity(dto, userResolver, client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(response);

        ClientResponseDto result = service.updateClient(8L, dto);

        assertEquals(8L, result.getId());
    }

    @Test
    @DisplayName("deleteClient — удаляет клиента")
    void testDeleteClient() {
        Client client = new Client();
        client.setId(9L);

        when(clientRepository.findById(9L)).thenReturn(Optional.of(client));

        service.deleteClient(9L);

        verify(clientRepository).delete(client);
    }
}
