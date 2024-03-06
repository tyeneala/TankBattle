DROP TABLE IF EXISTS player_info CASCADE;

CREATE TABLE IF NOT EXISTS player_info (id BIGSERIAL PRIMARY KEY NOT NULL, position_x INTEGER, position_y INTEGER, health_points INTEGER);

DROP TABLE IF EXISTS player_stat CASCADE;

CREATE TABLE IF NOT EXISTS player_stat (id BIGSERIAL PRIMARY KEY NOT NULL, player_id BIGINT,
                                      shot_counter INTEGER, hit_counter INTEGER, miss_counter INTEGER,
                                      FOREIGN KEY (player_id) REFERENCES player_info(id) ON DELETE CASCADE);