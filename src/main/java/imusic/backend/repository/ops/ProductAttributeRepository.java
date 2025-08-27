package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.ProductAttribute;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long>{
    List<ProductAttribute> findAllByProduct_Id(Long productId);
    Optional<ProductAttribute> findByProduct_IdAndCategoryAttribute_Id(Long productId, Long categoryAttributeId);

}