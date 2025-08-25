package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.RoleRequestDto;
import imusic.backend.dto.response.ref.RoleResponseDto;
import imusic.backend.dto.create.ref.RoleCreateDto;
import imusic.backend.dto.update.ref.RoleUpdateDto;
import imusic.backend.service.ref.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> create(@RequestBody RoleCreateDto dto) {
        return ResponseEntity.ok(roleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> update(@PathVariable Long id, @RequestBody RoleUpdateDto dto) {
        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RoleResponseDto>> getWithFilters(@RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(roleService.getRolesWithFilters(request));
    }
}
