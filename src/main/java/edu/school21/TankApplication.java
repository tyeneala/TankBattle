package edu.school21;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class TankApplication extends Application {
    private final Image player= new Image("player.png", 41, 53, true, true);
    private final Image enemy= new Image("enemy.png", 41, 53, true, true);
    private final Image field = new Image("field.png", 521, 521, true, true);

    private int signOrientation = -1;
    private Group root = new Group();

    double positionPlayer = 240;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage mainStage) {
        mainStage.setTitle("Tank Battle");

//        Group root = new Group();
        Scene myScene = new Scene(root);
        mainStage.setScene(myScene);


        myScene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.LEFT && positionPlayer > 25) {
                positionPlayer -= 5;
            }
            if (event.getCode() == KeyCode.RIGHT && positionPlayer < 455) {
                positionPlayer += 5;
            }
            event.consume();
        });


        Canvas canvas = new Canvas(521, 521);
        root.getChildren().add(canvas);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        Thread t1 = new Thread(() -> { gameProcess(graphicsContext); });
        t1.start();

//        graphicsContext.drawImage(field, 0, 0);
//        graphicsContext.drawImage(player, 480, 886);
//        graphicsContext.drawImage(enemy, 480, 50);

        mainStage.show();

    }

    private void gameProcess(GraphicsContext graphicsContext) {
        boolean expect = true;
        double positionEnemy = 240;

        while (expect) {

            graphicsContext.drawImage(field, 0, 0);

            graphicsContext.drawImage(player, positionPlayer, 455);

            graphicsContext.drawImage(enemy, positionEnemy, 25);
            positionEnemy += autoMove(positionEnemy);

            try {
                TimeUnit.MILLISECONDS.sleep(30);
            } catch (InterruptedException ex) {

            }

        }
    }


    private double autoMove(double position) {
        if (position < 25) {
            signOrientation = 1;
        }
        if (position > 455) {
            signOrientation = -1;
        }
        return 5 * signOrientation;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}