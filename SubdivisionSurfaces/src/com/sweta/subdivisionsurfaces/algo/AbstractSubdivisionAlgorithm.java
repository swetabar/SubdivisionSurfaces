package com.sweta.subdivisionsurfaces.algo;

import java.util.Set;

import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonMesh;

public abstract class AbstractSubdivisionAlgorithm {

	protected PolygonMesh mesh;

	public AbstractSubdivisionAlgorithm(final PolygonMesh mesh) {
		this.mesh = mesh;
	}

	public abstract Set<Polygon> run();
}
