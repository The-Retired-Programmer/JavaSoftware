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
import uk.theretiredprogrammer.sketch.core.control.IOFailure;
import uk.theretiredprogrammer.sketch.core.ui.AbstractWindow;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyBoat;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyFlowComponent;
import uk.theretiredprogrammer.sketch.properties.entity.PropertyMark;
import uk.theretiredprogrammer.sketch.properties.entity.PropertySketch;
import uk.theretiredprogrammer.sketch.properties.ui.PropertiesWindow;
import uk.theretiredprogrammer.sketch.upgraders.ConfigFileController;

/**
 * Controller for Properties Management, reading, parsing and saving
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class PropertiesController extends AbstractController {

    private PropertySketch sketchproperty;

    public void fileReadUpgradeParse(Path path) {
        new WorkRunner(() -> fileWork(path)).run();
    }

    public void resourceReadUpgradeParse(String resourcename) {
        new WorkRunner(() -> resourceWork(resourcename)).run();
    }

    private void fileWork(Path path) {
        ConfigFileController configfilecontroller;
        try {
            configfilecontroller = new ConfigFileController(path);
            if (configfilecontroller.needsUpgrade()) {
                configfilecontroller.upgrade();
                configfilecontroller.rewriteFile(path);
            }
        } catch (IOException ex) {
            throw new IOFailure(ex);
        }
        sketchproperty = new PropertySketch();
        sketchproperty.parse(configfilecontroller.getParsedConfigFile());
    }

    private void resourceWork(String resourcename) {
        ConfigFileController configfilecontroller = new ConfigFileController(this.getClass().getResourceAsStream(resourcename));
        if (configfilecontroller.needsUpgrade()) {
            configfilecontroller.upgrade();
        }
        sketchproperty = new PropertySketch();
        sketchproperty.parse(configfilecontroller.getParsedConfigFile());
    }

    public PropertySketch getProperty() {
        return sketchproperty;
    }
    
    public void showPropertiesWindow(String fn, AbstractWindow parent){
        new PropertiesWindow(fn, this, sketchproperty, parent);
    }
    
    public void addNewMark() {
        PropertyMark newmarkproperty = new PropertyMark();
        sketchproperty.getMarks().add(newmarkproperty);
    }

    public void addNewBoat(String type)  {
        sketchproperty.getBoats().add(new PropertyBoat(type));
    }

    public void addNewWindFlowComponent(String flowtype) {
        PropertyFlowComponent newflow = PropertyFlowComponent.factory(flowtype, () -> sketchproperty.getDisplayArea());
        sketchproperty.getWind().add(newflow);
    }

    public void addNewWaterFlowComponent(String flowtype) {
        PropertyFlowComponent newflow = PropertyFlowComponent.factory(flowtype, () -> sketchproperty.getDisplayArea());
        sketchproperty.getWater().add(newflow);
    }

    public void addNewLeg() {
        sketchproperty.getCourse().getPropertyLegValues().add();
    }
}
