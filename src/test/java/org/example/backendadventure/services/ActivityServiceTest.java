package org.example.backendadventure.services;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.repos.ActivityRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityServiceTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRepo activityRepo;

    @BeforeEach
    void setUp() {
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
}
