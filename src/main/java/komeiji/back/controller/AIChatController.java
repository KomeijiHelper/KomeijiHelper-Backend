package komeiji.back.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import komeiji.back.utils.DeepSeekAPI;
import komeiji.back.utils.Result;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIChatController {

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody AIChatRequest message) {
        try {
            String response = DeepSeekAPI.chat(message.getMessage());
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            String content = jsonObject
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();
            return Result.success(content);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Setter
    @Getter
    private static class AIChatRequest{
        private String message;
    }
}

