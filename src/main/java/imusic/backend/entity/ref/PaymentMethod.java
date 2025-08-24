package imusic.backend.entity.ref;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_methods", schema = "ref")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // CASH, CARD, BANK_TRANSFER, ONLINE

    @Column(nullable = false)
    private String name;
}
