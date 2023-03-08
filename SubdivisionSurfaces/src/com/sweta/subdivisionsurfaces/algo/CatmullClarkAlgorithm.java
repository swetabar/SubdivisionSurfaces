package com.sweta.subdivisionsurfaces.algo;

import java.util.ArrayList;
import java.util.HashMap;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Point;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonFactory;
import com.sweta.subdivisionsurfaces.PolygonMesh;
import com.sweta.subdivisionsurfaces.Polygon.Edge;

public class CatmullClarkAlgorithm extends AbstractSubdivisionAlgorithm {

	private final HashMap<Polygon, Point> facePoints = new HashMap<>();

	private final HashMap<Edge, Point> edgePoints = new HashMap<>();

	private final HashMap<Point, Point> newVertices = new HashMap<>();

	public CatmullClarkAlgorithm(final PolygonMesh mesh) {
		super(mesh);
		this.facePoints.clear();
		this.edgePoints.clear();
		this.newVertices.clear();
	}

	private Point getNewVertex(final Point p) {
		if (this.mesh.isPointOnHole(p)) {
			final Point a = p.times(1.0f / 2);
			final Point c = this.getAverageMidBoundaryEdges(p).times(1.0f / 2);
			return a.add(c);
		} else {
			final int n = this.mesh.pointFaces.get(p).size();
			final Point a = p.times((float) (n - 3) / n);
			final Point b = this.getAverageFacePoint(p).times(1.0f / n);
			final Point c = this.getAverageMidEdges(p).times(2.0f / n);
			return a.add(b).add(c);
		}
	}

	private Point getAverageMidEdges(final Point p) {
		final MyHashSet<Edge> edges = this.mesh.pointEdges.get(p);
		Point retVal = new Point();
		for (final Edge e : edges) {
			retVal = retVal.add(e.midPoint());
		}
		retVal = retVal.times(1.0f / edges.size());
		return retVal;
	}

	private Point getAverageMidBoundaryEdges(final Point p) {
		final MyHashSet<Edge> edges = this.mesh.pointEdges.get(p);
		Point retVal = new Point();
		int n = 0;
		for (final Edge e : edges) {
			if (this.mesh.isEdgeOnHole(e)) {
				retVal = retVal.add(e.midPoint());
				n++;
			}
		}
		if (n > 0) {
			retVal = retVal.times(1.0f / n);
		}
		return retVal;
	}

	private Point getAverageFacePoint(final Point p) {
		final MyHashSet<Polygon> faces = this.mesh.pointFaces.get(p);
		Point retVal = new Point();
		for (final Polygon poly : faces) {
			retVal = retVal.add(this.facePoints.get(poly));
		}
		retVal = retVal.times(1.0f / faces.size());
		return retVal;
	}

	private Point getFacePoint(final Polygon face) {
		Point retVal = new Point();
		for (final Point p : face.getPoints()) {
			retVal = retVal.add(p);
		}
		return retVal.times(1.0f / face.getPoints().size());
	}

	private Point getEdgePoint(final Edge edge) {
		if (this.mesh.isEdgeOnHole(edge)) {
			return edge.midPoint();
		} else {
			final MyHashSet<Polygon> faces = this.mesh.edgeFaces.get(edge);
			Point retVal = new Point();
			for (final Polygon face : faces) {
				retVal = retVal.add(this.facePoints.get(face));
			}
			retVal = retVal.times(1.0f / faces.size());
			retVal = retVal.add(edge.midPoint());
			retVal = retVal.times(1.0f / 2);
			return retVal;
		}
	}

	@Override
	public MyHashSet<Polygon> run() {

		// Filling the facepoints
		for (final Polygon face : this.mesh.faces) {
			this.facePoints.put(face, this.getFacePoint(face));
		}

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

		return set;
	}

	private MyHashSet<Polygon> getNewFaces(final Polygon face) {
		if (face.getPoints().size() < 3) {
			throw new RuntimeException("Invalid polygon!");
		}

		final Point[] points = face.getPoints().toArray(new Point[0]);
		final MyHashSet<Polygon> retVal = new MyHashSet<>();
		for (final Point p : points) {
			retVal.add(this.getNewFaceForVertex(face, p));
		}
		return retVal;
	}

	private Polygon getNewFaceForVertex(final Polygon face, final Point p) {
		if (!face.hasVertex(p)) {
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
		final Point c = this.facePoints.get(face);
		final Point d = this.edgePoints.get(incidentEdges.get(1));

		final ArrayList<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);

		return PolygonFactory.fromOrderedPoints(points);
	}

}
