package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.NotificationTypeRequestDto;
import imusic.backend.dto.response.ref.NotificationTypeResponseDto;
import imusic.backend.dto.create.ref.NotificationTypeCreateDto;
import imusic.backend.dto.update.ref.NotificationTypeUpdateDto;

import java.util.List;

public interface NotificationTypeService {
    List<NotificationTypeResponseDto> getAll();
    NotificationTypeResponseDto getById(Long id);
    NotificationTypeResponseDto create(NotificationTypeCreateDto dto);
    NotificationTypeResponseDto update(Long id, NotificationTypeUpdateDto dto);
    void delete(Long id);

    List<NotificationTypeResponseDto> getTypesWithFilters(NotificationTypeRequestDto request);
}
