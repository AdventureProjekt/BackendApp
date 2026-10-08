package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Kunde;
import org.example.backendadventure.model.LedigTid;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.KundeRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    // Hver aktivitet har sine egne åbningstider (se Activity). Starttiderne følger aktivitetens varighed.
    private final BookingRepo bookingRepo;
    private final ActivityRepo activityRepo;
    private final KundeRepo kundeRepo;

    public BookingService(BookingRepo bookingRepo, ActivityRepo activityRepo, KundeRepo KundeRepo) {
        this.bookingRepo = bookingRepo;
        this.activityRepo = activityRepo;
        this.kundeRepo = KundeRepo;
    }

    public List<Booking> findAll() {
        return bookingRepo.findAll();
    }

    public Booking findById(int id) {
        return bookingRepo.findById(id).orElse(null);
    }

    // US10: alle bookinger på en dato, sorteret efter tid
    public List<Booking> findByDato(LocalDate dato) {
        return bookingRepo.findByDatoOrderByTidAsc(dato);
    }

    // US11: bookinger for én aktivitet på en dato
    public List<Booking> findByActivityOgDato(int activityId, LocalDate dato) {
        return bookingRepo.findByActivityIdAndDatoOrderByTidAsc(activityId, dato);
    }

    // US2: alle starttider for en aktivitet på en dato, med antal ledige pladser
    public List<LedigTid> ledigeTider(int activityId, LocalDate dato) {
        Activity activity = findActivity(activityId);

        List<LedigTid> tider = new ArrayList<>();
        LocalTime tid = activity.getAabner();
        while (activity.erGyldigStarttid(tid)) {
            // I dag vises kun tider, der ikke er passeret
            if (!erPasseret(dato, tid)) {
                int ledige = activity.getKapacitet() - optagnePladser(activityId, dato, tid, 0);
                tider.add(new LedigTid(tid, ledige));
            }
            tid = tid.plusMinutes(activity.getVarighed());
        }
        return tider;
    }

    // US3 + US8: opret en booking (kunden oprettes, hvis den ikke findes)
    public Booking opret(Booking booking) {
        if (booking.getActivity() == null) {
            throw new IllegalArgumentException("Vælg en aktivitet");
        }
        Activity activity = findActivity(booking.getActivity().getId());

        validerKunde(booking.getKunde());
        validerBooking(booking, activity, 0);

        // Kunden gemmes først nu, så den kun oprettes når bookingen er godkendt
        Kunde kunde = findEllerOpretKunde(booking.getKunde());

        booking.setActivity(activity);
        booking.setKunde(kunde);
        return bookingRepo.save(booking);
    }

    // US9: manageren retter dato, tid, antal personer eller alder på en booking
    public Booking opdater(int id, Booking nyeVaerdier) {
        Booking eksisterende = findById(id);
        if (eksisterende == null) {
            return null;
        }

        Activity activity = eksisterende.getActivity();
        if (nyeVaerdier.getActivity() != null && nyeVaerdier.getActivity().getId() != 0) {
            activity = findActivity(nyeVaerdier.getActivity().getId());
        }

        // Bookingen selv tæller ikke med, når kapaciteten tjekkes
        validerBooking(nyeVaerdier, activity, id);

        eksisterende.setActivity(activity);
        eksisterende.setDato(nyeVaerdier.getDato());
        eksisterende.setTid(nyeVaerdier.getTid());
        eksisterende.setAntalPersoner(nyeVaerdier.getAntalPersoner());
        eksisterende.setMinAlder(nyeVaerdier.getMinAlder());
        return bookingRepo.save(eksisterende);
    }

    // US9: slet en booking
    public boolean deleteById(int id) {
        if (!bookingRepo.existsById(id)) {
            return false;
        }
        bookingRepo.deleteById(id);
        return true;
    }

    private Activity findActivity(int activityId) {
        Activity activity = activityRepo.findById(activityId).orElse(null);
        if (activity == null) {
            throw new IllegalArgumentException("Aktivitet findes ikke");
        }
        return activity;
    }

    private void validerKunde(Kunde kunde) {
        if (kunde == null) {
            throw new IllegalArgumentException("Kundeoplysninger mangler");
        }
        if (kunde.getNavn() == null || kunde.getNavn().isBlank()) {
            throw new IllegalArgumentException("Navn skal udfyldes");
        }
        if (kunde.getMail() == null || kunde.getMail().isBlank()) {
            throw new IllegalArgumentException("Mail skal udfyldes");
        }
        if (kunde.getTlfnr() == null || kunde.getTlfnr().isBlank()) {
            throw new IllegalArgumentException("Telefonnummer skal udfyldes");
        }
    }

    // Tjekker dato, antal, starttid, alder og kapacitet.
    // ignorerBookingId bruges ved opdatering, så bookingen ikke tæller sig selv med.
    private void validerBooking(Booking booking, Activity activity, int ignorerBookingId) {
        if (booking.getDato() == null || booking.getDato().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Dato kan ikke være i fortiden");
        }
        if (booking.getAntalPersoner() <= 0) {
            throw new IllegalArgumentException("Antal personer skal være mindst 1");
        }

        // Tidspunktet skal være en gyldig starttid
        LocalTime tid = booking.getTid();
        if (tid == null) {
            throw new IllegalArgumentException("Vælg et tidspunkt");
        }
        if (!activity.erGyldigStarttid(tid)) {
            throw new IllegalArgumentException(activity.getNavn() + " har åbent kl. " + activity.getAabner() + "–" + activity.getLukker()
                    + " og starter hvert " + activity.getVarighed() + ". minut");
        }
        if (erPasseret(booking.getDato(), tid)) {
            throw new IllegalArgumentException("Tidspunktet er allerede passeret");
        }

        if (booking.getMinAlder() < activity.getAldersgrænse()) {
            throw new IllegalArgumentException("Deltagerne er for unge til denne aktivitet (aldersgrænse " + activity.getAldersgrænse() + " år)");
        }

        int optaget = optagnePladser(activity.getId(), booking.getDato(), tid, ignorerBookingId);
        if (optaget + booking.getAntalPersoner() > activity.getKapacitet()) {
            int ledige = activity.getKapacitet() - optaget;
            throw new IllegalArgumentException("Der er kun " + ledige + " ledige pladser kl. " + tid);
        }
    }

    private boolean erPasseret(LocalDate dato, LocalTime tid) {
        return dato.equals(LocalDate.now()) && tid.isBefore(LocalTime.now());
    }

    private int optagnePladser(int activityId, LocalDate dato, LocalTime tid, int ignorerBookingId) {
        List<Booking> eksisterende = bookingRepo.findByActivityIdAndDatoAndTid(activityId, dato, tid);
        int optaget = 0;
        for (Booking b : eksisterende) {
            if (b.getId() != ignorerBookingId) {
                optaget = optaget + b.getAntalPersoner();
            }
        }
        return optaget;
    }

    private Kunde findEllerOpretKunde(Kunde nyKunde) {
        Kunde kunde = kundeRepo.findByMail(nyKunde.getMail()).orElse(null);
        if (kunde == null) {
            return kundeRepo.save(nyKunde);
        }
        kunde.setNavn(nyKunde.getNavn());
        kunde.setTlfnr(nyKunde.getTlfnr());
        return kundeRepo.save(kunde);
    }
}
