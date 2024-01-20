package de.bushnaq.abdalla.mercator.renderer.reports;

import java.util.ArrayList;
import java.util.List;

import de.bushnaq.abdalla.mercator.util.TimeUnit;
import com.badlogic.gdx.graphics.Color;

public class GraphChartData {
	public static final int CREDIT_HISTORY_SIZE = 250;
	public String caption;
	public Color color;
	List<GraphChartPointData> items = new ArrayList<GraphChartPointData>();
	int startTime = 0;
	int zoom = 100;

	public GraphChartData(final String caption, final Color color) {
		super();
		this.caption = caption;
		this.color = color;
	}

	public void add(final long currentTime) {
		add(currentTime, 1);
	}

	public void add(final long time, final int dead) {
		final GraphChartPointData graphChartPointData = get(time / (TimeUnit.TICKS_PER_DAY * zoom));
		graphChartPointData.absolute += dead;
	}

	private GraphChartPointData get(final long currentTime) {
		GraphChartPointData item = null;
		if (currentTime >= items.size() + startTime) {
			item = new GraphChartPointData();
			items.add(item);
			while (items.size() > CREDIT_HISTORY_SIZE) {
				items.remove(0);
				startTime++;
			}
		} else {
			item = items.get((int) currentTime - startTime);
		}
		return item;
	}
}
