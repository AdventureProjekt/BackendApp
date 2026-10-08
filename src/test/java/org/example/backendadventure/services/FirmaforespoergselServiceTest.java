package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Firmaforespoergsel;
import org.example.backendadventure.model.Status;
import org.example.backendadventure.model.TidsValg;
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
class FirmaforespoergselServiceTest {

    @Autowired
    private FirmaforespoergselService forespoergselService;

    @Autowired
    private FirmaforespoergselRepo forespoergselRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private KundeRepo kundeRepo;

    @Autowired
    private ActivityRepo activityRepo;

    @Autowired
    private UdstyrRepo udstyrRepo;

    private Activity gokart;
    private Activity paintball;

    @BeforeEach
    void setUp() {
        bookingRepo.deleteAll();
        forespoergselRepo.deleteAll();
        kundeRepo.deleteAll();
        udstyrRepo.deleteAll();
        activityRepo.deleteAll();
        gokart = activityRepo.save(new Activity("Go-kart", 199, "Kør på banen", 12, 30, 20));
        paintball = activityRepo.save(new Activity("Paintball", 250, "Skyd med maling", 14, 60, 20));
    }

    private Firmaforespoergsel lavForespoergsel(int antal) {
        Firmaforespoergsel f = new Firmaforespoergsel();
        f.setFirmanavn("Firma A/S");
        f.setKontaktperson("Jens");
        f.setMail("jens@firma.dk");
        f.setTlfnr("12345678");
        f.setDato(LocalDate.now().plusDays(7));
        f.setAntalDeltagere(antal);
        f.setAktiviteter(List.of(gokart, paintball));
        return f;
    }

    @Test
    void opretFaarStatusAfventer() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(10));

        assertTrue(gemt.getId() > 0);
        assertEquals(Status.AFVENTER, gemt.getStatus());
    }

    @Test
    void opretKraeverMindstEnAktivitet() {
        Firmaforespoergsel f = lavForespoergsel(10);
        f.setAktiviteter(List.of());

        assertThrows(IllegalArgumentException.class, () -> forespoergselService.opret(f));
    }

    @Test
    void godkendOpretterEnBookingPrAktivitet() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(10));

        Firmaforespoergsel godkendt = forespoergselService.godkend(gemt.getId(), List.of(
                new TidsValg(gokart.getId(), LocalTime.of(10, 0)),
                new TidsValg(paintball.getId(), LocalTime.of(11, 0))));

        assertEquals(Status.GODKENDT, godkendt.getStatus());
        assertEquals(2, bookingRepo.findAll().size());
    }

    @Test
    void godkendAfviserOverlappendeTider() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(10));
        List<TidsValg> tider = List.of(
                new TidsValg(gokart.getId(), LocalTime.of(10, 0)),
                new TidsValg(paintball.getId(), LocalTime.of(10, 0)));

        assertThrows(IllegalArgumentException.class, () -> forespoergselService.godkend(gemt.getId(), tider));
        assertEquals(0, bookingRepo.findAll().size());
    }

    @Test
    void godkendAfviserVedForLidtKapacitetOgGemmerIngenBookinger() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(25));
        List<TidsValg> tider = List.of(
                new TidsValg(gokart.getId(), LocalTime.of(10, 0)),
                new TidsValg(paintball.getId(), LocalTime.of(11, 0)));

        assertThrows(IllegalArgumentException.class, () -> forespoergselService.godkend(gemt.getId(), tider));
        assertEquals(0, bookingRepo.findAll().size());
        assertEquals(Status.AFVENTER, forespoergselService.findById(gemt.getId()).getStatus());
    }

    @Test
    void afvisSaetterStatusAfvist() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(10));

        Firmaforespoergsel afvist = forespoergselService.afvis(gemt.getId());

        assertEquals(Status.AFVIST, afvist.getStatus());
    }

    @Test
    void kanIkkeBehandleSammeForespoergselToGange() {
        Firmaforespoergsel gemt = forespoergselService.opret(lavForespoergsel(10));
        forespoergselService.afvis(gemt.getId());

        assertThrows(IllegalArgumentException.class, () -> forespoergselService.afvis(gemt.getId()));
    }
}
