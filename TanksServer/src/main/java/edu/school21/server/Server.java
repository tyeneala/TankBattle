package edu.school21.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.school21.models.Bullet;
import edu.school21.models.GameObject;
import edu.school21.models.JsonContainer;
import edu.school21.models.Player;
import edu.school21.services.GameService;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static final ArrayList<ClientHandler> CLIENT_HANDLER_ARRAY_LIST = new ArrayList<>();
    private static final boolean PRINT_JSON = false;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static GameService gameService;
    private final ServerSocket serverSocket;


    public Server(int port, GameService gameService) throws IOException, SecurityException, IllegalArgumentException {
        serverSocket = new ServerSocket(port);
        Server.gameService = gameService;
    }

    public void startServer() {
        System.out.println("Server inet address = " + serverSocket.getInetAddress() + " is listening on port " + serverSocket.getLocalPort());
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected by port " + socket.getPort());
                if (CLIENT_HANDLER_ARRAY_LIST.size() < 2) {
                    CLIENT_HANDLER_ARRAY_LIST.add(new ClientHandler(socket));
                    if (CLIENT_HANDLER_ARRAY_LIST.size() == 2) {
                        for (ClientHandler clientHandler : CLIENT_HANDLER_ARRAY_LIST) {
                            Thread thread = new Thread(clientHandler);
                            thread.start();
                        }
                    }
                }
            }
        } catch (IOException e) {
            serverShutdown();
        }
    }

    private void serverShutdown() {
        for (ClientHandler clientHandler : CLIENT_HANDLER_ARRAY_LIST) {
            clientHandler.closeResources();
        }
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private Player clientPlayer;

        public ClientHandler(@NotNull Socket socket) {
            try {
                this.socket = socket;
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientPlayer = new Player();
                gameService.addPlayer(clientPlayer);
                out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(clientPlayer), JsonContainer.ClassType.PLAYER)));
            } catch (IOException e) {
                closeResources();
            }
        }

        private void closeResources() {
            try {
                CLIENT_HANDLER_ARRAY_LIST.remove(this);
                if (socket != null && !socket.isClosed()) {
                    System.out.println("Client on port " + socket.getPort() + " has been disconnected");
                    socket.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            broadcastGameObject(clientPlayer);
            while (!socket.isClosed()) {
                readClientInput();
            }
        }

        synchronized public void processKeyKode(KeyCode keyCode) {
            if (keyCode == KeyCode.LEFT && clientPlayer.getPositionX() > Player.MIN_POSITION_X) {
                clientPlayer.setPositionX(clientPlayer.getPositionX() - 1);
                gameService.updatePlayer(clientPlayer);
                broadcastGameObject(clientPlayer);
            } else if (keyCode == KeyCode.RIGHT && clientPlayer.getPositionX() < Player.MAX_POSITION_X) {
                clientPlayer.setPositionX(clientPlayer.getPositionX() + 1);
                gameService.updatePlayer(clientPlayer);
                broadcastGameObject(clientPlayer);
            } else if (keyCode == KeyCode.SPACE) {
                gameService.addShot(clientPlayer.getId());
                broadcastGameObject(new Bullet(null, clientPlayer.getPositionX(), clientPlayer.getPositionY(), clientPlayer, -1));
            }
        }

        synchronized private void processBullet(@NotNull Bullet receivedBullet) {
            boolean hitTarget = !receivedBullet.isOutOfBounds();
            if (clientPlayer.getId().equals(receivedBullet.getShooterId())) {
                gameService.addShot(clientPlayer.getId(), hitTarget);
            } else {
                if (hitTarget) {
                    clientPlayer.setHealthPoints(clientPlayer.getHealthPoints() - Bullet.DAMAGE);
                    gameService.updatePlayer(clientPlayer);
                    broadcastGameObject(clientPlayer);
                    if (clientPlayer.getHealthPoints() <= 0) {
                        broadcastStatistic();
                    }
                }
            }
        }

        synchronized private void broadcastStatistic() {

            for (ClientHandler clientHandler : CLIENT_HANDLER_ARRAY_LIST) {
                Long id = clientHandler.clientPlayer.getId();
                for (ClientHandler thread : CLIENT_HANDLER_ARRAY_LIST) {
                    try {
                        thread.out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(gameService.getStatistic(id)), JsonContainer.ClassType.PLAYER_STAT)));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


        synchronized private <T extends GameObject> void broadcastGameObject(@NotNull T gameObject) {
            JsonContainer.ClassType classType = JsonContainer.getClassType(gameObject.getClass());
            if (classType != null && CLIENT_HANDLER_ARRAY_LIST.size() == 2) {
                for (ClientHandler client : CLIENT_HANDLER_ARRAY_LIST) {
                    if (!client.equals(this)) {
                        try {
                            this.out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(gameObject), classType)));
                            client.out.println(objectMapper.writeValueAsString(new JsonContainer(objectMapper.writeValueAsString(gameObject.getObjectWithMirroredCoordinates()), classType)));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }


        private void readClientInput() {
            try {
                String json = in.readLine();
                if (json == null) {
                    closeResources();
                } else {
                    if (PRINT_JSON) {
                        System.out.println(json);
                    }
                    readObject(objectMapper.readValue(json, JsonContainer.class));
                }
            } catch (IOException e) {
                closeResources();
            }
        }

        private void readObject(@NotNull JsonContainer jsonContainer) throws JsonProcessingException {
            switch (jsonContainer.getClassType()) {
                case BULLET:
                    processBullet(objectMapper.readValue(jsonContainer.getJson(), Bullet.class));
                    break;
                case PLAYER:
                    processPlayer(objectMapper.readValue(jsonContainer.getJson(), Player.class));
                    break;
                case KEY_CODE:
                    processKeyKode(objectMapper.readValue(jsonContainer.getJson(), KeyCode.class));
                    break;
                case PLAYER_STAT:
                    //ignore
                    break;
            }
        }

        private void processPlayer(@NotNull Player receivedPlayer) {
            if (receivedPlayer.getId() != null && receivedPlayer.getId().equals(clientPlayer.getId())) {
                clientPlayer.setName(receivedPlayer.getName());
                gameService.updatePlayer(clientPlayer);
            }
        }
    }
}
