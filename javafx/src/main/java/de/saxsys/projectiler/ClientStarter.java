package de.saxsys.projectiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.saxsys.projectiler.initialization.ApplicationAlreadyRunningChecker;
import de.saxsys.projectiler.initialization.ProjectilerFonts;
import de.saxsys.projectiler.misc.MovablePane;
import de.saxsys.projectiler.misc.Notification;
import de.saxsys.projectiler.tray.Tray;

public class ClientStarter extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
        initLogging();
        ApplicationAlreadyRunningChecker.check();
        ProjectilerFonts.initFonts();
        initStage(stage, createRootElement(stage));
        initNotification(stage);
    }

    private void initLogging() {
        final InputStream inputStream = ClientStarter.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (SecurityException | IOException e) {
            System.err.println("Could not initialize logging");
            e.printStackTrace();
        }
    }

    private MovablePane createRootElement(final Stage stage) throws IOException {
        final MovablePane movablePane = new MovablePane(stage);
        final URL rootUrl = ClientStarter.class.getResource("/Projectiler.fxml");
        final StackPane stackPane = FXMLLoader.load(rootUrl);
        movablePane.getChildren().add(stackPane);
        return movablePane;
    }

    private void initStage(final Stage stage, final MovablePane rootElement) {
        rootElement.setStyle("-fx-background-color:transparent;");
        final Scene scene = new Scene(rootElement);
        stage.setScene(scene);
        stage.setTitle("Projectiler");
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.getIcons().add(new Image(ClientStarter.class.getResourceAsStream("/projectiler.png")));
        stage.show();
        Tray.getInstance().initTrayForStage(stage);
    }

    private void initNotification(final Stage stage) {
        final double notHeight = 40;
        final double notWidth = 322;
        Notification.Notifier.setOffsetY(-20);
        Notification.Notifier.setWidth(notWidth);
        Notification.Notifier.setHeight(notHeight);
        Notification.Notifier.setPopupLocation(stage, Pos.BOTTOM_CENTER);
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
