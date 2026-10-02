package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Kunde;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.KundeRepo;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private final BookingRepo bookingRepo;
    private final ActivityRepo activityRepo;
    private final KundeRepo kundeRepo;

    public BookingService (BookingRepo bookingRepo, ActivityRepo activityRepo, KundeRepo KundeRepo) {
        this.bookingRepo = bookingRepo;
        this.activityRepo = activityRepo;
        this.kundeRepo = KundeRepo;
    }
    public Booking opret(int activityId, int kundeId, Booking booking) {
        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Aktivitet findes ikke"));
        Kunde kunde = kundeRepo.findById(kundeId)
                .orElseThrow(() -> new IllegalArgumentException("Kunde findes ikke"));

        if (booking.getDato().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Dato kan ikke være i fortiden");
        if (booking.getAntalPersoner() <= 0
                || booking.getAntalPersoner() > activity.getKapacitet())
            throw new IllegalArgumentException("Ugyldigt antal personer");

        booking.setActivity(activity);
        booking.setKunde(kunde);
        return bookingRepo.save(booking);
    }
}
