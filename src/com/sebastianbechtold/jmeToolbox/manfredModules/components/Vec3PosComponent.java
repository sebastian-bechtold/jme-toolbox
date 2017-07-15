package com.sebastianbechtold.jmeToolbox.manfredModules.components;

import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.w3c.dom.Element;

import com.sebastianbechtold.jmeToolbox.StaticUtils;
import com.sebastianbechtold.manfred.IManfredComponent;

public class Vec3PosComponent extends XmlPersistableComponent implements IManfredComponent {
	
	private Vector3D mPos = new Vector3D(0,0,0);

	public Vec3PosComponent(Vector3D pos) {
		mPos = pos;
	}
	
	public Vec3PosComponent(Element xml) {
		mPos = StaticUtils.stringToVec3(xml.getAttribute("pos"));
	}
	
	
	

	@Override
	public HashMap<String, Object> getXmlAttribs() {
		
		HashMap<String, Object> result = new HashMap<>();

		result.put("pos", mPos.getX() + "," + mPos.getY() + "," + mPos.getZ());


		return result;


	}
	
	
	public Vector3D getPos() {
		return mPos;
	}

}
