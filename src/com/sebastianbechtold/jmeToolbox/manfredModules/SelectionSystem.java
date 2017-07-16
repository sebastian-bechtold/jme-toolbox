package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntityDeselected;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntitySelected;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.SelectableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedLeft;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;
import com.sebastianbechtold.manfred.EntityManager;

public class SelectionSystem extends AbstractManfredJmeSystemAppState {

	private int mSelectionId = -1;
	
	public SelectionSystem(EntityManager em) {
		super(em);
		
	}

	
	@Override
	public void cleanup() {
	
	}
	
	public int getSelectionId() {
		return mSelectionId;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		Easyvents.defaultDispatcher.addListener(ManfredJmeSpatialCompClickedRight.class, this::onSpatialComponentClickedLeft);
	}

	
	private void onSpatialComponentClickedLeft(Object payload) {
		int id = (int) payload;

		if (id == mSelectionId) {
			return;
		}
		
		//########## BEGIN Fire deselection event for previously selected entity ###########
		SelectableFlagComponent prevSelected = mEm.getComponent(mSelectionId, SelectableFlagComponent.class);
		
		if (prevSelected != null) {
			Easyvents.defaultDispatcher.fire(ManfredEntityDeselected.class, mSelectionId);
		}
		//########## END Fire deselection event for previously selected entity ###########
		
		
		SelectableFlagComponent sfc = mEm.getComponent(id, SelectableFlagComponent.class);
		
		if (sfc != null) {
			//System.out.println("Entity selected!");
			mSelectionId = id;
		}
		else {
			//System.out.println("Entity deselected!");
			mSelectionId = -1;
		}

		// Fire selection event for newly selected entity:
		Easyvents.defaultDispatcher.fire(ManfredEntitySelected.class, mSelectionId);
	}

}
