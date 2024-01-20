package com.abdalla.bushnaq.mercator.universe;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.openal.ALC10;

public class TestOpenAl {
	public static void main(final String[] args) {
		new TestOpenAl().execute();
	}

	private long device;

	public void execute() {
		final long device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
	}

}
