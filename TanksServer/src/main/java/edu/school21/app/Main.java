package edu.school21.app;

import edu.school21.config.SocketsApplicationConfig;
import edu.school21.server.Server;
import edu.school21.services.GameService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static boolean isConnectionValid(DataSource ds) {
        try {
            Connection connection = ds.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.prepareStatement("SELECT 1");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Unable to establish connection to SQL server");
        }
        return false;
    }

    private static void initializeDB(Connection connection, Resource resource) {
        ScriptUtils.executeSqlScript(connection, resource);
    }

    public static void main(String[] args) {
        if ((args.length == 1) && args[0].startsWith("--port=")) {
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class)) {
                JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                if (jdbcTemplate.getDataSource() != null && isConnectionValid(jdbcTemplate.getDataSource())) {
                    GameService gameService = context.getBean(GameService.class);
                    Server server = new Server(Integer.parseInt(args[0].substring("--port=".length())), gameService);
                    initializeDB(jdbcTemplate.getDataSource().getConnection(), context.getResource("schema.sql"));
                    server.startServer();
                }
            } catch (IOException e) {
                System.out.println("Cannot create a server socket on port " + Integer.parseInt(args[0].substring("--port=".length())));
            } catch (SecurityException e) {
                //ignore
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid port value. Should be between 0 and 65535, inclusive.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Wrong parameter(-s) given. Server should have only one parameter that starts with \"--port=\"");
        }
    }
}