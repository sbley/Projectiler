package de.saxsys.projectiler.misc;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Class for checking whether a point has a minimum distance to an event.
 * 
 * @author alexander.casall
 * 
 */
public abstract class MouseEventDistanceChecker {

    private static final int TRESHOLD = 5;
    private boolean alreadyCrossed;

    private double startX = -1.0;
    private double startY = -1.0;

    public MouseEventDistanceChecker(Pane pane) {
        initListenersOnPane(pane);
    }

    private void initListenersOnPane(Pane pane) {
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                if (shouldFire(event)) {
                    fire();
                }
            }
        });
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                setStartX(event.getSceneX());
                setStartY(event.getSceneY());
                reset();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                checkForAlreadyCrossedTreshold(event);
            }
        });
    }

    /**
     * Check whether the minimun distance of startX and startY to event is bigger than a treshold.
     * 
     * @param event to check
     * @return fire or not
     */
    private boolean shouldFire(final MouseEvent event) {
        final double deltaX = Math.abs(startX - event.getSceneX());
        final double deltaY = Math.abs(startY - event.getSceneY());

        if (deltaX > TRESHOLD || deltaY > TRESHOLD) {
            return false;
        }
        return !alreadyCrossed;
    }

    /**
     * Check whether the minimun distance of startX and startY to event is bigger than a treshold. If yes, the
     * {@link #shouldFire(double, double, MouseEvent)} will return true the next time.
     * 
     * @param event to check
     */
    private void checkForAlreadyCrossedTreshold(final MouseEvent event) {
        final double deltaX = Math.abs(startX - event.getSceneX());
        final double deltaY = Math.abs(startY - event.getSceneY());
        if (deltaX > TRESHOLD || deltaY > TRESHOLD) {
            alreadyCrossed = true;
        }
    }

    /**
     * Reset the state of the object, so in case of a
     * {@link #checkForAlreadyCrossedTreshold(double, double, MouseEvent)} manipulated the behaviour of
     * {@link #shouldFire(double, double, MouseEvent)}, it will be reseted.
     */
    private void reset() {
        alreadyCrossed = false;
    }

    /**
     * 
     * @param startX initial X of the event. This value will be used for the check.
     */
    private void setStartX(final double startX) {
        this.startX = startX;
    }

    /**
     * 
     * @param startY initial Y of the event. This value will be used for the check.
     */
    private void setStartY(final double startY) {
        this.startY = startY;
    }

    public abstract void fire();
}
