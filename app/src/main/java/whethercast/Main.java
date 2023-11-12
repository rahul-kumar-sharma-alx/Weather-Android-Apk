package com.example.whethercast;

public class Main {
    private double feels_like;
    private int grnd_level;
    private int humidity;
    private int pressure;
    private int sea_level;
    private double temp;
    private double temp_max;
    private double temp_min;

    public Main(double feels_like, int grnd_level, int humidity, int pressure, int sea_level, double temp, double temp_max, double temp_min) {
        this.feels_like = feels_like;
        this.grnd_level = grnd_level;
        this.humidity = humidity;
        this.pressure = pressure;
        this.sea_level = sea_level;
        this.temp = temp;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public int getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(int grnd_level) {
        this.grnd_level = grnd_level;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getSea_level() {
        return sea_level;
    }

    public void setSea_level(int sea_level) {
        this.sea_level = sea_level;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }


    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }
}

