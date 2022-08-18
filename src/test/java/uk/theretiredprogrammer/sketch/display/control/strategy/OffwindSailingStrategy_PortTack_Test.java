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

import uk.theretiredprogrammer.sketch.display.entity.course.Decision;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class OffwindSailingStrategy_PortTack_Test extends SailingStrategyTest {

    @Test
    public void test1() throws IOException {
        System.out.println("on port reach - sailon");
        Decision decision = makeDecision("/reach-porttack.json");
        assertSAILON(decision);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("on port reach - pointing high - turn");
        Decision decision = makeDecision("/reach-porttack.json",
                () -> setboatdirection(350));
        assertTURN(decision, 0, true);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("on port reach - pointing low - turn");
        Decision decision = makeDecision("/reach-porttack.json",
                () -> setboatdirection(10));
        assertTURN(decision, 0, false);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("on port reach - sailing low - turn to closer reach");
        Decision decision = makeDecision("/reach-porttack.json",
                () -> setboatlocation(60, 50));
        assertTURN(decision, 354, false);
    }
}
