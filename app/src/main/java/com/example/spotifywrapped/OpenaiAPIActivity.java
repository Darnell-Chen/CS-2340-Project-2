package com.example.spotifywrapped;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenaiAPIActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openai_page);
    }

    public void openaiDescription(String[] songs) {
        String prompt = generatePrompt(songs);

        executor.execute(() -> {
            try {
                String response = sendOpenAIRequest(prompt);
                handler.post(() -> {
                    // TODO: We can update our UI with the response here
                });
            } catch (IOException e) {
                e.printStackTrace();
            }});
    }

    private String generatePrompt(String[] songs) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Describe the behavior, thoughts, and dress style of someone who listens to these songs: ");
        for (int i = 0; i < songs.length; i++) {
            promptBuilder.append(songs[i]);
            if (i < songs.length - 1) {
                promptBuilder.append(", ");
            } else {
                promptBuilder.append(".");
            }
        }
        return promptBuilder.toString();
    }

    private String sendOpenAIRequest(String prompt) throws IOException {
        String apiKey = "";
        URL url = new URL("https://api.openai.com/v1/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        String jsonInputString = String.format("{\"model\": \"gpt-4-0125-preview\", \"prompt\": \"%s\"}", prompt.replace("\"", "\\\""));

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }
}