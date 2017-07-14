package com.sebastianbechtold.jmeToolbox.manfredModules;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.sebastianbechtold.manfred.IManfredComponent;

public class RotationComponent extends XmlPersistableComponent implements IManfredComponent {

	private double mAngle = 0;

	public RotationComponent() {

	}

	public RotationComponent(Element xml) {
	
		try {
			mAngle = Double.parseDouble(xml.getAttribute("angle"));
		} catch (Exception e) {

		}

	}

	@Override
	public boolean doPersist() {
		return true;
	}
	
	public double getAngle() {
		return mAngle % (Math.PI * 2);
	}
	
	public void setAngle(double angle) {
		mAngle = (angle % (Math.PI * 2));
	}

	@Override
	public HashMap<String, Object> getXmlAttribs() {

		HashMap<String, Object> result = new HashMap<>();

		// ATTENTION: We MUST NOT ROUND here! The precision of many decimals is really required!
		result.put("angle", mAngle);

		return result;

	}
}
