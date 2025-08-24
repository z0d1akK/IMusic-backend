package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.ProductCategoryRepository;

@Component
public class ProductCategoryResolver extends BaseEntityResolver<ProductCategory, Long> {

    public ProductCategoryResolver(ProductCategoryRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "ProductCategory";
    }
}

