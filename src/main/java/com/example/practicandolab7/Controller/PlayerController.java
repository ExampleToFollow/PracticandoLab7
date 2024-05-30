package com.example.practicandolab7.Controller;

import com.example.practicandolab7.Entity.Player;
import com.example.practicandolab7.Repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class PlayerController {
    final PlayerRepository playerRepository;
    public PlayerController(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }
    //Listado del leaderboard:
    @GetMapping("/player/leaderBoard")
    public Object listLeaderBoardByRegion(@RequestParam(value = "region" ,required = false ,defaultValue = "Europa") String region){
        //En caso encuentre la region - no haya errores
        try {
            if ((Arrays.asList("Américas", "Europa", "SE Asíatico", "China")).contains(region)) {
                List<Player> lista = playerRepository.findLeaderBoardPlayersByRegion(region);
                ArrayList<LinkedHashMap<String, Object>> newList = new ArrayList<LinkedHashMap<String, Object>>();
                for(Player p :lista){
                    LinkedHashMap<String, Object> playerAcomodado = new LinkedHashMap<>();
                    playerAcomodado.put("posición",p.getPosition());
                    playerAcomodado.put("mmr",p.getMmr());
                    playerAcomodado.put("nombre",p.getName());
                    playerAcomodado.put("región",p.getRegion());
                    newList.add(playerAcomodado);
                }
                return ResponseEntity.ok(newList);
            } else {//En caso la region que puso no es ninguna de las propuestas
                //Devolvemos un Http error
                LinkedHashMap<String, Object> err = new LinkedHashMap<>();
                err.put("error", "Ingreso una region no existente para el leaderBoard");
                err.put("date", "" + LocalDateTime.now());
                return ResponseEntity.badRequest().body(err);
            }
        }catch (Exception e){
            HashMap<String, Object> er = new HashMap<>();
            er.put("error","ocurrio un error inesperado");
            er.put("date",""+ LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }



}
