package com.sweta.subdivisionsurfaces.algo;

import java.util.ArrayList;
import java.util.HashMap;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Point;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonFactory;
import com.sweta.subdivisionsurfaces.PolygonMesh;
import com.sweta.subdivisionsurfaces.Polygon.Edge;

public class LoopAlgorithm extends AbstractSubdivisionAlgorithm {

	private final HashMap<Edge, Point> edgePoints = new HashMap<>();

	private final HashMap<Point, Point> newVertices = new HashMap<>();

	public LoopAlgorithm(final PolygonMesh mesh) {
		super(mesh);
		if (!mesh.hasOnlyTriangles()) {
			System.out.println("\tmesh is not composed of only triangles! Triangulating mesh.");
			this.mesh = mesh.triangulate();
		}
		if (mesh.faces.size() <= 1) {
			throw new RuntimeException("mesh mush have > 1 triangle!");
		}
		this.edgePoints.clear();
		this.newVertices.clear();
	}

	private static float s(final int n) {
		if (n < 3) {
			throw new IllegalArgumentException("n must be >= 3");
		} else if (n == 3) {
			return 3.0f / 16;
		} else {
			float a = (float) Math.cos(2.0f * Math.PI / n);
			a = 1.0f / 4 * a + 3.0f / 8;
			a = 5.0f / 8 - a * a;
			a /= n;
			return a;
		}
	}

	private Point getEdgePoint(final Edge edge) {
		final MyHashSet<Polygon> polySet = this.mesh.edgeFaces.get(edge);
		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Polygon poly : polySet) {
			points.addAll(poly.getPoints());
		}
		points.removeAll(edge.getEnds());
		Point a = edge.midPoint();
		a = a.times(3.0f / 4);
		Point b = new Point();
		for (final Point p : points) {
			b = b.add(p);
		}
		b = b.times(1.0f / 8);
		return a.add(b);
	}

	private Point getNewVertex(final Point p) {
		final MyHashSet<Edge> edges = this.mesh.pointEdges.get(p);
		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Edge e : edges) {
			points.addAll(e.getEnds());
		}
		points.remove(p);
		final int n = points.size();
		Point sumNeighbour = new Point();
		for (final Point pt : points) {
			sumNeighbour = sumNeighbour.add(pt);
		}

		final float s = LoopAlgorithm.s(n);
		Point retVal = p.times(1.0f - n * s);
		retVal = retVal.add(sumNeighbour.times(s));
		return retVal;
	}

	@Override
	public MyHashSet<Polygon> run() {
		System.out.println("\tInput mesh has " + this.mesh.describe());

		// Filling the edgepoints
		for (final Edge edge : this.mesh.edges) {
			this.edgePoints.put(edge, this.getEdgePoint(edge));
		}

		// Get the new vertices
		for (final Point p : this.mesh.points) {
			this.newVertices.put(p, this.getNewVertex(p));
		}

		final MyHashSet<Polygon> set = new MyHashSet<>();
		for (final Polygon face : this.mesh.faces) {
			set.addAll(this.getNewFaces(face));
		}

		System.out.println("\tOutput set has " + set.size() + " faces.\n");
		return set;
	}

	private MyHashSet<Polygon> getNewFaces(final Polygon face) {
		if (face.getPoints().size() != 3) {
			throw new RuntimeException("Invalid polygon!");
		}
		final MyHashSet<Polygon> retVal = new MyHashSet<>();
		for (final Point p : face.getPoints()) {
			retVal.add(this.getNewFaceForVertex(face, p));
		}
		retVal.add(this.getNewFaceForFace(face));
		return retVal;
	}

	private Polygon getNewFaceForVertex(final Polygon face, final Point p) {
		if (!face.isVertex(p)) {
			throw new RuntimeException("Invalid vertex!");
		}

		final ArrayList<Edge> incidentEdges = new ArrayList<>();
		for (final Edge e : face.getEdges()) {
			if (e.isEnd(p)) {
				incidentEdges.add(e);
			}
		}

		if (incidentEdges.size() != 2) {
			throw new RuntimeException("Invalid vertex!");
		}

		final Point a = this.newVertices.get(p);
		final Point b = this.edgePoints.get(incidentEdges.get(0));
		final Point c = this.edgePoints.get(incidentEdges.get(1));

		final ArrayList<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);

		return PolygonFactory.fromOrderedPoints(points);
	}

	private Polygon getNewFaceForFace(final Polygon face) {
		final MyHashSet<Edge> edges = face.getEdges();
		if (edges.size() != 3) {
			throw new RuntimeException("Invalid face!");
		}

		final Edge[] edgeArr = edges.toArray(new Edge[0]);
		final Point a = this.edgePoints.get(edgeArr[0]);
		final Point b = this.edgePoints.get(edgeArr[1]);
		final Point c = this.edgePoints.get(edgeArr[2]);

		final ArrayList<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);

		return PolygonFactory.fromOrderedPoints(points);
	}

}