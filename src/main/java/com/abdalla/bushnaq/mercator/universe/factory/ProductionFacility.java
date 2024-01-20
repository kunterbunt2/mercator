package com.abdalla.bushnaq.mercator.universe.factory;

import com.abdalla.bushnaq.mercator.renderer.Renderable;
import com.abdalla.bushnaq.mercator.universe.good.Good;
import com.abdalla.bushnaq.mercator.universe.planet.Planet;
import com.abdalla.bushnaq.mercator.universe.sim.Sim;
import com.abdalla.bushnaq.mercator.universe.sim.SimList;
import com.abdalla.bushnaq.mercator.universe.sim.SimProfession;
import com.abdalla.bushnaq.mercator.util.TimeUnit;

public abstract class ProductionFacility extends Renderable {
	protected static final long TIME_NEEDED_TO_PRODUCE = 1L * TimeUnit.TICKS_PER_DAY;
	protected int currentProducedAmount;
	public SimList engineers;
	long lastProductionTime = 0L;
	public int lastYearProducedAmount;
	public Planet planet;
	public Good producedGood;
	public float productionProgress;
	public ProductionFacilityStatus status = ProductionFacilityStatus.RESEARCHING;

	public ProductionFacility(final Planet planet, final Good producedGood) {
		this.planet = planet;
		engineers = new SimList(planet);
		this.producedGood = producedGood;
		set2DRenderer(new Factory2DRenderer(this));
		//		set3DRenderer(new Factory3DRenderer(this));
		//TODO add 3d renderer
	}

	public void addEngineer(final Sim engineer) {
		engineers.add(engineer);
	}

	public abstract void advanceInTime(long currentTime);

	public void employ(final Sim sim) {
		sim.profession = SimProfession.ENGINEERING;
		sim.productionFacility = this;
		addEngineer(sim);
	}

	public abstract String getName();

	// public abstract int queryEngineersNeeded();

	public String getStatusName() {
		return status.getName();
	}

	public float queryAverageProfit() {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract float queryProfit();

	public void removeEngineer(final Sim sim) {
		engineers.remove(sim);
	}

	public void unemploy(final Sim sim) {
		removeEngineer(sim);
		sim.productionFacility = null;
		sim.profession = SimProfession.UNIMPLOYED;
	}
}
