package com.sweta.subdivisionsurfaces.algo;

import java.util.HashMap;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Point;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonFactory;
import com.sweta.subdivisionsurfaces.PolygonMesh;
import com.sweta.subdivisionsurfaces.Polygon.Edge;

public class MidpointAlgorithm extends AbstractSubdivisionAlgorithm {

	private final HashMap<Edge, Point> edgePoints = new HashMap<>();

	public MidpointAlgorithm(final PolygonMesh mesh) {
		super(mesh);
		this.edgePoints.clear();
	}

	private Point getEdgePoint(final Edge edge) {
		return edge.midPoint();
	}

	@Override
	public MyHashSet<Polygon> run() {
		System.out.println("\tInput mesh has " + this.mesh.describe());

		// Filling the edgepoints
		for (final Edge edge : this.mesh.edges) {
			this.edgePoints.put(edge, this.getEdgePoint(edge));
		}

		final MyHashSet<Polygon> set = new MyHashSet<>();
		for (final Polygon face : this.mesh.faces) {
			set.add(this.getNewFaceForFace(face));
		}
		for (final Point point : this.mesh.points) {
			final Polygon newFace = this.getNewFaceForVertex(point);
			if (newFace != null) {
				set.add(newFace);
			}
		}

		System.out.println("\tOutput set has " + set.size() + " faces.\n");
		return set;
	}

	private Polygon getNewFaceForFace(final Polygon face) {
		if (face.getPoints().size() < 3) {
			throw new RuntimeException("Invalid polygon!");
		}

		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Edge e : face.getEdges()) {
			points.add(this.edgePoints.get(e));
		}
		return PolygonFactory.fromPointsConvex(points);
	}

	// private Polygon getNewFaceForFace(final Polygon face) {
	// if (face.getPoints().size() < 3)
	// throw new RuntimeException("Invalid polygon!");
	//
	// final ArrayList<Point> points = new ArrayList<Point>();
	// for (final Edge e : face.getEdges())
	// points.add(this.edgePoints.get(e));
	// return PolygonFactory.fromOrderedPoints(points);
	// }

	private Polygon getNewFaceForVertex(final Point p) {
		if (!this.mesh.points.contains(p)) {
			throw new RuntimeException("Invalid vertex!");
		}

		final MyHashSet<Edge> edges = this.mesh.pointEdges.get(p);
		if (edges.size() < 3) {
			return null;
		}

		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Edge e : edges) {
			if (e.isEnd(p)) {
				points.add(this.edgePoints.get(e));
			}
		}
		return PolygonFactory.fromPointsConvex(points);
	}

	// private Polygon getNewFaceForVertex(final Point p) {
	// if (!this.mesh.points.contains(p))
	// throw new RuntimeException("Invalid vertex!");
	//
	// final MyHashSet<Edge> edges = this.mesh.pointEdges.get(p);
	// if (edges.size() < 3)
	// return null;
	//
	// final ArrayList<Point> points = new ArrayList<Point>();
	// for (final Edge e : edges)
	// if (e.isEnd(p))
	// points.add(this.edgePoints.get(e));
	// return PolygonFactory.fromOrderedPoints(points);
	// }
}
