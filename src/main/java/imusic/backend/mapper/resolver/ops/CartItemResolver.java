package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.CartItem;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.CartItemRepository;

@Component
public class CartItemResolver extends BaseEntityResolver<CartItem, Long> {

    public CartItemResolver(CartItemRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "CartItem";
    }
}
