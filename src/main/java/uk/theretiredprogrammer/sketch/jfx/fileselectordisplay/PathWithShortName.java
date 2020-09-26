/*
 * Copyright 2020 richard.
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
package uk.theretiredprogrammer.sketch.jfx.fileselectordisplay;

import java.nio.file.Path;

/**
 *
 * @author richard
 */
public class PathWithShortName {
    
    private final Path path;
    
    public PathWithShortName(Path path) {
        this.path = path;
    }
    
    public Path getPath() {
        return path;
    }
    
    @Override
    public String toString() {
        String fn = path.getFileName().toString();
        int pt = fn.lastIndexOf(".");
        return pt == -1 ? fn : fn.substring(0, pt);
    }
}
