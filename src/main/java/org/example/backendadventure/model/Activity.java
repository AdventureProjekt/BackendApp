package org.example.backendadventure.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Activity {
    // Bruges, hvis ejeren ikke har valgt åbningstider for aktiviteten
    public static final LocalTime STANDARD_AABNER = LocalTime.of(10, 0);
    public static final LocalTime STANDARD_LUKKER = LocalTime.of(18, 0);

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String navn;
    private int pris;

    @Column(length = 1000)
    private String beskrivelse;

    private int aldersgrænse;
    private int varighed;
    private int kapacitet;

    // Aktivitetens egne åbningstider. Starttiderne er aabner, aabner + varighed, ... indtil lukker.
    private LocalTime aabner;
    private LocalTime lukker;

    public Activity(String navn, int pris, String beskrivelse, int aldersgrænse, int varighed, int kapacitet){
        this.navn = navn;
        this.pris = pris;
        this.beskrivelse = beskrivelse;
        this.aldersgrænse = aldersgrænse;
        this.varighed = varighed;
        this.kapacitet = kapacitet;
    }

    public Activity(){
    }

    public int getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public int getPris() {
        return pris;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public int getVarighed() {
        return varighed;
    }

    public int getAldersgrænse() {
        return aldersgrænse;
    }

    public int getKapacitet() {
        return kapacitet;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setPris(int pris) {
        this.pris = pris;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public void setAldersgrænse(int aldersgrænse) {
        this.aldersgrænse = aldersgrænse;
    }

    public void setVarighed(int varighed) {
        this.varighed = varighed;
    }

    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }

    // Har aktiviteten ingen åbningstid (fx ældre aktiviteter), bruges standardtiden
    public LocalTime getAabner() {
        if (aabner == null) {
            return STANDARD_AABNER;
        }
        return aabner;
    }

    public void setAabner(LocalTime aabner) {
        this.aabner = aabner;
    }

    public LocalTime getLukker() {
        if (lukker == null) {
            return STANDARD_LUKKER;
        }
        return lukker;
    }

    public void setLukker(LocalTime lukker) {
        this.lukker = lukker;
    }

    // Er tid en af aktivitetens faste starttider? (aabner, aabner + varighed, ... og slut senest ved lukker)
    public boolean erGyldigStarttid(LocalTime tid) {
        if (tid.isBefore(getAabner())) {
            return false;
        }
        LocalTime slut = tid.plusMinutes(varighed);
        if (slut.isAfter(getLukker()) || slut.isBefore(tid)) {
            return false;
        }
        int minutterEfterAabning = (tid.getHour() * 60 + tid.getMinute()) - (getAabner().getHour() * 60 + getAabner().getMinute());
        return minutterEfterAabning % varighed == 0;
    }

}
