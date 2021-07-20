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

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class USBSerialDevice implements Closeable {

    private final SerialPort comPort;
    private final OutputStream out;
    private final InputStream in;

    public USBSerialDevice() {
        comPort = SerialPort.getCommPort("/dev/tty.usbmodem14201");
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        out = comPort.getOutputStream();
        in = comPort.getInputStream();
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        comPort.closePort();
    }

    public void writeln(String s) throws IOException {
        for (int val : s.toCharArray()) {
            if (val >= 32 && val <= 126) {
                out.write(val);
            }
        }
        out.write(10); // newline
    }

    public String readln() throws IOException {
        String response = "";
        int c = 0;
        while (c != '\n') {
            c = in.read();
            if (c >= 32 && c <= 126) {
                response += (char) c;
            }
        }
        return response;
    }

}
