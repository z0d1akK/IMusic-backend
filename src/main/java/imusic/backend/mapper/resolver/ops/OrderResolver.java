package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.Order;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.OrderRepository;

@Component
public class OrderResolver extends BaseEntityResolver<Order, Long> {

    public OrderResolver(OrderRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "Order";
    }
}
