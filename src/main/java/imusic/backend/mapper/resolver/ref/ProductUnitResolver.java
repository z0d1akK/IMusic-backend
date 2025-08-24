package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.ProductUnit;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.ProductUnitRepository;

@Component
public class ProductUnitResolver extends BaseEntityResolver<ProductUnit, Long> {

    public ProductUnitResolver(ProductUnitRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "ProductUnit";
    }
}
