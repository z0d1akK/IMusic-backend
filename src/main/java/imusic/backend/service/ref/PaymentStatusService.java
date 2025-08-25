package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.PaymentStatusRequestDto;
import imusic.backend.dto.response.ref.PaymentStatusResponseDto;
import imusic.backend.dto.create.ref.PaymentStatusCreateDto;
import imusic.backend.dto.update.ref.PaymentStatusUpdateDto;

import java.util.List;

public interface PaymentStatusService {
    List<PaymentStatusResponseDto> getAll();
    PaymentStatusResponseDto getById(Long id);
    PaymentStatusResponseDto create(PaymentStatusCreateDto dto);
    PaymentStatusResponseDto update(Long id, PaymentStatusUpdateDto dto);
    void delete(Long id);

    List<PaymentStatusResponseDto> getStatusesWithFilters(PaymentStatusRequestDto request);
}
