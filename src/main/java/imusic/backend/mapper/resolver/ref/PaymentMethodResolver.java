package imusic.backend.mapper.resolver.ref;

import imusic.backend.entity.ref.PaymentMethod;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.PaymentMethodRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodResolver extends BaseEntityResolver<PaymentMethod, Long> {

    public PaymentMethodResolver(PaymentMethodRepository paymentMethodRepository) {
        super(paymentMethodRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "InventoryMovement";
    }
}
