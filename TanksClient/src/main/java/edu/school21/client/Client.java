package edu.school21.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.models.Bullet;
import edu.school21.models.JsonContainer;
import edu.school21.models.Player;
import edu.school21.models.PlayerStat;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Client extends Application {

    private static final boolean PRINT_JSON = false;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Painter painter = new Painter();
    private static Socket socket;
    private static PrintWriter out;
    private static Scene myScene;
    private static Stage myStage;
    private static boolean notPressedSpace = true;
    private static Connector connector;

    public static void main(String[] args) {
        launch(args);
    }

    public static void closeResources() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized private static void initKey() {
        myScene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT || (event.getCode() == KeyCode.SPACE && notPressedSpace)) {
                try {
                    out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(event.getCode()), JsonContainer.ClassType.KEY_CODE)));
                    notPressedSpace = event.getCode() != KeyCode.SPACE;
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            event.consume();
        });

        myScene.setOnKeyReleased((KeyEvent event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                notPressedSpace = true;
            }
            event.consume();
        });
    }

    public void initEverything() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            Thread thread = new Thread(new OutputListener(socket));
            thread.start();
        } catch (IOException e) {
            closeResources();
        }
    }

    public void connect(String address, String port) {
        try {
            socket = new Socket(address, Integer.parseInt(port));
            initEverything();
            connector.closeDialog();
        } catch (IOException e) {
            connector.outMessage("Cannot establish a connection on  address " + address +
                    " port " + port);
        } catch (SecurityException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            connector.outMessage("Invalid port value. Should be between 0 and 65535, inclusive.");
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        myStage = primaryStage;
        primaryStage.setTitle("Tank Battle");
        Group root = new Group();
        myScene = new Scene(root);
        primaryStage.setScene(myScene);
        primaryStage.setResizable(false);
        root.getChildren().add(painter.getCanvas());
        connector = new Connector(primaryStage);
        connector.getConnect(this);
        painter.drawInitialText();
        primaryStage.show();
    }

    private static class OutputListener implements Runnable {
        private final ConcurrentLinkedQueue<Bullet> playerBullets = new ConcurrentLinkedQueue<>();
        private final ConcurrentLinkedQueue<Bullet> enemyBullets = new ConcurrentLinkedQueue<>();
        private Socket socket;
        private BufferedReader in;
        private Player clientPlayer = new Player();
        private Player clientEnemy = (Player) new Player().getObjectWithMirroredCoordinates();
        private ArrayList<PlayerStat> playerStatArrayList = new ArrayList<>();
        private boolean isNotInitialized = true;

        public OutputListener(Socket socket) {
            try {
                this.socket = socket;
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientPlayer = objectMapper.readValue(objectMapper.readValue(in.readLine(), JsonContainer.class).getJson(), Player.class);
                clientPlayer.setName(connector.getNamePlayer());
                out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(clientPlayer), JsonContainer.ClassType.PLAYER)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void drawItems() {
            painter.drawBackground();
            painter.drawPlayer(clientPlayer.getPositionX(), clientPlayer.getPositionY());
            painter.drawPlayerHealthState(clientPlayer.getHealthPoints());
            painter.drawEnemy(clientEnemy.getPositionX(), clientEnemy.getPositionY());
            painter.drawEnemyHealthState(clientEnemy.getHealthPoints());
            updateBulletsStatus();

        }

        private void closeResources() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket.setSoTimeout(20);
                while (!socket.isClosed()) {
                    readServerInput();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        private void readServerInput() {
            try {
                String json = in.readLine();
                if (PRINT_JSON) {
                    System.out.println(json);
                }
                if (isNotInitialized) {
                    initKey();
                    isNotInitialized = false;
                }
                readObject(objectMapper.readValue(json, JsonContainer.class));
            } catch (SocketTimeoutException e) {
                if (!playerBullets.isEmpty() || !enemyBullets.isEmpty()) {
                    drawItems();
                }
            } catch (IOException e) {
                closeResources();
            }
        }

        public void updateBulletsStatus() {
            for (Bullet playerBullet : playerBullets) {
                playerBullet.setNextPositionY();
                if (playerBullet.isInArea(clientEnemy.getPositionX() - painter.getEnemyImageOffset(), clientEnemy.getPositionX() + painter.getEnemyImageOffset(), clientEnemy.getPositionY(), clientEnemy.getPositionY() + 40)) {
                    sendBulletToServer(playerBullet);
                    painter.drawExplosionAtEnemy(clientEnemy.getPositionX(), clientEnemy.getPositionY());
                    playerBullets.remove(playerBullet);
                } else if (playerBullet.isOutOfBounds()) {
                    sendBulletToServer(playerBullet);
                    playerBullets.remove(playerBullet);
                } else {
                    painter.drawPlayerBullet(playerBullet.getPositionX(), playerBullet.getPositionY());
                }

            }
            for (Bullet enemyBullet : enemyBullets) {
                enemyBullet.setNextPositionY();
                if (enemyBullet.isInArea(clientPlayer.getPositionX() - painter.getPlayerImageOffset(), clientPlayer.getPositionX() + painter.getPlayerImageOffset(), clientPlayer.getPositionY() - 40, clientPlayer.getPositionY())) {
                    sendBulletToServer(enemyBullet);
                    painter.drawExplosionAtPlayer(clientPlayer.getPositionX(), clientPlayer.getPositionY());
                    enemyBullets.remove(enemyBullet);
                } else if (enemyBullet.isOutOfBounds()) {
                    sendBulletToServer(enemyBullet);
                    enemyBullets.remove(enemyBullet);
                } else {
                    painter.drawEnemyBullet(enemyBullet.getPositionX(), enemyBullet.getPositionY());
                }
            }
        }

        synchronized private void sendBulletToServer(Bullet bullet) {
            try {
                out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(bullet), JsonContainer.ClassType.BULLET)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public void readObject(JsonContainer jsonContainer) throws JsonProcessingException {
            switch (jsonContainer.getClassType()) {
                case BULLET:
                    processBullet(objectMapper.readValue(jsonContainer.getJson(), Bullet.class));
                    break;
                case PLAYER:
                    processPlayer(objectMapper.readValue(jsonContainer.getJson(), Player.class));
                    break;
                case KEY_CODE:
                    //ignore
                    break;
                case PLAYER_STAT:
                    processStatistic(objectMapper.readValue(jsonContainer.getJson(), PlayerStat.class));
                    break;

            }
        }

        private void processStatistic(PlayerStat receivedPlayerStat) {
            playerStatArrayList.add(receivedPlayerStat);
            if (playerStatArrayList.size() == 2) {
                runEndGameSequence();
            }

        }

        private void processPlayer(Player receivedPlayer) {
            if (receivedPlayer.getId().equals(clientPlayer.getId())) {
                clientPlayer = receivedPlayer;
            } else {
                clientEnemy = receivedPlayer;
            }
            drawItems();
        }

        private void processBullet(Bullet receivedBullet) {
            if (receivedBullet.getShooterId().equals(clientPlayer.getId())) {
                playerBullets.add(receivedBullet);
            } else {
                enemyBullets.add(receivedBullet);
            }
            drawItems();
        }

        private void runEndGameSequence() {
            if (playerStatArrayList.size() == 2) {
                Finisher finisher = new Finisher(myStage);
                finisher.addData(new String[]{playerStatArrayList.get(0).getPlayer().getName(), String.valueOf(playerStatArrayList.get(0).getShotCounter()), String.valueOf(playerStatArrayList.get(0).getHitCounter()), String.valueOf(playerStatArrayList.get(0).getMissCounter())});
                finisher.addData(new String[]{playerStatArrayList.get(1).getPlayer().getName(), String.valueOf(playerStatArrayList.get(1).getShotCounter()), String.valueOf(playerStatArrayList.get(1).getHitCounter()), String.valueOf(playerStatArrayList.get(1).getMissCounter())});
                Client.closeResources();
            }

        }

    }

}
