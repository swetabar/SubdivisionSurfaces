package com.sweta.subdivisionsurfaces.algo;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonMesh;

public abstract class AbstractSubdivisionAlgorithm {

	protected PolygonMesh mesh;

	public AbstractSubdivisionAlgorithm(final PolygonMesh mesh) {
		this.mesh = mesh;
	}

	public abstract MyHashSet<Polygon> run();
}
