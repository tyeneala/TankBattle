package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class Player extends GameObject implements Cloneable {

    public static final int DEFAULT_HEALTH_POINTS = 100;
    private String name;
    private int healthPoints;

    public Player() {
        this.id = null;
        this.name = "";
        this.positionX = Player.MAX_POSITION_X / 2;
        this.positionY = Player.MAX_POSITION_Y;
        this.healthPoints = Player.DEFAULT_HEALTH_POINTS;
    }

    public Player(@JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("position_x") int positionX, @JsonProperty("position_y") int positionY, @JsonProperty("health_points") int healthPoints) {
        this.id = id;
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;
        this.healthPoints = healthPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    @Override
    @JsonIgnore
    public GameObject getObjectWithMirroredCoordinates() {
        try {
            Player player = (Player) this.clone();
            player.positionX = MAX_POSITION_X - player.positionX;
            player.positionY = MAX_POSITION_Y - player.positionY;
            return player;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, positionX, positionY, healthPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player other = (Player) o;
        boolean idEquals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        return idEquals && this.positionX == other.positionX && this.positionY == other.positionY && this.healthPoints == other.healthPoints && name.equals(other.name);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getName() + " [", "]")
                .add("\"id\": " + id)
                .add("\"name\": \"" + name + '"')
                .add("\"positionX\": " + positionX)
                .add("\"positionY\": " + positionY)
                .add("\"healthPoints\": " + healthPoints)
                .toString();
    }
}