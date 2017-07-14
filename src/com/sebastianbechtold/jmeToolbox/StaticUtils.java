package com.sebastianbechtold.jmeToolbox;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class StaticUtils {
	public static Vector3D stringToVec3(String str) {
		String[] psa = str.split(",");

		double x = 0;
		double y = 0;
		double z = 0;

		try {
			x = Double.parseDouble(psa[0]);
		} catch (Exception e) {

		}

		try {
			y = Double.parseDouble(psa[1]);
		} catch (Exception e) {

		}

		try {
			z = Double.parseDouble(psa[2]);
		} catch (Exception e) {

		}

		return new Vector3D(x, y, z);
	}

	public static Rotation stringToRot(String str) {
		String[] psa = str.split(",");

		double x = 0;
		double y = 1;
		double z = 0;
		double a = 0;

		try {
			x = Double.parseDouble(psa[0]);
		} catch (Exception e) {

		}

		try {
			y = Double.parseDouble(psa[1]);
		} catch (Exception e) {

		}

		try {
			z = Double.parseDouble(psa[2]);
		} catch (Exception e) {

		}

		try {
			a = Double.parseDouble(psa[3]);
		} catch (Exception e) {

		}

		Vector3D axis = new Vector3D(x, y, z);

		return new Rotation(axis, a);
	}
	
	public static double round(double value, double stellen) {
		
		return Math.round(value * stellen) / stellen;
	}
	
	
	public static Vector3f am2jme_vector(Vector3D vecSim) {

		if (vecSim == null) {
			return null;
		}
		return new Vector3f((float) vecSim.getX(), (float) vecSim.getY(), (float) vecSim.getZ());
	}
	
	
	public static Vector3D jme2am_vector(Vector3f vec) {

		if (vec == null) {
			return null;
		}
		return new Vector3D(vec.getX(), vec.getY(), vec.getZ());
	}
	
	
	public static Quaternion am2jme_quaternion(Rotation r) {
		
		Quaternion q = new Quaternion();
		
		if (r == null) System.out.println("r is null");
		Vector3f axis = am2jme_vector(r.getAxis());
		float angle = (float) r.getAngle();
		
		return q.fromAngleAxis(angle, axis);
	}
	
	public static Vector3f toFlat(Vector3f v) {
		return new Vector3f(v.getX(), 0, v.getZ());
	}


	public static Vector3D toFlat(Vector3D v) {
		return new Vector3D(v.getX(), 0, v.getZ());
	}
}
