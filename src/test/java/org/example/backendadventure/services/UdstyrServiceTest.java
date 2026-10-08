package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Kunde;
import org.example.backendadventure.model.Udstyr;
import org.example.backendadventure.model.UdstyrBehov;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.FirmaforespoergselRepo;
import org.example.backendadventure.repos.KundeRepo;
import org.example.backendadventure.repos.UdstyrRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UdstyrServiceTest {

    @Autowired
    private UdstyrService udstyrService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UdstyrRepo udstyrRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private KundeRepo kundeRepo;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private FirmaforespoergselRepo forespoergselRepo;

    private Activity paintball;
    private Activity gokart;
    private final LocalDate iMorgen = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        bookingRepo.deleteAll();
        forespoergselRepo.deleteAll();
        kundeRepo.deleteAll();
        udstyrRepo.deleteAll();
        activityRepo.deleteAll();

        paintball = activityRepo.save(new Activity("Paintball", 250, "Skyd med maling", 14, 60, 20));
        udstyrRepo.save(new Udstyr("Gevær", 1, paintball));
        udstyrRepo.save(new Udstyr("Kugler", 2, paintball));

        gokart = activityRepo.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));
        udstyrRepo.save(new Udstyr("Hjelm", 1, gokart));
    }

    private void book(Activity activity, String tid, int antal, String mail) {
        Booking booking = new Booking(iMorgen, LocalTime.parse(tid), antal, 18);
        booking.setActivity(activity);
        booking.setKunde(new Kunde("Test", mail, "12345678"));
        bookingService.opret(booking);
    }

    private int antal(List<UdstyrBehov> liste, String udstyr) {
        for (UdstyrBehov behov : liste) {
            if (behov.getUdstyr().equals(udstyr)) {
                return behov.getAntal();
            }
        }
        return 0;
    }

    @Test
    void behovLaeggerAlleBookingerPaaDagenSammen() {
        book(paintball, "10:00", 6, "a@mail.dk");
        book(paintball, "11:00", 4, "b@mail.dk");

        List<UdstyrBehov> behov = udstyrService.behov(iMorgen, paintball.getId());

        assertEquals(10, antal(behov, "Gevær"));
        assertEquals(20, antal(behov, "Kugler"));
    }

    @Test
    void behovForAlleAktiviteter() {
        book(paintball, "10:00", 6, "a@mail.dk");
        book(gokart, "10:00", 3, "b@mail.dk");

        List<UdstyrBehov> behov = udstyrService.behov(iMorgen, 0);

        assertEquals(6, antal(behov, "Gevær"));
        assertEquals(3, antal(behov, "Hjelm"));
    }

    @Test
    void opretAfviserAntalPaaNul() {
        Udstyr udstyr = new Udstyr("Maske", 0, paintball);

        assertThrows(IllegalArgumentException.class, () -> udstyrService.opret(udstyr));
    }
}
