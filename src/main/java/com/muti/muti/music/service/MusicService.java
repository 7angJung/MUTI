package com.muti.muti.music.service;

import com.muti.muti.music.domain.Music;
import com.muti.muti.music.dto.MusicDto;
import com.muti.muti.music.repository.MusicRepository;
import com.muti.muti.survey.domain.MutiTrait;
import com.muti.muti.user.domain.User;
import com.muti.muti.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    private static final Map<MutiTrait, List<String>> TRAIT_GENRE_MAP;

    static {
        Map<MutiTrait, List<String>> map = new EnumMap<>(MutiTrait.class);
        // E(감성) / I(악기/구조)
        map.put(MutiTrait.E, List.of("Pop", "Classical"));
        map.put(MutiTrait.I, List.of("Rock", "Jazz", "Hip-Hop"));
        // S(느림) / F(빠름)
        map.put(MutiTrait.S, List.of("Jazz", "Classical"));
        map.put(MutiTrait.F, List.of("Rock", "Pop", "Hip-Hop"));
        // A(적극적) / D(수동적) - 장르보다는 추천 방식에 영향을 줄 수 있으나, 여기서는 특정 장르와 연결
        map.put(MutiTrait.A, List.of("Jazz", "Rock")); // 좀 더 매니악한 장르
        map.put(MutiTrait.D, List.of("Pop", "Classical")); // 좀 더 대중적인 장르
        // P(대중적) / U(비주류)
        map.put(MutiTrait.P, List.of("Pop", "Hip-Hop"));
        map.put(MutiTrait.U, List.of("Jazz", "Rock", "Classical")); // 상대적으로 비주류로 간주
        TRAIT_GENRE_MAP = Collections.unmodifiableMap(map);
    }

    @Transactional(readOnly = true)
    public List<MusicDto> getRecommendations(String username) {
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String mutiType = user.getMutiType();
        if (mutiType == null || mutiType.length() != 4) {
            // MUTI 타입이 없는 경우, 빈 리스트나 기본 추천을 반환할 수 있음
            return Collections.emptyList();
        }

        Set<String> recommendedGenres = Arrays.stream(mutiType.split(""))
                .map(MutiTrait::valueOf)
                .flatMap(trait -> TRAIT_GENRE_MAP.getOrDefault(trait, Collections.emptyList()).stream())
                .collect(Collectors.toSet());

        if (recommendedGenres.isEmpty()) {
            return Collections.emptyList();
        }

        List<Music> recommendations = recommendedGenres.stream()
                .flatMap(genre -> musicRepository.findByGenre(genre).stream())
                .distinct()
                .collect(Collectors.toList());

        // 랜덤으로 섞어서 일부만 반환할 수도 있음
        Collections.shuffle(recommendations);
        return recommendations.stream()
                .map(MusicDto::new)
                .collect(Collectors.toList());
    }
}
