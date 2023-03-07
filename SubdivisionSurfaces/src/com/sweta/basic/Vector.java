package com.sweta.basic;

public class Vector extends Triple {

	public Vector(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public Vector(final Triple t) {
		super(t);
	}

	public Vector() {
		super();
	}

	public Vector(final Point A, final Point B) {
		super(B.x - A.x, B.y - A.y, B.z - A.z);
	}

	public Vector add(final Vector v) {
		return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	public float length() {
		return (float) Math.sqrt(Triple.dotProduct(this, this));
	}

	public Vector times(final float k) {
		return new Vector(k * this.x, k * this.y, k * this.z);
	}

	public Vector normalize() {
		final float k = 1.0f / this.length();
		return this.times(k);
	}

	public static Vector cross(final Vector u, final Vector v) {
		final float rX = u.y * v.z - u.z * v.y;
		final float rY = u.z * v.x - u.x * v.z;
		final float rZ = u.x * v.y - u.y * v.x;
		return new Vector(rX, rY, rZ);
	}

	public static Vector cross(final Point o, final Point a, final Point b) {
		final Vector u = new Vector(a.x - o.x, a.y - o.y, a.z - o.z);
		final Vector v = new Vector(b.x - o.x, b.y - o.y, b.z - o.z);
		return Vector.cross(u, v);
	}

	public static Vector proj(final Vector u, final Vector v) {
		final float k = Triple.dotProduct(u, v) / v.length() / v.length();
		return v.times(k);
	}

	public static Vector projPlane(final Vector u, final Vector n) {
		final Vector proj = Vector.proj(u, n);
		final Vector retVal = new Vector(u.x - proj.x, u.y - proj.y, u.z - proj.z);
		return retVal;
	}

}
