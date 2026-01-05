package com.example.audio_api.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AudioController {

    private static final String SDK_URL = "https://api.eslyqurity.com/api/sdk/incidents";
    private static final String API_KEY = "$2b$10$c6Y402lEQeUn51VGKLzunuI2flbapcSWufvz4CuDl9nQtXBKS4GfK";
    private static final String EMAIL = "simmykheswa@outlook.com";

    @PostMapping("/send-audio")
    public ResponseEntity<String> sendAudio(@RequestParam("audio") MultipartFile file) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Setup Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + API_KEY + ":" + EMAIL);

            // 2. Setup Body (File)
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 3. Forward to SDK
            return restTemplate.postForEntity(SDK_URL, requestEntity, String.class);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }
}