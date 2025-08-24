package imusic.backend.dto.update.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateDto {
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private String inn;
    private String contactPerson;
    private String documentsPath;
}

