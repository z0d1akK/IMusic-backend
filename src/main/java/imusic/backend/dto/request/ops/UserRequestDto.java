package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class UserRequestDto extends BaseRequestDto {
    private Long roleId;
    private Long statusId;
    private String email;
    private String username;
    private String phone;
}

