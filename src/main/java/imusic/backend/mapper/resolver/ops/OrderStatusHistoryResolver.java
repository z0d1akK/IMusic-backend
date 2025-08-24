package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.OrderStatusHistory;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.OrderStatusHistoryRepository;

@Component
public class OrderStatusHistoryResolver extends BaseEntityResolver<OrderStatusHistory, Long> {

    public OrderStatusHistoryResolver(OrderStatusHistoryRepository orderStatusHistoryRepository) {
        super(orderStatusHistoryRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "OrderStatusHistory";
    }
}
