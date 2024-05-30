package com.example.practicandolab7.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players", schema = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playerid", nullable = false)
    private Integer id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "mmr")
    private Long mmr;

    @Column(name = "position")
    private Integer position;

    @Column(name = "region", length = 100)
    private String region;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMmr() {
        return mmr;
    }

    public void setMmr(Long mmr) {
        this.mmr = mmr;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}