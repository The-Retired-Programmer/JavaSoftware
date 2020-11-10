/*
 * Copyright 2020 Richard Linsdale (richard at theretiredprogrammer.uk).
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
package uk.theretiredprogrammer.sketch.display.control.strategy;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.entity.PropertyDegrees;

public class OffwindtoWindwardPortRoundingStrategyTest extends SailingStrategyTest {

    private static final double DELTAANGLE = 5;

    @Test
    public void layline1() throws IOException {
        System.out.println("layline 1");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47, 12));
        this.assertSailing(decision, -180 + DELTAANGLE, 180);
    }

    @Test
    public void layline2() throws IOException {
        System.out.println("layline 2");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47, 11));
        this.assertSailing(decision, -180 + DELTAANGLE, 180);
    }

    @Test
    public void layline3() throws IOException {
        System.out.println("layline 3");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47, 10));
        assertTURN(decision, 90, false);
    }

    @Test
    public void layline4() throws IOException {
        System.out.println("layline 4");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47.5, 9.5));
        assertTURN(decision, 90, false);
    }

    @Test
    public void layline5() throws IOException {
        System.out.println("layline 5");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setwindflow(4, 45),
                () -> setboatlocation(50, 13));
        PropertyDegrees target = new PropertyDegrees(-135);
        this.assertSailing(decision, -135, -135 + DELTAANGLE);
    }

    @Test
    public void layline6() throws IOException {
        System.out.println("layline 6");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setwindflow(4, 45),
                () -> setboatdirection(-135),
                () -> setboatlocation(48, 12));
        assertTURN(decision, 135, false);
    }
}
