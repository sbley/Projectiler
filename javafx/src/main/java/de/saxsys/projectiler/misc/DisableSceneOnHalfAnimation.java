package de.saxsys.projectiler.misc;

import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

public class DisableSceneOnHalfAnimation implements ChangeListener<Duration> {

    private final Transition transition;

    public DisableSceneOnHalfAnimation(final Transition transition) {
        this.transition = transition;
    }

    @Override
    public void changed(final ObservableValue<? extends Duration> arg0, final Duration arg1, final Duration newDuration) {
        if (newDuration.greaterThanOrEqualTo(transition.getTotalDuration().divide(2))) {
            transition.pause();
            transition.currentTimeProperty().removeListener(this);
        }
    }

}
