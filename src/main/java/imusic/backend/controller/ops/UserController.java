package imusic.backend.controller.ops;

import imusic.backend.dto.auth.ChangeLoginRequest;
import imusic.backend.dto.auth.ChangePasswordRequest;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.request.ops.UserRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.service.ops.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/paged")
    public ResponseEntity<PageResponseDto<UserResponseDto>> getPagedUsers(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.getPagedUsers(request));
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<UserResponseDto> getClientUsers() {
        return userService.getUsersByRoleCode("CLIENT");
    }

    @GetMapping("/clients/available")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponseDto>> getAvailableClientUsers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(userService.findAvailableClientUsers(query));
    }

    @GetMapping("/by-role/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable String code) {
        return ResponseEntity.ok(userService.getUsersByRoleCode(code));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLIENT')")
    public ResponseEntity<UserResponseDto> updateUserProfile(@PathVariable Long id,
                                                             @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUserProfile(id, dto));
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Void> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLIENT')")

    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/change-login")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CLIENT')")
    public ResponseEntity<Void> changeLogin(@PathVariable Long id, @RequestBody ChangeLoginRequest request) {
        userService.changeLogin(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<Void> uploadAvatar(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file) {
        userService.uploadAvatar(id, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
