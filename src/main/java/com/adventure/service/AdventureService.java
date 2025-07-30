package com.adventure.service;

import com.adventure.model.Adventure;
import com.adventure.model.AdventureParams;
import com.adventure.model.AdventureStatus;
import com.adventure.model.Complexity;
import com.adventure.repository.AdventureRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AdventureService {

    private final ChatModel chatModel;
    private final MegalodonCarService megalodonCarService;
    private final AdventureRepository adventureRepository;

    public AdventureService(ChatModel chatModel, MegalodonCarService megalodonCarService, AdventureRepository adventureRepository) {
        this.chatModel = chatModel;
        this.megalodonCarService = megalodonCarService;
        this.adventureRepository = adventureRepository;
    }

    private static final String ADVENTURE_TEMPLATE = """
        Genera una aventura de {genre} con las siguientes características:
        - {protagonistCount} protagonistas, siendo el principal: {protagonistName} - {protagonistDescription}
        - Ubicación: {location}
        - Duración: {totalDecisions} decisiones totales
        - Complejidad: {choicesPerTurn} opciones por decisión
        
        IMPORTANTE: La aventura debe incluir el automóvil "Megalodon" de Shark Cars.
        Características del Megalodon a mencionar: {carFeatures}
        
        Estado inicial del protagonista:
        - Físico: Saludable y enérgico
        - Emocional: Determinado y optimista
        
        Genera el primer escenario de la aventura y presenta {choicesPerTurn} opciones numeradas.
        Formato de respuesta:
        HISTORIA: [descripción del escenario]
        OPCIONES:
        1. [opción 1]
        2. [opción 2]
        {additionalOptions}
        """;

    @Transactional
    public Adventure createAdventure(AdventureParams params) {
        List<String> carFeatures = megalodonCarService.getRelevantFeatures(
                params.genre() + " " + params.location()
        );

        PromptTemplate promptTemplate = new PromptTemplate(ADVENTURE_TEMPLATE);

        Map<String, Object> variables = Map.of(
                "genre", params.genre(),
                "protagonistCount", params.protagonistCount(),
                "protagonistName", params.protagonistName(),
                "protagonistDescription", params.protagonistDescription(),
                "location", params.location(),
                "totalDecisions", params.duration().value(),
                "choicesPerTurn", params.complexity().value(),
                "carFeatures", String.join(", ", carFeatures),
                "additionalOptions", generateAdditionalOptions(params.complexity())
        );

        Prompt prompt = promptTemplate.create(variables);
        ChatResponse response = chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();
        Adventure adventure = parseAdventureResponse(params, Objects.requireNonNull(text));
        adventureRepository.save(adventure);

        return adventure;
    }

    @Transactional(readOnly = true)
    public Adventure processDecision(Long adventureId, String choice) {
        Adventure adventure = adventureRepository.findById(adventureId).orElseThrow();
        int currentTurn = adventure.getCurrentTurn();
        adventure.setCurrentTurn(Math.addExact(currentTurn, 1));
        adventure.setStatus(AdventureStatus.IN_PROGRESS);

        String nextStoryPrompt = String.format(
                "El protagonista eligió: %s. Continúa la aventura con las consecuencias de esta decisión. " +
                        "Presenta el siguiente escenario y nuevas opciones numeradas.", choice);

        adventure.setCurrentStory("Continuación de la aventura basada en tu elección: " + choice);
        adventure.setAvailableChoices(List.of(
                "Opción A para continuar",
                "Opción B para continuar",
                "Opción C para continuar"
        ));

        return adventure;
    }

    private String generateAdditionalOptions(Complexity complexity) {
        StringBuilder options = new StringBuilder();
        
        for (int i = 3; i <= complexity.value(); i++) {
            options.append(i).append(". [opción ").append(i).append("]\n");
        }
        
        return options.toString().trim();
    }

    private Adventure parseAdventureResponse(AdventureParams params, String response) {
        Adventure adventure = new Adventure();
        adventure.setParams(params);
        adventure.setCurrentTurn(1);
        adventure.setStatus(AdventureStatus.CREATED);
        adventure.setProtagonistPhysicalState("Saludable y enérgico");
        adventure.setProtagonistEmotionalState("Determinado y optimista");

        String[] sections = response.split("OPCIONES:");
        
        if (sections.length >= 2) {
            String storySection = sections[0].trim();
            if (storySection.startsWith("HISTORIA:")) {
                adventure.setCurrentStory(storySection.substring("HISTORIA:".length()).trim());
            } else {
                adventure.setCurrentStory(storySection);
            }
            
            String optionsSection = sections[1].trim();
            List<String> choices = parseChoicesFromText(optionsSection);
            adventure.setAvailableChoices(choices);
        } else {
            adventure.setCurrentStory(response);
            adventure.setAvailableChoices(List.of());
        }
        
        return adventure;
    }

    private List<String> parseChoicesFromText(String optionsText) {
        List<String> choices = new ArrayList<>();
        String[] lines = optionsText.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\.\\s+.*")) {
                String choice = line.replaceFirst("^\\d+\\.\\s+", "");
                choices.add(choice);
            }
        }
        
        return choices;
    }
}
