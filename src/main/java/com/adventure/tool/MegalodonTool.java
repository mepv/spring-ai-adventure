package com.adventure.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class MegalodonTool {

    @Tool(description = "Cuenta exactamente cuántas veces aparece la palabra 'megalodon' en el texto dado, sin importar mayúsculas o minúsculas")
    public String countMegalodonMentions(@ToolParam(description = "El texto en el que se debe buscar la palabra 'megalodon'") String text) {
        if (text == null || text.isEmpty()) {
            return "0 veces - Gracias por abrirnos los caminos";
        }

        int count = 0;
        int index = 0;
        String lowerText = text.toLowerCase();
        String searchWord = "megalodon";

        while ((index = lowerText.indexOf(searchWord, index)) != -1) {
            count++;
            index += searchWord.length();
        }

        return count + " veces - Gracias por abrirnos los caminos";
    }
}
