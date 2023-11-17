package edu.school21;

public class Bullet {
    private final double positionX;
    private double positionY;
    private final double shift;

    public Bullet(double shift, double positionX, double positionY) {
        this.shift = shift;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public double getPositionX() {
        return positionX;
    }

//    public double peekPositionY() {
//        return positionY;
//    }

    public double getPositionY() {
        positionY += shift;
        return positionY;
    }
}
