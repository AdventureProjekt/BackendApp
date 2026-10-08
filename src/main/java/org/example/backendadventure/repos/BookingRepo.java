package org.example.backendadventure.repos;

import org.example.backendadventure.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findByActivityIdAndDatoAndTid(int activityId, LocalDate dato, LocalTime tid);

    // Alle bookinger på en dato, sorteret efter tid (dagsoversigt)
    List<Booking> findByDatoOrderByTidAsc(LocalDate dato);

    // Bookinger for én aktivitet på en dato (aktivitetsmedarbejder)
    List<Booking> findByActivityIdAndDatoOrderByTidAsc(int activityId, LocalDate dato);

    // Kommende bookinger for en aktivitet (bruges når ejeren ændrer aktiviteten)
    List<Booking> findByActivityIdAndDatoGreaterThanEqual(int activityId, LocalDate dato);

    // Bruges til at tjekke om en aktivitet har bookinger, før den slettes
    boolean existsByActivityId(int activityId);
}
