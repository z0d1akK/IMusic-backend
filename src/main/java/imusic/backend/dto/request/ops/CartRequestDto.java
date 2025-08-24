package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class CartRequestDto extends BaseRequestDto {
    private Long userId;
    private Long clientId;
    private Boolean activeOnly;
}