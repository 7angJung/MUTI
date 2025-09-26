package com.muti.muti.music.repository;

import com.muti.muti.music.domain.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findByGenreIn(List<String> genres);
}