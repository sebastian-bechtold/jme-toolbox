package com.sebastianbechtold.jmeToolbox.manfredModules.components;

import java.util.HashMap;

import com.sebastianbechtold.manfred.EntityManager;

public abstract class XmlPersistableComponent {
	
	private boolean mPersist = true;
	
	public boolean doPersist() {
		return mPersist;
	}
	
	public abstract HashMap<String, Object> getXmlAttribs(EntityManager em);
	
	public void setPersistance(boolean persist) {
		mPersist = persist;
	}
}
