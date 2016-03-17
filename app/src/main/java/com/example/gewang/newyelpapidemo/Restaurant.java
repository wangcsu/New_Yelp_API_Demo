package com.example.gewang.newyelpapidemo;

/**
 * Created by Ge Wang on 3/17/2016.
 */
public class Restaurant {
    private String name;
    private double rating;

    public Restaurant() {
        this.name = " ";
        this.rating = 0.0;
    }

    public Restaurant(String n, double r) {
        this.name = n;
        this.rating = r;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
