package com.sweta.basic.algo;

import java.util.ArrayList;
import java.util.HashMap;

import com.sweta.basic.MyHashSet;
import com.sweta.basic.Point;
import com.sweta.basic.Polygon;
import com.sweta.basic.Polygon.Edge;
import com.sweta.basic.PolygonFactory;
import com.sweta.basic.PolygonMesh;

public class Root3Algorithm extends AbstractSubdivisionAlgorithm {

	private final HashMap<Polygon, Point> facePoints = new HashMap<Polygon, Point>();

	private final HashMap<Point, Point> newVertices = new HashMap<Point, Point>();

	public Root3Algorithm(final PolygonMesh mesh) {
		super(mesh);
		if (!mesh.hasOnlyTriangles()) {
			System.out
					.println("\tmesh is not composed of only triangles! Triangulating mesh.");
			this.mesh = mesh.triangulate();
		}
		this.facePoints.clear();
		this.newVertices.clear();
	}

	private static float a(final int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n must be > 0");
		else {
			float a = 2.0f - (float) Math.cos(2.0f * Math.PI / n);
			a *= 2.0f / 9;
			return a;
		}
	}

	private Point getFacePoint(final Polygon face) {
		Point sum = new Point();
		for (final Point p : face.getPoints())
			sum = sum.add(p);
		sum = sum.times(1.0f / face.getPoints().size());
		return sum;
	}

	private Point getNewVertex(final Point p) {
		if (!this.mesh.points.contains(p))
			throw new RuntimeException("Invalid vertex!");

		final MyHashSet<Point> neighbours = new MyHashSet<Point>();
		for (final Edge e : this.mesh.pointEdges.get(p))
			neighbours.addAll(e.getEnds());
		neighbours.remove(p);

		Point b = new Point();
		for (final Point pt : neighbours)
			b = b.add(pt);
		final int n = neighbours.size();
		final float a = Root3Algorithm.a(n);
		return p.times(1.0f - a).add(b.times(a / n));
	}

	@Override
	public MyHashSet<Polygon> run() {
		System.out.println("\tInput mesh has " + this.mesh.describe());

		// Filling the facepoints
		for (final Polygon face : this.mesh.faces)
			this.facePoints.put(face, this.getFacePoint(face));

		// Get the new vertices
		for (final Point p : this.mesh.points)
			this.newVertices.put(p, this.getNewVertex(p));

		final MyHashSet<Polygon> set = new MyHashSet<Polygon>();
		for (final Polygon face : this.mesh.faces) {
			final MyHashSet<Polygon> newFaces = this.getNewFaces(face);
			set.addAll(newFaces);
		}

		System.out.println("\tOutput set has " + set.size() + " faces.\n");
		return set;
	}

	private MyHashSet<Polygon> getNewFaces(final Polygon face) {
		if (face.getPoints().size() != 3)
			throw new RuntimeException("Invalid polygon!");

		final MyHashSet<Polygon> retVal = new MyHashSet<Polygon>();
		for (final Edge edge : face.getEdges())
			retVal.addAll(this.getNewFacesForEdge(face, edge));
		return retVal;
	}

	private MyHashSet<Polygon> getNewFacesForEdge(final Polygon face,
			final Edge edge) {
		if (!face.isEdge(edge))
			throw new RuntimeException("Invalid edge!");

		final MyHashSet<Polygon> retVal = new MyHashSet<Polygon>();

		final MyHashSet<Polygon> faces = this.mesh.edgeFaces.get(edge);
		faces.remove(face);
		final Point[] ends = edge.getEnds().toArray(new Point[0]);
		for (final Polygon f : faces)
			for (final Point end : ends) {
				final Point a = this.facePoints.get(face);
				final Point b = this.newVertices.get(end);
				final Point c = this.facePoints.get(f);
				final ArrayList<Point> points = new ArrayList<Point>();
				points.add(a);
				points.add(b);
				points.add(c);
				retVal.add(PolygonFactory.fromOrderedPoints(points));
			}

		return retVal;
	}
}
