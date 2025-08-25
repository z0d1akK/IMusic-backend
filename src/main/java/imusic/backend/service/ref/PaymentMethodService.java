package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.PaymentMethodRequestDto;
import imusic.backend.dto.response.ref.PaymentMethodResponseDto;
import imusic.backend.dto.create.ref.PaymentMethodCreateDto;
import imusic.backend.dto.update.ref.PaymentMethodUpdateDto;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethodResponseDto> getAll();
    PaymentMethodResponseDto getById(Long id);
    PaymentMethodResponseDto create(PaymentMethodCreateDto dto);
    PaymentMethodResponseDto update(Long id, PaymentMethodUpdateDto dto);
    void delete(Long id);

    List<PaymentMethodResponseDto> getStatusesWithFilters(PaymentMethodRequestDto request);
}
