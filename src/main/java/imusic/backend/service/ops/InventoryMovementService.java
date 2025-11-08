package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.request.ops.InventoryMovementRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.entity.ops.Product;

import java.util.List;

public interface InventoryMovementService {
    List<InventoryMovementResponseDto> getAll();
    InventoryMovementResponseDto getById(Long id);
    InventoryMovementResponseDto create(InventoryMovementCreateDto dto);
    void createInventoryMovement(Product product, int quantity, String movementCode, String comment);
    InventoryMovementResponseDto update(Long id, InventoryMovementUpdateDto dto);
    void delete(Long id);
    PageResponseDto<InventoryMovementResponseDto> getPagedMovements(InventoryMovementRequestDto request);
}
