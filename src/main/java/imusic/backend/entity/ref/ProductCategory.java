package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_categories", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // MICROPHONES, HEADPHONES, SPEAKERS, DJ_EQUIPMENT, STUDIO_MONITORS, MUSICAL_INSTRUMENTS, ACCESSORIES, CABLES_ADAPTERS, AUDIO_INTERFACE, SOFTWARE


    @Column(nullable = false)
    private String name;
}
