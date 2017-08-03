package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.JmeSceneNodeCmp;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.Vec3PosCmp;
import com.sebastianbechtold.manfred.IManfredComponent;

public class JmeSceneNodeUpdateSystem extends AbstractManfredJmeAppState {

	Quaternion mQ = new Quaternion();

	

	@Override
	public void cleanup() {

		mEm.removeComponentReplacedListener(Vec3PosCmp.class, this::onVec3PositionComponentChanged);
		mEm.removeComponentReplacedListener(RotationCmp.class, this::onRotationComponentChanged);
		mEm.removeComponentReplacedListener(JmeSceneNodeCmp.class, this::onSceneNodeComponentChanged);
		
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		mEm.addComponentReplacedListener(Vec3PosCmp.class, this::onVec3PositionComponentChanged);
		mEm.addComponentReplacedListener(RotationCmp.class, this::onRotationComponentChanged);
		mEm.addComponentReplacedListener(JmeSceneNodeCmp.class, this::onSceneNodeComponentChanged);		
	}

	
	void onWillDeleteEntity(Object payload) {

		int id = (int) payload;

		// ############# BEGIn Remove spatial from scene graph ################
		JmeSceneNodeCmp spc = mEm.getComponent(id, JmeSceneNodeCmp.class);

		if (spc == null) {
			return;
		}

		spc.mNode.removeFromParent();
		// ############# END Remove spatial from scene graph ################
	}

	//////// ############ BEGIN New Event handlers #################

	void onSceneNodeComponentChanged(int id, IManfredComponent comp) {

		JmeSceneNodeCmp spc_old = (JmeSceneNodeCmp) comp;
		JmeSceneNodeCmp spc_new = mEm.getComponent(id, JmeSceneNodeCmp.class);
		
		if (spc_old != null && spc_new == null) {
			System.out.println("Spatial component removed!");
			spc_old.mNode.removeFromParent();
		}
				
	}
	
	void onVec3PositionComponentChanged(int id, IManfredComponent comp) {

		JmeSceneNodeCmp spc = (JmeSceneNodeCmp) mEm.getComponent(id, JmeSceneNodeCmp.class);

		if (spc == null) {
			return;
		}

		// ########## BEGIN Apply information from PositionComponent to SpatialComponent ###########
		Vec3PosCmp pc = (Vec3PosCmp) comp;

		if (pc != null) {
			spc.mNode.setLocalTranslation(StaticUtils.am2jme_vector(pc.getPos()));
		}		
	}
	
	
	void onRotationComponentChanged(int id, IManfredComponent comp) {

		JmeSceneNodeCmp spc = (JmeSceneNodeCmp) mEm.getComponent(id, JmeSceneNodeCmp.class);

		if (spc == null) {
			return;
		}

		// ########## BEGIN Apply information from RotationComponent to SpatialComponent ###########
		RotationCmp rc = (RotationCmp) comp;

		if (rc != null) {
			spc.mNode.setLocalRotation(mQ.fromAngleAxis((float) rc.getAngle(), DIR_UP));
		}
	}

}
