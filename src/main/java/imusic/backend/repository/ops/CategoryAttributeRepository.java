package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.CategoryAttribute;

import java.util.List;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long>{
    List<CategoryAttribute> findByCategoryId(Long categoryId);
}