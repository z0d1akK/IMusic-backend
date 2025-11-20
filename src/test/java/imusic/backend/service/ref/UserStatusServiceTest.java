package imusic.backend.service.ref;

import imusic.backend.dto.create.ref.UserStatusCreateDto;
import imusic.backend.dto.update.ref.UserStatusUpdateDto;
import imusic.backend.dto.request.ref.UserStatusRequestDto;
import imusic.backend.dto.response.ref.UserStatusResponseDto;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.UserStatusMapper;
import imusic.backend.repository.ref.UserStatusRepository;
import imusic.backend.service.impl.ref.UserStatusServiceImpl;
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

class UserStatusServiceTest {

    @Mock
    private UserStatusRepository repository;

    @Mock
    private UserStatusMapper mapper;

    @InjectMocks
    private UserStatusServiceImpl service;

    private UserStatus entity;
    private UserStatusResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = UserStatus.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        responseDto = UserStatusResponseDto.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();
    }

    @Test
    @DisplayName("Get all user statuses")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<UserStatusResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get user status by ID")
    void testGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        UserStatusResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get by invalid ID should throw exception")
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getById(1L));
        assertTrue(exception.getMessage().contains("Статус пользователя не найден"));
    }

    @Test
    @DisplayName("Create new user status")
    void testCreate() {
        UserStatusCreateDto dto = UserStatusCreateDto.builder()
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        UserStatusResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update user status")
    void testUpdate() {
        UserStatusUpdateDto dto = UserStatusUpdateDto.builder()
                .code("NEW_CODE")
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                UserStatusResponseDto.builder()
                        .id(1L)
                        .code("NEW_CODE")
                        .name("New Name")
                        .build()
        );

        UserStatusResponseDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getCode());
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update non-existent user status should throw exception")
    void testUpdateNotFound() {
        UserStatusUpdateDto dto = new UserStatusUpdateDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.update(1L, dto));
        assertTrue(exception.getMessage().contains("Статус пользователя не найден"));
    }

    @Test
    @DisplayName("Delete user status")
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Delete non-existent user status should throw exception")
    void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.delete(1L));
        assertTrue(exception.getMessage().contains("Статус пользователя не найден"));
    }

    @Test
    @DisplayName("Get statuses with filters")
    void testGetStatusesWithFilters() {
        UserStatusRequestDto request = UserStatusRequestDto.builder()
                .page(0)
                .size(10)
                .code("TEST")
                .name("Test")
                .sortBy("name")
                .sortDirection("asc")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<UserStatusResponseDto> result = service.getStatusesWithFilters(request);

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }
}
