package imusic.backend.service.ref;

import imusic.backend.dto.create.ref.InventoryMovementTypeCreateDto;
import imusic.backend.dto.request.ref.InventoryMovementTypeRequestDto;
import imusic.backend.dto.response.ref.InventoryMovementTypeResponseDto;
import imusic.backend.dto.update.ref.InventoryMovementTypeUpdateDto;
import imusic.backend.entity.ref.InventoryMovementType;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.InventoryMovementTypeMapper;
import imusic.backend.repository.ref.InventoryMovementTypeRepository;
import imusic.backend.service.impl.ref.InventoryMovementTypeServiceImpl;
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

class InventoryMovementTypeServiceTest {

    @Mock
    private InventoryMovementTypeRepository repository;

    @Mock
    private InventoryMovementTypeMapper mapper;

    @InjectMocks
    private InventoryMovementTypeServiceImpl service;

    private InventoryMovementType entity;
    private InventoryMovementTypeResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = InventoryMovementType.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        responseDto = InventoryMovementTypeResponseDto.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();
    }

    @Test
    @DisplayName("Get all inventory movement types")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<InventoryMovementTypeResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get inventory movement type by ID")
    void testGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        InventoryMovementTypeResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get by invalid ID should throw exception")
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getById(1L));
        assertTrue(exception.getMessage().contains("Тип движения запасов не найден"));
    }

    @Test
    @DisplayName("Create new inventory movement type")
    void testCreate() {
        InventoryMovementTypeCreateDto dto = InventoryMovementTypeCreateDto.builder()
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        InventoryMovementTypeResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update inventory movement type")
    void testUpdate() {
        InventoryMovementTypeUpdateDto dto = InventoryMovementTypeUpdateDto.builder()
                .code("NEW_CODE")
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                InventoryMovementTypeResponseDto.builder()
                        .id(1L)
                        .code("NEW_CODE")
                        .name("New Name")
                        .build()
        );

        InventoryMovementTypeResponseDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getCode());
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update non-existent inventory movement type should throw exception")
    void testUpdateNotFound() {
        InventoryMovementTypeUpdateDto dto = new InventoryMovementTypeUpdateDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.update(1L, dto));
        assertTrue(exception.getMessage().contains("Тип движения запасов не найден"));
    }

    @Test
    @DisplayName("Delete inventory movement type")
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Delete non-existent inventory movement type should throw exception")
    void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.delete(1L));
        assertTrue(exception.getMessage().contains("Тип движения запасов не найден"));
    }

    @Test
    @DisplayName("Get inventory movement types with filters")
    void testGetStatusesWithFilters() {
        InventoryMovementTypeRequestDto request = InventoryMovementTypeRequestDto.builder()
                .page(0)
                .size(10)
                .code("TEST")
                .name("Test")
                .sortBy("name")
                .sortDirection("asc")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<InventoryMovementTypeResponseDto> result = service.getMovementsWithFilters(request);

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }
}

