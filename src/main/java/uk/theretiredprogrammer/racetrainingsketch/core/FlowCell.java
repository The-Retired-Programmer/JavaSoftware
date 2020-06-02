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
package uk.theretiredprogrammer.racetrainingsketch.core;

import java.io.IOException;

/**
 * a flow geography is made an regular array of rectangular cells each with
 * defined flow at its centre and may have edge flows defined if it is an edge
 * cell.
 *
 * the geometry measures from:
 *
 * Top (min) to Bottom (max)
 *
 * Left (min) to Right (max)
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class FlowCell {

    // EDGES
    public final static int TOP = 0;
    public final static int RIGHT = 1;
    public final static int BOTTOM = 2;
    public final static int LEFT = 3;
    // CORNERS
    public final static int TOPRIGHT = 0;
    public final static int BOTTOMRIGHT = 1;
    public final static int BOTTOMLEFT = 2;
    public final static int TOPLEFT = 3;

    // data points
    private final double top;
    private final double left;
    private final double width;
    private final double height;
    private SpeedPolar flow;
    private final FlowCell[] connected = new FlowCell[]{null, null, null, null};
    private final SpeedPolar[] edgeflow = new SpeedPolar[]{null, null, null, null};
    // calculated edge and corner points
    private final SpeedPolar[] calculatededgeflow = new SpeedPolar[]{null, null, null, null};
    private final SpeedPolar[] calculatedcornerflow = new SpeedPolar[]{null, null, null, null};

    public FlowCell(double left, double width, double top, double height) {
        this.left = left;
        this.width = width;
        this.top = top;
        this.height = height;
    }
    
    public FlowCell setFlow(SpeedPolar flow) {
        this.flow = flow;
        return this;
    }

    public FlowCell connectedCells(int side, FlowCell cell) {
        this.connected[side] = cell;
        return this;
    }

    public FlowCell setEdgeFlow(int side, SpeedPolar flow) {
        this.edgeflow[side] = flow;
        return this;
    }

    public FlowCell calculate_edgeflow() throws IOException {
        for (int i = 0; i < 4; i++) {
            FlowCell edge_cell = connected[i];
            if (edge_cell != null) {
                SpeedPolar connectedcalculatedflow = edge_cell.calculatededgeflow[(i + 2) % 4];
                calculatededgeflow[i] = connectedcalculatedflow == null
                        ? calculateedgeflowusingajacentcell(edge_cell, i)
                        : connectedcalculatedflow;
            } else {
                SpeedPolar edge_flow = edgeflow[i];
                calculatededgeflow[i] = edge_flow == null ? flow : edge_flow; // needs change
            }
        }
        return this;
    }

    private SpeedPolar calculateedgeflowusingajacentcell(FlowCell adjacent, int edge) throws IOException {
        switch (edge) {
            case LEFT:
                return adjacent.flow.extrapolate(flow, adjacent.width / (adjacent.width + width));
            case RIGHT:
                return flow.extrapolate(adjacent.flow, width / (adjacent.width + width));
            case TOP:
                return adjacent.flow.extrapolate(flow, adjacent.height / (adjacent.height + height));
            case BOTTOM:
                return flow.extrapolate(adjacent.flow, height / (adjacent.height + height));
            default:
                throw new IOException("Illegal edge value");
        }

    }

    public FlowCell calculate_cornerflow() throws IOException {
        // calculate TOPLEFT if a connected TOP cell exists
        FlowCell edge_cell = connected[TOP];
        if (edge_cell != null) {
            SpeedPolar connectedcalculatedflow = edge_cell.calculatedcornerflow[BOTTOMLEFT];
            calculatedcornerflow[TOPLEFT] = connectedcalculatedflow == null
                    ? edge_cell.calculatededgeflow[LEFT].extrapolate(calculatededgeflow[LEFT], edge_cell.height / (edge_cell.height + height))
                    : connectedcalculatedflow;
        }
        // calculate TOPRIGHT if a connected TOP cell exists
        if (edge_cell != null) {
            SpeedPolar connectedcalculatedflow = edge_cell.calculatedcornerflow[BOTTOMRIGHT];
            calculatedcornerflow[TOPRIGHT] = connectedcalculatedflow == null
                    ? edge_cell.calculatededgeflow[RIGHT].extrapolate(calculatededgeflow[RIGHT], edge_cell.height / (edge_cell.height + height))
                    : connectedcalculatedflow;
        }
        // calculate BOTTOMLEFT if a connected BOTTOM cell exists
        edge_cell = connected[BOTTOM];
        if (edge_cell != null) {
            SpeedPolar connectedcalculatedflow = edge_cell.calculatedcornerflow[TOPLEFT];
            calculatedcornerflow[BOTTOMLEFT] = connectedcalculatedflow == null
                    ? calculatededgeflow[LEFT].extrapolate(edge_cell.calculatededgeflow[LEFT], height / (edge_cell.height + height))
                    : connectedcalculatedflow;
        }
        // calculate BOTTOMRIGHT if a connected BOTTOM cell exists
        if (edge_cell != null) {
            SpeedPolar connectedcalculatedflow = edge_cell.calculatedcornerflow[TOPRIGHT];
            calculatedcornerflow[BOTTOMRIGHT] = connectedcalculatedflow == null
                    ? calculatededgeflow[RIGHT].extrapolate(edge_cell.calculatededgeflow[RIGHT], height / (edge_cell.height + height))
                    : connectedcalculatedflow;
        }
        // calculate a missing TOPLEFT - this must be a TOP edge cell 
        if (calculatedcornerflow[TOPLEFT] == null) {
            FlowCell left_cell = connected[LEFT];
            calculatedcornerflow[TOPLEFT] = (left_cell != null && left_cell.calculatedcornerflow[TOPRIGHT] != null)
                    ? left_cell.calculatedcornerflow[TOPRIGHT]
                    : flow; // needs change
        }
        // calculate a missining TOPRIGHT - this must be a TOP edge cell
        if (calculatedcornerflow[TOPRIGHT] == null) {
            FlowCell right_cell = connected[RIGHT];
            calculatedcornerflow[TOPRIGHT] = (right_cell != null && right_cell.calculatedcornerflow[TOPLEFT] != null)
                    ? right_cell.calculatedcornerflow[TOPLEFT]
                    : flow; // needs change
        }
        // calculate a missing BOTTOMLEFT - this must be a BOTTOM edge cell
        if (calculatedcornerflow[BOTTOMLEFT] == null) {
            FlowCell left_cell = connected[LEFT];
            calculatedcornerflow[BOTTOMLEFT] = (left_cell != null && left_cell.calculatedcornerflow[BOTTOMRIGHT] != null)
                    ? left_cell.calculatedcornerflow[BOTTOMRIGHT]
                    : flow; // needs change
        }
        // calculate a missing BOTTOMRIGHT - this must be a BOTTOM edge cell
        if (calculatedcornerflow[BOTTOMRIGHT] == null) {
            FlowCell right_cell = connected[RIGHT];
            calculatedcornerflow[BOTTOMRIGHT] = (right_cell != null && right_cell.calculatedcornerflow[BOTTOMLEFT] != null)
                    ? right_cell.calculatedcornerflow[BOTTOMLEFT]
                    : flow; // needs change
        }
        return this;
    }

    public SpeedPolar getFlow(double leftpos, double toppos) throws IOException {
        if (leftpos < left || leftpos > left + width
                || toppos < top || toppos > top + height) {
            throw new IOException("getFlow request outside the FlowCell boundary");
        }
        double topfraction = (toppos - top) / height;
        double leftfraction = (leftpos - left) / width;
        SpeedPolar leftedgeflow = calculatedcornerflow[TOPLEFT].extrapolate(calculatedcornerflow[BOTTOMLEFT], topfraction);
        SpeedPolar rightedgeflow = calculatedcornerflow[TOPRIGHT].extrapolate(calculatedcornerflow[BOTTOMRIGHT], topfraction);
        return leftedgeflow.extrapolate(rightedgeflow, leftfraction);
    }
    
    public SpeedPolar getFlow() {
        return flow;
    }
}
