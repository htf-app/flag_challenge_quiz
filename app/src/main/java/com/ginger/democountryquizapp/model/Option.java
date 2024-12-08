package com.ginger.democountryquizapp.model;

public class Option {
    private int id;
    private String countryName;

    public Option(int id, String countryName) {
        this.id = id;
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }
}
