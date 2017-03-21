package com.sweta.basic;

import java.util.HashMap;

import com.sweta.basic.Polygon.Edge;

public class PolygonMesh {

	public final MyHashSet<Polygon> faces = new MyHashSet<Polygon>();

	public final MyHashSet<Polygon.Edge> edges = new MyHashSet<Polygon.Edge>();

	public final MyHashSet<Point> points = new MyHashSet<Point>();

	public final HashMap<Point, MyHashSet<Polygon>> pointFaces = new HashMap<Point, MyHashSet<Polygon>>();

	public final HashMap<Polygon.Edge, MyHashSet<Polygon>> edgeFaces = new HashMap<Polygon.Edge, MyHashSet<Polygon>>();

	public final HashMap<Point, MyHashSet<Polygon.Edge>> pointEdges = new HashMap<Point, MyHashSet<Polygon.Edge>>();

	public PolygonMesh(final MyHashSet<Polygon> polySet) {
		for (final Polygon poly : polySet) {
			this.faces.add(poly);
			this.edges.addAll(poly.getEdges());
			this.points.addAll(poly.getPoints());
		}

		for (final Point p : this.points) {
			final MyHashSet<Polygon> set = new MyHashSet<Polygon>();
			for (final Polygon poly : this.faces)
				if (poly.isVertex(p))
					set.add(poly);
			this.pointFaces.put(p, set);
		}

		for (final Edge e : this.edges) {
			final MyHashSet<Polygon> set = new MyHashSet<Polygon>();
			for (final Polygon poly : this.faces)
				if (poly.isEdge(e))
					set.add(poly);
			this.edgeFaces.put(e, set);
		}

		for (final Point p : this.points) {
			final MyHashSet<Polygon.Edge> set = new MyHashSet<Polygon.Edge>();
			for (final Polygon.Edge edge : this.edges)
				if (edge.isEnd(p))
					set.add(edge);
			this.pointEdges.put(p, set);
		}
	}

	public String describe() {
		return this.faces.size() + " faces, " + this.edges.size() + " edges, "
				+ this.points.size() + " points.";
	}

	public boolean isEdgeOnHole(final Edge e) {
		if (!this.edges.contains(e))
			throw new IllegalArgumentException("Edge not in mesh!");
		return this.edgeFaces.get(e) != null
				&& this.edgeFaces.get(e).size() == 1;
	}

	public boolean isPointOnHole(final Point p) {
		if (!this.points.contains(p))
			throw new IllegalArgumentException("Point not in mesh!");
		return this.pointFaces.get(p) != null
				&& this.pointEdges.get(p) != null
				&& this.pointFaces.get(p).size() != this.pointEdges.get(p)
						.size();
	}

	public boolean hasOnlyTriangles() {
		for (final Polygon face : this.faces)
			if (face.getPoints().size() != 3)
				return false;
		return true;
	}

	public boolean hasOnlyTrianglesAndQuads() {
		for (final Polygon face : this.faces)
			if (face.getPoints().size() != 3 && face.getPoints().size() != 4)
				return false;
		return true;
	}

	public PolygonMesh triangulate() {
		final MyHashSet<Polygon> newFaces = new MyHashSet<Polygon>();
		for (final Polygon face : this.faces)
			newFaces.addAll(face.triangulateCentroid());
		return new PolygonMesh(newFaces);
	}

}
