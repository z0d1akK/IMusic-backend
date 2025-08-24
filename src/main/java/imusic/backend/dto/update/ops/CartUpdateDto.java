package imusic.backend.dto.update.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateDto {
    private Long clientId;
    private List<CartItemUpdateDto> items;
}