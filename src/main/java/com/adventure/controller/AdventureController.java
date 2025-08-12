package com.adventure.controller;

import com.adventure.model.Adventure;
import com.adventure.model.AdventureParams;
import com.adventure.service.AdventureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdventureController {

    private static final String ADVENTURE = "adventure";
    private final AdventureService adventureService;

    public AdventureController(AdventureService adventureService) {
        this.adventureService = adventureService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/adventure/create")
    public String createAdventure(@ModelAttribute AdventureParams params, Model model) {
        Adventure adventure = adventureService.createAdventure(params);
        model.addAttribute(ADVENTURE, adventure);
        return ADVENTURE;
    }

    @PostMapping("/adventure/{id}/decision")
    public String makeDecision(@PathVariable Long id,
                               @RequestParam String choice,
                               Model model) {
        Adventure adventure = adventureService.processDecision(id, choice);
        model.addAttribute(ADVENTURE, adventure);
        return ADVENTURE;
    }

    @PostMapping("/adventure/{id}/summary")
    public String generateSummary(@PathVariable Long id, Model model) {
        Adventure adventure = adventureService.adventureSummary(id);
        model.addAttribute(ADVENTURE, adventure);
        return ADVENTURE;
    }

    @GetMapping("/adventure/{id}/text-speech")
    public ResponseEntity<byte[]> streamAudio(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=output.mp3");
        return new ResponseEntity<>(adventureService.textToSpeech(id), headers, HttpStatus.OK);
    }
}
