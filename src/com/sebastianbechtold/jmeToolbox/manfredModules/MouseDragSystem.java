package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeMouseButtonReleasedRight;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.DraggableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.Vec3PosCmp;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;

public class MouseDragSystem extends AbstractManfredJmeAppState {

	public Node mTerrainNode = null;
	Camera mCamera;

	int mDraggedEntity = -1;

	Vector3f mCenterToGrabOffset = null;

	Vector2f mDiff = null;

	public MouseDragSystem(Node pickRoot, Camera camera) {

		mCamera = camera;
		mTerrainNode = pickRoot;

	}

	AnalogListener mAnalogListener = new AnalogListener() {

		@Override
		public void onAnalog(String name, float value, float tpf) {

			if ("mouseMoveX".equals(name) || "mouseMoveY".equals(name)) {

				if (mDraggedEntity == -1) {
					return;
				}

				Vec3PosCmp pc = mEm.getComponent(mDraggedEntity, Vec3PosCmp.class);

				if (pc == null) {
					return;
				}

				Camera cam = mCamera;

				Vector2f shiftedMouseCoords = new Vector2f(mInputManager.getCursorPosition()).add(mDiff);

				Vector3f mouseRayOrigin = cam.getWorldCoordinates(shiftedMouseCoords, 0);

				Vector3f mouseRayDir = cam.getWorldCoordinates(shiftedMouseCoords, 1).subtractLocal(cam.getWorldCoordinates(shiftedMouseCoords, 0))
						.normalizeLocal();

				Ray baseRay = new Ray(mouseRayOrigin, mouseRayDir);

				CollisionResults results = new CollisionResults();

				mTerrainNode.collideWith(baseRay, results);

				CollisionResult closest = results.getClosestCollision();

				if (closest != null) {

					Vector3f newObjectPos = closest.getContactPoint();

					// Update PositionComponent:
					mEm.setComponent(mDraggedEntity, new Vec3PosCmp(StaticUtils.jme2am_vector(newObjectPos)));

					// ############## BEGIN Update 2D drag offset #################
					
					Vector2f objectScreenPos2D = shiftedMouseCoords;

					Vector3f grabScreenPos3d = mCamera.getScreenCoordinates(newObjectPos.add(mCenterToGrabOffset));
					Vector2f grabScreenPos2d = new Vector2f(grabScreenPos3d.getX(), grabScreenPos3d.getY());

					// TODO 4: Fix object mouse dragging
					//mDiff = objectScreenPos2D.subtract(grabScreenPos2d);
					// ############## END Update 2D drag offset #################

				}

			}
		}
	};

	@Override
	public void cleanup() {

		Easyvents.defaultDispatcher.removeListener(ManfredJmeSpatialCompClickedRight.class, this::onComponentRightClicked);
		Easyvents.defaultDispatcher.removeListener(JmeMouseButtonReleasedRight.class, this::onRmbUp);

	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		Easyvents.defaultDispatcher.addListener(ManfredJmeSpatialCompClickedRight.class, this::onComponentRightClicked);
		Easyvents.defaultDispatcher.addListener(JmeMouseButtonReleasedRight.class, this::onRmbUp);

		mInputManager.addMapping("mouseMoveX", new MouseAxisTrigger(MouseInput.AXIS_X, false), new MouseAxisTrigger(MouseInput.AXIS_X, true));
		mInputManager.addMapping("mouseMoveY", new MouseAxisTrigger(MouseInput.AXIS_Y, false), new MouseAxisTrigger(MouseInput.AXIS_Y, true));

		mInputManager.addListener(mAnalogListener, "mouseMoveX", "mouseMoveY");

	}

	protected CollisionResults mousePick(Spatial node) {

		Camera cam = mCamera;

		Vector2f mouseCoords = new Vector2f(mInputManager.getCursorPosition());

		Vector3f rayOrigin = cam.getWorldCoordinates(mouseCoords, 0);
		Vector3f rayDir = cam.getWorldCoordinates(mouseCoords, 1).subtractLocal(cam.getWorldCoordinates(mouseCoords, 0)).normalizeLocal();

		Ray ray = new Ray(rayOrigin, rayDir);

		CollisionResults results = new CollisionResults();

		node.collideWith(ray, results);

		return results;
	}

	// ######################## BEGIN Event Handlers ########################
	public void onRmbUp(Object payload) {
		mDraggedEntity = -1;
	}

	public void onComponentRightClicked(Object payload) {

		JmeNodeComponentMouseEvent event = (JmeNodeComponentMouseEvent) payload;
		// TODO 3: Include collision results in JmeNodeComponentMouseEvent
		int id = event.mEntityId;

		if (id == -1) {
			return;
		}

		DraggableFlagComponent dc = mEm.getComponent(id, DraggableFlagComponent.class);
		Vec3PosCmp pc = mEm.getComponent(id, Vec3PosCmp.class);

		if (dc == null || pc == null) {
			return;
		}

		CollisionResults results_obj = mousePick(event.mSpatial);
		CollisionResult closest_obj = results_obj.getClosestCollision();

		if (closest_obj != null) {

			Vector3f contact_obj = closest_obj.getContactPoint();
			Vector3f objCenter = StaticUtils.am2jme_vector(pc.getPos());

			mCenterToGrabOffset = contact_obj.subtract(objCenter);

			Vector3f grabScreenPos3D = mCamera.getScreenCoordinates(contact_obj);
			Vector2f grabScreenPos2d = new Vector2f(grabScreenPos3D.getX(), grabScreenPos3D.getY());

			Vector3f objectScreenPos3D = mCamera.getScreenCoordinates(objCenter);
			Vector2f objectScreenPos2D = new Vector2f(objectScreenPos3D.getX(), objectScreenPos3D.getY());

			mDiff = objectScreenPos2D.subtract(grabScreenPos2d);
		}

		mDraggedEntity = id;
	}
}
