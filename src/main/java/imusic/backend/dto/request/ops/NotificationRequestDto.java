package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class NotificationRequestDto extends BaseRequestDto {
    private Long userId;
    private Long typeId;
    private Boolean read;
}

