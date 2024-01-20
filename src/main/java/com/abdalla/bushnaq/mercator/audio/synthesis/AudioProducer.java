package com.abdalla.bushnaq.mercator.audio.synthesis;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Vector3;

public interface AudioProducer {

	public void adaptToVelocity(final float speed) throws OpenAlException;

	public OpenAlSource disable() throws OpenAlException;

	public void dispose() throws OpenAlException;

	public void enable(final OpenAlSource source) throws OpenAlException;

	public int getChannels();

	public int getOpenAlFormat();

	public Vector3 getPosition();

	public boolean isEnabled();

	public boolean isPlaying() throws OpenAlException;

	public void pause() throws OpenAlException;

	public void play() throws OpenAlException;

	public void processBuffer(ByteBuffer byteBuffer);

	public void setGain(final float gain) throws OpenAlException;

	public void setPositionAndVelocity(final float[] position, final float[] velocity) throws OpenAlException;

	public void waitForPlay() throws InterruptedException, OpenAlException;

	//	public short process(long l);

	public void writeWav(final String fileName) throws IOException, OpenAlcException;

}
