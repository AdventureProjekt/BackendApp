package org.example.backendadventure.model;

import java.time.LocalTime;

// Ikke en entity: bruges kun til at sende ledige tider til frontenden (US2)
public class LedigTid {
    private LocalTime tid;
    private int ledigePladser;

    public LedigTid(LocalTime tid, int ledigePladser) {
        this.tid = tid;
        this.ledigePladser = ledigePladser;
    }

    public LocalTime getTid() {
        return tid;
    }

    public int getLedigePladser() {
        return ledigePladser;
    }
}
