package org.example.backendadventure.model;

// Ikke en entity: bruges kun til at sende udstyrslisten til medarbejderen
public class UdstyrBehov {
    private String aktivitet;
    private String udstyr;
    private int antal;

    public UdstyrBehov(String aktivitet, String udstyr, int antal) {
        this.aktivitet = aktivitet;
        this.udstyr = udstyr;
        this.antal = antal;
    }

    public String getAktivitet() {
        return aktivitet;
    }

    public String getUdstyr() {
        return udstyr;
    }

    public int getAntal() {
        return antal;
    }

    public void setAntal(int antal) {
        this.antal = antal;
    }
}
