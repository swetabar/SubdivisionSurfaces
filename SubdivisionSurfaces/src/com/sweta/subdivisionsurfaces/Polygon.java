package com.sweta.subdivisionsurfaces;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class Polygon {

	private final LinkedHashSet<Point> points = new LinkedHashSet<>();

	private final MyHashSet<Edge> edges = new MyHashSet<>();

	private final Vector normal;

	private final Color color;

	public Polygon(final List<Point> argPoints, final MyHashSet<Edge> argEdges, final Vector argNormal, final Color argColor) {
		if (argPoints == null || argEdges == null || argNormal == null) {
			throw new NullPointerException("Non-null normal and sets of points and edges required!");
		}
		final MyHashSet<Point> edgeEnds = Polygon.validateEdges(argEdges);
		if (edgeEnds == null) {
			throw new IllegalArgumentException("Invalid set of edges!");
		}
		final MyHashSet<Point> pointsSet = new MyHashSet<>();
		pointsSet.addAll(argPoints);
		if (!pointsSet.equals(edgeEnds)) {
			throw new IllegalArgumentException("Invalid set of points and edges!");
		}
		this.points.addAll(argPoints);
		this.edges.addAll(argEdges);
		this.normal = new Vector(argNormal).normalize();
		this.color = argColor;
	}

	public boolean hasVertex(final Point point) {
		return points.contains(point);
	}

	public boolean hasEdge(final Edge e) {
		return this.edges.contains(e);
	}

	private static MyHashSet<Point> validateEdges(final MyHashSet<Edge> edges) {
		if (edges.size() < 3) {
			return null;
		}

		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Edge e : edges) {
			points.addAll(e.ends);
		}
		if (points.size() < 3) {
			return null;
		}

		final HashMap<Point, Integer> pointDegrees = new HashMap<>();
		for (final Point p : points) {
			pointDegrees.put(p, 0);
		}
		for (final Edge e : edges) {
			for (final Point p : e.ends) {
				int d = pointDegrees.get(p).intValue();
				pointDegrees.put(p, ++d);
			}
		}

		final MyHashSet<Integer> degreeSet = new MyHashSet<>();
		degreeSet.addAll(pointDegrees.values());
		final Integer[] degreeArr = degreeSet.toArray(new Integer[0]);
		if (degreeArr == null || degreeArr.length != 1) {
			return null;
		}

		final int degInt = degreeArr[0].intValue();
		if (degInt != 2) {
			return null;
		}

		return points;
	}

	public MyHashSet<Edge> getEdges() {
		return this.edges;
	}

	public Collection<Point> getPoints() {
		return this.points;
	}

	public MyHashSet<Polygon> triangulateCentroid() {
		return PolygonFactory.triangulateCentroid(this.points, this.color);
	}

	public MyHashSet<Polygon> triangulateFan() {
		return PolygonFactory.triangulateFan(this.points, this.color);
	}

	@Override
	public String toString() {
		final Vector n = this.normal;
		final StringBuilder builder = new StringBuilder();
		builder.append("P " + this.points.size() + " 0 0\n");
		if (this.color != Color.DEFAULT) {
			builder.append("d " + this.color.x + " " + this.color.y + " " + this.color.z + "\n");
		}
		for (final Point p : this.points) {
			builder.append("n " + n.x + " " + n.y + " " + n.z + "\n");
			builder.append("v " + p.x + " " + p.y + " " + p.z + "\n");
		}
		builder.append("E 0 0 0\n");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.getPoints() == null ? 0 : this.getPoints().hashCode());
		result = prime * result + (this.getEdges() == null ? 0 : this.getEdges().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final Polygon other = (Polygon) obj;
		if (this.getEdges() == null) {
			if (other.getEdges() != null) {
				return false;
			}
		} else if (!this.getEdges().equals(other.getEdges())) {
			return false;
		}
		if (this.getPoints() == null) {
			if (other.getPoints() != null) {
				return false;
			}
		} else if (!this.getPoints().equals(other.getPoints())) {
			return false;
		}
		return true;
	}

	public static class Edge implements Comparable<Edge> {

		private final MyHashSet<Point> ends = new MyHashSet<>();
		public final Point a, b;

		public Edge(final Point endA, final Point endB) {
			if (endA == null || endB == null) {
				throw new NullPointerException();
			}
			if (endA.equals(endB)) {
				throw new IllegalArgumentException("Invalid edge! endA=" + endA.toString() + " endB=" + endB.toString());
			}
			this.ends.add(endA);
			this.ends.add(endB);
			this.a = endA;
			this.b = endB;
		}

		public MyHashSet<Point> getEnds() {
			return this.ends;
		}

		public float length() {
			return new Vector(a, b).length();
		}

		public Point midPoint() {
			return Point.linearCombination(a, b, 0.5f);
		}

		public boolean isEnd(final Point p) {
			return (p.equals(a) || p.equals(b));
		}

		public boolean meets(final Edge other) {
			return !this.ends.intersection(other.ends).isEmpty();
		}

		@Override
		public int compareTo(final Edge o) {
			return Float.compare(this.length(), o.length());
		}

		@Override
		public int hashCode() {
			return Float.floatToIntBits(b.y + a.y) ^ Float.floatToIntBits(b.x + a.x - 1);
		}

		@Override
		public String toString() {
			return "Edge [ends=" + this.ends.toString() + "]";
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (this.getClass() != obj.getClass())) {
				return false;
			}
			final Edge other = (Edge) obj;
			if (this.ends == null) {
				if (other.ends != null) {
					return false;
				}
			}
			return (other.a.equals(a) && other.b.equals(b)) || (other.a.equals(b) && other.b.equals(a));
		}
	}

}