package com.adventure.service;

import com.adventure.model.Adventure;
import com.adventure.repository.AdventureRepository;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService {

    private final OpenAiImageModel openAiImageModel;
    private final AdventureRepository adventureRepository;

    public ImageService(OpenAiImageModel openAiImageModel, AdventureRepository adventureRepository) {
        this.openAiImageModel = openAiImageModel;
        this.adventureRepository = adventureRepository;
    }

    @Transactional
    public void generateImage(Adventure adventure, String prompt) {
        ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt, OpenAiImageOptions
                .builder()
                .height(1024)
                .width(1024)
                .N(1)
                .build())
        );

        String imageUrl = response.getResult().getOutput().getUrl();
        adventure.setInitialImage(imageUrl);
        adventureRepository.save(adventure);
    }
}
