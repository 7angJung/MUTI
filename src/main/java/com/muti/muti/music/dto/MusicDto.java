package com.muti.muti.music.dto;

import com.muti.muti.music.domain.Music;
import lombok.Getter;

@Getter
public class MusicDto {
    private Long id;
    private String title;
    private String artist;
    private String genre;

    public MusicDto(Music music) {
        this.id = music.getId();
        this.title = music.getTitle();
        this.artist = music.getArtist();
        this.genre = music.getGenre();
    }
}
