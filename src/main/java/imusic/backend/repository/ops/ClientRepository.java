package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{
    Optional<Client> findByUserId(Long userId);
    Client getClientsByUserId(Long id);
}