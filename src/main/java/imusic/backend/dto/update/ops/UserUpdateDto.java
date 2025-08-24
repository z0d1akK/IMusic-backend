package imusic.backend.dto.update.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String avatarPath;
    private Long roleId;
    private Long statusId;
    private Boolean isBlocked;
    private Boolean isDeleted;
}

