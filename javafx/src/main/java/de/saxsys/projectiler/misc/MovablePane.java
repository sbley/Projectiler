package de.saxsys.projectiler.misc;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Pane which is dragable to move it around the screen.
 * 
 * @author alexander.casall
 */
public class MovablePane extends Pane {

    /**
     * Create the pane.
     * 
     * @param stage which contains the moveable pane.
     */
    public MovablePane(final Stage stage) {
        final Delta dragDelta = new Delta();
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
    }

    class Delta {
        double x, y;
    }
}
