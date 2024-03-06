package edu.school21.repositories;

import edu.school21.models.Player;
import org.jetbrains.annotations.NotNull;
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
public class PlayerCrudRepositoryImpl implements CrudRepository<Player> {

    private static final RowMapper<Player> MAPPER_USER =
            (rs, i) -> new Player(rs.getLong("id"), rs.getString("name"), rs.getInt("position_x"), rs.getInt("position_y"), rs.getInt("health_points"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PlayerCrudRepositoryImpl(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    @Override
    public void save(@NotNull Player entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO player_info (name, position_x, position_y, health_points) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getPositionX());
            ps.setInt(3, entity.getPositionY());
            ps.setInt(4, entity.getHealthPoints());
            return ps;
        }, keyHolder);
        entity.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
    }

    @Override
    public Optional<Player> findById(Long id) {
        String sql = "SELECT * FROM player_info WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> rs.next() ? MAPPER_USER.mapRow(rs, 1) : null, id));
    }

    @Override
    public List<Player> findAll() {
        String sql = "SELECT * FROM player_info";
        return jdbcTemplate.query(sql, MAPPER_USER);
    }

    @Override
    public void update(@NotNull Player entity) {
        String sql = "UPDATE player_info SET name = ?, position_x = ?, position_y = ?, health_points = ? WHERE id = ?";
        jdbcTemplate.update(sql, entity.getName(), entity.getPositionX(), entity.getPositionY(), entity.getHealthPoints(), entity.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM player_info WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}
