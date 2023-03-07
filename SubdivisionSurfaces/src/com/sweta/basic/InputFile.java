package com.sweta.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class InputFile {

	public MyHashSet<Polygon> polygons = new MyHashSet<>();

	public InputFile(final String filePath) throws ParseException, IOException {
		final ArrayList<Point> points = new ArrayList<>();
		// final MyHashSet<Point> points = new MyHashSet<Point>();
		final File file = new File(filePath);
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			final String[] tokens = line.split(" ");
			if (tokens.length != 4) {
				continue;
			}
			final float a = Float.parseFloat(tokens[1]);
			final float b = Float.parseFloat(tokens[2]);
			final float c = Float.parseFloat(tokens[3]);
			if (tokens[0].equalsIgnoreCase("P")) {
				points.clear();
			} else if (tokens[0].equalsIgnoreCase("v")) {
				points.add(new Point(a, b, c));
			} else if (tokens[0].equalsIgnoreCase("E")) {
				this.polygons.add(PolygonFactory.fromOrderedPoints(points));
				// this.polygons.add(PolygonFactory.fromPointsConvex(points));
			}
		}
		br.close();
	}
}