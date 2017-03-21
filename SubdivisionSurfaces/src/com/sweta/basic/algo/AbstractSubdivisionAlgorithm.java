package com.sweta.basic.algo;

import com.sweta.basic.MyHashSet;
import com.sweta.basic.Polygon;
import com.sweta.basic.PolygonMesh;

public abstract class AbstractSubdivisionAlgorithm {

	protected PolygonMesh mesh;

	public AbstractSubdivisionAlgorithm(final PolygonMesh mesh) {
		this.mesh = mesh;
	}

	public abstract MyHashSet<Polygon> run();
}
