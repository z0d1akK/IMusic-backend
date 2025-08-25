package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.NotificationTypeRequestDto;
import imusic.backend.dto.response.ref.NotificationTypeResponseDto;
import imusic.backend.dto.create.ref.NotificationTypeCreateDto;
import imusic.backend.dto.update.ref.NotificationTypeUpdateDto;
import imusic.backend.service.ref.NotificationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/notification-types")
@RequiredArgsConstructor
public class NotificationTypeController {

    private final NotificationTypeService service;

    @GetMapping
    public ResponseEntity<List<NotificationTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTypeResponseDto> create(@RequestBody NotificationTypeCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationTypeResponseDto> update(@PathVariable Long id, @RequestBody NotificationTypeUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<NotificationTypeResponseDto>> getWithFilters(@RequestBody NotificationTypeRequestDto request) {
        return ResponseEntity.ok(service.getTypesWithFilters(request));
    }
}
