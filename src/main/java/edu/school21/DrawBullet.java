package edu.school21;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DrawBullet {
    public enum Category {PLAYER, EMENY}

    private final Image bulletE = new Image("enemyBullet.png",
            5, 11, true, true);
    private final Image bulletP = new Image("playerBullet.png",
            5, 11, true, true);

//    private List<Bullet> enemyBullets;
//    private List<Bullet> playerBullets;

    private Queue<Bullet> enemyBullets;
    private Queue<Bullet> playerBullets;

    GraphicsContext graphicsContext;

    public DrawBullet(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        enemyBullets = new ConcurrentLinkedQueue<>();
        playerBullets = new ConcurrentLinkedQueue<>();
    }

    public void addBullet(Category category, double position) {
        if (category == Category.EMENY) {
//            synchronized (enemyBullets) {
                enemyBullets.add(new Bullet(11, position + 20, 83));
//            }
        } else {
//            synchronized (playerBullets) {
                playerBullets.add(new Bullet(-11, position + 20, 438));
//            }
        }
    }

    public boolean drawBullets() {
        for (Bullet bullet: enemyBullets) {
            double y = bullet.getPositionY();
            if (y < 510) {
                graphicsContext.drawImage(bulletE, bullet.getPositionX(), y);
            } else {
                enemyBullets.remove(bullet);
            }
        }

        for (Bullet bullet: playerBullets) {
            double y = bullet.getPositionY();
            if (y > 11) {
                graphicsContext.drawImage(bulletP, bullet.getPositionX(), y);
            } else {
                playerBullets.remove(bullet);
            }
        }

        return false;
    }


}
