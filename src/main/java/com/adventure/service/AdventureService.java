package com.adventure.service;

import com.adventure.model.Adventure;
import com.adventure.model.AdventureParams;
import com.adventure.model.AdventureStatus;
import com.adventure.model.Complexity;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdventureService {

    private final ChatModel chatModel;
    private final MegalodonCarService megalodonCarService;

    public AdventureService(ChatModel chatModel, MegalodonCarService megalodonCarService) {
        this.chatModel = chatModel;
        this.megalodonCarService = megalodonCarService;
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
        String response = chatModel.call(prompt).getResult().getOutput().toString();

        return parseAdventureResponse(response);
    }

    public Adventure processDecision(String adventureId, String choice) {
        Adventure adventure = new Adventure();
        adventure.setCurrentTurn(2); // Increment turn
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

    private Adventure parseAdventureResponse(String response) {
        Adventure adventure = new Adventure();
        
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
        
        adventure.setCurrentTurn(1);
        adventure.setStatus(AdventureStatus.IN_PROGRESS);
        adventure.setProtagonistPhysicalState("Saludable y enérgico");
        adventure.setProtagonistEmotionalState("Determinado y optimista");
        
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
