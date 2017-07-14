package com.sebastianbechtold.jmeToolbox.manfredModules;

import java.util.HashMap;

// TODO 3: Move XML persist functionality out of OpenTrains
public abstract class XmlPersistableComponent {
	public boolean doPersist() {
		return false;
	}

	public HashMap<String, Object> getXmlAttribs() {
		return null;
	}

}
