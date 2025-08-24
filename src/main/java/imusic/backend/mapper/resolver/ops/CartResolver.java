package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.Cart;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.CartRepository;

@Component
public class CartResolver extends BaseEntityResolver<Cart, Long> {

    public CartResolver(CartRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "Cart";
    }
}
