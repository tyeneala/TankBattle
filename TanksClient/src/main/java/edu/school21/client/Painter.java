package edu.school21.client;

import edu.school21.models.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

public class Painter {

    private static final Image playerImage = new Image("player.png",
            41, 53, true, true);
    private static final Image playerBulletImage = new Image("playerBullet.png",
            5, 11, true, true);
    private static final Image enemyImage = new Image("enemy.png",
            41, 53, true, true);
    private static final Image enemyBulletImage = new Image("enemyBullet.png",
            5, 11, true, true);
    private static final Image explosionImage = new Image("explosion.png",
            41, 46, true, true);
    private static final double CANVAS_HEIGHT = 533;
    private static final double CANVAS_WIDTH = 533;
    private static final Image fieldImage = new Image("field.png",
            CANVAS_HEIGHT, CANVAS_WIDTH, true, true);
    private static final Canvas canvas = new Canvas(CANVAS_HEIGHT, CANVAS_WIDTH);
    private static final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    public Painter() {
        graphicsContext.setLineWidth(5);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        graphicsContext.setFill(Color.BROWN);
    }

    public void drawBackground() {
        graphicsContext.drawImage(fieldImage, 0, 0);
    }

    public void drawPlayer(int playerPosX, int playerPosY) {
        graphicsContext.drawImage(playerImage, (CANVAS_WIDTH - playerImage.getWidth()) * playerPosX * 0.01, (CANVAS_HEIGHT - playerImage.getHeight()) * playerPosY * 0.01 - playerImage.getHeight());
    }

    public void drawEnemy(int enemyPosX, int enemyPosY) {
        graphicsContext.drawImage(enemyImage, (CANVAS_WIDTH - enemyImage.getWidth()) * enemyPosX * 0.01, (CANVAS_HEIGHT - enemyImage.getHeight()) * enemyPosY * 0.01 + enemyImage.getHeight());
    }

    public void drawPlayerHealthState(int playerHealth) {
        graphicsContext.strokeRect(graphicsContext.getLineWidth() * 2, CANVAS_HEIGHT - playerImage.getHeight() / 2, graphicsContext.getLineWidth() * 21, graphicsContext.getLineWidth() * 3);
        graphicsContext.fillRect(graphicsContext.getLineWidth() * 2.5, CANVAS_HEIGHT - playerImage.getHeight() / 2.25, (double) playerHealth / Player.DEFAULT_HEALTH_POINTS * 100, 10);
    }

    public void drawEnemyHealthState(int enemyHealth) {
        graphicsContext.strokeRect(409, 5, 106, 15);
        graphicsContext.fillRect(412, 8, enemyHealth, 10);
    }

    public void drawExplosionAtPlayer(int playerPosX, int playerPosY) {
        graphicsContext.drawImage(explosionImage, (CANVAS_WIDTH - playerImage.getWidth()) * playerPosX * 0.01, CANVAS_HEIGHT - playerPosY);
    }

    public void drawExplosionAtEnemy(int enemyPosX, int enemyPosY) {
        graphicsContext.drawImage(explosionImage, (CANVAS_WIDTH - enemyImage.getWidth()) * enemyPosX * 0.01, enemyPosY + enemyImage.getHeight());
    }

    public void drawPlayerBullet(int bulletPosX, int bulletPosY) {
        graphicsContext.drawImage(playerBulletImage, (CANVAS_WIDTH - playerImage.getWidth()) * bulletPosX * 0.01 + (playerImage.getWidth() - playerBulletImage.getWidth()) * 0.5, CANVAS_HEIGHT * bulletPosY * 0.01 - playerImage.getHeight() * 2);
    }

    public void drawEnemyBullet(int bulletPosX, int bulletPosY) {
        graphicsContext.drawImage(enemyBulletImage, (CANVAS_WIDTH - enemyImage.getWidth()) * bulletPosX * 0.01 + (enemyImage.getWidth() - enemyBulletImage.getWidth()) * 0.5, CANVAS_HEIGHT * bulletPosY * 0.01 + enemyImage.getHeight() * 2);
    }

    public void drawInitialText() {
        drawBackground();
        graphicsContext.setFont(Font.font(75));
        graphicsContext.strokeText("Waiting for other player to connect", 0, CANVAS_HEIGHT / 2, CANVAS_WIDTH);
    }

    public double getEnemyImageOffset() {
        return enemyImage.getWidth() * (playerBulletImage.getWidth() / enemyImage.getWidth());
    }

    public double getPlayerImageOffset() {
        return playerImage.getWidth() * (enemyBulletImage.getWidth() / playerImage.getWidth());
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
