package com.example.spotifywrapped;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_3_5_TURBO;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;


public class GPTRequest extends AppCompatActivity {

    private WrappedViewModel wrappedVM;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openai_page);
    }

    public void openaiDescription(ArrayList<Track> songs) {
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

    public String generatePrompt(ArrayList<Track> songs) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Describe the behavior, thoughts, and dress style of someone who listens to these songs: ");
        for (int i = 0; i < songs.size(); i++) {
            Track song = songs.get(i);
            promptBuilder.append(song.getTrackName()).append(" by ").append(song.getArtistName());
            if (i < songs.size() - 1) {
                promptBuilder.append(", ");
            } else {
                promptBuilder.append(".");
            }
        }
        return promptBuilder.toString();
    }

    public String sendOpenAIRequest(String prompt) throws IOException {
        String apiKey = BuildConfig.OPENAI_API_KEY;
        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey(apiKey)
                .build();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_3_5_TURBO)
                .addUserMessage(prompt)
                .temperature(0.9)
                .build();

        ChatCompletionResponse response = client.chatCompletion(request).execute();
        System.out.println(String.valueOf(response));
        System.out.println("test");
        return String.valueOf(response);
    }

}