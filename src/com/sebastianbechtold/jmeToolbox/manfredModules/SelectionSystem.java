package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntityDeselected;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntitySelected;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.SelectableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedRight;
import com.sebastianbechtold.manfred.IManfredComponent;

public class SelectionSystem extends AbstractManfredJmeAppState {

	private int mSelectionId = -1;

	
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

		mEm.addComponentReplacedListener(SelectableFlagComponent.class, this::onSelectableComponentReplaced);

	}

	private void onSelectableComponentReplaced(int id, IManfredComponent prevComp) {

		SelectableFlagComponent sfc = mEm.getComponent(id, SelectableFlagComponent.class);
		
		if (sfc == null) {
			Easyvents.defaultDispatcher.fire(ManfredEntityDeselected.class, id);
			select(-1);
		}
	}

	private void onSpatialComponentClickedLeft(Object payload) {
		JmeNodeComponentMouseEvent event = (JmeNodeComponentMouseEvent) payload;

		int id = event.mEntityId;

		if (id == mSelectionId) {
			return;
		}

		select(id);
	}

	public void select(int id) {
		// ########## BEGIN Fire deselection event for previously selected entity ###########
		SelectableFlagComponent prevSelected = mEm.getComponent(mSelectionId, SelectableFlagComponent.class);

		if (prevSelected != null) {
			Easyvents.defaultDispatcher.fire(ManfredEntityDeselected.class, mSelectionId);
		}
		// ########## END Fire deselection event for previously selected entity ###########

		SelectableFlagComponent sfc = mEm.getComponent(id, SelectableFlagComponent.class);

		if (sfc != null) {
			// System.out.println("Entity selected!");
			mSelectionId = id;
		} else {
			// System.out.println("Entity deselected!");
			mSelectionId = -1;
		}

		// Fire selection event for newly selected entity:
		Easyvents.defaultDispatcher.fire(ManfredEntitySelected.class, mSelectionId);
	}

}
