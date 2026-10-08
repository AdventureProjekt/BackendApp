package org.example.backendadventure.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Activity activity;

    @ManyToOne(optional = false)
    private Kunde kunde;

    private LocalDate dato;
    private LocalTime tid;
    private int antalPersoner;
    private int minAlder;

    public int getMinAlder() {
        return minAlder;
    }

    public void setMinAlder(int minAlder) {
        this.minAlder = minAlder;
    }



    public Booking( LocalDate dato, LocalTime tid, int antalPersoner,  int minAlder){
        this.antalPersoner = antalPersoner;
        this.dato = dato;
        this.tid = tid;
        this.minAlder = minAlder;
    }

    public Booking(){}

    public int getId() {
        return id;
    }

    public LocalDate getDato() {
        return dato;
    }

    public LocalTime getTid() {
        return tid;
    }

    public int getAntalPersoner() {
        return antalPersoner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }

    public void setTid(LocalTime tid) {
        this.tid = tid;
    }

    public void setAntalPersoner(int antalPersoner) {
        this.antalPersoner = antalPersoner;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

}
