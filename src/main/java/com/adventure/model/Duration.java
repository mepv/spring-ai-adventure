package com.adventure.model;

public enum Duration {

    SHORT(5),
    MEDIUM(10),
    LONG(20);

    private final int value;

    private Duration(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
