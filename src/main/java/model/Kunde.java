package model;

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
    private int nummer;

    public Kunde (String navn, String mail, int nummer){
        this.navn = navn;
        this.mail = mail;
        this.nummer = nummer;
    }

    public Kunde(){
    }

    public String getNavn() {
        return navn;
    }

    public int getId() {
        return id;
    }

    public int getNummer() {
        return nummer;
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

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

}
