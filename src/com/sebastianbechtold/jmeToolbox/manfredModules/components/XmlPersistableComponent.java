package com.sebastianbechtold.jmeToolbox.manfredModules.components;

import java.util.HashMap;

public abstract class XmlPersistableComponent {
	
	private boolean mPersist = true;
	
	public boolean doPersist() {
		return mPersist;
	}
	
	public abstract HashMap<String, Object> getXmlAttribs();
	
	public void setPersistance(boolean persist) {
		mPersist = persist;
	}
}
