package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.eventTypes.ManfredEntitySelected;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.JmeSceneNodeCmp;
import com.sun.javafx.scene.control.behavior.OrientedKeyBinding;
import com.jme3.scene.Spatial.CullHint;

public class SelectionVisualizationSystem extends AbstractManfredJmeAppState {

	Geometry mSelectionFrameModel = null;

	@Override
	public void cleanup() {

	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		Easyvents.defaultDispatcher.addListener(ManfredEntitySelected.class, this::onEntitySelected);

		
		WireBox box1 = new WireBox(1,1,1);

		box1.setMode(Mode.Lines);
		box1.setLineWidth(2);
		mSelectionFrameModel = new Geometry("Box", box1);

		Material mat1 = new Material(mAssetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat1.setColor("Color", ColorRGBA.Yellow);

		mSelectionFrameModel.setMaterial(mat1);

		mApp.getRootNode().attachChild(mSelectionFrameModel);

	}

	void onEntitySelected(Object payload) {
		int id = (int) payload;

	
		JmeSceneNodeCmp jsc = mEm.getComponent(id, JmeSceneNodeCmp.class);

		if (jsc != null) {
			
			BoundingBox bv = (BoundingBox) jsc.mNode.getWorldBound();
			new WireBox();

			mSelectionFrameModel.setCullHint(CullHint.Inherit);
			mSelectionFrameModel.setLocalTranslation(jsc.mNode.getWorldTranslation().add(0, bv.getYExtent(), 0));

			mSelectionFrameModel.setLocalScale(bv.getXExtent(), bv.getYExtent(), bv.getZExtent());
		}
		else {
			mSelectionFrameModel.setCullHint(CullHint.Always);
		}

	
	}

	@Override
	public void update(float tpf) {

		int id = mApp.mSelectionSystem.getSelectionId();

		
		if (id != -1) {
			JmeSceneNodeCmp jsc = mEm.getComponent(id, JmeSceneNodeCmp.class);

			if (jsc != null) {

				BoundingBox bv = (BoundingBox) jsc.mNode.getWorldBound();
				
				
				// jsc.mNode.attachChild(mSelectionFrameModel);
				mSelectionFrameModel.setLocalTranslation(jsc.mNode.getWorldTranslation().add(0, bv.getYExtent(), 0));

				mSelectionFrameModel.setLocalScale(bv.getXExtent(), bv.getYExtent(), bv.getZExtent());
				
				//System.out.println("Update!");
			}
		}

	}
}
