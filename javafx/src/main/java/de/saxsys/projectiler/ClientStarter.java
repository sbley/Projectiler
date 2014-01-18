package de.saxsys.projectiler;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientStarter extends Application {

    @Override
    public void start(final Stage arg0) throws Exception {
        final URL rootUrl = ClientStarter.class.getResource("/Projectiler.fxml");
        final StackPane vBox = FXMLLoader.load(rootUrl);

        final Scene scene = new Scene(vBox, 320, 568);
        arg0.setScene(scene);
        arg0.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        arg0.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
