package com.example.practicandolab7.Controller;

import com.example.practicandolab7.Entity.Player;
import com.example.practicandolab7.Repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class PlayerController {
    final PlayerRepository playerRepository;
    public PlayerController(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }
    //Listado del leaderboard:
    @GetMapping(value = "/player/leaderBoard" )
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
    @PostMapping(value = "/player/addBody" )
    public Object addPlayerRequestBody(@RequestBody Player player ,
            @RequestParam(value ="fetchId",required = false ) boolean fetchId) {
        try {
            //Validar que sea una región valida , validar que el mmr sea un número entero mayor a cero
            if (player.getMmr() > 0 && (Arrays.asList("Américas", "Europa", "SE Asíatico", "China")).contains(player.getRegion())) {
                try {
                    Player newPlayer = playerRepository.save(player);
                    playerRepository.reCalculateRelativePosition(player.getRegion());
                    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<String, Object>();
                    if (fetchId) {
                        responseMap.put("id", newPlayer.getId());
                    }
                    responseMap.put("estado", "Creado");
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
                } catch (Exception e) {
                    HashMap<String, Object> er = new HashMap<>();
                    er.put("error", "ocurrio un error inesperado");
                    er.put("date", "" + LocalDateTime.now());
                    return ResponseEntity.badRequest().body(er);
                }
            } else {
                //Gestionamos error
                if (!(Arrays.asList("Américas", "Europa", "SE Asíatico", "China")).contains(player.getRegion())) {
                    HashMap<String, Object> er = new HashMap<>();
                    er.put("error", "Se ingreso una region no valida");
                    er.put("date", "" + LocalDateTime.now());
                    return ResponseEntity.badRequest().body(er);
                } else if (player.getMmr() < 0) {
                    HashMap<String, Object> er = new HashMap<>();
                    er.put("error", "Se ingreso una cantidad negativa");
                    er.put("date", "" + LocalDateTime.now());
                    return ResponseEntity.badRequest().body(er);
                }
            }
            return ResponseEntity.badRequest().body("");
        }catch(Exception errorFinal) {
            HashMap<String, Object> er = new HashMap<>();
            er.put("error","ocurrio un error inesperado");
            er.put("date",""+ LocalDateTime.now());
            return ResponseEntity.badRequest().body(er);
        }
    }
    //Gestionamos errores del POST y PUT
    @ExceptionHandler({HttpMessageNotReadableException.class} )
    public Object gestionExcetion (HttpServletRequest request){
        HashMap<String , Object> responseMap = new HashMap<>();
        if(request.getMethod().equals("POST") || request.getMethod().equals("PUT") ){
            responseMap.put("estado" , "error");
            responseMap.put("msg" , "debe enviar un producto");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

    @PutMapping(value="/player/update")
    public Object updateMmrPlayer(@RequestBody Player player ) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (player.getId() != null && player.getId() > 0) {
            Optional<Player> optPlayer = playerRepository.findById(player.getId());
            if (optPlayer.isPresent()) {
                if(player.getMmr() != null  && player.getMmr()>0){
                    Player auxPlayer = optPlayer.get();
                    auxPlayer.setMmr(player.getMmr());
                    Player newPlayer = playerRepository.save(auxPlayer);
                    playerRepository.reCalculateRelativePosition(newPlayer.getRegion());
                    responseMap.put("estado", "actualizado");
                    return ResponseEntity.ok(responseMap);
                }else {
                    if(player.getMmr() == null){
                        responseMap.put("estado", "error");
                        responseMap.put("msg", "debe ingresar un valor de mmr");
                        return ResponseEntity.badRequest().body(responseMap);
                    }else{
                        responseMap.put("estado", "error");
                        responseMap.put("msg", "debe ingresar un valor de mmr mayor a 0");
                        return ResponseEntity.badRequest().body(responseMap);
                    }
                }
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "El jugador a actualizar no existe");
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un ID");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }
    //Asumimos borrado por id
    @DeleteMapping(value = "/player/delete/{id}")
    public Object borrar(@PathVariable("id") String idPlayer) {
        HashMap<String, Object> responseMap = new HashMap<>();
        try {
            int id = Integer.parseInt(idPlayer);
            if (playerRepository.existsById(id)) {
                String region =  playerRepository.findById(id).get().getRegion();
                playerRepository.deleteById(id);
                playerRepository.reCalculateRelativePosition(region);
                responseMap.put("estado", "borrado exitoso");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "no se encontró el producto con id: " + id);
                return ResponseEntity.badRequest().body(responseMap);
            }
        } catch (NumberFormatException ex) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "El ID debe ser un número");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }




}


