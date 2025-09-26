package com.muti.muti.music.service;

import com.muti.muti.music.domain.Music;
import com.muti.muti.music.dto.MusicDto;
import com.muti.muti.music.repository.MusicRepository;
import com.muti.muti.survey.domain.MutiTrait;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    private static final Map<MutiTrait, List<String>> TRAIT_GENRE_MAP;

    static {
        TRAIT_GENRE_MAP = new EnumMap<>(MutiTrait.class);
        TRAIT_GENRE_MAP.put(MutiTrait.E, Arrays.asList("발라드", "R&B"));
        TRAIT_GENRE_MAP.put(MutiTrait.I, Arrays.asList("재즈", "클래식"));
        TRAIT_GENRE_MAP.put(MutiTrait.S, Arrays.asList("발라드", "재즈"));
        TRAIT_GENRE_MAP.put(MutiTrait.F, Arrays.asList("댄스", "록"));
        TRAIT_GENRE_MAP.put(MutiTrait.A, Arrays.asList("인디", "록"));
        TRAIT_GENRE_MAP.put(MutiTrait.D, Arrays.asList("팝", "댄스"));
        TRAIT_GENRE_MAP.put(MutiTrait.P, Arrays.asList("팝", "발라드"));
        TRAIT_GENRE_MAP.put(MutiTrait.U, Arrays.asList("인디", "재즈"));
    }

    public List<MusicDto> getRecommendations(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String mutiType = user.getMutiType();
        if (mutiType == null || mutiType.length() != 4) {
            return Collections.emptyList();
        }

        Set<String> recommendedGenres = new HashSet<>();
        for (char traitChar : mutiType.toCharArray()) {
            try {
                MutiTrait trait = MutiTrait.valueOf(String.valueOf(traitChar));
                recommendedGenres.addAll(TRAIT_GENRE_MAP.get(trait));
            } catch (IllegalArgumentException e) {
                // Ignore if character is not a valid MutiTrait enum
            }
        }

        return musicRepository.findByGenreIn(new ArrayList<>(recommendedGenres)).stream()
                .map(MusicDto::new)
                .collect(Collectors.toList());
    }

    public List<MusicDto> getRecommendationsByMutiType(String mutiType) {
        if (mutiType == null || mutiType.length() != 4) {
            return Collections.emptyList();
        }
        Set<String> recommendedGenres = new HashSet<>();
        for (char traitChar : mutiType.toCharArray()) {
            try {
                MutiTrait trait = MutiTrait.valueOf(String.valueOf(traitChar));
                recommendedGenres.addAll(TRAIT_GENRE_MAP.get(trait));
            } catch (IllegalArgumentException e) {
                // Ignore if character is not a valid MutiTrait enum
            }
        }
        return musicRepository.findByGenreIn(new ArrayList<>(recommendedGenres)).stream()
                .map(MusicDto::new)
                .collect(Collectors.toList());
    }

    public List<MusicDto> getAllMusic() {
        return musicRepository.findAll().stream()
                .map(MusicDto::new)
                .collect(Collectors.toList());
    }
}
