package imusic.backend.dto.request.ops;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderRequestDto {

    private Long clientId;
    private Long statusId;
    private Long createdById;

    private String deliveryAddress;

    private Double minTotalPrice;
    private Double maxTotalPrice;

    private String fromDate;
    private String toDate;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 12;

    @Builder.Default
    private String sortBy = "id";

    @Builder.Default
    private String sortDirection = "asc";

    @Builder.Default
    private List<String> filters = List.of();
}
