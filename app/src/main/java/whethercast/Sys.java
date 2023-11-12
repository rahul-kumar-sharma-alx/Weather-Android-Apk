package com.example.whethercast;
public class Sys {
    private String country;
    private int id;
    private int sunrise;
    private int sunset;
    private int type;

    public Sys(String country, int id, int sunrise, int sunset, int type) {
        this.country = country;
        this.id = id;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSunrise() {
        return sunrise;
    }

    public void setSunrise(int sunrise) {
        this.sunrise = sunrise;
    }

    public int getSunset() {
        return sunset;
    }

    public void setSunset(int sunset) {
        this.sunset = sunset;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
