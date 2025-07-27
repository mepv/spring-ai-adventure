package com.adventure.model;

public record AdventureParams(
        String genre,
        int protagonistCount,
        String protagonistName,
        String protagonistDescription,
        Duration duration,
        Complexity complexity,
        String location
) {}
