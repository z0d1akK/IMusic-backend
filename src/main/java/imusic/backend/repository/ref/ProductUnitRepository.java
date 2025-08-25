package imusic.backend.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ref.ProductUnit;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {}