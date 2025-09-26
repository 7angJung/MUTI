package com.muti.muti.music.controller;

import com.muti.muti.music.dto.MusicDto;
import com.muti.muti.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<MusicDto>> getRecommendations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(musicService.getRecommendations(principal.getName()));
    }
}
