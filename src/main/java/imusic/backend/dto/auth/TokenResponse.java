package imusic.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenResponse {
    private Long id;
    private String token;
    private List<String> roles;
}

