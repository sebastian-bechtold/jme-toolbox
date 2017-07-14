package com.sebastianbechtold.jmeToolbox.manfredModules;

import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.w3c.dom.Element;

import com.sebastianbechtold.manfred.IManfredComponent;

public class GeometryTransformComponent extends XmlPersistableComponent implements IManfredComponent {

	private double mAngle = 0;

	private double mScale = 1;
	private Rotation mRot = new Rotation(new Vector3D(1,0,0),0);
	private Vector3D mPos = new Vector3D(0,0,0);

	public GeometryTransformComponent(Element xml) {
	
		try {
			mAngle = Double.parseDouble(xml.getAttribute("angle"));
		} catch (Exception e) {

		}

	}

	@Override
	public boolean doPersist() {
		return true;
	}
	
	
	public Vector3D getPos() {
		return mPos;
	}
	
	public Rotation getRot() {
		return mRot;
	}
	
	public double getScale() {
		return mScale;
	}
	
	
	public void setPos(Vector3D pos) {
		mPos = pos;
	}
	
	
	public void setRot(Rotation r) {
		mRot = r;
	}

	
	public void setScale(double scale) {
		mScale = scale;
	}
	
	
	@Override
	public HashMap<String, Object> getXmlAttribs() {

		HashMap<String, Object> result = new HashMap<>();

		// ATTENTION: We MUST NOT ROUND here! The precision of many decimals is really required!
		result.put("angle", mAngle);

		return result;

	}
}
