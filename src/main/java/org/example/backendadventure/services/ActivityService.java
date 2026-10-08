package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.FirmaforespoergselRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepo activityRepo;
    private final BookingRepo bookingRepo;
    private final FirmaforespoergselRepo forespoergselRepo;

    public ActivityService(ActivityRepo activityRepo, BookingRepo bookingRepo, FirmaforespoergselRepo forespoergselRepo) {
        this.activityRepo = activityRepo;
        this.bookingRepo = bookingRepo;
        this.forespoergselRepo = forespoergselRepo;
    }

    public List<Activity> findAll() {
        return activityRepo.findAll();
    }

    public Activity findById(int id) {
        return activityRepo.findById(id).orElse(null);
    }

    public Activity save(Activity activity) {
        valider(activity);
        return activityRepo.save(activity);
    }

    public Activity opdater(int id, Activity activity) {
        Activity eksisterende = findById(id);
        if (eksisterende == null) {
            return null;
        }
        valider(activity);
        tjekKommendeBookinger(id, activity);

        eksisterende.setNavn(activity.getNavn());
        eksisterende.setPris(activity.getPris());
        eksisterende.setBeskrivelse(activity.getBeskrivelse());
        eksisterende.setAldersgrænse(activity.getAldersgrænse());
        eksisterende.setVarighed(activity.getVarighed());
        eksisterende.setKapacitet(activity.getKapacitet());
        eksisterende.setAabner(activity.getAabner());
        eksisterende.setLukker(activity.getLukker());
        return activityRepo.save(eksisterende);
    }

    // Ejeren må ikke ændre tider, varighed eller kapacitet, så kommende bookinger bliver ugyldige
    private void tjekKommendeBookinger(int activityId, Activity nyeVaerdier) {
        List<Booking> kommende = bookingRepo.findByActivityIdAndDatoGreaterThanEqual(activityId, LocalDate.now());

        for (Booking booking : kommende) {
            if (!nyeVaerdier.erGyldigStarttid(booking.getTid())) {
                throw new IllegalArgumentException("Ændringen passer ikke med en eksisterende booking d. "
                        + booking.getDato() + " kl. " + booking.getTid() + ". Flyt eller slet bookingen først.");
            }

            // Læg alle bookinger på samme dato og tid sammen
            int personer = 0;
            for (Booking anden : kommende) {
                if (anden.getDato().equals(booking.getDato()) && anden.getTid().equals(booking.getTid())) {
                    personer = personer + anden.getAntalPersoner();
                }
            }
            if (personer > nyeVaerdier.getKapacitet()) {
                throw new IllegalArgumentException("Kapaciteten kan ikke sættes til " + nyeVaerdier.getKapacitet()
                        + ", fordi der allerede er booket " + personer + " personer d. " + booking.getDato()
                        + " kl. " + booking.getTid());
            }
        }
    }

    public boolean deleteById(int id) {
        if (!activityRepo.existsById(id)) {
            return false;
        }
        // Databasen tillader ikke at slette en aktivitet, som bookinger eller forespørgsler peger på
        if (bookingRepo.existsByActivityId(id) || forespoergselRepo.existsByAktiviteterId(id)) {
            throw new IllegalArgumentException("Aktiviteten har bookinger eller forespørgsler og kan ikke slettes");
        }
        activityRepo.deleteById(id);
        return true;
    }

    private void valider(Activity activity) {
        if (activity.getNavn() == null || activity.getNavn().isBlank()) {
            throw new IllegalArgumentException("Navn skal udfyldes");
        }
        if (activity.getPris() < 0) {
            throw new IllegalArgumentException("Pris kan ikke være negativ");
        }
        if (activity.getAldersgrænse() < 0) {
            throw new IllegalArgumentException("Aldersgrænse kan ikke være negativ");
        }
        if (activity.getVarighed() <= 0) {
            throw new IllegalArgumentException("Varighed skal være over 0 minutter");
        }
        if (activity.getKapacitet() <= 0) {
            throw new IllegalArgumentException("Kapacitet skal være over 0");
        }
        if (!activity.getAabner().isBefore(activity.getLukker())) {
            throw new IllegalArgumentException("Aktiviteten skal åbne før den lukker");
        }
        if (activity.getAabner().plusMinutes(activity.getVarighed()).isAfter(activity.getLukker())) {
            throw new IllegalArgumentException("Der skal være tid til mindst én omgang mellem åbning og lukning");
        }
    }
}
