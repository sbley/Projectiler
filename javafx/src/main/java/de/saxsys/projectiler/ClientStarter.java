package de.saxsys.projectiler;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientStarter extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
        final URL rootUrl = ClientStarter.class.getResource("/Projectiler.fxml");
        final StackPane vBox = FXMLLoader.load(rootUrl);

        final Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Projectiler");
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
        initConfiguration(stage);

    }

    /**
     * @param stage
     */
    private void initConfiguration(final Stage stage) {
        final double notHeight = 30;
        final double notWidth = stage.getWidth() - 28;
        Notification.Notifier.setOffsetY(-20);
        Notification.Notifier.setWidth(notWidth);
        Notification.Notifier.setHeight(notHeight);
        Notification.Notifier.setPopupLocation(stage, Pos.BOTTOM_CENTER);
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
