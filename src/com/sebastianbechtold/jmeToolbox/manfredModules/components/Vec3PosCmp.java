package com.sebastianbechtold.jmeToolbox.manfredModules.components;

import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.sebastianbechtold.manfred.EntityManager;
import com.sebastianbechtold.manfred.IManfredComponent;

public class Vec3PosCmp extends XmlPersistableComponent implements IManfredComponent {

	private Vector3D mPos = new Vector3D(0, 0, 0);

	public Vec3PosCmp(Vector3D pos) {
		mPos = pos;
	}

	@Override
	public HashMap<String, Object> getXmlAttribs(EntityManager em) {

		HashMap<String, Object> result = new HashMap<>();

		result.put("pos", mPos.getX() + "," + mPos.getY() + "," + mPos.getZ());

		return result;

	}

	public Vector3D getPos() {
		return mPos;
	}

}
