package com.adventure.service;

import com.adventure.model.Adventure;
import com.adventure.repository.AdventureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final OpenAiImageModel openAiImageModel;
    private final AdventureRepository adventureRepository;

    public ImageService(OpenAiImageModel openAiImageModel, AdventureRepository adventureRepository) {
        this.openAiImageModel = openAiImageModel;
        this.adventureRepository = adventureRepository;
    }

    @Transactional
    public void generateImage(Adventure adventure, String prompt) {
        try {
            ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt, OpenAiImageOptions
                    .builder()
                    .height(1024)
                    .width(1024)
                    .N(1)
                    .build())
            );

            String imageUrl = response.getResult().getOutput().getUrl();
            adventure.setInitialImage(imageUrl);
        } catch (NonTransientAiException e) {
            logger.warn("Error while generating image: {}", e.getMessage());
            adventure.setInitialImage("https://media.istockphoto.com/id/1290154699/vector/comic-speech-bubble-with-text-oops-message-in-pop-art-style.jpg?s=612x612&w=0&k=20&c=4J-vEqvSWx-HEq8IiG5qP1WcM4Sf1xNsmXNInjMzWHY=");
        }
        adventureRepository.save(adventure);
    }
}
