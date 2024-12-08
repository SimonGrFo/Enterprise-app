package com.example.enterprise.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    @Value("${lastfm.api.key}")
    private String apiKey;

    @Value("${lastfm.api.url}")
    private String lastFmApiUrl;

    @GetMapping("/search")
    public ResponseEntity<?> searchTracks(@RequestParam String track) {
        System.out.println("Track search requested for: " + track);
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?method=track.search&track=%s&api_key=%s&format=json",
                lastFmApiUrl, track, apiKey);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tracks");
        }
    }
}

