package imusic.backend.dto.request.ops;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {
    private Long cartId;
    private Long clientId;
    private Long productId;
    private Integer minQuantity;
    private Integer maxQuantity;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}