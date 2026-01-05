package com.example;

import okhttp3.*;
import java.io.File;
import java.io.IOException;

public class AudioUploader {

    private static final String SDK_URL = "https://api.eslyqurity.com/api/sdk/incidents";
    private static final String API_KEY = "";
    private static final String EMAIL = "";

    private final OkHttpClient client = new OkHttpClient();

    public String sendAudio(File file) throws IOException {
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("audio/webm"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(SDK_URL)
                .addHeader("Authorization", "Bearer " + API_KEY + ":" + EMAIL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : "Empty response";
        }
    }
}


/// root and mvn clean install and mvn javafx:run on powershell