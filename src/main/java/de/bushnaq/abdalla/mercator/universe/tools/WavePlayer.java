/*
 * Copyright (C) 2024 Abdalla Bushnaq
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

package de.bushnaq.abdalla.mercator.universe.tools;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavePlayer extends Thread {
    private final int BUFFER_SIZE = 128000;
    String       filename;
    SoundManager soundManager;
    private AudioFormat      audioFormat;
    private AudioInputStream audioStream;
    private File             soundFile;
    private SourceDataLine   sourceLine;

    public WavePlayer(final SoundManager soundManager, final String filename) {
        this.soundManager = soundManager;
        this.filename     = filename;
    }

    @Override
    public void run() {
        try {
            soundFile = new File(filename);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        audioFormat = audioStream.getFormat();
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (final LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        sourceLine.start();
        int          nBytesRead = 0;
        final byte[] abData     = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused") final int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }
        sourceLine.drain();
        sourceLine.close();
        soundManager.remove(this);
    }
}
