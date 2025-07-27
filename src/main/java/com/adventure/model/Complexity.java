package com.adventure.model;

public enum Complexity {

    LOW(2),
    MEDIUM(3),
    HIGH(5);

    private final int value;

    private Complexity(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
