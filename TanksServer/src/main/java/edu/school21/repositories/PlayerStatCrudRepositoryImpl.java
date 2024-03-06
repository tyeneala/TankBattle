package edu.school21.repositories;

import edu.school21.models.Player;
import edu.school21.models.PlayerStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PlayerStatCrudRepositoryImpl implements PlayerStatCrudRepository {

    private static PlayerCrudRepositoryImpl playerCrudRepository;
    private static final RowMapper<PlayerStat> MAPPER_PLAYER_STAT =
            (rs, i) -> new PlayerStat(rs.getLong("id"), getPlayer(rs.getLong("player_id")), rs.getInt("shot_counter"), rs.getInt("hit_counter"), rs.getInt("miss_counter"));
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PlayerStatCrudRepositoryImpl(DataSource ds, PlayerCrudRepositoryImpl playerCrudRepository) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        PlayerStatCrudRepositoryImpl.playerCrudRepository = playerCrudRepository;
    }

    private static Player getPlayer(Long id) {
        Optional<Player> optionalPlayer = playerCrudRepository.findById(id);
        return optionalPlayer.orElse(null);
    }


    @Override
    public void save(PlayerStat entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO player_stat (player_id, shot_counter, hit_counter, miss_counter) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getPlayerId());
            ps.setInt(2, entity.getShotCounter());
            ps.setInt(3, entity.getHitCounter());
            ps.setInt(4, entity.getMissCounter());
            return ps;
        }, keyHolder);
        entity.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
    }

    @Override
    public Optional<PlayerStat> findById(Long id) {
        String sql = "SELECT * FROM player_stat WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> rs.next() ? MAPPER_PLAYER_STAT.mapRow(rs, 1) : null, id));
    }

    @Override
    public List<PlayerStat> findAll() {
        String sql = "SELECT * FROM player_stat";
        return jdbcTemplate.query(sql, MAPPER_PLAYER_STAT);
    }

    @Override
    public void update(PlayerStat entity) {
        String sql = "UPDATE player_stat SET player_id = ?, shot_counter = ?, hit_counter = ?, miss_counter = ? WHERE id = ?";
        jdbcTemplate.update(sql, entity.getPlayerId(), entity.getShotCounter(), entity.getHitCounter(), entity.getMissCounter(), entity.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM player_stat WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<PlayerStat> findByPlayerId(Long id) {
        String sql = "SELECT * FROM player_stat WHERE player_id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> rs.next() ? MAPPER_PLAYER_STAT.mapRow(rs, 1) : null, id));
    }
}
