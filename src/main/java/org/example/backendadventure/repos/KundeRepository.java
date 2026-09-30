package org.example.backendadventure.repos;
import org.example.backendadventure.model.Kunde;
import org.springframework.data.jpa.repository.JpaRepository;
public interface KundeRepository extends JpaRepository<Kunde, Integer> {
}
