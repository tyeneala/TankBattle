package edu.school21.services;

import edu.school21.models.Player;
import edu.school21.models.PlayerStat;
import edu.school21.repositories.CrudRepository;
import edu.school21.repositories.PlayerCrudRepositoryImpl;
import edu.school21.repositories.PlayerStatCrudRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private static PlayerCrudRepositoryImpl playerCrudRepository;
    private static PlayerStatCrudRepositoryImpl playerStatCrudRepository;

    @Autowired
    public GameServiceImpl(PlayerCrudRepositoryImpl playerCrudRepository,
                           PlayerStatCrudRepositoryImpl playerStatCrudRepository) {
        GameServiceImpl.playerCrudRepository = playerCrudRepository;
        GameServiceImpl.playerStatCrudRepository = playerStatCrudRepository;
    }

    @Override
    public void addPlayer(Player player) {
        playerCrudRepository.save(player);
        PlayerStat playerStat = new PlayerStat();
        playerStat.setPlayer(player);
        playerStatCrudRepository.save(playerStat);
    }

    @Override
    public void updatePlayer(Player player) {
        playerCrudRepository.update(player);
    }

    @Override
    public void addShot(long shooterId) {
        Optional<PlayerStat> optionalPlayerStat = playerStatCrudRepository.findByPlayerId(shooterId);
        if(optionalPlayerStat.isPresent()){
            PlayerStat playerStat = optionalPlayerStat.get();
            playerStat.setShotCounter(playerStat.getShotCounter() + 1);
            playerStatCrudRepository.update(playerStat);
        };
    }


    @Override
    public void addShot(long shooterId, boolean hitTarget) {
        Optional<PlayerStat> optionalPlayerStat = playerStatCrudRepository.findByPlayerId(shooterId);
        if(optionalPlayerStat.isPresent()){
            PlayerStat playerStat = optionalPlayerStat.get();
            if(hitTarget) {
                playerStat.setHitCounter(playerStat.getHitCounter() + 1);
            } else {
                playerStat.setMissCounter(playerStat.getMissCounter() + 1);
            }
            playerStatCrudRepository.update(playerStat);
        };
    }

    @Override
    public PlayerStat getStatistic(Long id) {
        return playerStatCrudRepository.findById(id).get();
    }


}