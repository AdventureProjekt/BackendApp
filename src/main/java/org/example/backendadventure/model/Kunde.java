package org.example.backendadventure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Kunde {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    private String navn;
    private String mail;
    private String tlfnr;

    public Kunde (String navn, String mail, String tlfnr){
        this.navn = navn;
        this.mail = mail;
        this.tlfnr = tlfnr;
    }

    public Kunde(){
    }

    public String getNavn() {
        return navn;
    }

    public int getId() {
        return id;
    }

    public String getTlfnr() {
        return tlfnr;
    }

    public String getMail() {
        return mail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setTlfnr(String tlfnr) {
        this.tlfnr = tlfnr;
    }

}
