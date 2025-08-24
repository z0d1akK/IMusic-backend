package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class OrderRequestDto extends BaseRequestDto {
    private Long clientId;
    private Long statusId;
    private Long paymentStatusId;
    private Long paymentMethodId;
    private String deliveryAddress;
}