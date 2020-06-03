/*
 * Copyright 2014-2020 Richard Linsdale.
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
package uk.theretiredprogrammer.racetrainingsketch.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.Scrollable;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 * The Multiview DisplayableElement to display the simulation, based on the
 * definition file.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
@MultiViewElement.Registration(
        displayName = "#LBL_DefFile_SIMULATION",
        iconBase = "uk/theretiredprogrammer/racetrainingsketch/shape_flip_horizontal.png",
        mimeType = "text/x-rtd",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "DefFileSimulation",
        position = 2000)
@Messages("LBL_DefFile_SIMULATION=Simulation")
public final class ScenarioSimulationDisplay extends JPanel implements MultiViewElement {

    private final DefFileDataObject dataobj;
    private final JToolBar toolbar = new JToolBar();
    private final JLabel timeinfo = new JLabel("Time: 0:00");
//    private JLabel firstleginfo = null;
    private transient MultiViewElementCallback callback;
    //
    private ScenarioElement scenario;
    private DisplayPanel dp;
    private boolean isRunning = false;
    private Timer timer;
    private TimeStepsRunner runner;
    private int simulationtime = 0;
    private String displayablefailuremessage = null;

    /**
     * Get the simulation display instance which is in focus.
     *
     * @return this instance
     */
    public static ScenarioSimulationDisplay getSimulationInFocus() {
        TopComponent tc = TopComponent.getRegistry().getActivated();
        if (tc == null) {
            return null;
        }
        return tc.getLookup().lookup(ScenarioSimulationDisplay.class);
    }

    /**
     * Constructor.
     *
     * @param lkp the top component lookup
     */
    public ScenarioSimulationDisplay(Lookup lkp) {
        dataobj = lkp.lookup(DefFileDataObject.class);
        assert dataobj != null;
        initComponents();
        dataobj.getPrimaryFile().addFileChangeListener(new FileChangeAdapter() {
            @Override
            public void fileChanged(FileEvent fe) {
                reset();
            }
        });
        try {
            parseAndCreateSimulationDisplay();
        } catch (IOException ex) {
            displayablefailuremessage = ex.getLocalizedMessage();
        }
        //
        toolbar.addSeparator();
        toolbar.add(new ResetAction(this));
        toolbar.add(new StartAction(this));
        toolbar.add(new PauseAction(this));
        toolbar.addSeparator();
//        if (firstleginfo != null) {
//            toolbar.add(firstleginfo);
//            toolbar.addSeparator();
//        }
        toolbar.add(timeinfo);
        toolbar.addSeparator();
    }
    
    private void parseAndCreateSimulationDisplay() throws IOException {
        simulationtime = 0;
        scenario = new DefFile(dataobj.getPrimaryFile()).parse();
//        MarkElement m1 = scenario.getFirstmark();
//        if (m1 != null) {
//            DistancePolar p1 = new DistancePolar(scenario.getStartlocation(), m1.getLocation());
//            firstleginfo = new JLabel("First Mark: " + Integer.toString((int) p1.getDistance()) + "m");
//        }
        dp = new DisplayPanel();
        attachPanelScrolling(dp);
        validate();
        repaint();
    }


    /**
     * Start running the simulation.
     */
    public void start() {
        if (isRunning) {
            return;
        }
        int rate = (int) (scenario.getSecondsperdisplay() * 1000 / scenario.getSpeedup());
        timer = new Timer();
        runner = new TimeStepsRunner();
        timer.scheduleAtFixedRate(runner, 0, rate);
        isRunning = true;
    }

    /**
     * Reset the simulation.
     */
    public void reset(){
        terminate();
        removeAll();
        scenario.finish();
        try {
            parseAndCreateSimulationDisplay();
        } catch (IOException ex) {
            displayablefailuremessage = ex.getLocalizedMessage();
        }
    }

    /**
     * Terminate the simulation.
     */
    public void terminate() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        timer.cancel();
    }

    /**
     * Act on a function keystroke.
     *
     * @param key the keystroke
     */
    // TODO - keyaction body disabled - needs to be reworked at a later date
    public void keyAction(String key) {
//        try {
//            scenario.actionKey(key);
//            for (BoatElement boat : boats.values()) {
//                boat.actionKey(key);
//            }
//        } catch (IOException ex) {
//            displayablefailuremessage = ex.getLocalizedMessage();
//        }

    }

    /**
     * Attach a panel to this element (embedded in a scroll pane). This would
     * typically be the display canvas for the simulation display.
     *
     * @param panel the display canvas
     */
    public void attachPanelScrolling(JPanel panel) {
        this.add(new JScrollPane(panel));
    }

    @Override
    public String getName() {
        return "DefFileSimulation";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("SimulationPanel"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return new ProxyLookup(dataobj.getLookup(), Lookups.singleton(this));
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
        terminate();
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private class TimeStepsRunner extends TimerTask {

        @Override
        public void run() {
            try {
                int secondsperdisplay = scenario.getSecondsperdisplay();
                while (secondsperdisplay > 0) {
//TODO timer call to actionFutureParameters disabled - will needto be enabled in the future                   
//                    scenario.actionFutureParameters(simulationtime);
                    scenario.timerAdvance(simulationtime);
                    secondsperdisplay--;
                    simulationtime++;
                }
                timeinfo.setText("Time: " + mmssformat(simulationtime));
                dp.updateDisplay();
            } catch (IOException ex) {
                dp.failure(ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Format a time in seconds into mm:ss format.
     *
     * @param seconds the time interval in seconds
     * @return the time interval expressed as a mm:ss string
     */
    public static String mmssformat(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        String ss = Integer.toString(secs);
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        return Integer.toString(mins) + ":" + ss;
    }

    /**
     * The display canvas for the simulation.
     *
     * @author Richard Linsdale (richard at theretiredprogrammer.uk)
     */
    private final class DisplayPanel extends JPanel implements Scrollable {

        private final Dimension preferredsize;

        /**
         * Constructor
         *
         */
        public DisplayPanel() {
            double width = scenario.getEast() - scenario.getWest();
            double depth = scenario.getNorth() - scenario.getSouth();
            double scale = scenario.getZoom();
            preferredsize = new Dimension((int) (width * scale), (int) (depth * scale));
        }

        public void failure(String failuremessage) {
            displayablefailuremessage = failuremessage;
            this.repaint();
        }

        /**
         * Update the display
         */
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
            if (displayablefailuremessage == null) {
                try {
                    scenario.draw(g2D);
                } catch (IOException ex) {
                    displayablefailuremessage = ex.getLocalizedMessage();
                }
            }
            if (displayablefailuremessage != null) {
                g2D.setFont(new Font("Sans Serif", Font.PLAIN, 12));
                g2D.drawString(displayablefailuremessage, 0, 0);
                displayablefailuremessage = null;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return preferredsize;
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return preferredsize;
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
}
