package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class ProductAttributeRequestDto extends BaseRequestDto {
    private Long productId;
    private Long categoryAttributeId;
    private String value;
}

