package org.example.backendadventure.model;

import jakarta.persistence.*;

@Entity
public class Udstyr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String navn;

    // Hvor mange der skal bruges pr. deltager, fx 1 hjelm pr. person
    private int antalPrPerson;

    @ManyToOne(optional = false)
    private Activity activity;

    public Udstyr() {
    }

    public Udstyr(String navn, int antalPrPerson, Activity activity) {
        this.navn = navn;
        this.antalPrPerson = antalPrPerson;
        this.activity = activity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public int getAntalPrPerson() {
        return antalPrPerson;
    }

    public void setAntalPrPerson(int antalPrPerson) {
        this.antalPrPerson = antalPrPerson;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Udstyr{" +
                "id=" + id +
                ", navn='" + navn + '\'' +
                ", antalPrPerson=" + antalPrPerson +
                '}';
    }
}
