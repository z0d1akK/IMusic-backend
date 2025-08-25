package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.UserStatusRequestDto;
import imusic.backend.dto.response.ref.UserStatusResponseDto;
import imusic.backend.dto.create.ref.UserStatusCreateDto;
import imusic.backend.dto.update.ref.UserStatusUpdateDto;
import imusic.backend.service.ref.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService service;

    @GetMapping
    public ResponseEntity<List<UserStatusResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStatusResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatusResponseDto> create(@RequestBody UserStatusCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatusResponseDto> update(@PathVariable Long id, @RequestBody UserStatusUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserStatusResponseDto>> getWithFilters(@RequestBody UserStatusRequestDto request) {
        return ResponseEntity.ok(service.getStatusesWithFilters(request));
    }
}
