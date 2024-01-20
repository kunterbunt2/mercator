package de.bushnaq.abdalla.mercator.renderer.camera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class MyCameraInputController extends CameraInputController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//	private final CameraInputControllerListener cameraInputControllerListener;
	private final Vector3 tmpV1 = new Vector3();
	private final Vector3 tmpV2 = new Vector3();

	public MyCameraInputController(final Camera camera/*, final CameraInputControllerListener cameraInputControllerListener*/) throws Exception {
		super(camera);
		//		this.cameraInputControllerListener = cameraInputControllerListener;
		rotateButton = Buttons.MIDDLE;
		//		notifyListener(camera);
	}

	//	public void notifyListener(final Camera camera) throws Exception {
	//		cameraInputControllerListener.setPositionDirectionUp(camera.position, camera.direction, camera.up);
	//	}

	@Override
	protected boolean process(final float deltaX, final float deltaY, final int button) {
		try {
			final MovingCamera myCamera = (MovingCamera) camera;
			if (button == rotateButton) {
				tmpV1.set(myCamera.direction).crs(myCamera.up)/*.y = 0f*/;
				myCamera.rotateAround(myCamera.lookat, tmpV1.nor(), deltaY * rotateAngle);
				myCamera.rotateAround(myCamera.lookat, Vector3.Y, deltaX * -rotateAngle);
				myCamera.setDirty(true);
				//				notifyListener(myCamera);
			} else if (button == translateButton) {
				final Vector3 tx = Vector3.X.cpy().scl(-deltaX * translateUnits);
				final Vector3 tz = Vector3.Z.cpy().scl(deltaY * translateUnits);
				//			System.out.println(String.format("x=%f %f y=%f %f %f", deltaX, tx.x, deltaY, tz.z, translateUnits));
				myCamera.translate(tx);
				myCamera.translate(tz);
				myCamera.lookat.x += tx.x;
				myCamera.lookat.z += tz.z;
				if (translateTarget)
					target.add(tmpV1).add(tmpV2);
				myCamera.setDirty(true);
				//				notifyListener(myCamera);
			} else if (button == forwardButton) {
				myCamera.translate(tmpV1.set(myCamera.direction).scl(deltaY * translateUnits));
				if (forwardTarget)
					target.add(tmpV1);
				myCamera.setDirty(true);
				//				notifyListener(myCamera);
			} else {
				return false;
			}
			if (autoUpdate)
				myCamera.update();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean zoom(final float amount) {
		try {
			if (!alwaysScroll && activateKey != 0 && !activatePressed)
				return false;
			final MovingCamera myCamera = (MovingCamera) camera;
			myCamera.translate(0, -amount, 0);
			myCamera.setDirty(true);
			//			notifyListener(myCamera);
			if (autoUpdate)
				camera.update();
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

}
