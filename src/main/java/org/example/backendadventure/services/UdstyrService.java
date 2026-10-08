package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Udstyr;
import org.example.backendadventure.model.UdstyrBehov;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.UdstyrRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UdstyrService {

    @Autowired
    UdstyrRepo udstyrRepo;

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    BookingRepo bookingRepo;

    public List<Udstyr> findByActivity(int activityId) {
        return udstyrRepo.findByActivityId(activityId);
    }

    public Udstyr opret(Udstyr udstyr) {
        if (udstyr.getNavn() == null || udstyr.getNavn().isBlank()) {
            throw new IllegalArgumentException("Navn skal udfyldes");
        }
        if (udstyr.getAntalPrPerson() <= 0) {
            throw new IllegalArgumentException("Antal pr. person skal være mindst 1");
        }
        Activity activity = activityRepo.findById(udstyr.getActivity().getId()).orElse(null);
        if (activity == null) {
            throw new IllegalArgumentException("Aktivitet findes ikke");
        }
        udstyr.setActivity(activity);
        return udstyrRepo.save(udstyr);
    }

    public boolean deleteById(int id) {
        if (!udstyrRepo.existsById(id)) {
            return false;
        }
        udstyrRepo.deleteById(id);
        return true;
    }

    // Hvor meget udstyr skal bruges i alt på en dato.
    // activityId = 0 betyder alle aktiviteter.
    // Eksempel: paintball har 1 gevær pr. person, og der er 6 + 4 deltagere -> 10 geværer.
    public List<UdstyrBehov> behov(LocalDate dato, int activityId) {
        List<Booking> bookinger;
        if (activityId == 0) {
            bookinger = bookingRepo.findByDatoOrderByTidAsc(dato);
        } else {
            bookinger = bookingRepo.findByActivityIdAndDatoOrderByTidAsc(activityId, dato);
        }

        List<UdstyrBehov> liste = new ArrayList<>();
        for (Booking booking : bookinger) {
            for (Udstyr udstyr : udstyrRepo.findByActivityId(booking.getActivity().getId())) {
                int antal = udstyr.getAntalPrPerson() * booking.getAntalPersoner();
                tilfoej(liste, booking.getActivity().getNavn(), udstyr.getNavn(), antal);
            }
        }
        return liste;
    }

    // Lægger antallet til, hvis udstyret allerede er på listen, ellers tilføjes en ny linje
    private void tilfoej(List<UdstyrBehov> liste, String aktivitet, String udstyr, int antal) {
        for (UdstyrBehov behov : liste) {
            if (behov.getAktivitet().equals(aktivitet) && behov.getUdstyr().equals(udstyr)) {
                behov.setAntal(behov.getAntal() + antal);
                return;
            }
        }
        liste.add(new UdstyrBehov(aktivitet, udstyr, antal));
    }
}
