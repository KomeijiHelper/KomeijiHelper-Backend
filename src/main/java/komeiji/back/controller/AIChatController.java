package komeiji.back.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import komeiji.back.utils.DeepSeekAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIChatController {

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        try {
            String response = DeepSeekAPI.chat(message);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            String content = jsonObject
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}

