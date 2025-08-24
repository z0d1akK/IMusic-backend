package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications_types", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // INFO, WARNING, ALERT

    @Column(nullable = false)
    private String name;
}
