package edu.school21.repositories;

import edu.school21.models.PlayerStat;

import java.util.Optional;

public interface PlayerStatCrudRepository extends CrudRepository<PlayerStat>{

    Optional<PlayerStat> findByPlayerId(Long id);
}
