package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.manfred.EntityManager;
import com.sebastianbechtold.manfred.IManfredComponent;

public class JmeSceneNodeUpdateSystem extends AbstractManfredJmeSystemAppState {

	Quaternion mQ = new Quaternion();

	public JmeSceneNodeUpdateSystem(EntityManager em) {
		super(em);
	}

	@Override
	public void cleanup() {

		mEm.removeComponentReplacedListener(Vec3PosComponent.class, this::onVec3PositionComponentChanged);
		mEm.removeComponentReplacedListener(RotationComponent.class, this::onRotationComponentChanged);
		mEm.removeComponentReplacedListener(JmeSceneNodeComponent.class, this::onSceneNodeComponentChanged);
		
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		mEm.addComponentReplacedListener(Vec3PosComponent.class, this::onVec3PositionComponentChanged);
		mEm.addComponentReplacedListener(RotationComponent.class, this::onRotationComponentChanged);
		mEm.addComponentReplacedListener(JmeSceneNodeComponent.class, this::onSceneNodeComponentChanged);		
	}

	
	void onWillDeleteEntity(Object payload) {

		int id = (int) payload;

		// ############# BEGIn Remove spatial from scene graph ################
		JmeSceneNodeComponent spc = mEm.getComponent(id, JmeSceneNodeComponent.class);

		if (spc == null) {
			return;
		}

		spc.mNode.removeFromParent();
		// ############# END Remove spatial from scene graph ################
	}

	//////// ############ BEGIN New Event handlers #################

	void onSceneNodeComponentChanged(int id, IManfredComponent comp) {

		JmeSceneNodeComponent spc_old = (JmeSceneNodeComponent) comp;
		JmeSceneNodeComponent spc_new = mEm.getComponent(id, JmeSceneNodeComponent.class);
		
		if (spc_old != null && spc_new == null) {
			System.out.println("Spatial component removed!");
			spc_old.mNode.removeFromParent();
		}
				
	}
	
	void onVec3PositionComponentChanged(int id, IManfredComponent comp) {

		JmeSceneNodeComponent spc = (JmeSceneNodeComponent) mEm.getComponent(id, JmeSceneNodeComponent.class);

		if (spc == null) {
			return;
		}

		// ########## BEGIN Apply information from PositionComponent to SpatialComponent ###########
		Vec3PosComponent pc = (Vec3PosComponent) comp;

		if (pc != null) {
			spc.mNode.setLocalTranslation(StaticUtils.am2jme_vector(pc.getPos()));
		}		
	}
	
	
	void onRotationComponentChanged(int id, IManfredComponent comp) {
		System.out.println("rot comp changed");

		JmeSceneNodeComponent spc = (JmeSceneNodeComponent) mEm.getComponent(id, JmeSceneNodeComponent.class);

		if (spc == null) {
			return;
		}

		// ########## BEGIN Apply information from RotationComponent to SpatialComponent ###########
		RotationComponent rc = (RotationComponent) comp;

		if (rc != null) {
			spc.mNode.setLocalRotation(mQ.fromAngleAxis((float) rc.getAngle(), DIR_UP));
		}
	}

}
