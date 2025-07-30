package com.adventure.controller;

import com.adventure.model.Adventure;
import com.adventure.model.AdventureParams;
import com.adventure.service.AdventureService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdventureController {

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
        model.addAttribute("adventure", adventure);

        return "adventure";
    }

    @PostMapping("/adventure/{id}/decision")
    public String makeDecision(@PathVariable Long id,
                               @RequestParam String choice,
                               Model model) {
        Adventure adventure = adventureService.processDecision(id, choice);

        model.addAttribute("adventure", adventure);
        return "adventure";
    }
}
