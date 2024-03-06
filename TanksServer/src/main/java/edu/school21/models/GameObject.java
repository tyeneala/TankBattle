package edu.school21.models;

public abstract class GameObject {

    public static final int MIN_POSITION_X = 0;
    public static final int MAX_POSITION_X = 100;
    public static final int MIN_POSITION_Y = 0;
    public static final int MAX_POSITION_Y = 100;
    public int positionY;
    public int positionX;
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public abstract GameObject getObjectWithMirroredCoordinates();
}
