package com.example.practicandolab7.Controller;

import com.example.practicandolab7.Repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    final PlayerRepository playerRepository;
    public PlayerController(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }




}
