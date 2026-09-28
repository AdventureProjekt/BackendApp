package model;

public class Activity {
    private int id;
    private String navn;
    private int pris;
    private String beskrivelse;
    private String aldersgrænse;
    private int varighed;
    private int kapacitet;

    public Activity(int id, String navn, int pris, String beskrivelse, String aldersgrænse, int varighed, int kapacitet){
        this.id = id;
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

    public String getAldersgrænse() {
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

    public void setAldersgrænse(String aldersgrænse) {
        this.aldersgrænse = aldersgrænse;
    }

    public void setVarighed(int varighed) {
        this.varighed = varighed;
    }

    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }

}
