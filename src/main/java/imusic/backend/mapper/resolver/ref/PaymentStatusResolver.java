package imusic.backend.mapper.resolver.ref;

import imusic.backend.entity.ref.PaymentStatus;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.PaymentStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusResolver extends BaseEntityResolver<PaymentStatus, Long> {

    public PaymentStatusResolver(PaymentStatusRepository paymentStatusRepository) {
        super(paymentStatusRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "InventoryMovement";
    }
}