package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.RoleRequestDto;
import imusic.backend.dto.response.ref.RoleResponseDto;
import imusic.backend.dto.create.ref.RoleCreateDto;
import imusic.backend.dto.update.ref.RoleUpdateDto;

import java.util.List;

public interface RoleService {
    List<RoleResponseDto> getAll();
    RoleResponseDto getById(Long id);
    RoleResponseDto create(RoleCreateDto dto);
    RoleResponseDto update(Long id, RoleUpdateDto dto);
    void delete(Long id);
    List<RoleResponseDto> getRolesWithFilters(RoleRequestDto request);
}

