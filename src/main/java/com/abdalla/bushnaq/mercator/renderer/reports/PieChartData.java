package com.abdalla.bushnaq.mercator.renderer.reports;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

public class PieChartData {
	public String caption;
	public List<PieChartSectionData> pices = new ArrayList<PieChartSectionData>();

	public PieChartData(final String caption) {
		super();
		this.caption = caption;
	}

	public void add(final String name, final String absolute, final float percentage, final Color color) {
		final PieChartSectionData c = new PieChartSectionData();
		c.name = name;
		c.percentage = percentage;
		c.absolute = absolute;
		c.color = color;
		pices.add(c);
	}
}
