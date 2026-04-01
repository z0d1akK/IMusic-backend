package imusic.backend.repository.ops;

import imusic.backend.entity.ops.ComparisonItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComparisonItemRepository extends JpaRepository<ComparisonItem, Long> {
    List<ComparisonItem> findAllByComparison_Id(Long comparisonId);
    boolean existsByComparison_IdAndProduct_Id(Long comparisonId, Long productId);
    void deleteByComparison_IdAndProduct_Id(Long comparisonId, Long productId);
    void deleteByProductId(Long productId);
    long countByComparisonId(Long comparisonId);
}