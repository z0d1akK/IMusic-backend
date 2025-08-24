package imusic.backend.dto.request.ops;


import imusic.backend.dto.request.BaseRequestDto;

public class CategoryAttributeRequestDto extends BaseRequestDto {
    private Long categoryId;
    private String name;
    private String defaultValue;
}