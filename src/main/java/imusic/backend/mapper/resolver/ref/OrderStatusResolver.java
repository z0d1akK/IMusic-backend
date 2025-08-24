package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.OrderStatus;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.OrderStatusRepository;

@Component
public class OrderStatusResolver extends BaseEntityResolver<OrderStatus,Long> {

    public OrderStatusResolver(OrderStatusRepository orderStatusRepository) {
        super(orderStatusRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "OrderStatus";
    }
}
