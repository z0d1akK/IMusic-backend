package imusic.backend.dto.response.ops;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    private Long id;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private String inn;
    private String contactPerson;
    private String documentsPath;
    private Long userId;
    private String username;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
