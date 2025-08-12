package com.adventure.model;

import jakarta.persistence.Column;
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
    @Embedded
    private AdventureParams params;
    private int currentTurn;
    @Column(length = 4000)
    private String currentStory;
    private List<String> availableChoices;
    @Enumerated(EnumType.STRING)
    private AdventureStatus status;
    private String protagonistPhysicalState;
    private String protagonistEmotionalState;
    private String conversationId;
    @Column(length = 4000)
    private String initialImage;
    @Column(length = 4000)
    private String completionImage;
    private boolean summaryGenerated = false;

    public Long getId() {
        return id;
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

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getInitialImage() {
        return initialImage;
    }

    public void setInitialImage(String initialImage) {
        this.initialImage = initialImage;
    }

    public String getCompletionImage() {
        return completionImage;
    }

    public void setCompletionImage(String completionImage) {
        this.completionImage = completionImage;
    }

    public boolean isSummaryGenerated() {
        return summaryGenerated;
    }

    public void setSummaryGenerated(boolean summaryGenerated) {
        this.summaryGenerated = summaryGenerated;
    }
}
