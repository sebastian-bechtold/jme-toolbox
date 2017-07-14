package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.manfred.EntityManager;

public class DecoObjectSystem extends AbstractManfredJmeSystemAppState {

	Node mDecoObjectsNode = new Node();

	Node mRootNode = null;

	public DecoObjectSystem(EntityManager em, Node rootNode) {
		super(em);

		mRootNode = rootNode;

	}

	@Override
	public void cleanup() {
		mDecoObjectsNode.removeFromParent();
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		mRootNode.attachChild(mDecoObjectsNode);

		setUpComponents();
	}

	void setUpComponents() {
		
		Quaternion mQ = new Quaternion();

		for (int id : mEm.getEntitiesWith(DecoObjectComponent.class)) {

			DecoObjectComponent dc = mEm.getComponent(id, DecoObjectComponent.class);

			// Make entity persistent:
			PersistFlagComponent pc = new PersistFlagComponent();
			mEm.setComponent(id, pc);

			
			// ############ BEGIN Set up SpatialComponent ############
			JmeSceneNodeComponent spc = new JmeSceneNodeComponent();

			Spatial model = mAssetManager.loadModel(dc.mModelPath);
			model.setShadowMode(ShadowMode.CastAndReceive);
			mDecoObjectsNode.attachChild(model);

			spc.mNode.attachChild(model);
			mDecoObjectsNode.attachChild(spc.mNode);
			
			mEm.setComponent(id, spc);
			// ############ END Set up SpatialComponent ############

			//############ BEGIN Update spatial position and orientation #############
			RotationComponent rc = mEm.getComponent(id,  RotationComponent.class);			
			spc.mNode.setLocalRotation(mQ.fromAngleAxis((float) rc.getAngle(), DIR_UP));
			
			Vec3PosComponent vpc = mEm.getComponent(id,  Vec3PosComponent.class);
			spc.mNode.setLocalTranslation(StaticUtils.am2jme_vector(vpc.getPos()));
			//############ END Update spatial position and orientation #############			
			
			
			// ############# BEGIN Set up "flag" components ###############
			DraggableFlagComponent drc = new DraggableFlagComponent();
			mEm.setComponent(id, drc);

			RotateableFlagComponent roc = new RotateableFlagComponent();
			mEm.setComponent(id, roc);

			// ############# END Set up "flag" components ###############
		}

	}

}
