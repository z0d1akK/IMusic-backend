package imusic.backend.service.ref;

import imusic.backend.dto.create.ref.RoleCreateDto;
import imusic.backend.dto.request.ref.RoleRequestDto;
import imusic.backend.dto.response.ref.RoleResponseDto;
import imusic.backend.dto.update.ref.RoleUpdateDto;
import imusic.backend.entity.ref.Role;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.RoleMapper;
import imusic.backend.repository.ref.RoleRepository;
import imusic.backend.service.impl.ref.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @Mock
    private RoleMapper mapper;

    @InjectMocks
    private RoleServiceImpl service;

    private Role entity;
    private RoleResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = Role.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        responseDto = RoleResponseDto.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();
    }

    @Test
    @DisplayName("Get all roles")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<RoleResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get role by ID")
    void testGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        RoleResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get by invalid ID should throw exception")
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getById(1L));
        assertTrue(exception.getMessage().contains("Роль не найдена"));
    }

    @Test
    @DisplayName("Create new role")
    void testCreate() {
        RoleCreateDto dto = RoleCreateDto.builder()
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        RoleResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update role")
    void testUpdate() {
        RoleUpdateDto dto = RoleUpdateDto.builder()
                .code("NEW_CODE")
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                RoleResponseDto.builder()
                        .id(1L)
                        .code("NEW_CODE")
                        .name("New Name")
                        .build()
        );

        RoleResponseDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getCode());
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update non-existent role should throw exception")
    void testUpdateNotFound() {
        RoleUpdateDto dto = new RoleUpdateDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.update(1L, dto));
        assertTrue(exception.getMessage().contains("Роль не найдена"));
    }

    @Test
    @DisplayName("Delete role")
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Delete non-existent role should throw exception")
    void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.delete(1L));
        assertTrue(exception.getMessage().contains("Роль не найдена"));
    }

    @Test
    @DisplayName("Get roles with filters")
    void testGetStatusesWithFilters() {
        RoleRequestDto request = RoleRequestDto.builder()
                .page(0)
                .size(10)
                .code("TEST")
                .name("Test")
                .sortBy("name")
                .sortDirection("asc")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<RoleResponseDto> result = service.getRolesWithFilters(request);

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }
}

