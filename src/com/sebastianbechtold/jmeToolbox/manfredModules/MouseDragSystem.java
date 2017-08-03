package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
	}

	@Override
	public void update(float tpf) {

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

		Vector3f mouseRayDir = cam.getWorldCoordinates(shiftedMouseCoords, 1).subtractLocal(cam.getWorldCoordinates(shiftedMouseCoords, 0)).normalizeLocal();

		Ray baseRay = new Ray(mouseRayOrigin, mouseRayDir);

		CollisionResults results = new CollisionResults();

		mTerrainNode.collideWith(baseRay, results);

		CollisionResult closest = results.getClosestCollision();

		if (closest != null) {

			Vector3f hitPoint = closest.getContactPoint();

			// Update PositionComponent:
			mEm.setComponent(mDraggedEntity, new Vec3PosCmp(StaticUtils.jme2am_vector(hitPoint)));

			//############## BEGIN Update 2D drag offset #################
			Vector3f foo = mCamera.getScreenCoordinates(hitPoint);
			Vector2f obsc = new Vector2f(foo.getX(), foo.getY());

			Vector3f grabScreenPos = mCamera.getScreenCoordinates(hitPoint.add(mCenterToGrabOffset));

			Vector2f grabScreenPos2d = new Vector2f(grabScreenPos.getX(), grabScreenPos.getY());

			mDiff = obsc.subtract(grabScreenPos2d);
			//############## END Update 2D drag offset #################
		}

	}

	protected CollisionResults mousePick(Spatial node) {

		Camera cam = mCamera;

		Vector2f mouseCoords = new Vector2f(mInputManager.getCursorPosition());

		Vector3f rayOrigin = cam.getWorldCoordinates(mouseCoords, 0);
		Vector3f rayDir = cam.getWorldCoordinates(mouseCoords, 1).subtractLocal(cam.getWorldCoordinates(mouseCoords, 0)).normalizeLocal();

		// Ray ray = new Ray( cam.getWorldCoordinates(mouseCoords, 0), cam.getWorldCoordinates(mouseCoords,
		// 1).subtractLocal(cam.getWorldCoordinates(mouseCoords, 0)).normalizeLocal());

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
		int id = event.mEntityId;

		if (id == -1) {
			return;
		}

		DraggableFlagComponent dc = mEm.getComponent(id, DraggableFlagComponent.class);
		Vec3PosCmp pc = mEm.getComponent(id, Vec3PosCmp.class);

		if (dc == null || pc == null) {
			return;
		}

		CollisionResults results_terrain = mousePick(mTerrainNode);
		CollisionResult closest_terrain = results_terrain.getClosestCollision();

		CollisionResults results_obj = mousePick(event.mSpatial);
		CollisionResult closest_obj = results_obj.getClosestCollision();

		if (closest_terrain != null) {

			Vector3f contact_obj = closest_obj.getContactPoint();

			Vector3f objCenter = StaticUtils.am2jme_vector(pc.getPos());

			mCenterToGrabOffset = contact_obj.subtract(objCenter);

			Vector3f foo = mCamera.getScreenCoordinates(objCenter);
			Vector2f obsc = new Vector2f(foo.getX(), foo.getY());

			Vector3f grabScreenPos = mCamera.getScreenCoordinates(objCenter.add(mCenterToGrabOffset));

			Vector2f grabScreenPos2d = new Vector2f(grabScreenPos.getX(), grabScreenPos.getY());

			mDiff = obsc.subtract(grabScreenPos2d);

		}

		mDraggedEntity = id;
	}
}
