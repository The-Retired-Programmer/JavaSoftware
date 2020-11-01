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
package uk.theretiredprogrammer.sketch.properties.control;

import java.io.IOException;
import java.nio.file.Path;
import uk.theretiredprogrammer.sketch.core.control.AbstractController;
import uk.theretiredprogrammer.sketch.core.control.Failure;
import uk.theretiredprogrammer.sketch.core.entity.PathWithShortName;
import uk.theretiredprogrammer.sketch.display.entity.boats.PropertyBoat;
import uk.theretiredprogrammer.sketch.display.entity.flows.PropertyFlowComponent;
import uk.theretiredprogrammer.sketch.display.entity.course.PropertyMark;
import uk.theretiredprogrammer.sketch.display.entity.base.SketchModel;
import uk.theretiredprogrammer.sketch.properties.ui.PropertiesWindow;
import uk.theretiredprogrammer.sketch.upgraders.ConfigFileController;

/**
 * Controller for Properties Management, reading, parsing and saving
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertiesController extends AbstractController<PropertiesWindow> {

    private SketchModel sketchmodel;

    public PropertiesController(PathWithShortName pn) {
        initialisefromFile(pn.getPath());
        setWindow(new PropertiesWindow(pn.toString(), this, sketchmodel), ExternalCloseAction.HIDE);
    }

    public PropertiesController(String resource, String fn) {
        initialisefromResource(resource);
        setWindow(new PropertiesWindow(fn, this, sketchmodel), ExternalCloseAction.HIDE);
    }

    public PropertiesController(String resource) {
        initialisefromResource(resource);
    }

    private void initialisefromFile(Path path) {
        ConfigFileController configfilecontroller;
        try {
            configfilecontroller = new ConfigFileController(path);
            if (configfilecontroller.needsUpgrade()) {
                configfilecontroller.upgrade();
                configfilecontroller.rewriteFile(path);
            }
        } catch (IOException ex) {
            throw new Failure(ex);
        }
        sketchmodel = new SketchModel();
        sketchmodel.parse(configfilecontroller.getParsedConfigFile());
    }

    private void initialisefromResource(String resourcename) {
        ConfigFileController configfilecontroller = new ConfigFileController(this.getClass().getResourceAsStream(resourcename));
        if (configfilecontroller.needsUpgrade()) {
            configfilecontroller.upgrade();
        }
        sketchmodel = new SketchModel();
        sketchmodel.parse(configfilecontroller.getParsedConfigFile());
    }

    public SketchModel getProperty() {
        return sketchmodel;
    }

    public void addNewMark() {
        PropertyMark newmarkproperty = new PropertyMark();
        sketchmodel.getMarks().add(newmarkproperty);
    }

    public void addNewBoat(String type) {
        sketchmodel.getBoats().add(new PropertyBoat(type));
    }

    public void addNewWindFlowComponent(String flowtype) {
        PropertyFlowComponent newflow = PropertyFlowComponent.factory(flowtype, () -> sketchmodel.getDisplayArea());
        sketchmodel.getWind().add(newflow);
    }

    public void addNewWaterFlowComponent(String flowtype) {
        PropertyFlowComponent newflow = PropertyFlowComponent.factory(flowtype, () -> sketchmodel.getDisplayArea());
        sketchmodel.getWater().add(newflow);
    }

    public void addNewLeg() {
        sketchmodel.getCourse().getPropertyLegValues().add();
    }
}
