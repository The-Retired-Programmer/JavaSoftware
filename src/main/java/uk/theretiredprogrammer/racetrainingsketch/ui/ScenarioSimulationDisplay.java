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

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
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
    private JToolBar toolbar;
    private final JLabel timeinfo = new JLabel("Time: 0:00");
    private transient MultiViewElementCallback callback;
    //
    private Scenario scenario;
    private DisplayPanel dp;
    private Controller controller;

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
        setup();
    }

    private void setup() {
        controller = new Controller(dataobj, (t) -> updateDisplay(t));
        dp = new DisplayPanel(controller);
        this.add(new JScrollPane(dp));
        validate();
        repaint();
        toolbar = new JToolBar();
        toolbar.addSeparator();
        toolbar.add(new ResetAction(this));
        toolbar.add(new StartAction(this));
        toolbar.add(new PauseAction(this));
        toolbar.addSeparator();
        toolbar.add(new DisplayAllLogAction(this));
        toolbar.add(new DisplayFilteredLogAction(this));
        toolbar.addSeparator();
        toolbar.add(timeinfo);
        toolbar.addSeparator();
    }

    private void updateDisplay(String t) {
        timeinfo.setText("Time: " + t);
        dp.updateDisplay();
    }

    public void start() {
        controller.start();
    }

    public void stop() {
        controller.stop();
    }

    public void reset() {
        controller.stop();
        removeAll();
        setup();
    }

    public void displaylog() {
        controller.displaylog();
    }

    public void displayfilteredlog() {
        controller.displayfilteredlog("SELECTED");
    }

    public void keyAction(String key) {
        controller.keyAction(key);
    }

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
        stop();
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
}
