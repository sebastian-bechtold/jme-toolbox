package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntitySelected;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.SelectableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedLeft;
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
		
		Easyvents.defaultDispatcher.addListener(ManfredJmeSpatialCompClickedLeft.class, this::onSpatialComponentClickedLeft);
	}

	
	private void onSpatialComponentClickedLeft(Object payload) {
		int id = (int) payload;

		if (id == mSelectionId) {
			return;
		}
		
		
		
		SelectableFlagComponent sfc = mEm.getComponent(id, SelectableFlagComponent.class);
		
		if (sfc != null) {
			System.out.println("Entity selected!");
			mSelectionId = id;
		}
		else {
			System.out.println("Entity deselected!");
			mSelectionId = -1;
		}

		Easyvents.defaultDispatcher.fire(ManfredEntitySelected.class, mSelectionId);
	}

}
