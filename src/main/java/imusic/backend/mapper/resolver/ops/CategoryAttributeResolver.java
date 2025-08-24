package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.CategoryAttribute;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.CategoryAttributeRepository;

@Component
public class CategoryAttributeResolver extends BaseEntityResolver<CategoryAttribute, Long> {

    public CategoryAttributeResolver(CategoryAttributeRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "CategoryAttribute";
    }
}
