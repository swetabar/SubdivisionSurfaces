package com.sweta.subdivisionsurfaces;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.sweta.subdivisionsurfaces.Polygon.Edge;

public class PolygonMesh {

	public final MyHashSet<Polygon> faces = new MyHashSet<>();
	public final MyHashSet<Polygon.Edge> edges = new MyHashSet<>();
	public final MyHashSet<Point> points = new MyHashSet<>();
	public final Map<Point, MyHashSet<Polygon>> pointFaces = new HashMap<>();
	public final Map<Polygon.Edge, MyHashSet<Polygon>> edgeFaces = new HashMap<>();
	public final Map<Point, MyHashSet<Polygon.Edge>> pointEdges = new HashMap<>();

	public PolygonMesh(final Collection<Polygon> polySet) {
		for (final Polygon poly : polySet) {
			this.faces.add(poly);
			this.edges.addAll(poly.getEdges());
			this.points.addAll(poly.getPoints());
		}

		for (final Point p : this.points) {
			final MyHashSet<Polygon> set = new MyHashSet<>();
			for (final Polygon poly : this.faces) {
				if (poly.isVertex(p)) {
					set.add(poly);
				}
			}
			this.pointFaces.put(p, set);
		}

		for (final Edge e : this.edges) {
			final MyHashSet<Polygon> set = new MyHashSet<>();
			for (final Polygon poly : this.faces) {
				if (poly.isEdge(e)) {
					set.add(poly);
				}
			}
			this.edgeFaces.put(e, set);
		}

		for (final Point p : this.points) {
			final MyHashSet<Polygon.Edge> set = new MyHashSet<>();
			for (final Polygon.Edge edge : this.edges) {
				if (edge.isEnd(p)) {
					set.add(edge);
				}
			}
			this.pointEdges.put(p, set);
		}
	}

	public String describe() {
		return this.faces.size() + " faces, " + this.edges.size() + " edges, " + this.points.size() + " points.";
	}

	public boolean isEdgeOnHole(final Edge e) {
		if (!this.edges.contains(e)) {
			throw new IllegalArgumentException("Edge not in mesh!");
		}
		return this.edgeFaces.get(e) != null && this.edgeFaces.get(e).size() == 1;
	}

	public boolean isPointOnHole(final Point p) {
		if (!this.points.contains(p)) {
			throw new IllegalArgumentException("Point not in mesh!");
		}
		return this.pointFaces.get(p) != null && this.pointEdges.get(p) != null
				&& this.pointFaces.get(p).size() != this.pointEdges.get(p).size();
	}

	public boolean hasOnlyTriangles() {
		for (final Polygon face : this.faces) {
			if (face.getPoints().size() != 3) {
				return false;
			}
		}
		return true;
	}

	public boolean hasOnlyTrianglesAndQuads() {
		for (final Polygon face : this.faces) {
			if (face.getPoints().size() != 3 && face.getPoints().size() != 4) {
				return false;
			}
		}
		return true;
	}

	public PolygonMesh triangulate() {
		final MyHashSet<Polygon> newFaces = new MyHashSet<>();
		for (final Polygon face : this.faces) {
			newFaces.addAll(face.triangulateCentroid());
		}
		return new PolygonMesh(newFaces);
	}

}
