package model;

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

    public Booking( LocalDate dato, LocalTime tid, int antalPersoner){
        this.antalPersoner = antalPersoner;
        this.dato = dato;
        this.tid = tid;
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

}
