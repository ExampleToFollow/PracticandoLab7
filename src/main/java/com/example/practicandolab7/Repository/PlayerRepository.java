package com.example.practicandolab7.Repository;

import com.example.practicandolab7.Entity.Player;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player,Integer>  {


    @Query(nativeQuery = true, value = "select *  from  players p  where region= ?1  ORDER BY mmr desc limit 10;\n")
    List<Player> findLeaderBoardPlayersByRegion(String region);
    //Si quizás se refiere con su posicion relativa de ese tablero:
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "insert into players(name , mmr, region) values(?1,?2,?3)")
    void addPlayer(String name , Long mmr , String region);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "UPDATE players p JOIN ( SELECT playerId AS id, ROW_NUMBER() OVER (ORDER BY mmr DESC) AS newPosition FROM players where region = ?1 ) pe ON p.playerId = pe.id SET p.position = pe.newPosition")
    void reCalculateRelativePosition(String region);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "update players set mmr = ?1 where id=?2")
    void updateMmrById(int mmr , int idPlayer);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "update players set mmr = ?1 where name=?2")
    void updateMmrByName(int mmr , String name);
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "delete from players where name = ?1")
    void deleteByName(String name);





}
