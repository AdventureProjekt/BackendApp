package org.example.backendadventure.repos;
import org.example.backendadventure.model.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KundeRepo extends JpaRepository<Kunde, Integer> {
    Optional<Kunde> findByMail(String mail);
}

