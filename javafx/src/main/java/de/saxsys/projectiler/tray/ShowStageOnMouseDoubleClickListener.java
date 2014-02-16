package de.saxsys.projectiler.tray;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class ShowStageOnMouseDoubleClickListener implements MouseListener {
    /**
     * 
     */
    private final Tray tray;

    /**
     * @param tray
     */
    ShowStageOnMouseDoubleClickListener(Tray tray) {
        this.tray = tray;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
            this.tray.showStage();
        }
    }
}
