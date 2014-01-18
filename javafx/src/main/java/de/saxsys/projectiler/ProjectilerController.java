package de.saxsys.projectiler;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ProjectilerController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView cardImage;

    @FXML
    private ImageView timeImage;

    @FXML
    private Label timeLabel;

    @FXML
    void initialize() {
        assert cardImage != null : "fx:id=\"cardImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeImage != null : "fx:id=\"timeImage\" was not injected: check your FXML file 'Projectiler.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'Projectiler.fxml'.";

        cardImage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent arg0) {
                new Thread(new ProjectilerTask()).start();
            }
        });

    }

}
