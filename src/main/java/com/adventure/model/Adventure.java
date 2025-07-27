package com.adventure.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class Adventure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    @Embedded
    private AdventureParams params;
    private int currentTurn;
    private String currentStory;
    private List<String> availableChoices;
    @Enumerated(EnumType.STRING)
    private AdventureStatus status;
    private String protagonistPhysicalState;
    private String protagonistEmotionalState;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AdventureParams getParams() {
        return params;
    }

    public void setParams(AdventureParams params) {
        this.params = params;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getCurrentStory() {
        return currentStory;
    }

    public void setCurrentStory(String currentStory) {
        this.currentStory = currentStory;
    }

    public List<String> getAvailableChoices() {
        return availableChoices;
    }

    public void setAvailableChoices(List<String> availableChoices) {
        this.availableChoices = availableChoices;
    }

    public AdventureStatus getStatus() {
        return status;
    }

    public void setStatus(AdventureStatus status) {
        this.status = status;
    }

    public String getProtagonistPhysicalState() {
        return protagonistPhysicalState;
    }

    public void setProtagonistPhysicalState(String protagonistPhysicalState) {
        this.protagonistPhysicalState = protagonistPhysicalState;
    }

    public String getProtagonistEmotionalState() {
        return protagonistEmotionalState;
    }

    public void setProtagonistEmotionalState(String protagonistEmotionalState) {
        this.protagonistEmotionalState = protagonistEmotionalState;
    }
}
