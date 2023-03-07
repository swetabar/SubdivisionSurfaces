package com.sweta.basic;

import java.util.ArrayList;
import java.util.HashMap;

public class Polygon {

	public static class Edge implements Comparable<Edge> {

		private final MyHashSet<Point> ends = new MyHashSet<>();

		public Edge(final Point endA, final Point endB) {
			if (endA == null || endB == null) {
				throw new NullPointerException();
			}
			if (endA.equals(endB)) {
				throw new IllegalArgumentException("Invalid edge! endA=" + endA.toString() + " endB=" + endB.toString());
			}
			this.ends.add(endA);
			this.ends.add(endB);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (this.ends == null ? 0 : this.ends.hashCode());
			return result;
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
			} else if (!this.ends.equals(other.ends)) {
				return false;
			}
			return true;
		}

		public MyHashSet<Point> getEnds() {
			return this.ends;
		}

		public float length() {
			final Point[] ends = this.ends.toArray(new Point[0]);
			return new Vector(ends[0], ends[1]).length();
		}

		public Point midPoint() {
			final Object[] arr = this.ends.toArray();
			return Point.linearCombination((Point) arr[0], (Point) arr[1], 0.5f);
		}

		public boolean isEnd(final Point p) {
			final Object[] arr = this.ends.toArray();
			if (p.equals(arr[0]) || p.equals(arr[1])) {
				return true;
			}
			return false;
		}

		@Override
		public int compareTo(final Edge o) {
			return Float.compare(this.length(), o.length());
		}

		public boolean meets(final Edge other) {
			return this.ends.intersection(other.ends).size() > 0;
		}
	}

	private final ArrayList<Point> points = new ArrayList<>();

	private final MyHashSet<Edge> edges = new MyHashSet<>();

	private final Vector normal;

	private final Color color;

	public Polygon(final ArrayList<Point> argPoints, final MyHashSet<Edge> argEdges, final Vector argNormal, final Color argColor) {
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

	public boolean isVertex(final Point point) {
		for (final Point p : this.getPoints()) {
			if (point.equals(p)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEdge(final Edge e) {
		for (final Edge edge : this.getEdges()) {
			if (e.equals(edge)) {
				return true;
			}
		}
		return false;
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

	public MyHashSet<Edge> getEdges() {
		return this.edges;
	}

	public ArrayList<Point> getPoints() {
		return this.points;
	}

	public MyHashSet<Polygon> triangulateCentroid() {
		return PolygonFactory.triangulateCentroid(this.points, this.color);
	}

	public MyHashSet<Polygon> triangulateFan() {
		return PolygonFactory.triangulateFan(this.points, this.color);
	}

}