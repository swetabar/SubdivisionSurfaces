package com.sweta.basic;

public class Point extends Triple {

	public Point(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public Point(final Triple t) {
		super(t);
	}

	public Point() {
		super();
	}

	public Point add(final Point p) {
		return new Point(this.x + p.x, this.y + p.y, this.z + p.z);
	}

	public Point times(final float k) {
		return new Point(k * this.x, k * this.y, k * this.z);
	}

	public static Point linearCombination(final Point P0, final Point P1,
			final float a0) {
		final float x = a0 * P0.x + (1 - a0) * P1.x;
		final float y = a0 * P0.y + (1 - a0) * P1.y;
		final float z = a0 * P0.z + (1 - a0) * P1.z;
		return new Point(x, y, z);
	}

	public static Point displacement(final Point O, final Vector V) {
		return new Point(O.x + V.x, O.y + V.y, O.z + V.z);
	}
}
