import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import com.sweta.basic.InputFile;
import com.sweta.basic.MyHashSet;
import com.sweta.basic.Polygon;
import com.sweta.basic.PolygonMesh;
import com.sweta.basic.algo.AbstractSubdivisionAlgorithm;
import com.sweta.basic.algo.CatmullClarkAlgorithm;
import com.sweta.basic.algo.DooSabinAlgorithm;
import com.sweta.basic.algo.LoopAlgorithm;
import com.sweta.basic.algo.MidpointAlgorithm;
import com.sweta.basic.algo.Root3Algorithm;
import com.sweta.basic.algo.TriangulateAlgorithm;

public class SubD {

	private enum Algos {
		TRIANGULATE("Triangulate"), //
		CATMULL_CLARK("Catmull-Clark"), //
		DOO_SABIN("Doo-Sabin"), //
		MIDPOINT("Midpoint"), //
		LOOP("Loop"), //
		ROOT3("√3"); //

		final String name;

		Algos(final String name) {
			this.name = name;
		}

	}

	public static void main(final String[] args) {
		final Algos algo;

		if (args.length != 4) {
			System.out.println("4 args expected!\n");
			return;
		}

		final String algoString = args[2];
		if (algoString.equalsIgnoreCase("T")) {
			algo = Algos.TRIANGULATE;
		} else if (algoString.equalsIgnoreCase("C")) {
			algo = Algos.CATMULL_CLARK;
		} else if (algoString.equalsIgnoreCase("D")) {
			algo = Algos.DOO_SABIN;
		} else if (algoString.equalsIgnoreCase("M")) {
			algo = Algos.MIDPOINT;
		} else if (algoString.equalsIgnoreCase("L")) {
			algo = Algos.LOOP;
		} else if (algoString.equalsIgnoreCase("R")) {
			algo = Algos.ROOT3;
		} else {
			System.out.println("Invalid algorithm selection!");
			return;
		}

		final String inputFilePath = args[0];
		System.out.println("\nReading input file '" + inputFilePath + "' ...");
		InputFile inFile = null;
		try {
			inFile = new InputFile(inputFilePath);
		} catch (final ParseException | IOException e) {
			e.printStackTrace();
			return;
		}
		final int n = inFile.polygons.size();
		System.out.println("Input file has " + n + " polygons.");

		System.out.println("Using " + algo.name + " subdivision algorithm.");

		final int level = Integer.parseInt(args[3]);
		System.out.println("Using subdivision level = " + level);

		final String outputFilePath = args[1];
		System.out.println("Writing to output file '" + outputFilePath + "' ...\n");
		final File outFile = new File(outputFilePath);

		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

			MyHashSet<Polygon> polySet = new MyHashSet<>();
			polySet.addAll(inFile.polygons);

			for (int i = 1; i <= level; i++) {
				System.out.println("\tRunning subdivision level " + i);
				final PolygonMesh mesh = new PolygonMesh(polySet);

				AbstractSubdivisionAlgorithm algorithm = null;
				if (algo == Algos.TRIANGULATE) {
					algorithm = new TriangulateAlgorithm(mesh);
				} else if (algo == Algos.CATMULL_CLARK) {
					algorithm = new CatmullClarkAlgorithm(mesh);
				} else if (algo == Algos.DOO_SABIN) {
					algorithm = new DooSabinAlgorithm(mesh);
				} else if (algo == Algos.MIDPOINT) {
					algorithm = new MidpointAlgorithm(mesh);
				} else if (algo == Algos.LOOP) {
					algorithm = new LoopAlgorithm(mesh);
				} else if (algo == Algos.ROOT3) {
					algorithm = new Root3Algorithm(mesh);
				}

				polySet = algorithm.run();
			}

			for (final Polygon poly : polySet) {
				bw.append(poly.toString());
				bw.append("\n");
			}

			bw.close();
			System.out.println("Done.\n");

		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}

	}
}
