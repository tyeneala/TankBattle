package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class PlayerStat {

    private Long id;
    private Player player;
    private int shotCounter;
    private int hitCounter;
    private int missCounter;

    public PlayerStat(){
        this.id= null;
        this.player = null;
        this.shotCounter = 0;
        this.hitCounter = 0;
        this.missCounter = 0;
    }

    public PlayerStat(@JsonProperty("id") Long id, @JsonProperty("player") Player player, @JsonProperty("shots_counter") int shotCounter, @JsonProperty("hit_counter") int hitCounter, @JsonProperty("miss_counter") int missCounter) {
        this.id = id;
        this.player = player;
        this.shotCounter = shotCounter;
        this.hitCounter = hitCounter;
        this.missCounter = missCounter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getShotCounter() {
        return shotCounter;
    }

    public void setShotCounter(int shotCounter) {
        this.shotCounter = shotCounter;
    }

    public int getHitCounter() {
        return hitCounter;
    }

    public void setHitCounter(int hitCounter) {
        this.hitCounter = hitCounter;
    }

    public int getMissCounter() {
        return missCounter;
    }

    public void setMissCounter(int missCounter) {
        this.missCounter = missCounter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, player, shotCounter, hitCounter, missCounter);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerStat)) {
            return false;
        }
        PlayerStat other = (PlayerStat) o;
        boolean idEquals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        boolean playerEquals = (this.player == null && other.player == null)
                || (this.player != null && this.player.equals(other.player));
        return idEquals && playerEquals && this.shotCounter == other.shotCounter && this.hitCounter == other.hitCounter && this.missCounter == other.missCounter;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getName() + " [", "]")
                .add("\"id\": " + id)
                .add("\"player id\": " + getPlayerId())
                .add("\"shotsCounter\": " + shotCounter)
                .add("\"hitCounter\": " + hitCounter)
                .add("\"missCounter\": " + missCounter)
                .toString();
    }

    @JsonIgnore
    public Long getPlayerId() {
        return (player == null) ? null : player.getId();
    }

}
