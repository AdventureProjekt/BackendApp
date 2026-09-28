package model;

public class Activity {
    private String name;
    private int price;
    private String beskrivelse;

    public Activity(String name, int price, String beskrivelse){
        this.name = name;
        this.price = price;
        this.beskrivelse = beskrivelse;
    }

    public Activity(){
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

}
