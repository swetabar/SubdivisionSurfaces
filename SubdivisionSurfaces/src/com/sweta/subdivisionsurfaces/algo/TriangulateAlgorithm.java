package com.sweta.subdivisionsurfaces.algo;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonMesh;

public class TriangulateAlgorithm extends AbstractSubdivisionAlgorithm {

	public TriangulateAlgorithm(final PolygonMesh mesh) {
		super(mesh);
	}

	@Override
	public MyHashSet<Polygon> run() {
		final MyHashSet<Polygon> set = new MyHashSet<>();
		for (final Polygon face : this.mesh.faces) {
			set.addAll(face.triangulateCentroid());
		}
		return set;
	}
}
