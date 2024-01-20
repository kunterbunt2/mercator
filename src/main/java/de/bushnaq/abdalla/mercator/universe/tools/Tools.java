package de.bushnaq.abdalla.mercator.universe.tools;

public class Tools {
	static boolean enable = false;
	static SoundManager soundManager = new SoundManager();

	public static void beep() {
		if (enable)
			soundManager.play("ping.wav");
	}

	public static void error(final String format, final Object... arguments) {
		System.err.printf(format, arguments);
	}

	public static void print(final String format, final Object... arguments) {
		System.out.printf(format, arguments);
	}

	public static void speak(final String text) {
		error(text);
		if (enable) {
			soundManager.speak(text);
		}
	}

	public static void waitForSoundManager() {
		while (soundManager.isBusy()) {
		}
	}
}
