package com.muti.muti.music.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String artist;

    private String genre;

    private String mutiType;

    public Music(String title, String artist, String genre, String mutiType) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.mutiType = mutiType;
    }
}
