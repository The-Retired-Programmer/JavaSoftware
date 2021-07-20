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
import javafx.scene.Group;
import javafx.scene.text.Text;

public class FrontPanelDisplay extends Group {

    private final FrontPanelController controller;
    private final Text segmenttextarea;

    public FrontPanelDisplay(FrontPanelController controller) {
        this.controller = controller;
        this.getChildren().addAll(
                segmenttextarea = new Text()
        );
    }

    public void refresh() {
        Map<Integer, List<String>> samples = controller.getSamples();
        samples.entrySet().stream().forEach((entry) -> drawSample(entry.getKey(), entry.getValue()));
    }

    private void drawSample(int pin, List<String> sample) {
        sample.stream().forEach(s -> drawSamplesegment(s));
    }

    private void drawSamplesegment(String samplesegment) {
        segmenttextarea.setText(rle2display(samplesegment));
    }

    private String rle2display(String s) {
        StringBuilder sb = new StringBuilder();
        for (int cptr = 0; cptr < s.length(); cptr++) {
            switch (s.charAt(cptr)) {
                case 'H' ->
                    sb.append('-');
                case 'L' ->
                    sb.append('_');
                default ->
                    cptr = decoderle(cptr, s, sb);
            }
        }
        return sb.toString();
    }

    private int decoderle(int cptr, String s, StringBuilder sb) {
        char c = s.charAt(cptr);
        int count = 0;
        while ('0' <= c && c <= '9') {
            count = count * 10 + (int) (c - '0');
            c = s.charAt(++cptr);
        }
        switch (c) {
            case 'H' ->
                sb.append("-".repeat(count));
            case 'L' ->
                sb.append("_".repeat(count));
            default ->
                throw new Failure("Badly encoded RLE data: " + c);
        }
        return cptr;
    }
}
