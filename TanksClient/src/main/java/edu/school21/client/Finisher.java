package edu.school21.client;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Finisher {
    private Stage dialog;
    private GridPane layout;
    private int indexNextRow = 0;
    private Label[] labels = new Label[12];

    public Finisher(Stage mainStage) {
        Platform.runLater(() -> {
            dialog = new Stage();
            dialog.setTitle("Statistics");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(mainStage);

            layout = new GridPane();
            layout.setGridLinesVisible(true);

            building();

            Scene dialogScene = new Scene(layout, 300, 150);
            dialog.setScene(dialogScene);
            dialog.show();
        });
    }

    private void building() {
        for (int i = 0; i < 4; i++) {
            layout.getColumnConstraints()
                    .add(new ColumnConstraints(75, 75, 75,
                            Priority.ALWAYS, HPos.CENTER, false));
        }

        for (int i = 0; i < 3; i++) {
            layout.getRowConstraints()
                    .add(new RowConstraints(50, 50, 50,
                            Priority.ALWAYS, VPos.CENTER, false));
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                labels[i + j * 4] = new Label("-");
                layout.add(labels[i + j * 4], i, j);
            }
        }
        addRowWithData(new String[]{"name", "shots", "hits", "misses"});
    }

    private void addRowWithData(String[] data) {
        if (indexNextRow == 3) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (data.length >= i) {
                labels[i + indexNextRow * 4].setText(data[i]);
            } else {
                break;
            }
        }
        ++indexNextRow;
    }

    public void addData(String[] data) {
        Platform.runLater(() -> {
            addRowWithData(data);
        });
    }


}
