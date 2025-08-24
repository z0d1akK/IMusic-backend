package imusic.backend.entity.ops;

import jakarta.persistence.*;
import lombok.*;
import imusic.backend.entity.ref.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history", schema = "ops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "old_status_id", nullable = false)
    private OrderStatus oldStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "new_status_id", nullable = false)
    private OrderStatus newStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;
}
