package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntitySelected;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.RotateableFlagComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompClickedLeft;
import com.sebastianbechtold.manfred.EntityManager;

public class KeyboardRotateSystem extends AbstractManfredJmeAppState {

	static final float mMaxRotSpeed = 0.05f;
	static final float mRotSpeedInc = 1.1f;
	float mRotSpeed = 0.01f;
	int mRotDir = 0;

	RotateableFlagComponent rcp = null;
	RotationComponent rc = null;

	
	int mSelectedComponentId = -1;
	
	public KeyboardRotateSystem(EntityManager em) {
		super(em);

	}

	// ################# BEGIN ActionListener ##################
	private ActionListener mActionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {
			//System.out.println("key pressed");
			mKeyPressed.put(name, keyPressed);
		}
	};
	// ################# END ActionListener ##################

	@Override
	public void cleanup() {
		com.sebastianbechtold.easyvents.Easyvents.defaultDispatcher.removeListener(ManfredEntitySelected.class, this::onEntitySelected);
		
		mInputManager.removeListener(mActionListener);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		
		com.sebastianbechtold.easyvents.Easyvents.defaultDispatcher.addListener(ManfredEntitySelected.class, this::onEntitySelected);
		
		mInputManager.addMapping("KEY_LEFT", new KeyTrigger(KeyInput.KEY_LEFT));
		mInputManager.addMapping("KEY_RIGHT", new KeyTrigger(KeyInput.KEY_RIGHT));

		mInputManager.addListener(mActionListener, "KEY_LEFT", "KEY_RIGHT");

	}

	@Override
	public void update(float tpf) {
		// ################ BEGIN Update RotateComponents ###################

		 
		
		RotateableFlagComponent rcp = mEm.getComponent(mSelectedComponentId, RotateableFlagComponent.class);
		RotationComponent rc = mEm.getComponent(mSelectedComponentId, RotationComponent.class);

		if (rcp != null && rc != null) {

			if (keyDown("KEY_RIGHT")) {

				mRotDir = -1;

			} else if (keyDown("KEY_LEFT")) {

				mRotDir = 1;
			} else {
				mRotDir = 0;
			}

			if (mRotDir != 0) {
				System.out.println("rot dir != 0");

				double angle = rc.getAngle() + mRotSpeed * mRotDir;

				// #################### BEGIN Update RotationComponent ####################
				rc.setAngle(angle);
				mEm.setComponent(mSelectedComponentId,rc);
								
				// #################### END Update RotationComponent ####################

				if (mRotSpeed < mMaxRotSpeed) {
					mRotSpeed *= mRotSpeedInc;
				}
			} else {
				mRotSpeed = 0.001f;
				mRotDir = 0;
			}

		}
		// ################ END Update RotateComponents ###################

	}


	private void onEntitySelected(Object payload) {
		int id = (int) payload;
		
		
		mSelectedComponentId = id;
	}
	
	protected boolean keyDown(String key) {
		
		return mKeyPressed.containsKey(key) && mKeyPressed.get(key);
	}
}
