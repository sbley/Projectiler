package de.saxsys.projectiler.misc;

import javafx.animation.FadeTransitionBuilder;
import javafx.scene.Node;

public class UITools {
    public static void hide(final Node... nodes) {
        for (final Node node : nodes) {
            node.setOpacity(0.0);
        }
    }

    public static void disable(final Node... nodes) {
        for (final Node node : nodes) {
            node.setDisable(true);
        }
    }

    public static void enable(final Node... nodes) {
        for (final Node node : nodes) {
            node.setDisable(false);
        }
    }

    public static void fadeIn(final Node... nodes) {
        for (final Node node : nodes) {
            final FadeTransitionBuilder build = FadeTransitionBuilder.create().toValue(1.0);
            build.node(node).build().play();
        }
    }

    public static void makeVisible(final Node... nodes) {
        for (final Node node : nodes) {
            node.setVisible(true);
        }
    }

    public static void makeInvisible(final Node... nodes) {
        for (final Node node : nodes) {
            node.setVisible(false);
        }
    }
}
