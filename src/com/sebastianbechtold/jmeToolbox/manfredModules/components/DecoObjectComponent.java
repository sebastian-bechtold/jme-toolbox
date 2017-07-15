package com.sebastianbechtold.jmeToolbox.manfredModules.components;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.sebastianbechtold.manfred.IManfredComponent;

public class DecoObjectComponent extends XmlPersistableComponent implements IManfredComponent {

	public String mModelPath = "";

	public DecoObjectComponent(Element xml) {
		mModelPath = xml.getAttribute("modelPath");
	}

	

	@Override
	public HashMap<String, Object> getXmlAttribs() {
		HashMap<String, Object> result = new HashMap<>();

		result.put("modelPath", mModelPath);

		return result;

	}
}
