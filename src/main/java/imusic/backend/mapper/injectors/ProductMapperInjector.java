package imusic.backend.mapper.injectors;

import imusic.backend.service.ops.ProductAttributeService;
import imusic.backend.mapper.ops.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperInjector {

    @Autowired
    public void init(ProductAttributeService productAttributeService) {
        ProductMapper.ProductAttributeServiceStaticHolder.productAttributeService = productAttributeService;
    }
}
