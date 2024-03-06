package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class Bullet extends GameObject implements Cloneable {

    public static final int DAMAGE = 5;
    public static final int DEFAULT_VELOCITY = 1;
    private int velocity;
    private Player shooter;

    public Bullet(@JsonProperty("id") Long id, @JsonProperty("position_x") int positionX, @JsonProperty("position_y") int positionY, @JsonProperty("shooter") Player shooter, @JsonProperty("velocity") int velocity) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.shooter = shooter;
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Player getShooter() {
        return shooter;
    }

    public void setShooter(Player shooter) {
        this.shooter = shooter;
    }

    public void setNextPositionY() {
        this.positionY += velocity;
    }

    @JsonIgnore
    public boolean isInArea(double leftBorder, double rightBorder, double lowerBorder, double upperBorder) {
        return positionX >= leftBorder && positionX <= rightBorder && positionY >= lowerBorder && positionY <= upperBorder;
    }

    @JsonIgnore
    public boolean isOutOfBounds() {
        return this.positionY < Bullet.MIN_POSITION_Y || this.positionY > Bullet.MAX_POSITION_Y;
    }

    @JsonIgnore
    public Long getShooterId() {
        return (this.shooter == null) ? null : shooter.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, positionX, positionY, shooter, velocity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Bullet)) {
            return false;
        }
        Bullet other = (Bullet) o;
        boolean idEquals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        return idEquals && this.positionX == other.positionX && this.positionY == other.positionY && this.velocity == other.velocity;
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getName() + " [", "]")
                .add("\"id\": " + id)
                .add("\"positionX\": " + positionX)
                .add("\"positionY\": " + positionY)
                .add("\"shooterId\": " + getShooterId())
                .add("\"velocity\": " + velocity)
                .toString();
    }

    @Override
    @JsonIgnore
    public GameObject getObjectWithMirroredCoordinates() {
        try {
            Bullet bullet = (Bullet) this.clone();
            bullet.positionX = MAX_POSITION_X - bullet.positionX;
            bullet.positionY = MAX_POSITION_Y - bullet.positionY;
            bullet.velocity *= -1;
            return bullet;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
