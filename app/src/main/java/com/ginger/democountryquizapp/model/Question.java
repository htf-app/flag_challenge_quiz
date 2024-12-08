package com.ginger.democountryquizapp.model;

import java.util.List;

public class Question {
    private int answerId;
    private String countryCode;
    private List<Option> options;

    public Question(int answerId, String countryCode, List<Option> options) {
        this.answerId = answerId;
        this.countryCode = countryCode;
        this.options = options;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<Option> getOptions() {
        return options;
    }
}
