package edu.school21.services;

import edu.school21.models.Player;
import edu.school21.models.PlayerStat;
import edu.school21.repositories.CrudRepository;

public interface GameService {

    void addPlayer(Player player);

    void updatePlayer(Player player);

    void addShot(long shooterId);

    void addShot(long shooterId, boolean hitTarget);

    PlayerStat getStatistic(Long id);

}