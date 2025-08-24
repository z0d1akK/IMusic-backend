package imusic.backend.dto.response.ref;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTypeResponseDto{
    private Long id;
    private String code;
    private String name;
}