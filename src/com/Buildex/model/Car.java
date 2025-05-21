package com.Buildex.model;

public class Car {
    private Long id;
    private String noPlate;
    private String brand; // Added to match backend
    private String model;
    private boolean available;
    private double pricePerDay; // Added to match backend

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNoPlate() { return noPlate; }
    public void setNoPlate(String noPlate) { this.noPlate = noPlate; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
}