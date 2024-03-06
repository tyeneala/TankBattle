package edu.school21.client;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Connector {
    private Stage dialog;
    private TextField fieldName;
    private TextField fieldAddress;
    private TextField fieldPort;
    private Label message;
    private Button button;

    public Connector(Stage mainStage) {
        Platform.runLater(() -> {
            dialog = new Stage();
            dialog.setTitle("Connection");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(mainStage);
            dialog = new Stage();
            GridPane layout = new GridPane();
            building(layout);
            Scene dialogScene = new Scene(layout, 300, 250);
            dialog.setScene(dialogScene);
            dialog.show();
        });
    }

    public void getConnect(Client mainClass) {
        Platform.runLater(() -> {
            button.setOnAction(event -> {
                mainClass.connect(fieldAddress.getText(), fieldPort.getText());
            });
        });
    }

    public void outMessage(String message) {
        Platform.runLater(() -> {
            this.message.setText(message);
        });
    }

    public String getNamePlayer() {
        return fieldName.getText();
    }


    private void building(GridPane layout) {
        layout.getColumnConstraints().add(new ColumnConstraints(100, 100, 100,
                Priority.ALWAYS, HPos.CENTER, false));
        layout.getColumnConstraints()
                .add(new ColumnConstraints(200, 200, 200,
                        Priority.ALWAYS, HPos.CENTER, false));

        for (int i = 0; i < 5; i++) {
            layout.getRowConstraints()
                    .add(new RowConstraints(50, 50, 50,
                            Priority.ALWAYS, VPos.CENTER, false));
        }

        Label name = new Label("Yur name");
        layout.add(name, 0, 0);
        Label address = new Label("Address");
        layout.add(address, 0, 1);
        Label port = new Label("Port");
        layout.add(port, 0, 2);
        fieldName = new TextField("name");
        layout.add(fieldName, 1, 0);
        fieldAddress = new TextField("127.0.0.1");
        layout.add(fieldAddress, 1, 1);
        fieldPort = new TextField("8081");
        layout.add(fieldPort, 1, 2);
        button = new Button("Connect");
        button.setPrefWidth(100);
        layout.add(button, 0, 3, 2, 1);
        message = new Label();
        message.setPrefWidth(300);
        layout.add(message, 0, 4, 2, 1);
    }

    public void closeDialog() {
        dialog.close();
    }


}
