package imusic.backend.entity.ops;

import jakarta.persistence.*;
import lombok.*;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.entity.ref.ProductUnit;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", schema = "ops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Float price;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private ProductUnit unit;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "warehouse_quantity", nullable = false)
    private Integer warehouseQuantity;

    @Column(name = "min_stock_level")
    private Integer minStockLevel;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
