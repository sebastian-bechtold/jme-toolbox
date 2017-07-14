package com.sebastianbechtold.jmeToolbox.manfredModules;

import java.util.HashMap;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.sebastianbechtold.manfred.EntityManager;

/**
 *
 * @author sebastian
 */
public abstract class AbstractManfredJmeSystemAppState extends AbstractAppState {

	public static final Vector3f DIR_UP = new Vector3f(0, 1, 0);

	protected HashMap<String, Boolean> mKeyPressed = new HashMap<String, Boolean>();
	
	protected InputManager mInputManager;
	protected AssetManager mAssetManager;

	protected EntityManager mEm = null;

	public AbstractManfredJmeSystemAppState(EntityManager em) {
		mEm = em;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		

		mAssetManager = app.getAssetManager();
		mInputManager = app.getInputManager();
	}

	
	protected boolean keyDown(String key) {
		return mKeyPressed.containsKey(key) && mKeyPressed.get(key);
	}
}
