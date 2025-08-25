package imusic.backend.dto.request.ops;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    private Long userId;
    private Long typeId;
    private Boolean read;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}

