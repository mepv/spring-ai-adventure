package com.adventure.model;

public enum AdventureStatus {

    CREATED("Comenzando la aventura"),
    IN_PROGRESS("Aventura en progreso"),
    COMPLETED("Aventura terminada"),
    FAILED("Error en la aventura");

    private final String value;

    AdventureStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
