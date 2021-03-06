package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedLeft;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialClickedRight;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOut;
import com.sebastianbechtold.jmeToolbox.eventTypes.JmeSpatialMouseOver;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.JmeSceneNodeCmp;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedLeft;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOut;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOver;

public class SpatialMouseEventsSystem extends AbstractManfredJmeAppState {

	

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
			
			// TODO 4: OPTIMIZE: Replace the loop over all JmeSceneNodeComponents with with storing 
			// the entity ID as user data in the JmeSceneNodeComponent's "root" node.

			//############## BEGIN ... and loop through all JmeSceneNodeComponents, trying to find one that references the current node ################
			for (int id : mEm.getEntitiesWith(JmeSceneNodeCmp.class)) {

				JmeSceneNodeCmp spc = mEm.getComponent(id, JmeSceneNodeCmp.class);

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
	
		Spatial s = (Spatial) payload;
		
		if (s == null) { System.out.println("start spatial is null!");}

		int id = getJmeSceneNodeComponentForSpatial(s);

		JmeNodeComponentMouseEvent event = new JmeNodeComponentMouseEvent(s, id);
	
		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompClickedLeft.class, event);
	}

	public void onRmbDownOnSpatial(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);
		
		// NOTE: If no scene node component was clicked, id is -1. We still forward the event to notify other
		// systems that no component was selected, because this should still trigger deselection of the previously selected component.

		JmeNodeComponentMouseEvent event = new JmeNodeComponentMouseEvent(s, id);
		
		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompClickedRight.class, event);
	}

	void onSpatialMouseOver(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);

		if (id == -1) {
			return;
		}

		// TODO 4: Fire event only if entity has really changed
		JmeNodeComponentMouseEvent event = new JmeNodeComponentMouseEvent(s, id);
		
		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompMouseOver.class, event);
	}

	void onSpatialMouseOut(Object payload) {

		Spatial s = (Spatial) payload;

		int id = getJmeSceneNodeComponentForSpatial(s);
		
		// TODO 4: Fire event only if entity has really changed
		JmeNodeComponentMouseEvent event = new JmeNodeComponentMouseEvent(s, id);

		Easyvents.defaultDispatcher.fire(ManfredJmeSpatialCompMouseOut.class, event);
	}

}
