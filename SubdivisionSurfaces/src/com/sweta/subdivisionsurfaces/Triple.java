package com.sweta.subdivisionsurfaces;

public class Triple {

	public final float x, y, z;

	public Triple(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Triple(final Triple t) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
	}

	public Triple() {
		this(0.0f, 0.0f, 0.0f);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.x);
		result = prime * result + Float.floatToIntBits(this.y);
		result = prime * result + Float.floatToIntBits(this.z);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final Triple p = (Triple) obj;
		return x == p.x && y == p.y && z == p.z;
	}

	public static float dotProduct(final Triple a, final Triple b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

}