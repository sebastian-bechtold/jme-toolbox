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
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeMouseButtonReleasedRight;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.DraggableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.Vec3PosComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;
import com.sebastianbechtold.manfred.EntityManager;

public class MouseDragSystem extends AbstractManfredJmeSystemAppState {

	public Node mTerrainNode = null;
	Camera mCamera;

	Vector3f mMouseOffset = null;
	int mDraggedEntity = -1;

	
	public MouseDragSystem(EntityManager em, Node pickRoot, Camera camera) {
		super(em);
		
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

		Vec3PosComponent pc = mEm.getComponent(mDraggedEntity, Vec3PosComponent.class);

		if (pc == null) {
			return;
		}

		CollisionResults results = mousePick(mTerrainNode);
		CollisionResult closest = results.getClosestCollision();

		if (closest != null) {

			Vector3f contact3d = closest.getContactPoint();

			Vector3f intersect = StaticUtils.toFlat(contact3d.subtract(mMouseOffset));

			if (!intersect.equals(pc.getPos())) {

				// Update PositionComponent:				 										
				mEm.setComponent(mDraggedEntity, new Vec3PosComponent(StaticUtils.jme2am_vector(intersect)));
			}
		}
	}

	protected CollisionResults mousePick(Node node) {

		Camera cam = mCamera;

		Vector2f mouseCoords = new Vector2f(mInputManager.getCursorPosition());

		Ray ray = new Ray(cam.getWorldCoordinates(mouseCoords, 0),
				cam.getWorldCoordinates(mouseCoords, 1).subtractLocal(cam.getWorldCoordinates(mouseCoords, 0)).normalizeLocal());

		CollisionResults results = new CollisionResults();

		node.collideWith(ray, results);

		return results;
	}

	
	// ######################## BEGIN Event Handlers ########################
	public void onRmbUp(Object payload) {
		mDraggedEntity = -1;
	}

	
	
	public void onComponentRightClicked(Object payload) {

		int id = (int) payload;

		if (id == -1) {
			return;
		}

		DraggableFlagComponent dc = mEm.getComponent(id, DraggableFlagComponent.class);
		Vec3PosComponent pc = mEm.getComponent(id, Vec3PosComponent.class);

		if (dc == null || pc == null) {
			return;
		}

		CollisionResults results2 = mousePick(mTerrainNode);
		CollisionResult closest2 = results2.getClosestCollision();

		if (closest2 != null) {

			Vector3f contactTerrain3d = closest2.getContactPoint();
			mMouseOffset = contactTerrain3d.subtract(StaticUtils.am2jme_vector(pc.getPos()));
		}

		mDraggedEntity = id;
	}
}
