package imusic.backend.dto.response.ops;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryPointDto {
    private String label;
    private Float price;
}