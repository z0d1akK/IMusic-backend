package imusic.backend.controller.ops;

import imusic.backend.dto.request.ops.ClientRequestDto;
import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.dto.response.ops.ClientResponseDto;
import imusic.backend.entity.ops.User;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.ops.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final RoleResolver roleResolver;
    private final UserStatusResolver userStatusResolver;

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients(new ClientRequestDto()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> getProfile() {
        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(),roleResolver,userStatusResolver);
        ClientResponseDto clientDTO = clientService.getClientProfileByUserId(currentUser.getId());
        return ResponseEntity.ok(clientDTO);
    }
    @PostMapping("/paged")
    public ResponseEntity<List<ClientResponseDto>> getClientsWithFilters(@RequestBody ClientRequestDto request) {
        return ResponseEntity.ok(clientService.getClientsWithFilters(request));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientCreateDto dto) {
        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id,
                                                          @RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
}
