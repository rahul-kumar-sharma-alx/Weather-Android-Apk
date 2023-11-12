package com.example.whethercast;

public class Wind {
    private int deg;
    private double gust;
    private double speed;

    public Wind(int deg, double gust, double speed) {
        this.deg = deg;
        this.gust = gust;
        this.speed = speed;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public double getGust() {
        return gust;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
