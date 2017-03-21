package com.sweta.basic.algo;

import com.sweta.basic.MyHashSet;
import com.sweta.basic.Polygon;
import com.sweta.basic.PolygonMesh;

public class TriangulateAlgorithm extends AbstractSubdivisionAlgorithm {

	public TriangulateAlgorithm(final PolygonMesh mesh) {
		super(mesh);
	}

	@Override
	public MyHashSet<Polygon> run() {
		System.out.println("\tInput mesh has " + this.mesh.describe());
		final MyHashSet<Polygon> set = new MyHashSet<Polygon>();
		for (final Polygon face : this.mesh.faces)
			set.addAll(face.triangulateCentroid());
		System.out.println("\tOutput set has " + set.size() + " faces.\n");
		return set;
	}
}
