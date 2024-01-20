package de.bushnaq.abdalla.mercator.audio.synthesis;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL10;

public class Synthesizer extends AbstractAudioProducer {

	private BassBoost bassBoost = null;
	private float bassBoostDbGain = 12;
	private float bassBoostFrequency = 440;
	private boolean enableBassBoost = false;
	//	private boolean enabled = false;//a disabled synth does not possess an audio source and any of the source attached resource like filters and buffers
	private boolean enableFilter;
	//	private float gain = 1.0f;
	private float highGain = 0.0f;
	long lastIndex;
	private final List<Lfo> lfos = new ArrayList<>();
	private float lowGain = 1.0f;
	private final List<Oscilator> oscillators = new ArrayList<>();

	//	private boolean play = false;//is the source playing?
	//	private final Vector3 position = new Vector3();//position of the audio source
	private final int samplerate;
	//	private OpenAlSource source = null;//if enabled, this will hold the attached openal source, otherwise null
	//	private final Vector3 velocity = new Vector3();//velocity of the audio source

	//	/**
	//	 * adapt synthesizer to the current source velocity
	//	 * @param speed
	//	 * @throws OpenAlException
	//	 */
	//	public void adaptToVelocity(final float speed) throws OpenAlException {
	//	}

	public Synthesizer(final int samplerate) throws OpenAlException {
		this.samplerate = samplerate;
	}

	public void add(final Lfo lfo) {
		lfo.setSampleRate(samplerate);
		lfos.add(lfo);
	}

	public void add(final Oscilator generator) {
		generator.setSampleRate(samplerate);
		oscillators.add(generator);
	}

	//	public OpenAlSource disable() throws OpenAlException {
	//		enabled = false;
	//		source.pause();
	//		final OpenAlSource sourceBuffer = source;
	//		source = null;
	//		return sourceBuffer;
	//	}

	protected void createBassBoost() {
		bassBoost = new BassBoost(bassBoostFrequency, bassBoostDbGain, samplerate);
	}

	@Override
	public void dispose() throws OpenAlException {
		super.dispose();
		//		if (isEnabled())
		//			source.dispose();
		for (final Oscilator osc : oscillators) {
			osc.dispose();
		}
		for (final Lfo lfo : lfos) {
			lfo.dispose();
		}
	}

	//	public Vector3 getPosition() {
	//		return position;
	//	}

	//	public boolean isEnabled() {
	//		return enabled;
	//	}

	//	public boolean isPlaying() throws OpenAlException {
	//		return play;
	//	}

	//	public void pause() throws OpenAlException {
	//		if (this.play) {
	//			play = false;
	//		}
	//		if (isEnabled())
	//			source.pause();
	//	}

	//	public void play() throws OpenAlException {
	//		if (!this.play) {
	//			play = true;
	//		}
	//		if (isEnabled())
	//			source.play();
	//	}

	@Override
	public void enable(final OpenAlSource source) throws OpenAlException {
		enabled = true;
		this.source = source;
		this.source.attach(this);
		this.source.setGain(gain);
		this.source.updateFilter(enableFilter, lowGain, highGain);
		if (isPlaying())
			this.source.play();//we should be playing
		this.source.unparkOrStartThread();
	}

	@Override
	public int getChannels() {
		return 1;
	}

	@Override
	public int getOpenAlFormat() {
		return AL10.AL_FORMAT_MONO16;
	}

	public short process(final long i) {
		double value = 0;
		for (final Oscilator osc : oscillators) {
			value += osc.gen(i) / oscillators.size();
		}
		for (final Lfo lfo : lfos) {
			value *= (1 + lfo.gen(i)) / (1 + lfo.getFactor());
		}

		if (bassBoost != null)
			value = bassBoost.process(value, 1);

		//Short.MAX_VALUE;
		return (short) (32760 * value);

	}

	//	/**
	//	 * Convenience method used for debugging
	//	 * @throws OpenAlcException
	//	 */
	//	public void renderBuffer() throws OpenAlcException {
	//		if (isEnabled()) {
	//			source.renderBuffer();
	//		} else {
	//			throw new OpenAlcException("Synth is disabled");
	//		}
	//	}

	@Override
	public void processBuffer(final ByteBuffer byteBuffer) {
		for (int sampleIndex = 0, bufferIndex = 0; sampleIndex < source.getSamples(); sampleIndex++, bufferIndex += 2) {
			byteBuffer.putShort(bufferIndex, process(lastIndex + sampleIndex));
		}
		lastIndex += source.getSamples();
	}

	private void removeBasBoost() {
		bassBoost = null;
	}

	public void setBassBoost(final boolean enableBassBoost) throws OpenAlException {
		this.enableBassBoost = enableBassBoost;
		if (isEnabled())
			this.updateBassBoost(enableFilter, lowGain, highGain);
	}

	public void setBassBoostGain(final float bassBoostFrequency, final float bassBoostDbGain) {
		this.bassBoostFrequency = bassBoostFrequency;
		this.bassBoostDbGain = bassBoostDbGain;
	}

	//	public void setGain(final float gain) throws OpenAlException {
	//		if (this.gain != gain && isEnabled()) {
	//			source.setGain(gain);
	//		}
	//		this.gain = gain;
	//	}

	//	public void setPositionAndVelocity(final float[] position, final float[] velocity) throws OpenAlException {
	//		if (this.getPosition().x != position[0] || this.getPosition().y != position[1] || this.getPosition().z != position[2]) {
	//			this.getPosition().set(position[0], position[1], position[2]);
	//		}
	//		if (isEnabled()) {
	//			source.setPosition(position);
	//		}
	//		//		}
	//		if (this.velocity.x != velocity[0] || this.velocity.y != velocity[1] || this.velocity.z != velocity[2]) {
	//			this.velocity.set(velocity[0], velocity[1], velocity[2]);
	//			adaptToVelocity(this.velocity.len());
	//		}
	//		if (isEnabled()) {
	//			source.setVelocity(position, velocity);
	//		}
	//		//		}
	//	}

	public void setFilter(final boolean enableFilter) throws OpenAlException {
		this.enableFilter = enableFilter;
		if (isEnabled())
			this.source.updateFilter(enableFilter, lowGain, highGain);
	}

	public void setFilterGain(final float lowGain, final float highGain) throws OpenAlException {
		this.lowGain = lowGain;
		this.highGain = highGain;
		if (isEnabled())
			this.source.updateFilter(enableFilter, lowGain, highGain);
	}

	void updateBassBoost(final boolean enableBassBostr, final float bassBoostFrequency, final float bassBoostDbGain) throws OpenAlException {
		if (bassBoost != null) {
			if (enableBassBoost) {
				bassBoost.set(bassBoostFrequency, bassBoostDbGain, samplerate);
			} else {
				removeBasBoost();
			}
		} else {
			//no filter
			if (enableBassBoost) {
				createBassBoost();
			} else {
				//ok
			}
		}
	}

	//	public void waitForPlay() throws InterruptedException, OpenAlException {
	//		if (isEnabled()) {
	//			do {
	//				Thread.sleep(100); // should use a thread sleep NOT sleep() for a more responsive finish
	//			} while (source.isPlaying());
	//		}
	//	}

	//	public void writeWav(final String fileName) throws IOException, OpenAlcException {
	//		if (isEnabled()) {
	//			source.writeWav(fileName);
	//		} else {
	//			throw new OpenAlcException("Synth is disabled");
	//		}
	//	}

}