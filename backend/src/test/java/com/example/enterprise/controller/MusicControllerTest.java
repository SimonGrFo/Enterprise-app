package com.example.enterprise.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicControllerTest {

    @Mock
    private RestTemplate restTemplate;

    private MusicController musicController;

    @BeforeEach
    public void setup() {
        musicController = new MusicController(restTemplate);
    }

    @Test
    public void testSearchTracks_success() {
        String trackName = "Bohemian Rhapsody";
        String mockResponse = "{ \"results\": { \"trackmatches\": { \"track\": [] } } }";
        String mockUrl = "https://mockapi.last.fm?method=track.search&track=Bohemian Rhapsody&api_key=testApiKey&format=json";

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        ResponseEntity<?> response = musicController.searchTracks(trackName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    public void testSearchTracks_failure() {
        String trackName = "Unknown Track";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        ResponseEntity<?> response = musicController.searchTracks(trackName);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error fetching tracks", response.getBody());
    }
}
