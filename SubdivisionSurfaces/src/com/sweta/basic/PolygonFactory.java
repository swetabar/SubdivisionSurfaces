package com.sweta.basic;

import java.util.ArrayList;

import com.sweta.basic.Polygon.Edge;

public class PolygonFactory {

	public static Polygon fromPointsConvex(final MyHashSet<Point> points) {
		return PolygonFactory.fromPointsConvex(points, Color.DEFAULT);
	}

	public static Polygon fromPointsConvex(final MyHashSet<Point> points,
			final Color color) {
		final ArrayList<Point> pts = ConvexHull
				.getConvexHullOrderedPoints(points);
		if (!PolygonFactory.validateInput(pts))
			throw new IllegalArgumentException("Invalid set of points!");
		final MyHashSet<Edge> edges = ConvexHull.getConvexHullEdges(pts);
		// final MyHashSet<Edge> edges = new MyHashSet<Edge>();
		// for (int i = 0; i < pts.size() - 1; i++)
		// edges.add(new Edge(pts.get(i), pts.get(i + 1)));
		// edges.add(new Edge(pts.get(pts.size() - 1), pts.get(0)));
		final Vector normal = Vector.cross(pts.get(0), pts.get(1), pts.get(2));
		return new Polygon(pts, edges, normal, color);
	}

	public static Polygon fromOrderedPoints(final ArrayList<Point> points) {
		return PolygonFactory.fromOrderedPoints(points, Color.DEFAULT);
	}

	private static Polygon fromOrderedPoints(final ArrayList<Point> points,
			final Color color) {
		if (!PolygonFactory.validateInput(points))
			throw new IllegalArgumentException("Invalid set of points!");
		final MyHashSet<Edge> edges = new MyHashSet<Edge>();
		for (int i = 0; i < points.size() - 1; i++)
			edges.add(new Edge(points.get(i), points.get(i + 1)));
		edges.add(new Edge(points.get(points.size() - 1), points.get(0)));
		final Vector normal = Vector.cross(points.get(0), points.get(1),
				points.get(2));
		return new Polygon(points, edges, normal, color);
	}

	public static MyHashSet<Polygon> triangulateCentroid(
			final ArrayList<Point> points, final Color color) {
		if (points.size() < 3)
			throw new IllegalArgumentException("Invalid set of points!");
		final MyHashSet<Polygon> triangles = new MyHashSet<Polygon>();

		// if (points.size() == 3)
		// triangles.add(PolygonFactory.createTriangle(points, color));
		// else
		// {
		Point centroid = new Point();
		for (final Point p : points)
			centroid = centroid.add(p);
		centroid = centroid.times(1.0f / points.size());

		final Point[] pts = points.toArray(new Point[0]);
		for (int i = 0; i < pts.length - 1; i++) {
			final ArrayList<Point> triPts = new ArrayList<Point>();
			triPts.add(centroid);
			triPts.add(pts[i]);
			triPts.add(pts[i + 1]);
			triangles.add(PolygonFactory.createTriangle(triPts, color));
		}
		{
			final ArrayList<Point> triPts = new ArrayList<Point>();
			triPts.add(centroid);
			triPts.add(pts[pts.length - 1]);
			triPts.add(pts[0]);
			triangles.add(PolygonFactory.createTriangle(triPts, color));
		}
		// }
		return triangles;
	}

	public static MyHashSet<Polygon> triangulateFan(
			final ArrayList<Point> points, final Color color) {
		if (points.size() < 3)
			throw new IllegalArgumentException("Invalid set of points!");
		final MyHashSet<Polygon> triangles = new MyHashSet<Polygon>();
		final Point[] pts = points.toArray(new Point[0]);
		for (int i = 1; i < pts.length - 1; i++) {
			final ArrayList<Point> triPts = new ArrayList<Point>();
			triPts.add(pts[0]);
			triPts.add(pts[i]);
			triPts.add(pts[i + 1]);
			triangles.add(PolygonFactory.createTriangle(triPts, color));
		}
		return triangles;
	}

	public static Polygon createTriangle(final ArrayList<Point> points,
			final Color color) {
		if (points.size() != 3)
			throw new IllegalArgumentException("Invalid set of points!");
		final Point[] pts = points.toArray(new Point[0]);
		final MyHashSet<Edge> edges = new MyHashSet<Edge>();
		edges.add(new Edge(pts[0], pts[1]));
		edges.add(new Edge(pts[1], pts[2]));
		edges.add(new Edge(pts[2], pts[0]));
		final Vector normal = Vector.cross(pts[0], pts[1], pts[2]);
		return new Polygon(points, edges, normal, color);
	}

	private static boolean PLANARITY_CHECKING = false;
	private static float PLANARITY_CHECKING_MAX_DOT_PRODUCT_SIZE = 1e-3f;

	private static boolean validateInput(final ArrayList<Point> points) {
		if (points.size() < 3)
			return false;

		if (PolygonFactory.PLANARITY_CHECKING) {
			final Point[] pts = points.toArray(new Point[0]);
			final Vector PQ = new Vector(pts[0], pts[1]);
			final Vector PR = new Vector(pts[0], pts[2]);
			final Vector cross = Vector.cross(PQ, PR);
			for (int i = 3; i < pts.length; i++) {
				final Vector PS = new Vector(pts[0], pts[i]);
				final float dotProduct = Triple.dotProduct(PS, cross);
				if (Math.abs(dotProduct) > PolygonFactory.PLANARITY_CHECKING_MAX_DOT_PRODUCT_SIZE) {
					System.out.println("Dot product = " + dotProduct
							+ " is too large to continue!");
					System.out.println("Points are: " + points.toString());
					return false;
				}
			}
		}

		return true;
	}

}
