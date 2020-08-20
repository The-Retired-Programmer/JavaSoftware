/*
 * Copyright 2020 richard linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.sketch.jfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import uk.theretiredprogrammer.sketch.ui.Controller;

/**
 * The display canvas for the simulation.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
class DisplayPanel extends JPanel implements Scrollable {

    private final Controller controller;

    public DisplayPanel(Controller controller) {
        this.controller = controller;
    }

    public void updateDisplay() {
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        this.setBackground(new Color(200, 255, 255));
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        // set the rendering hints
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        controller.paint(g2D);
    }

    @Override
    public Dimension getPreferredSize() {
        return controller.getGraphicDimension();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return controller.getGraphicDimension();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 200;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
