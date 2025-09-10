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

package de.bushnaq.abdalla.mercator.engine.camera;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.bushnaq.abdalla.engine.camera.MovingCamera;
import de.bushnaq.abdalla.mercator.engine.GameEngine3D;
import de.bushnaq.abdalla.mercator.universe.Universe;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomingCameraInputController extends CameraInputController {

    private final GameEngine3D       gameEngine;
    private final Logger             logger          = LoggerFactory.getLogger(this.getClass());
    private       float              progress        = 0;
    @Getter
    @Setter
    private       int                targetZoomIndex = 3;
    private final Vector3            tmpV1           = new Vector3();
    private final Vector3            tmpV2           = new Vector3();
    @Getter
    private final CameraProperties[] zoomFactors     = {//
            new CameraProperties(10, 10, 30f, 0, 300f),//zoomIndex=0
            new CameraProperties(15, 20, 40f, 0, 2000f),//zoomIndex=1
            new CameraProperties(25, 30, 46f, 0, 8000f),//zoomIndex=2
            new CameraProperties(40, 75, 60f, 0, 8000f),//zoomIndex=3
            new CameraProperties(100, 100, 46f, 0, 8000f),//zoomIndex=4
            new CameraProperties(200, 200, 46f, 0, 8000f),//zoomIndex=5
            new CameraProperties(500, 200, 60f, 0, 8000f),//zoomIndex=5
            new CameraProperties(1000, 400, 60f, 0, 8000f),//zoomIndex=5
            new CameraProperties(4000, 0, 60f, 0, 8000f),//zoomIndex=6
            new CameraProperties(10000, 0, 60f, 0, 20000f),//zoomIndex=7
    };
    @Getter
    @Setter
    private       int                zoomIndex       = 5;

    public ZoomingCameraInputController(final Camera camera, GameEngine3D gameEngine) throws Exception {
        super(camera);
        this.gameEngine = gameEngine;
        rotateButton    = Buttons.MIDDLE;
        pinchZoomFactor = 1f / Universe.WORLD_SCALE;
    }

    public int getCameraZoomThreshold() {
        return 7;
    }

    @Override
    protected boolean process(final float deltaX, final float deltaY, final int button) {
        try {
            final MovingCamera movingCamera = (MovingCamera) camera;
            if (button == rotateButton) {
                tmpV1.set(movingCamera.direction).crs(movingCamera.up)/*.y = 0f*/;
                if (gameEngine.renderEngine.isDebugMode()) {
                    movingCamera.rotateAround(movingCamera.lookat, tmpV1.nor(), deltaY * rotateAngle);
                }
                movingCamera.rotateAround(movingCamera.lookat, Vector3.Y, deltaX * -rotateAngle);
                movingCamera.setDirty(true);
                //				notifyListener(movingCamera);
            } else if (button == translateButton) {
                // move
                Vector2 lookAtVectorXZ = new Vector2(camera.position.x, camera.position.z);
                {
                    lookAtVectorXZ.sub(movingCamera.lookat.x, movingCamera.lookat.z);
                    lookAtVectorXZ.nor();
                }
                Vector2 perpendicularXZ = new Vector2(lookAtVectorXZ.y, -lookAtVectorXZ.x);
//                logger.info(String.format("lookAtVectorXZ=%f %f", lookAtVectorXZ.x, lookAtVectorXZ.y));
//                logger.info(String.format("perpendicularXZ=%f %f", perpendicularXZ.x, perpendicularXZ.y));
                final float tx = lookAtVectorXZ.x * (deltaY * translateUnits * camera.position.y / 500) + perpendicularXZ.x * (-deltaX * translateUnits * camera.position.y / 500);//towards lookat
                final float tz = lookAtVectorXZ.y * (deltaY * translateUnits * camera.position.y / 500) + perpendicularXZ.y * (-deltaX * translateUnits * camera.position.y / 500);//90 degrees from that
//                logger.info(String.format("t=%f %f", tx, tz));
                movingCamera.translate(tx, 0, tz);
                movingCamera.lookat.x += tx;
                movingCamera.lookat.z += tz;
                if (translateTarget)
                    target.add(tmpV1).add(tmpV2);
                movingCamera.setDirty(true);
            } else if (button == forwardButton) {
                movingCamera.translate(tmpV1.set(movingCamera.direction).scl(deltaY * translateUnits));
                if (forwardTarget)
                    target.add(tmpV1);
                movingCamera.setDirty(true);
            } else {
                return false;
            }
            if (autoUpdate)
                movingCamera.update();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void update() {
        update(false);
    }

    public void update(boolean force) {
        if (targetZoomIndex != zoomIndex || force) {
            if (camera instanceof MovingCamera movingCamera) {
                Vector2 distanceXZ = new Vector2(camera.position.x, camera.position.z);//current camera xy position
                {
                    distanceXZ.sub(movingCamera.lookat.x, movingCamera.lookat.z);//current xy distance of camera to look-at
                    if (zoomFactors[zoomIndex].distanceXZ() != 0f) {
                        float factor = zoomFactors[zoomIndex].distanceXZ() / distanceXZ.len();//old-target-distance / old-actual-distance factor
                        distanceXZ.scl(factor);
                    }
                }
                Vector2 targetDistanceXZ = new Vector2(camera.position.x, camera.position.z);
                {
                    targetDistanceXZ.sub(movingCamera.lookat.x, movingCamera.lookat.z);
                    if (zoomFactors[targetZoomIndex].distanceXZ() != 0f) {
                        float factor = zoomFactors[targetZoomIndex].distanceXZ() / targetDistanceXZ.len();
                        targetDistanceXZ.scl(factor);
                    }
                }
                float distanceY       = zoomFactors[zoomIndex].lookatY();
                float targetDistanceY = zoomFactors[targetZoomIndex].lookatY();


//                logger.info(String.format("distanceXZ.x=%f distanceXZ.y=%f ", distanceXZ.x, distanceXZ.y));
//                logger.info(String.format("movingCamera.lookat.x + distanceXZ.x=%f movingCamera.lookat.z + distanceXZ.y=%f ", movingCamera.lookat.x + distanceXZ.x, movingCamera.lookat.z + distanceXZ.y));
                float cameraY     = zoomFactors[zoomIndex].cameraY() + (zoomFactors[targetZoomIndex].cameraY() - zoomFactors[zoomIndex].cameraY()) * progress;
                float cameraX     = distanceXZ.x + (targetDistanceXZ.x - distanceXZ.x) * progress;
                float cameraZ     = distanceXZ.y + (targetDistanceXZ.y - distanceXZ.y) * progress;
                float farY        = zoomFactors[zoomIndex].farClippingDistance() + (zoomFactors[targetZoomIndex].farClippingDistance() - zoomFactors[zoomIndex].farClippingDistance()) * progress;
                float lookatY     = distanceY + (targetDistanceY - distanceY) * progress;
                float fieldOfView = zoomFactors[zoomIndex].fieldOfView() + (zoomFactors[targetZoomIndex].fieldOfView() - zoomFactors[zoomIndex].fieldOfView()) * progress;

                float x = cameraX - (camera.position.x - movingCamera.lookat.x);
                float y = cameraY - camera.position.y;
                float z = cameraZ - (camera.position.z - movingCamera.lookat.z);
//                logger.info(String.format("camera update zoomIndex=%d targetZoomIndex=%d x=%f y=%f z=%f", zoomIndex, targetZoomIndex, x, y, z));
                camera.translate(x, y, z);
                camera.far = farY;
                movingCamera.lookAt(movingCamera.lookat.x, lookatY, movingCamera.lookat.z);
                movingCamera.fieldOfView = fieldOfView;
                camera.update();
                movingCamera.setDirty(true);
//                {
//                    Vector3 max = new Vector3();
//                    Vector3 min = new Vector3();
//                    gameEngine.getRenderEngine().getSceneBox().getMax(max);
//                    gameEngine.getRenderEngine().getSceneBox().getMin(min);
//                    max.set(cameraY, cameraY, cameraY);
//                    gameEngine.getRenderEngine().getSceneBox().set(min, max);
//                }
//                logger.info(String.format("%f %f %f  %f %f %f", movingCamera.position.x, movingCamera.position.y, movingCamera.position.z, movingCamera.lookat.x, movingCamera.lookat.y, movingCamera.lookat.z));
//                logger.info("");
                //not if we are forcing and actually both zoom levels are the same, otherwise process will never be updated again
                if (!force || targetZoomIndex != zoomIndex)
                    progress += 0.03f;
                if (progress >= 1.0f) {
                    progress  = 0;
                    zoomIndex = targetZoomIndex;
                }
            }
        }
    }

//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        if (button == Buttons.RIGHT) {
//            Ray     pickRay      = camera.getPickRay(screenX, screenY);
//            Plane   plane        = new Plane(new Vector3(0, 1, 0), Vector3.Zero);
//            Vector3 intersection = new Vector3();
//            if (Intersector.intersectRayPlane(pickRay, plane, intersection)) {
//                logger.info(String.format("intersection=%f %f %f", intersection.x, intersection.y, intersection.z));
//                final MovingCamera movingCamera = (MovingCamera) camera;
//                // The ray has hit the plane, intersection is the point it hit
//                Vector2 moveXZ = new Vector2(intersection.x, intersection.z);
//                moveXZ.sub(camera.position.x, camera.position.z);
//
//                logger.info(String.format("moveXZ=%f %f", moveXZ.x, moveXZ.y));
//                movingCamera.translate(moveXZ.x, 0, moveXZ.y);
//                movingCamera.lookat.x += moveXZ.x;
//                movingCamera.lookat.z += moveXZ.y;
//                if (translateTarget)
//                    target.add(tmpV1).add(tmpV2);
//                movingCamera.setDirty(true);
//                movingCamera.update();
//                return true;
//            } else {
//                // Not hit
//            }
//        }
//        return false;
//    }

    @Override
    public boolean zoom(final float amount) {
        try {
            if (!alwaysScroll && activateKey != 0 && !activatePressed)
                return false;
            if (amount < 0 && zoomIndex < zoomFactors.length - 1 && progress == 0f) {
                targetZoomIndex = zoomIndex + 1;
            } else if (amount > 0 && zoomIndex > 0 && progress == 0f) {
                targetZoomIndex = zoomIndex - 1;
            }
            //todo need to find a fix for this otherwise camera rotation will lead to strange effects
//            camera.up.set(0, 1, 0);//careful, this will go wrong if camera is pointing directly at 0, 0, 0 from above.
            if (camera instanceof MovingCamera myCamera) {
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

//    private void zoomCamera() {
//        if (camera instanceof MovingCamera movingCamera) {
//            //create a vector from lookat to current camera position
//            Vector3 distanceXZ = new Vector3(movingCamera.lookat);
//            distanceXZ.sub(camera.position);
//            float factor = zoomFactors[zoomIndex].distanceXZ / distanceXZ.len();
//            distanceXZ.scl(factor);
//
//            float cameraY = zoomFactors[targetZoomIndex].y;
//            float cameraX = distanceXZ.x;
//            float cameraZ = distanceXZ.z;
//
//            float x = cameraX - (camera.position.x - movingCamera.lookat.x);
//            float y = cameraY - camera.position.y;
//            float z = cameraZ - (camera.position.z - movingCamera.lookat.z);
//            camera.translate(x, y, z);
//            progress += 0.1f;
//            if (progress >= 1.0f) {
//                progress  = 0;
//                zoomIndex = targetZoomIndex;
//            }
//        }
//    }

}
