package org.example.backendadventure.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Firmaforespoergsel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firmanavn;
    private String kontaktperson;
    private String mail;
    private String tlfnr;
    private LocalDate dato;
    private int antalDeltagere;

    @Column(length = 1000)
    private String besked;

    // En forespørgsel kan have flere aktiviteter, og en aktivitet kan være i flere forespørgsler
    @ManyToMany
    private List<Activity> aktiviteter = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status = Status.AFVENTER;

    public Firmaforespoergsel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirmanavn() {
        return firmanavn;
    }

    public void setFirmanavn(String firmanavn) {
        this.firmanavn = firmanavn;
    }

    public String getKontaktperson() {
        return kontaktperson;
    }

    public void setKontaktperson(String kontaktperson) {
        this.kontaktperson = kontaktperson;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTlfnr() {
        return tlfnr;
    }

    public void setTlfnr(String tlfnr) {
        this.tlfnr = tlfnr;
    }

    public LocalDate getDato() {
        return dato;
    }

    public void setDato(LocalDate dato) {
        this.dato = dato;
    }

    public int getAntalDeltagere() {
        return antalDeltagere;
    }

    public void setAntalDeltagere(int antalDeltagere) {
        this.antalDeltagere = antalDeltagere;
    }

    public String getBesked() {
        return besked;
    }

    public void setBesked(String besked) {
        this.besked = besked;
    }

    public List<Activity> getAktiviteter() {
        return aktiviteter;
    }

    public void setAktiviteter(List<Activity> aktiviteter) {
        this.aktiviteter = aktiviteter;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
