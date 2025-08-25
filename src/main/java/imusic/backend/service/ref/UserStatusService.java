package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.UserStatusRequestDto;
import imusic.backend.dto.response.ref.UserStatusResponseDto;
import imusic.backend.dto.create.ref.UserStatusCreateDto;
import imusic.backend.dto.update.ref.UserStatusUpdateDto;

import java.util.List;

public interface UserStatusService {
    List<UserStatusResponseDto> getAll();
    UserStatusResponseDto getById(Long id);
    UserStatusResponseDto create(UserStatusCreateDto dto);
    UserStatusResponseDto update(Long id, UserStatusUpdateDto dto);
    void delete(Long id);

    List<UserStatusResponseDto> getStatusesWithFilters(UserStatusRequestDto request);
}
