package de.bushnaq.abdalla.mercator.universe.tools;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class WavePlayer extends Thread {
	private AudioFormat audioFormat;
	private AudioInputStream audioStream;
	private final int BUFFER_SIZE = 128000;
	String filename;
	private File soundFile;
	SoundManager soundManager;
	private SourceDataLine sourceLine;

	public WavePlayer(final SoundManager soundManager, final String filename) {
		this.soundManager = soundManager;
		this.filename = filename;
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
		int nBytesRead = 0;
		final byte[] abData = new byte[BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioStream.read(abData, 0, abData.length);
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				@SuppressWarnings("unused")
				final int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
			}
		}
		sourceLine.drain();
		sourceLine.close();
		soundManager.remove(this);
	}
}
