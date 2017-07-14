package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedLeft;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedRight;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOut;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOver;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedLeft;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOut;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOver;
import com.sebastianbechtold.manfred.EntityManager;
import com.sebastianbechtold.manfred.IManfredComponent;

public class SpatialMouseEventsSystem extends AbstractManfredJmeSystemAppState {

	public SpatialMouseEventsSystem(EntityManager em) {
		super(em);
	}

	@Override
	public void cleanup() {
		Easyvents.defaultDispatcher.removeListener(JmeSpatialClickedLeft.class, this::onLmbDownOnSpatial);
		Easyvents.defaultDispatcher.removeListener(JmeSpatialClickedRight.class, this::onRmbDownOnSpatial);
		Easyvents.defaultDispatcher.removeListener(JmeSpatialMouseOver.class, this::onSpatialMouseOver);
		Easyvents.defaultDispatcher.removeListener(JmeSpatialMouseOut.class, this::onSpatialMouseOut);
	}

	public int getJmeSceneNodeComponentForSpatial(Spatial startSpatial) {

		if (startSpatial == null) {
			return -1;
		}

		
		
		Spatial s = startSpatial;

		//############## BEGIN Go up the scene tree towards the root... ################
		while (s.getParent() != null) {
			
			// TODO 3: OPTIMIZE: Replace the loop over all JmeSceneNodeComponents with with storing 
			// the entity ID as user data in the JmeSceneNodeComponent's "root" node.

			//############## BEGIN ... and loop through all JmeSceneNodeComponents, trying to find one that references the current node ################
			for (int id : mEm.getEntitiesWith(JmeSceneNodeComponent.class)) {

				JmeSceneNodeComponent spc = mEm.getComponent(id, JmeSceneNodeComponent.class);

				if (spc.mNode == s) {

					return id;
				}
			}
			//############## END ... and loop through all JmeSceneNodeComponents, trying to find one that references the current node ################

			s = s.getParent();
		}
		//############## END Go up the scene tree towards the root... ################

		return -1;

	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		Easyvents.defaultDispatcher.addListener(JmeSpatialClickedLeft.class, this::onLmbDownOnSpatial);
		Easyvents.defaultDispatcher.addListener(JmeSpatialClickedRight.class, this::onRmbDownOnSpatial);
		Easyvents.defaultDispatcher.addListener(JmeSpatialMouseOver.class, this::onSpatialMouseOver);
		Easyvents.defaultDispatcher.addListener(JmeSpatialMouseOut.class, this::onSpatialMouseOut);

	}

	public void onLmbDownOnSpatial(Object payload) {

		System.out.println("lmb down on spatial!");
		
		Spatial s = (Spatial) payload;
		
		if (s == null) { System.out.println("start spatial is null!");}

		int id = getJmeSceneNodeComponentForSpatial(s);

		if (id == -1) {
			return;
		}

		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompClickedLeft.class, id);
	}

	public void onRmbDownOnSpatial(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);

		if (id == -1) {

			return;
		}

		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompClickedRight.class, id);
	}

	void onSpatialMouseOver(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);

		if (id == -1) {
			return;
		}

		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompMouseOver.class, id);
	}

	void onSpatialMouseOut(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);

		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompMouseOut.class, id);
	}

}
