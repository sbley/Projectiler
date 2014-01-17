package de.saxsys.projectiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientStarter extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ClientStarter.class
				.getResource("/de/saxsys/projectiler/ProjectilerView.fxml"));

		Object controller = loader.getController();
		Parent root = (Parent) loader.getRoot();

		arg0.setScene(new Scene(root));
		arg0.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
