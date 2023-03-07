package com.sweta.basic;

import java.util.ArrayList;
import java.util.Arrays;

import com.sweta.basic.Polygon.Edge;

public class ConvexHull {

	public static ArrayList<Point> getConvexHullOrderedPoints(final MyHashSet<Point> points) {
		final int n = points.size();
		if (n == 3) {
			return ConvexHull.getConvexHullOrderedPointsTriangle(points);
		} else if (n == 4) {
			return ConvexHull.getConvexHullOrderedPointsQuad(points);
		} else {
			throw new RuntimeException("Only triangles and quads are supported!");
		}
	}

	private static ArrayList<Point> getConvexHullOrderedPointsTriangle(final MyHashSet<Point> points) {
		final ArrayList<Point> retVal = new ArrayList<>();
		retVal.addAll(points);
		return retVal;
	}

	private static ArrayList<Point> getConvexHullOrderedPointsQuad(final MyHashSet<Point> points) {
		final Point[] pts = points.toArray(new Point[0]);

		final Edge[] edges = new Edge[6];
		edges[0] = new Edge(pts[0], pts[1]);
		edges[1] = new Edge(pts[0], pts[2]);
		edges[2] = new Edge(pts[0], pts[3]);
		edges[3] = new Edge(pts[1], pts[2]);
		edges[4] = new Edge(pts[1], pts[3]);
		edges[5] = new Edge(pts[2], pts[3]);
		Arrays.sort(edges);
		// final Edge diag1 = edges[4];
		// final Edge diag2 = edges[5];

		final Edge diag1 = edges[5];
		Edge diag2 = null;
		for (int i = 4; i >= 0; i--) {
			diag2 = edges[i];
			if (!diag1.meets(diag2)) {
				break;
			}
		}

		final Point[] diag1Pts = diag1.getEnds().toArray(new Point[0]);
		final Point[] diag2Pts = diag2.getEnds().toArray(new Point[0]);

		final ArrayList<Point> retVal = new ArrayList<>();
		retVal.add(diag1Pts[0]);
		retVal.add(diag2Pts[0]);
		retVal.add(diag1Pts[1]);
		retVal.add(diag2Pts[1]);
		return retVal;
	}

	public static MyHashSet<Edge> getConvexHullEdges(final ArrayList<Point> points) {
		final int n = points.size();
		if (n == 3) {
			return ConvexHull.getConvexHullEdgesTriangle(points);
		} else if (n == 4) {
			return ConvexHull.getConvexHullEdgesQuad(points);
		} else {
			throw new RuntimeException("Only triangles and quads are supported!");
		}
	}

	private static MyHashSet<Edge> getConvexHullEdgesTriangle(final ArrayList<Point> points) {
		final MyHashSet<Edge> retVal = new MyHashSet<>();
		final Point[] pts = points.toArray(new Point[0]);
		retVal.add(new Edge(pts[0], pts[1]));
		retVal.add(new Edge(pts[1], pts[2]));
		retVal.add(new Edge(pts[2], pts[0]));
		return retVal;
	}

	private static MyHashSet<Edge> getConvexHullEdgesQuad(final ArrayList<Point> points) {
		final Point[] pts = points.toArray(new Point[0]);
		final Edge[] edges = new Edge[6];
		edges[0] = new Edge(pts[0], pts[1]);
		edges[1] = new Edge(pts[0], pts[2]);
		edges[2] = new Edge(pts[0], pts[3]);
		edges[3] = new Edge(pts[1], pts[2]);
		edges[4] = new Edge(pts[1], pts[3]);
		edges[5] = new Edge(pts[2], pts[3]);
		Arrays.sort(edges);

		final Edge diag1 = edges[5];
		Edge diag2 = null;
		for (int i = 4; i >= 0; i--) {
			diag2 = edges[i];
			if (!diag1.meets(diag2)) {
				break;
			}
		}

		final MyHashSet<Edge> retVal = new MyHashSet<>();
		retVal.add(edges[0]);
		retVal.add(edges[1]);
		retVal.add(edges[2]);
		retVal.add(edges[3]);
		retVal.add(edges[4]);
		retVal.add(edges[5]);
		retVal.remove(diag1);
		retVal.remove(diag2);
		return retVal;
	}

}
