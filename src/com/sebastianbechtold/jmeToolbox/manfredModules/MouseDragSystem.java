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
import com.sebastianbechtold.jmeToolbox.manfredModules.components.Vec3PosCmp;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;

public class MouseDragSystem extends AbstractManfredJmeAppState {

	public Node mTerrainNode = null;
	Camera mCamera;

	Vector3f mMouseOffset = null;
	int mDraggedEntity = -1;

	
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

		CollisionResults results = mousePick(mTerrainNode);
		CollisionResult closest = results.getClosestCollision();

		if (closest != null) {

			Vector3f contact3d = closest.getContactPoint();

			Vector3f intersect = StaticUtils.toFlat(contact3d.subtract(mMouseOffset));

			if (!intersect.equals(pc.getPos())) {

				// Update PositionComponent:			
				Vec3PosCmp vec3Pos = new Vec3PosCmp(StaticUtils.jme2am_vector(intersect));
				vec3Pos.setPersistance(pc.doPersist());
				
				mEm.setComponent(mDraggedEntity, vec3Pos);
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

		CollisionResults results2 = mousePick(mTerrainNode);
		CollisionResult closest2 = results2.getClosestCollision();

		if (closest2 != null) {

			Vector3f contactTerrain3d = closest2.getContactPoint();
			mMouseOffset = contactTerrain3d.subtract(StaticUtils.am2jme_vector(pc.getPos()));
		}

		mDraggedEntity = id;
	}
}
