package imusic.backend.dto.request.ref;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusRequestDto {
    private String code;
    private String name;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}