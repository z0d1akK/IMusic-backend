package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_movement_types", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;


    @Column(nullable = false)
    private String name;
}
