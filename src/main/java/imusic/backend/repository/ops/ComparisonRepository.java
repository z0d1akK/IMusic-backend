package imusic.backend.repository.ops;

import imusic.backend.entity.ops.Comparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComparisonRepository extends JpaRepository<Comparison, Long> {
    List<Comparison> findAllByUserId(Long userId);
}