package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.InventoryMovementTypeRequestDto;
import imusic.backend.dto.response.ref.InventoryMovementTypeResponseDto;
import imusic.backend.dto.create.ref.InventoryMovementTypeCreateDto;
import imusic.backend.dto.update.ref.InventoryMovementTypeUpdateDto;

import java.util.List;

public interface InventoryMovementTypeService {
    List<InventoryMovementTypeResponseDto> getAll();
    InventoryMovementTypeResponseDto getById(Long id);
    InventoryMovementTypeResponseDto create(InventoryMovementTypeCreateDto dto);
    InventoryMovementTypeResponseDto update(Long id, InventoryMovementTypeUpdateDto dto);
    void delete(Long id);

    List<InventoryMovementTypeResponseDto> getMovementsWithFilters(InventoryMovementTypeRequestDto request);
}
