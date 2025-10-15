package imusic.backend.entity.ops;

import jakarta.persistence.*;
import lombok.*;
import imusic.backend.entity.ref.InventoryMovementType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements", schema = "ops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "movement_type_id", nullable = false)
    private InventoryMovementType movementType;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "comment")
    private String comment;

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
