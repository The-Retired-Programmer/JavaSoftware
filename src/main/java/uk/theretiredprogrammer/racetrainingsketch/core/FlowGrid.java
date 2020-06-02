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
import static uk.theretiredprogrammer.racetrainingsketch.core.FlowCell.BOTTOM;
import static uk.theretiredprogrammer.racetrainingsketch.core.FlowCell.LEFT;
import static uk.theretiredprogrammer.racetrainingsketch.core.FlowCell.RIGHT;
import static uk.theretiredprogrammer.racetrainingsketch.core.FlowCell.TOP;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class FlowGrid {

    private final FlowCell[][] cells;
    private final double[] cellsleft;
    private final double[] cellstop;
    private final int numberofwidthcells;
    private final int numberofheightcells;
    private final double leftcoordinate;
    private final double rightcoordinate;
    private final double topcoordinate;
    private final double bottomcoordinate;

    public FlowGrid(Location topleft, Location bottomright, int numberofwidthcells, int numberofheightcells) {
        this.leftcoordinate = topleft.getX();
        this.rightcoordinate = bottomright.getX();
        this.topcoordinate = topleft.getY();
        this.bottomcoordinate = bottomright.getY();
        this.numberofwidthcells = numberofwidthcells;
        this.numberofheightcells = numberofheightcells;
        cells = new FlowCell[numberofwidthcells][numberofheightcells];
        cellsleft = new double[numberofwidthcells + 1];
        cellstop = new double[numberofheightcells + 1];
        double cwidth = (rightcoordinate - leftcoordinate) / numberofwidthcells;
        double cheight = (bottomcoordinate - topcoordinate) / numberofheightcells;
        for (int w = 0; w <= numberofwidthcells; w++) {
            cellsleft[w] = leftcoordinate + w * cwidth;
        }
        for (int h = 0; h <= numberofheightcells; h++) {
            cellstop[h] = topcoordinate + h * cheight;
        }
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                cells[w][h] = new FlowCell(cellsleft[w], cwidth, cellstop[h], cheight);
            }
        }
        // link all the cells
        //left
        for (int w = 1; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                cells[w][h].connectedCells(LEFT, cells[w - 1][h]);
            }
        }
        //right
        for (int w = 0; w < numberofwidthcells - 1; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                cells[w][h].connectedCells(RIGHT, cells[w + 1][h]);
            }
        }
        //top
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 1; h < numberofheightcells; h++) {
                cells[w][h].connectedCells(TOP, cells[w][h - 1]);
            }
        }
        //bottom
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells - 1; h++) {
                cells[w][h].connectedCells(BOTTOM, cells[w][h + 1]);
            }
        }

    }

    public FlowGrid setFlow(SpeedPolar flow) {
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                setFlow(flow, w, h);
            }
        }
        return this;
    }

    public FlowGrid setFlowRow(SpeedPolar flow, int heightindex) {
        for (int w = 0; w < numberofwidthcells; w++) {
            setFlow(flow, w, heightindex);
        }
        return this;
    }

    public FlowGrid setFlowColumn(SpeedPolar flow, int widthindex) {
        for (int h = 0; h < numberofheightcells; h++) {
            setFlow(flow, widthindex, h);
        }
        return this;
    }

    public FlowGrid setFlow(SpeedPolar flow, int widthindex, int heightindex) {
        cells[widthindex][heightindex].setFlow(flow);
        return this;
    }

    public void build() throws IOException {
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                cells[w][h].calculate_edgeflow();
            }
        }

        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                cells[w][h].calculate_cornerflow();
            }
        }
    }

    public SpeedPolar getFlow(Location pos) throws IOException {
        double posx = pos.getX();
        double posy = pos.getY();
        checkinrange(posx, posy);
        int widthindex = getwidthindex(posx);
        int heightindex = getheightindex(posy);
        return cells[widthindex][heightindex].getFlow(posx, posy);
    }

    private void checkinrange(double x, double y) throws IOException {
        if (x < leftcoordinate || x > rightcoordinate || y < topcoordinate || y > bottomcoordinate) {
            throw new IOException("location out of range when calculating flow");
        }
    }

    private int getwidthindex(double widthpos) {
        for (int w = 1; w < numberofwidthcells; w++) {
            if (widthpos < cellsleft[w]) {
                return w - 1;
            }
        }
        return numberofwidthcells - 1;
    }

    private int getheightindex(double heightpos) {
        for (int h = 1; h < numberofheightcells; h++) {
            if (heightpos < cellstop[h]) {
                return h - 1;
            }
        }
        return numberofheightcells - 1;
    }

    public Angle getMeanFlowDirection() {
        // TODO - rewrite with polars 
        int sumdegrees = 0;
        for (int w = 0; w < numberofwidthcells; w++) {
            for (int h = 0; h < numberofheightcells; h++) {
                sumdegrees += cells[w][h].getFlow().getAngle().getDegrees();
            }
        }
        return new Angle(sumdegrees / (numberofwidthcells * numberofheightcells));
    }
}
