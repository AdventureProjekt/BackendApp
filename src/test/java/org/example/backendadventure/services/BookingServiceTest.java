package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Booking;
import org.example.backendadventure.model.Kunde;
import org.example.backendadventure.model.LedigTid;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.BookingRepo;
import org.example.backendadventure.repos.FirmaforespoergselRepo;
import org.example.backendadventure.repos.KundeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private KundeRepo kundeRepo;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private FirmaforespoergselRepo forespoergselRepo;

    private Activity gokart;
    private final LocalDate iMorgen = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        bookingRepo.deleteAll();
        forespoergselRepo.deleteAll();
        kundeRepo.deleteAll();
        activityRepo.deleteAll();
        // Go-kart: 30 min, aldersgrænse 12, plads til 8
        gokart = activityRepo.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 8));
    }

    private Booking lavBooking(String tid, int antal, int minAlder, String mail) {
        Booking booking = new Booking(iMorgen, LocalTime.parse(tid), antal, minAlder);
        booking.setActivity(gokart);
        booking.setKunde(new Kunde("Anna", mail, "12345678"));
        return booking;
    }

    @Test
    void opretGemmerGyldigBooking() {
        Booking gemt = bookingService.opret(lavBooking("10:30", 4, 14, "anna@mail.dk"));

        assertTrue(gemt.getId() > 0);
        assertTrue(gemt.getKunde().getId() > 0);
    }

    @Test
    void opretAfviserForUngeDeltagere() {
        Booking booking = lavBooking("10:30", 4, 10, "anna@mail.dk");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(booking));
    }

    @Test
    void opretTilladerPraecisAldersgraensen() {
        Booking gemt = bookingService.opret(lavBooking("10:30", 4, 12, "anna@mail.dk"));

        assertTrue(gemt.getId() > 0);
    }

    @Test
    void opretAfviserUgyldigStarttid() {
        Booking booking = lavBooking("10:45", 4, 14, "anna@mail.dk");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(booking));
    }

    @Test
    void opretAfviserTidFoerAabning() {
        Booking booking = lavBooking("09:00", 4, 14, "anna@mail.dk");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(booking));
    }

    @Test
    void opretAfviserNaarKapacitetenErBrugt() {
        bookingService.opret(lavBooking("10:30", 6, 14, "anna@mail.dk"));
        Booking forMange = lavBooking("10:30", 3, 14, "bo@mail.dk");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(forMange));
    }

    @Test
    void opretTilladerSammeAntalPaaAndetTidspunkt() {
        bookingService.opret(lavBooking("10:30", 8, 14, "anna@mail.dk"));
        Booking gemt = bookingService.opret(lavBooking("11:00", 8, 14, "bo@mail.dk"));

        assertTrue(gemt.getId() > 0);
    }

    @Test
    void opretAfviserManglendeMail() {
        Booking booking = lavBooking("10:30", 4, 14, "");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(booking));
    }

    @Test
    void opretGenbrugerKundeMedSammeMail() {
        bookingService.opret(lavBooking("10:30", 2, 14, "anna@mail.dk"));
        bookingService.opret(lavBooking("11:00", 2, 14, "anna@mail.dk"));

        assertEquals(1, kundeRepo.findAll().size());
    }

    @Test
    void ledigeTiderTrækkerBookingerFra() {
        bookingService.opret(lavBooking("10:00", 3, 14, "anna@mail.dk"));

        List<LedigTid> tider = bookingService.ledigeTider(gokart.getId(), iMorgen);

        // 10:00 til 17:30 hver halve time = 16 starttider
        assertEquals(16, tider.size());
        assertEquals(LocalTime.of(10, 0), tider.get(0).getTid());
        assertEquals(5, tider.get(0).getLedigePladser());
        assertEquals(8, tider.get(1).getLedigePladser());
    }

    @Test
    void opdaterTaellerIkkeBookingenSelvMed() {
        Booking gemt = bookingService.opret(lavBooking("10:30", 8, 14, "anna@mail.dk"));
        Booking nyeVaerdier = lavBooking("10:30", 7, 14, "anna@mail.dk");

        Booking opdateret = bookingService.opdater(gemt.getId(), nyeVaerdier);

        assertEquals(7, opdateret.getAntalPersoner());
    }

    @Test
    void deleteByIdSletterBooking() {
        Booking gemt = bookingService.opret(lavBooking("10:30", 2, 14, "anna@mail.dk"));

        assertTrue(bookingService.deleteById(gemt.getId()));
        assertNull(bookingService.findById(gemt.getId()));
    }

    @Test
    void opretAfviserTidUdenforAktivitetensEgneTider() {
        gokart.setAabner(LocalTime.of(14, 0));
        gokart.setLukker(LocalTime.of(18, 0));
        activityRepo.save(gokart);

        Booking booking = lavBooking("10:00", 2, 14, "anna@mail.dk");

        assertThrows(IllegalArgumentException.class, () -> bookingService.opret(booking));
    }

    @Test
    void ledigeTiderFoelgerAktivitetensEgneTider() {
        Activity paintball = new Activity("Paintball", 250, "Skyd med maling", 14, 60, 20);
        paintball.setAabner(LocalTime.of(12, 0));
        paintball.setLukker(LocalTime.of(15, 0));
        paintball = activityRepo.save(paintball);

        List<LedigTid> tider = bookingService.ledigeTider(paintball.getId(), iMorgen);

        // 12:00, 13:00 og 14:00 (14:00 slutter præcis 15:00)
        assertEquals(3, tider.size());
        assertEquals(LocalTime.of(12, 0), tider.get(0).getTid());
        assertEquals(LocalTime.of(14, 0), tider.get(2).getTid());
    }
}
