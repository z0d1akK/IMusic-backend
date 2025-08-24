package imusic.backend.entity.ops;

import jakarta.persistence.*;
import lombok.*;
import imusic.backend.entity.ref.ProductCategory;

import java.io.Serializable;

@Entity
@Table(name = "category_attributes", schema = "ops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttribute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "default_value")
    private String defaultValue;
}

