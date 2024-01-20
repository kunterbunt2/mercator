package com.abdalla.bushnaq.mercator.renderer;

public interface ScreenListener /*extends ApplicationListener */ {
	//	public Canvas getCanvas();

	public void setCamera(float x, float z, boolean setDirty) throws Exception;

	public void setShowGood(ShowGood name);

}
