package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // ADMIN, MANAGER, CLIENT

    @Column(nullable = false)
    private String name;
}
