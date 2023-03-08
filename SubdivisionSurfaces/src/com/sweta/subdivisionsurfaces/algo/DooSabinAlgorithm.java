package com.sweta.subdivisionsurfaces.algo;

import java.util.ArrayList;
import java.util.HashMap;

import com.sweta.subdivisionsurfaces.MyHashSet;
import com.sweta.subdivisionsurfaces.Point;
import com.sweta.subdivisionsurfaces.Polygon;
import com.sweta.subdivisionsurfaces.PolygonFactory;
import com.sweta.subdivisionsurfaces.PolygonMesh;
import com.sweta.subdivisionsurfaces.Polygon.Edge;

public class DooSabinAlgorithm extends AbstractSubdivisionAlgorithm {

	private static class FaceVertex {

		public Point vertex;
		public Polygon face;

		public FaceVertex(final Point vertex, final Polygon face) {
			this.vertex = vertex;
			this.face = face;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (this.face == null ? 0 : this.face.hashCode());
			result = prime * result + (this.vertex == null ? 0 : this.vertex.hashCode());
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
			final FaceVertex other = (FaceVertex) obj;
			if (this.face == null) {
				if (other.face != null) {
					return false;
				}
			} else if (!this.face.equals(other.face)) {
				return false;
			}
			if (this.vertex == null) {
				if (other.vertex != null) {
					return false;
				}
			} else if (!this.vertex.equals(other.vertex)) {
				return false;
			}
			return true;
		}

	}

	private final HashMap<Polygon, Point> facePoints = new HashMap<>();

	private final HashMap<Edge, Point> edgePoints = new HashMap<>();

	private final HashMap<FaceVertex, Point> newVertices = new HashMap<>();

	public DooSabinAlgorithm(final PolygonMesh mesh) {
		super(mesh);
		this.facePoints.clear();
		this.edgePoints.clear();
		this.newVertices.clear();
	}

	private Point getNewVertex(final Point p, final Polygon face) {
		final MyHashSet<Edge> edges = new MyHashSet<>();
		for (final Edge e : face.getEdges()) {
			if (e.isEnd(p)) {
				edges.add(e);
			}
		}
		if (edges.size() != 2) {
			throw new RuntimeException("edges.size() != 2");
		}
		final Edge[] edgeArr = edges.toArray(new Edge[0]);
		final Point a = p;
		final Point b = this.edgePoints.get(edgeArr[0]);
		final Point c = this.edgePoints.get(edgeArr[1]);
		final Point d = this.facePoints.get(face);
		Point retVal = a.add(b).add(c).add(d);
		retVal = retVal.times(1.0f / 4);
		return retVal;
	}

	private Point getFacePoint(final Polygon face) {
		Point retVal = new Point();
		for (final Point p : face.getPoints()) {
			retVal = retVal.add(p);
		}
		retVal = retVal.times(1.0f / face.getPoints().size());
		return retVal;
	}

	private Point getEdgePoint(final Edge edge) {
		return edge.midPoint();
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
		for (final Polygon face : this.mesh.faces) {
			for (final Point p : face.getPoints()) {
				final FaceVertex fv = new FaceVertex(p, face);
				final Point pt = this.getNewVertex(p, face);
				this.newVertices.put(fv, pt);
			}
		}

		final MyHashSet<Polygon> set = new MyHashSet<>();

		for (final Polygon face : this.mesh.faces) {
			set.add(this.getNewFFace(face));
		}

		for (final Edge edge : this.mesh.edges) {
			set.add(this.getNewEFace(edge));
		}

		for (final Point vertex : this.mesh.points) {
			set.add(this.getNewVFace(vertex));
		}

		return set;
	}

	private Polygon getNewFFace(final Polygon face) {
		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Point p : face.getPoints()) {
			final FaceVertex fv = new FaceVertex(p, face);
			points.add(this.newVertices.get(fv));
		}
		return PolygonFactory.fromPointsConvex(points);
	}

	// private Polygon getNewFFace(final Polygon face) {
	// final ArrayList<Point> points = new ArrayList<Point>();
	// for (final Point p : face.getOrderedPoints()) {
	// final FaceVertex fv = new FaceVertex(p, face);
	// points.add(this.newVertices.get(fv));
	// }
	// return PolygonFactory.fromOrderedPoints(points);
	// }

	private Polygon getNewEFace(final Edge edge) {
		final ArrayList<Point> points = new ArrayList<>();

		final Point end0 = edge.a;
		final Point end1 = edge.b;

		final MyHashSet<Polygon> faces = this.mesh.edgeFaces.get(edge);
		if (faces.size() != 2) {
			throw new RuntimeException("faces.size() != 2");
		}
		final Polygon[] facesArr = faces.toArray(new Polygon[0]);
		final Polygon face0 = facesArr[0];
		final Polygon face1 = facesArr[1];

		points.add(this.newVertices.get(new FaceVertex(end0, face0)));
		points.add(this.newVertices.get(new FaceVertex(end0, face1)));
		points.add(this.newVertices.get(new FaceVertex(end1, face1)));
		points.add(this.newVertices.get(new FaceVertex(end1, face0)));

		return PolygonFactory.fromOrderedPoints(points);
	}

	private Polygon getNewVFace(final Point vertex) {
		final MyHashSet<Point> points = new MyHashSet<>();
		for (final Polygon face : this.mesh.pointFaces.get(vertex)) {
			final FaceVertex fv = new FaceVertex(vertex, face);
			points.add(this.newVertices.get(fv));
		}
		return PolygonFactory.fromPointsConvex(points);
	}

	// private Polygon getNewVFace(final Point vertex) {
	// final ArrayList<Point> points = new ArrayList<Point>();
	// for (final Polygon face : this.mesh.pointFaces.get(vertex)) {
	// final FaceVertex fv = new FaceVertex(vertex, face);
	// points.add(this.newVertices.get(fv));
	// }
	// return PolygonFactory.fromOrderedPoints(points);
	// }
}
