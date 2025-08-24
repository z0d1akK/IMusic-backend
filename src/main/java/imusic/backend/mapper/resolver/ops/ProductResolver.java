package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.Product;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.ProductRepository;

@Component
public class ProductResolver extends BaseEntityResolver<Product, Long> {

    public ProductResolver(ProductRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "Product";
    }
}
