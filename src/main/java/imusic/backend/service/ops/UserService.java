package imusic.backend.service.ops;

import imusic.backend.dto.auth.ChangeLoginRequest;
import imusic.backend.dto.auth.ChangePasswordRequest;
import imusic.backend.dto.request.ops.UserRequestDto;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getUsersByRoleCode(String code);
    List<UserResponseDto> findAvailableClientUsers(String query);
    UserResponseDto updateUser(Long id, UserUpdateDto dto);
    UserResponseDto updateUserProfile(Long id, UserUpdateDto dto);
    void blockUser(Long id);
    void unblockUser(Long id);
    void changePassword(Long id, ChangePasswordRequest request);
    void changeLogin(Long id, ChangeLoginRequest request);
    void uploadAvatar(Long id, MultipartFile file);
    void deleteUser(Long id);
    List<UserResponseDto> getUsersWithFilters(UserRequestDto request);
}