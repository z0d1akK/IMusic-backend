package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>{
    Optional<Client> findByUserId(Long userId);

    @Query("SELECT c.companyName FROM Client c WHERE c.id = :clientId")
    String findCompanyNameById(@Param("clientId") Long clientId);

}