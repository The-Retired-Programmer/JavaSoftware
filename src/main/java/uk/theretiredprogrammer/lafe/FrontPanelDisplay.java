/*
 * Copyright 2021 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.lafe;

import java.util.List;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import static javafx.scene.paint.Color.RED;

public class FrontPanelDisplay extends Canvas {

    private final FrontPanelController controller;
    private final ProbeConfiguration config;

    public FrontPanelDisplay(FrontPanelController controller) {
        super(500.0, 500.0);
        this.controller = controller;
        this.config = controller.getProbeConfiguration();
    }

    public void refresh() {
        Map<Integer, List<String>> samples = controller.getSamples();
        int numbersamples = samples.size();
        expectedsamplesize = config.samplesize.get();
        // calculate layout
        int margin = 20;
        int height = 500;
        int maxsampleheight = 220;
        int calcsampleheight = (height - margin) / numbersamples;
        if (calcsampleheight > maxsampleheight) {
            calcsampleheight = maxsampleheight;
        }
        hscale = 5;
        int width = expectedsamplesize * hscale + 2 * margin;
        // set the required dimensions for the canvas and clear it
        this.clear();
        setWidth(width);
        setHeight(height);
        int count = 0;
        for (var es : samples.entrySet()) {
            int topofsample = calcsampleheight * count++ + margin;
            drawSample(es.getKey(), es.getValue(), margin, topofsample, topofsample + calcsampleheight - margin);
        }
    }

    private void clear() {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
    }

    // sample drawing variables
    private int hstart;
    private int hscale;
    private int highpos;
    private int lowpos;
    private int expectedsamplesize;
    private double[] xpos;
    private double[] ypos;
    private int insertat;

    private void drawSample(int pin, List<String> sample, int hstart, int highpos, int lowpos) {
        xpos = new double[expectedsamplesize];
        ypos = new double[expectedsamplesize];
        insertat = 0;
        this.hstart = hstart;
        this.highpos = highpos;
        this.lowpos = lowpos;
        sample.forEach(segment -> buildSamplesegment(segment));
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(RED);
        gc.setLineWidth(2.0);
        gc.strokePolyline(xpos, ypos, insertat);
    }

    private void buildSamplesegment(String samplesegment) {
        for (int cptr = 0; cptr < samplesegment.length(); cptr++) {
            switch (samplesegment.charAt(cptr)) {
                case 'H' -> {
                    insertHigh();
                }
                case 'L' -> {
                    insertLow();
                }
                default ->
                    cptr = decoderle(cptr, samplesegment);
            }
        }
    }

    private int decoderle(int cptr, String s) {
        char c = s.charAt(cptr);
        int count = 0;
        while ('0' <= c && c <= '9') {
            count = count * 10 + (int) (c - '0');
            c = s.charAt(++cptr);
        }
        switch (c) {
            case 'H' ->
                insertHigh(count);
            case 'L' ->
                insertLow(count);
            default ->
                throw new Failure("Badly encoded RLE data: " + c);
        }
        return cptr;
    }

    private void insertHigh() {
        insertHigh(1);
    }

    private void insertHigh(int width) {
        insert(highpos, width);
    }

    private void insertLow() {
        insertLow(1);
    }

    private void insertLow(int width) {
        insert(lowpos, width);
    }

    private void insert(int vpos, int width) {
        xpos[insertat] = hstart;
        ypos[insertat++] = vpos;
        hstart += hscale * width;
        xpos[insertat] = hstart;
        ypos[insertat++] = vpos;
    }
}
