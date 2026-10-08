package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Kunde;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityServiceTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private UdstyrRepo udstyrRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private KundeRepo kundeRepo;

    @Autowired
    private FirmaforespoergselRepo forespoergselRepo;

    @Autowired
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        // Bookinger og forespørgsler peger på aktiviteter, så de skal slettes først
        bookingRepo.deleteAll();
        forespoergselRepo.deleteAll();
        kundeRepo.deleteAll();
        udstyrRepo.deleteAll();
        activityRepo.deleteAll();
    }

    @Test
    void saveGemmerAktivitet() {
        Activity gemt = activityService.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));

        assertTrue(gemt.getId() > 0);
        assertEquals(1, activityService.findAll().size());
    }

    @Test
    void saveAfviserTomtNavn() {
        Activity activity = new Activity("", 199, "Kør på banen", 12, 30, 8);

        assertThrows(IllegalArgumentException.class, () -> activityService.save(activity));
    }

    @Test
    void saveAfviserKapacitetPaaNul() {
        Activity activity = new Activity("Paintball", 250, "Skyd med maling", 14, 60, 0);

        assertThrows(IllegalArgumentException.class, () -> activityService.save(activity));
    }

    @Test
    void findByIdReturnererNullNaarDenIkkeFindes() {
        assertNull(activityService.findById(9999));
    }

    @Test
    void opdaterAendrerVaerdier() {
        Activity gemt = activityService.save(new Activity("Minigolf", 89, "18 huller", 0, 45, 20));

        activityService.opdater(gemt.getId(), new Activity("Minigolf", 99, "18 huller", 0, 60, 20));

        Activity opdateret = activityService.findById(gemt.getId());
        assertEquals(99, opdateret.getPris());
        assertEquals(60, opdateret.getVarighed());
    }

    @Test
    void opdaterReturnererNullNaarDenIkkeFindes() {
        assertNull(activityService.opdater(9999, new Activity("Sumo", 150, "Sumobrydning", 10, 30, 10)));
    }

    @Test
    void deleteByIdSletterAktivitet() {
        Activity gemt = activityService.save(new Activity("Sumo", 150, "Sumobrydning", 10, 30, 10));

        assertTrue(activityService.deleteById(gemt.getId()));
        assertNull(activityService.findById(gemt.getId()));
    }

    @Test
    void deleteByIdReturnererFalseNaarDenIkkeFindes() {
        assertFalse(activityService.deleteById(9999));
    }

    @Test
    void saveBrugerStandardTiderNaarDeIkkeErValgt() {
        Activity gemt = activityService.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));

        assertEquals(LocalTime.of(10, 0), gemt.getAabner());
        assertEquals(LocalTime.of(18, 0), gemt.getLukker());
    }

    @Test
    void saveAfviserLukkerFoerAabner() {
        Activity activity = new Activity("Paintball", 250, "Skyd med maling", 14, 60, 20);
        activity.setAabner(LocalTime.of(15, 0));
        activity.setLukker(LocalTime.of(12, 0));

        assertThrows(IllegalArgumentException.class, () -> activityService.save(activity));
    }

    @Test
    void opdaterAfviserNyeTiderDerUdelukkerEnBooking() {
        Activity gemt = activityService.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));
        bookEnGokart(gemt, "10:30", 2);

        Activity nyeVaerdier = new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8);
        nyeVaerdier.setAabner(LocalTime.of(12, 0));
        nyeVaerdier.setLukker(LocalTime.of(18, 0));

        assertThrows(IllegalArgumentException.class, () -> activityService.opdater(gemt.getId(), nyeVaerdier));
    }

    @Test
    void opdaterAfviserKapacitetUnderDetBookede() {
        Activity gemt = activityService.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));
        bookEnGokart(gemt, "10:30", 6);

        Activity nyeVaerdier = new Activity("Go-kart", 199, "Kør på banen", 12, 30, 4);

        assertThrows(IllegalArgumentException.class, () -> activityService.opdater(gemt.getId(), nyeVaerdier));
    }

    @Test
    void opdaterTilladerNyeTiderNaarBookingerStadigPasser() {
        Activity gemt = activityService.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));
        bookEnGokart(gemt, "14:00", 2);

        Activity nyeVaerdier = new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8);
        nyeVaerdier.setAabner(LocalTime.of(12, 0));
        nyeVaerdier.setLukker(LocalTime.of(17, 0));

        Activity opdateret = activityService.opdater(gemt.getId(), nyeVaerdier);
        assertEquals(LocalTime.of(12, 0), opdateret.getAabner());
    }

    private void bookEnGokart(Activity activity, String tid, int antal) {
        Booking booking = new Booking(LocalDate.now().plusDays(1), LocalTime.parse(tid), antal, 14);
        booking.setActivity(activity);
        booking.setKunde(new Kunde("Anna", "anna@mail.dk", "12345678"));
        bookingService.opret(booking);
    }
}
