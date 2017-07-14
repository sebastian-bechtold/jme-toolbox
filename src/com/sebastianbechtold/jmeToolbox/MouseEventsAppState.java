package com.sebastianbechtold.jmeToolbox;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeMouseButtonReleasedLeft;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeMouseButtonReleasedRight;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedLeft;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedRight;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOut;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOver;

/**
 *
 * @author sebastian
 */
public class MouseEventsAppState extends AbstractAppState {

	Application mApp = null;

	Node mMousePickRootNode = null;

	Geometry mGeometryUnderMouse = null;

	CollisionResults mCollisionResults = new CollisionResults();

	// ################# BEGIN ActionListener ##################
	private ActionListener mActionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {

			// TODO: 1 Check for spatial under mouse / yes or no?

			// NOTE: It looks like click events are always registered
			
			if (mGeometryUnderMouse != null) {
				if (keyPressed) {

					// ################## BEGIN on key pressed ###############
					switch (name) {

					case "MOUSE_BUTTON_LEFT":
						Easyvents.defaultDispatcher.fire(JmeSpatialClickedLeft.class, mGeometryUnderMouse);
						break;

					case "MOUSE_BUTTON_RIGHT":
						Easyvents.defaultDispatcher.fire(JmeSpatialClickedRight.class, mGeometryUnderMouse);
						break;
					}

					// ################## END on key pressed ###############
				} else {
					// ################## BEGIN on key released ###############

					switch (name) {
					case "MOUSE_BUTTON_LEFT":
						Easyvents.defaultDispatcher.fire(JmeMouseButtonReleasedLeft.class, mGeometryUnderMouse);
						break;

					case "MOUSE_BUTTON_RIGHT":
						Easyvents.defaultDispatcher.fire(JmeMouseButtonReleasedRight.class, mGeometryUnderMouse);
						break;
					}
					// ################## END on key released ###############
				}
			}
			else {
				System.out.println("No geometry under mouse");
			}
		}
	};
	// ################# END ActionListener ##################

	public MouseEventsAppState(Node rootNode) {

		mMousePickRootNode = rootNode;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		mApp = app;

		mApp.getInputManager().addMapping("MOUSE_BUTTON_LEFT", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		mApp.getInputManager().addMapping("MOUSE_BUTTON_RIGHT", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

		mApp.getInputManager().addListener(mActionListener, "MOUSE_BUTTON_LEFT", "MOUSE_BUTTON_RIGHT");

	}

	CollisionResults mousePick(Node node) {

		Camera cam = mApp.getCamera();

		Vector2f mouseCoords = mApp.getInputManager().getCursorPosition();

		//mApp.getInputManager().onMouseMotionEvent(evt);
		
		//System.out.println(mouseCoords.toString());
		
		// TODO: 1 Fix wrong ray direction that breaks picking at larger distances

		Ray ray = new Ray(cam.getWorldCoordinates(mouseCoords, 0),
				cam.getWorldCoordinates(mouseCoords, 1).subtractLocal(cam.getWorldCoordinates(mouseCoords, 0)).normalizeLocal());

		
		
		

		Vector3f click3d = cam.getWorldCoordinates(new Vector2f(mouseCoords.x, mouseCoords.y), 0f).clone();

		Vector3f dir = cam.getWorldCoordinates(new Vector2f(mouseCoords.x, mouseCoords.y), 1f).subtractLocal(click3d);

		// Aim the ray from the clicked spot forwards.

		ray = new Ray(click3d, dir);
		

		CollisionResults results = new CollisionResults();

		node.collideWith(ray, results);
		
		return results;
	}

	@Override
	public void update(float tpf) {

		mCollisionResults = mousePick(mMousePickRootNode);

		// TODO: 1 Understand why picking seems to be distance-limited
//		System.out.println(mCollisionResults.size());

		
		

		CollisionResult closest = mCollisionResults.getClosestCollision();
		

		Geometry closestGeom = null;
		
		if (closest != null) {
			closestGeom = closest.getGeometry();
		}

		if (closestGeom != mGeometryUnderMouse) {

			Easyvents.defaultDispatcher.fire(JmeSpatialMouseOut.class, mGeometryUnderMouse);

			mGeometryUnderMouse = closestGeom;

			Easyvents.defaultDispatcher.fire(JmeSpatialMouseOver.class, mGeometryUnderMouse);
		}

	}

}
