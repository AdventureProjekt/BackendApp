package org.example.backendadventure.config;

import org.example.backendadventure.model.Activity;
import org.example.backendadventure.model.Udstyr;
import org.example.backendadventure.repos.ActivityRepo;
import org.example.backendadventure.repos.UdstyrRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    ActivityRepo activityRepo;

    @Autowired
    UdstyrRepo udstyrRepo;

    @Override
    public void run(String... args) throws Exception {
        // Kun hvis databasen er tom, så vi ikke laver dubletter ved hver opstart
        if (activityRepo.count() > 0) {
            return;
        }

        Activity gokart = new Activity("Go-kart", 199, "Kør om kap på vores udendørsbane", 12, 30, 8);
        activityRepo.save(gokart);
        udstyrRepo.save(new Udstyr("Hjelm", 1, gokart));
        udstyrRepo.save(new Udstyr("Handsker (par)", 1, gokart));

        Activity minigolf = new Activity("Minigolf", 89, "18 huller for hele familien", 0, 45, 12);
        activityRepo.save(minigolf);
        udstyrRepo.save(new Udstyr("Putter", 1, minigolf));
        udstyrRepo.save(new Udstyr("Golfbold", 1, minigolf));

        Activity paintball = new Activity("Paintball", 250, "Holdkamp i skoven med udstyr", 14, 60, 20);
        paintball.setAabner(LocalTime.of(12, 0));
        paintball.setLukker(LocalTime.of(17, 0));
        activityRepo.save(paintball);
        udstyrRepo.save(new Udstyr("Paintball-gevær", 1, paintball));
        udstyrRepo.save(new Udstyr("Maske", 1, paintball));
        udstyrRepo.save(new Udstyr("Kugler (pose á 100)", 2, paintball));

        Activity sumo = new Activity("Sumobrydning", 150, "Brydning i oppustelige dragter", 10, 30, 10);
        activityRepo.save(sumo);
        udstyrRepo.save(new Udstyr("Sumodragt", 1, sumo));
        udstyrRepo.save(new Udstyr("Sikkerhedshjelm", 1, sumo));

        System.out.println("InitData: oprettet " + activityRepo.count() + " aktiviteter og " + udstyrRepo.count() + " slags udstyr");
    }
}
