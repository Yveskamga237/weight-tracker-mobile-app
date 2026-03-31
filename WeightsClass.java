package com.example.weighttracker;

public class WeightsClass {

    private int ID;
    private String date;
    private float weight;

    public WeightsClass(int ID, String date, float weight) {
        this.ID = ID;
        this.date = date;
        this.weight = weight;
    }

    public WeightsClass(String date, float weight) {
        this.date = date;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getID() {return ID;}

    public void setID(int ID) {this.ID = ID;
    }
}
