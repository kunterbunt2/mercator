package com.abdalla.bushnaq.mercator.util;

public class TimeStatistic {
	public long averageTime = 0;
	private long count = 0;
	public long lastTime = 0;
	public long maxTime = 0;
	private boolean measuring = false;
	public long minTime = 0;
	private long sum = 0;
	public long time = 0;

	public TimeStatistic() throws Exception {
		start();
	}

	public long getTime() {
		return System.currentTimeMillis() - time;
	}

	public void pause() throws Exception {
		if (measuring) {
			time = System.currentTimeMillis() - time;
			measuring = false;
		} else {
			throw new Exception("Cannot pause measurment. TimeStatistic not in measuring mode.");
		}
	}

	public void restart() throws Exception {
		stop();
		start();
	}

	public void resume() throws Exception {
		if (!measuring) {
			measuring = true;
			time = System.currentTimeMillis() - time;
		} else {
			throw new Exception("Cannot resume measurment. TimeStatistic is already in measuring mode.");
		}
	}

	public void start() throws Exception {
		if (!measuring) {
			measuring = true;
			time = System.currentTimeMillis();
		} else {
			throw new Exception("Cannot start measurment. TimeStatistic is already in measuring mode.");
		}
	}

	public void stop() throws Exception {
		if (measuring) {
			time = System.currentTimeMillis() - time;
			lastTime = time;
			sum += lastTime;
			count++;
			minTime = Math.min(lastTime, minTime);
			maxTime = Math.max(lastTime, maxTime);
			averageTime = sum / count;
			measuring = false;
		} else {
			throw new Exception("Cannot stop measurment. TimeStatistic not in measuring mode.");
		}
	}
}
