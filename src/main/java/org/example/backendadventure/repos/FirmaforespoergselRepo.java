package org.example.backendadventure.repos;

import org.example.backendadventure.model.Firmaforespoergsel;
import org.example.backendadventure.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FirmaforespoergselRepo extends JpaRepository<Firmaforespoergsel, Integer> {
    List<Firmaforespoergsel> findByStatusOrderByDatoAsc(Status status);

    List<Firmaforespoergsel> findAllByOrderByDatoAsc();

    // Bruges til at tjekke om en aktivitet indgår i en forespørgsel, før den slettes
    boolean existsByAktiviteterId(int activityId);
}
