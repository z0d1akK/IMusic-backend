package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.ProductAttribute;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.ProductAttributeRepository;

@Component
public class ProductAttributeResolver extends BaseEntityResolver<ProductAttribute, Long> {

    public ProductAttributeResolver(ProductAttributeRepository productAttributeRepository) {
        super(productAttributeRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "ProductAttribute";
    }
}
