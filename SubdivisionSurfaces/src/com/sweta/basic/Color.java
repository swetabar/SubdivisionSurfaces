package com.sweta.basic;

public class Color extends Triple {

	public static Color DEFAULT = null;
	public static Color RED = new Color(1.0f, 0.0f, 0.0f);
	public static Color GREEN = new Color(0.0f, 1.0f, 0.0f);
	public static Color BLUE = new Color(0.0f, 0.0f, 1.0f);

	public Color(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public Color(final Triple t) {
		super(t);
	}

}
