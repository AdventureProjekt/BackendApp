
package org.example.backendadventure.repos;

import org.example.backendadventure.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
}
