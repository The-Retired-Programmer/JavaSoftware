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
package uk.theretiredprogrammer.sketch.strategy;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.sketch.core.Angle;
import static uk.theretiredprogrammer.sketch.core.Angle.ANGLE180;

/**
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class OffwindtoWindwardPortRoundingStrategyTest extends SailingStrategyTest {

    private static final Angle DELTAANGLE = new Angle(5);

    @Test
    public void layline1() throws IOException {
        System.out.println("layline 1");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47, 12));
        this.assertSailing(decision, ANGLE180.add(DELTAANGLE), ANGLE180);
    }

    @Test
    public void layline2() throws IOException {
        System.out.println("layline 2");
        Decision decision = makeDecision("/offwindtowindward-portrounding.json",
                () -> setboatlocation(47, 11));
        this.assertSailing(decision, ANGLE180.add(DELTAANGLE), ANGLE180);
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
        Angle target = new Angle(-135);
        this.assertSailing(decision, target, target.add(DELTAANGLE));
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
