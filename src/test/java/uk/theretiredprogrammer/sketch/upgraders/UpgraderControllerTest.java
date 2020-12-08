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
package uk.theretiredprogrammer.sketch.upgraders;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class UpgraderControllerTest {

    @Test
    public void testConstructorActionTest0() throws IOException {
        System.out.println("Test0 -> bank bend");
        upgradeTest(Path.of("/Users/richard/Race training Scenarios/UnitTests/Test0.json"));
    }

    @Test
    public void testConstructorActionTest1() throws IOException {
        System.out.println("Test1 -> swinging");
        upgradeTest(Path.of("/Users/richard/Race training Scenarios/UnitTests/Test1.json"));
    }
    
    @Test
    public void testConstructorActionTest2() throws IOException {
        System.out.println("Test2 -> trapezoid");
        upgradeTest(Path.of("/Users/richard/Race training Scenarios/UnitTests/Test2.json"));
    }

    private void upgradeTest(Path path) throws IOException {
        ConfigFileController uc = new ConfigFileController(path);
        if (uc.needsUpgrade()) {
            uc.upgrade();
            uc.rewriteFile(path);
        }
    }
}
