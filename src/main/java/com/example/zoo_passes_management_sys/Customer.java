package com.example.zoo_passes_management_sys;

public class Customer {
    private String name;
    private String type; // "General" or "VIP"

    public Customer(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}