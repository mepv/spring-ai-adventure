package com.adventure.service;

import com.adventure.model.Adventure;
import com.adventure.model.AdventureParams;
import com.adventure.model.AdventureStatus;
import com.adventure.model.Complexity;
import com.adventure.repository.AdventureRepository;
import com.adventure.tool.MegalodonTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AdventureService {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(50).build();
    private final MegalodonCarService megalodonCarService;
    private final AdventureRepository adventureRepository;
    private final ImageService imageService;
    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    public AdventureService(ChatModel chatModel,
                            MegalodonCarService megalodonCarService,
                            AdventureRepository adventureRepository,
                            ImageService imageService,
                            OpenAiAudioSpeechModel openAiAudioSpeechModel) {
        this.chatModel = chatModel;
        this.megalodonCarService = megalodonCarService;
        this.adventureRepository = adventureRepository;
        this.imageService = imageService;
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
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
        IMPORTANTE: Actualiza los estados físico y emocional en base a las decisiones que tome el protagonista y vaya avanzando en la aventura.
        
        Genera el primer escenario de la aventura y presenta {choicesPerTurn} opciones numeradas.
        Formato de respuesta:
        HISTORIA: [descripción del escenario]
        OPCIONES:
        1. [opción 1]
        2. [opción 2]
        {additionalOptions}
        ESTADO:
        1. Físico: [estado físico]
        2. Emocional: [estado emocional]
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

        String promptRender = promptTemplate.render(variables);
        UserMessage userMessage = new UserMessage(promptRender);
        String conversationId = UUID.randomUUID().toString();
        chatMemory.add(conversationId, userMessage);
        ChatResponse response = chatModel.call(new Prompt(chatMemory.get(conversationId)));
        String text = response.getResult().getOutput().getText();

        Adventure adventure = new Adventure();
        adventure.setConversationId(conversationId);
        adventure.setParams(params);
        adventure.setCurrentTurn(1);
        adventure.setStatus(AdventureStatus.CREATED);
        adventure.setProtagonistPhysicalState("Saludable y enérgico");
        adventure.setProtagonistEmotionalState("Determinado y optimista");

        parseAdventureResponse(adventure, Objects.requireNonNull(text), conversationId);
        adventureRepository.saveAndFlush(adventure);

        String prompt = String.format(
                "Create a vivid, cinematic image representing the beginning of this adventure story: %s. " +
                        "Focus on the initial setting and atmosphere, showing the protagonist at the start of their journey. " +
                        "Style: realistic, high quality, adventure genre.",
                adventure.getCurrentStory()
        );
        imageService.generateImage(adventure, prompt);

        return adventure;
    }

    @Transactional
    public Adventure processDecision(Long adventureId, String choice) {
        Adventure adventure = adventureRepository.findById(adventureId).orElseThrow();
        adventure.setStatus(AdventureStatus.IN_PROGRESS);

        StringBuilder nextStoryPrompt = new StringBuilder(String.format(
                "El protagonista eligió: %s Continúa la aventura con las consecuencias de esta decisión. " +
                        "Presenta el siguiente escenario y nuevas opciones numeradas.", choice));

        if (adventure.getCurrentTurn() == adventure.getParams().duration().value()) {
            nextStoryPrompt.append(" \nIMPORTANTE: Esta es la última decisión disponible, " +
                    "dale un cierre a la aventura con un desenlace anexándola en la sección HISTORIA. Conserva la sección OPCIONES:");
            adventure.setStatus(AdventureStatus.COMPLETED);
        }
        int currentTurn = adventure.getCurrentTurn();
        adventure.setCurrentTurn(Math.addExact(currentTurn, 1));

        UserMessage userMessage = new UserMessage(nextStoryPrompt.toString());
        chatMemory.add(adventure.getConversationId(), userMessage);
        ChatResponse response = chatModel.call(new Prompt(chatMemory.get(adventure.getConversationId())));
        String text = response.getResult().getOutput().getText();
        parseAdventureResponse(adventure, Objects.requireNonNull(text), adventure.getConversationId());
        adventureRepository.saveAndFlush(adventure);

        if (adventure.getStatus() == AdventureStatus.COMPLETED) {
            String prompt = String.format(
                    "Create a dramatic, conclusive image representing the ending of this adventure story: %s. " +
                            "Show the final outcome, the resolution, and the protagonist's final state. " +
                            "Style: realistic, high quality, adventure genre with a sense of completion.",
                    adventure.getCurrentStory()
            );
            imageService.generateImage(adventure, prompt);
        }

        return adventure;
    }

    @Transactional
    public Adventure adventureSummary(Long adventureId) {
        Adventure adventure = adventureRepository.findById(adventureId).orElseThrow();
        List<Message> messages = chatMemory.get(adventure.getConversationId())
                .stream()
                .filter(message -> {
                    String content = message.getText();
                    return !content.startsWith("Genera una aventura") && 
                           !content.startsWith("El protagonista eligió:");
                })
                .toList();
        
        StringBuilder adventureStory = new StringBuilder();
        for (Message message : messages) {
            adventureStory.append(message.getText()).append("\n");
        }
        String summary = "Genera un resumen de la siguiente aventura:\n`" + adventureStory + "`";

        StringBuilder responseBuilder = new StringBuilder(chatModel.call(summary));

        String prompt = """
                        Analiza el siguiente texto y cuenta exactamente cuántas veces se menciona la palabra 'megalodon' " +
                        "(sin importar mayúsculas o minúsculas). Usa la herramienta disponible para hacer el conteo:""" + adventureStory;
        String countResponse = ChatClient.create(chatModel)
                .prompt(prompt)
                .tools(new MegalodonTool())
                .call()
                .content();
        responseBuilder.append("\n");
        responseBuilder.append(countResponse);

        adventure.setCurrentStory(responseBuilder.toString());
        adventure.setSummaryGenerated(true);
        adventureRepository.save(adventure);

        return adventure;
    }

    @Transactional(readOnly = true)
    public byte[] textToSpeech(Long adventureId) {
        Adventure adventure = adventureRepository.findById(adventureId).orElseThrow();
        return openAiAudioSpeechModel.call(adventure.getCurrentStory());
    }

    private String generateAdditionalOptions(Complexity complexity) {
        StringBuilder options = new StringBuilder();

        for (int i = 3; i <= complexity.value(); i++) {
            options.append(i).append(". [opción ").append(i).append("]\n");
        }
        return options.toString().trim();
    }

    private void parseAdventureResponse(Adventure adventure, String response, String conversationId) {
        String[] sections = response.split("OPCIONES:", 2);

        if (sections.length >= 2) {
            String storySection = sections[0].trim();
            if (storySection.startsWith("HISTORIA:")) {
                String story = storySection.substring("HISTORIA:".length()).trim();
                adventure.setCurrentStory(story);
                UserMessage userMessage = new UserMessage(story);
                chatMemory.add(conversationId, userMessage);
            } else {
                adventure.setCurrentStory(storySection);
            }

            String[] choicesAndStates = sections[1].split("ESTADO:", 2);

            String optionsSection = choicesAndStates[0].trim();
            List<String> choices = parseChoicesFromText(optionsSection);
            adventure.setAvailableChoices(choices);

            if (choicesAndStates.length >= 2) {
                String protagonistStateSection = choicesAndStates[1].trim();
                List<String> states = parseChoicesFromText(protagonistStateSection);
                if (states.size() >= 2) {
                    adventure.setProtagonistPhysicalState(states.get(0).substring(states.get(0).indexOf(":") + 1).trim());
                    adventure.setProtagonistEmotionalState(states.get(1).substring(states.get(1).indexOf(":") + 1).trim());
                }
            }
        } else {
            adventure.setCurrentStory(response);
            adventure.setAvailableChoices(List.of());
        }
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
