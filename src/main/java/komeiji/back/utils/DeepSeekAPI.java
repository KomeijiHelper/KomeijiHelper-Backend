package komeiji.back.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class DeepSeekAPI {

    private static final String API_KEY = "sk-444b7ce18eea441987c96defbf584c69"; // 替换成你的 API Key
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    public static String chat(String userMessage) throws Exception {
        String requestBody = """
        {
          "model": "deepseek-chat",
          "messages": [
            { "role": "system", "content": "你是一个KomeijiHelper平台的心理咨询AI助理。" },
            { "role": "user", "content": "%s" }
          ]
        }
        """.formatted(userMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
