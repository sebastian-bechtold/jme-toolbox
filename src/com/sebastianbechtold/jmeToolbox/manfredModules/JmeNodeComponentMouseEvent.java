package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.scene.Spatial;

public class JmeNodeComponentMouseEvent {

	public Spatial mSpatial = null;
	public int mEntityId = -1;
	
	public JmeNodeComponentMouseEvent(Spatial node, int id) {
		mSpatial = node;
		mEntityId = id;
	}
}
