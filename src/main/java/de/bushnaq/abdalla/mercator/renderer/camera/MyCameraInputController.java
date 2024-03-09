/*
 * Copyright (C) 2024 Abdalla Bushnaq
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bushnaq.abdalla.mercator.renderer.camera;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.renderer.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCameraInputController extends CameraInputController {

    private final GameEngine3D gameEngine;
    private final Logger       logger = LoggerFactory.getLogger(this.getClass());
    private final Vector3      tmpV1  = new Vector3();
    private final Vector3      tmpV2  = new Vector3();
    ZoomCoordinates[] zoomFactors = {//
            new ZoomCoordinates(0, 75, 75),//
            new ZoomCoordinates(0, 150, 200),//
            new ZoomCoordinates(0, 150, 300),//
            new ZoomCoordinates(0, 300, 400),//
            new ZoomCoordinates(0, 400, 500),//
            new ZoomCoordinates(0, 500, 400),//
            new ZoomCoordinates(0, 600, 300),//
            new ZoomCoordinates(0, 1000, 200),//
            new ZoomCoordinates(0, 1500, 100),//
            new ZoomCoordinates(0, 2000, 50),//
            new ZoomCoordinates(0, 2500, 10),//
            new ZoomCoordinates(0, 3000, 0),//
            new ZoomCoordinates(0, 4000, 0),//
            new ZoomCoordinates(0, 6000, 0),//
            new ZoomCoordinates(0, 10000, 0),//
    };
    int               zoomIndex   = 3;

    public MyCameraInputController(final Camera camera, GameEngine3D gameEngine) throws Exception {
        super(camera);
        this.gameEngine = gameEngine;
        //		this.cameraInputControllerListener = cameraInputControllerListener;
        rotateButton = Buttons.MIDDLE;
        //		notifyListener(camera);
        pinchZoomFactor = 1f / Universe.WORLD_SCALE;
    }

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
                // move
                final Vector3 tx = Vector3.X.cpy().scl(-deltaX * translateUnits * camera.position.y / 500);
                final Vector3 tz = Vector3.Z.cpy().scl(deltaY * translateUnits * camera.position.y / 500);
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
            if (amount < 0 && zoomIndex < zoomFactors.length - 1) {
                zoomIndex++;
                zoomCamera();
            } else if (amount > 0 && zoomIndex > 0) {
                zoomIndex--;
                zoomCamera();
            }
//            myCamera.translate(0, -amount * pinchZoomFactor, 0);
            //todo need to find a fix for this otherwise camera rotatiosn will lead to starnge effects
//            camera.up.set(0, 1, 0);//careful, this will go wrong if camera is pointing directly at 0, 0, 0 from above.
            if (camera instanceof MovingCamera) {
                final MovingCamera myCamera = (MovingCamera) camera;
                myCamera.lookAt(myCamera.lookat);
                myCamera.setDirty(true);
            }
            //			notifyListener(myCamera);
            if (autoUpdate)
                camera.update();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    private void zoomCamera() {
        if (camera instanceof MovingCamera) {
            final MovingCamera myCamera = (MovingCamera) camera;
            camera.translate(zoomFactors[zoomIndex].x - (camera.position.x - myCamera.lookat.x), zoomFactors[zoomIndex].y - camera.position.y, zoomFactors[zoomIndex].z - (camera.position.z - myCamera.lookat.z));
//            camera.translate(0, zoomFactors[zoomIndex].y - camera.position.y, 0);
        }
    }

    class ZoomCoordinates {
        float x, y, z;

        public ZoomCoordinates(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

}
