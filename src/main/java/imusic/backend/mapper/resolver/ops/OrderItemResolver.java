package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.OrderItem;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.OrderItemRepository;

@Component
public class OrderItemResolver extends BaseEntityResolver<OrderItem,Long> {

    public OrderItemResolver(OrderItemRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "OrderItem";
    }
}
