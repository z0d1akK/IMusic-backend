package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_statuses", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // NEW, PAID, IN_PROGRESS, SHIPPED, DELIVERED, CANCELED, RETURNED

    @Column(nullable = false)
    private String name;
}

