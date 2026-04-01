package imusic.backend.repository.ops;

import imusic.backend.entity.ops.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByUserId(Long userId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
}
