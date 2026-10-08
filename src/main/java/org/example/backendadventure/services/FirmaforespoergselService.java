package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Firmaforespoergsel;
import org.example.backendadventure.model.Kunde;
import org.example.backendadventure.model.Status;
import org.example.backendadventure.model.TidsValg;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.FirmaforespoergselRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirmaforespoergselService {
    // Antagelse: alle deltagere i et firmaarrangement er voksne
    private static final int FIRMA_MIN_ALDER = 18;

    @Autowired
    FirmaforespoergselRepo forespoergselRepo;

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    BookingService bookingService;

    // US5: alle forespørgsler, eller kun dem med en bestemt status
    public List<Firmaforespoergsel> findAll(Status status) {
        if (status == null) {
            return forespoergselRepo.findAllByOrderByDatoAsc();
        }
        return forespoergselRepo.findByStatusOrderByDatoAsc(status);
    }

    // US6
    public Firmaforespoergsel findById(int id) {
        return forespoergselRepo.findById(id).orElse(null);
    }

    // US4: firmakunden sender en forespørgsel
    public Firmaforespoergsel opret(Firmaforespoergsel forespoergsel) {
        if (forespoergsel.getFirmanavn() == null || forespoergsel.getFirmanavn().isBlank()) {
            throw new IllegalArgumentException("Firmanavn skal udfyldes");
        }
        if (forespoergsel.getKontaktperson() == null || forespoergsel.getKontaktperson().isBlank()) {
            throw new IllegalArgumentException("Kontaktperson skal udfyldes");
        }
        if (forespoergsel.getMail() == null || forespoergsel.getMail().isBlank()) {
            throw new IllegalArgumentException("Mail skal udfyldes");
        }
        if (forespoergsel.getTlfnr() == null || forespoergsel.getTlfnr().isBlank()) {
            throw new IllegalArgumentException("Telefonnummer skal udfyldes");
        }
        if (forespoergsel.getDato() == null || forespoergsel.getDato().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Dato kan ikke være i fortiden");
        }
        if (forespoergsel.getAntalDeltagere() <= 0) {
            throw new IllegalArgumentException("Antal deltagere skal være mindst 1");
        }
        if (forespoergsel.getAktiviteter() == null || forespoergsel.getAktiviteter().isEmpty()) {
            throw new IllegalArgumentException("Vælg mindst én aktivitet");
        }

        // Frontenden sender kun aktiviteternes id, så de rigtige aktiviteter hentes fra databasen
        List<Activity> aktiviteter = new ArrayList<>();
        for (Activity valgt : forespoergsel.getAktiviteter()) {
            Activity activity = activityRepo.findById(valgt.getId()).orElse(null);
            if (activity == null) {
                throw new IllegalArgumentException("Aktivitet findes ikke");
            }
            aktiviteter.add(activity);
        }

        forespoergsel.setId(0);
        forespoergsel.setAktiviteter(aktiviteter);
        forespoergsel.setStatus(Status.AFVENTER);
        return forespoergselRepo.save(forespoergsel);
    }

    // US7: manageren godkender og vælger en starttid for hver aktivitet.
    // @Transactional: fejler én booking, bliver ingen af dem gemt.
    @Transactional
    public Firmaforespoergsel godkend(int id, List<TidsValg> tider) {
        Firmaforespoergsel forespoergsel = findById(id);
        if (forespoergsel == null) {
            return null;
        }
        if (forespoergsel.getStatus() != Status.AFVENTER) {
            throw new IllegalArgumentException("Forespørgslen er allerede behandlet");
        }

        // Find den valgte tid for hver aktivitet
        List<LocalTime> starttider = new ArrayList<>();
        for (Activity activity : forespoergsel.getAktiviteter()) {
            LocalTime tid = null;
            for (TidsValg valg : tider) {
                if (valg.getActivityId() == activity.getId()) {
                    tid = valg.getTid();
                }
            }
            if (tid == null) {
                throw new IllegalArgumentException("Vælg en starttid for " + activity.getNavn());
            }
            starttider.add(tid);
        }

        tjekIngenOverlap(forespoergsel.getAktiviteter(), starttider);

        // Opret en booking pr. aktivitet. BookingService tjekker starttid og kapacitet.
        for (int i = 0; i < forespoergsel.getAktiviteter().size(); i++) {
            Booking booking = new Booking();
            booking.setActivity(forespoergsel.getAktiviteter().get(i));
            booking.setKunde(new Kunde(
                    forespoergsel.getFirmanavn() + " (" + forespoergsel.getKontaktperson() + ")",
                    forespoergsel.getMail(),
                    forespoergsel.getTlfnr()));
            booking.setDato(forespoergsel.getDato());
            booking.setTid(starttider.get(i));
            booking.setAntalPersoner(forespoergsel.getAntalDeltagere());
            booking.setMinAlder(FIRMA_MIN_ALDER);
            bookingService.opret(booking);
        }

        forespoergsel.setStatus(Status.GODKENDT);
        return forespoergselRepo.save(forespoergsel);
    }

    // US7: manageren afviser
    public Firmaforespoergsel afvis(int id) {
        Firmaforespoergsel forespoergsel = findById(id);
        if (forespoergsel == null) {
            return null;
        }
        if (forespoergsel.getStatus() != Status.AFVENTER) {
            throw new IllegalArgumentException("Forespørgslen er allerede behandlet");
        }
        forespoergsel.setStatus(Status.AFVIST);
        return forespoergselRepo.save(forespoergsel);
    }

    // Gruppen kan ikke være to steder på én gang
    private void tjekIngenOverlap(List<Activity> aktiviteter, List<LocalTime> starttider) {
        for (int i = 0; i < aktiviteter.size(); i++) {
            for (int j = i + 1; j < aktiviteter.size(); j++) {
                LocalTime startA = starttider.get(i);
                LocalTime slutA = startA.plusMinutes(aktiviteter.get(i).getVarighed());
                LocalTime startB = starttider.get(j);
                LocalTime slutB = startB.plusMinutes(aktiviteter.get(j).getVarighed());
                if (startA.isBefore(slutB) && startB.isBefore(slutA)) {
                    throw new IllegalArgumentException(aktiviteter.get(i).getNavn() + " og " + aktiviteter.get(j).getNavn() + " overlapper");
                }
            }
        }
    }
}
