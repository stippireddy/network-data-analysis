import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

public class CountMin {

	private int d;
	private long[] randomNumbers;
	private int m;
	private int[][] recorder;

	public CountMin(int m, int d) {
		this.m = m;
		this.d = d;
		randomNumbers = new long[d];
		recorder = new int[d][m];
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < d; i++) {
			randomNumbers[i] = r.nextLong();
		}
	}

	private void calculateAndOutputFlowSize(String fileIn, String fileOut) {
		LinkedHashMap<SourceDest, String> actualData = new LinkedHashMap<>();
		Scanner sc = null;
		try {
			sc = new Scanner(new File(fileIn));
			sc.nextLine();
			while (sc.hasNextLine()) {
				String[] input = null;
				try {
					input = Utils.parseInput(sc.nextLine());
					if (input.length < 3) {
						System.out.println("Input file not in the expected format");
						return;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Input file not in the expected format");
					return;
				}
				Long sourceDestination = getCombinedSourceDest(input[0], input[1]);
				actualData.put(new SourceDest(input[0], input[1]), input[2]);
				// Recording Online
				for (int i = 0; i < d; i++) {
					recorder[i][Utils.getHashcodeInRange(sourceDestination ^ randomNumbers[i], m)] += Integer
							.parseInt(input[2]);
				}
			}
			FileWriter fw = new FileWriter(new File(fileOut));
			Iterator<SourceDest> iterator = actualData.keySet().iterator();
			while (iterator.hasNext()) {
				SourceDest key = iterator.next();
				String source = key.source;
				String destination = key.destination;
				int estimate = offlineEstimation(source, destination);
				fw.write(source + "\t" + destination + "\t" + actualData.get(key) + "\t" + estimate + "\n");
			}
			fw.close();
		} catch (FileNotFoundException e) {
			System.out.println("File with this name can't be found");
		} catch (IOException e) {
			System.out.println("Unable to create a file with the given file output name.");
		} finally {
			sc.close();
		}
	}

	private int offlineEstimation(String source, String destination) {
		int result = Integer.MAX_VALUE;
		for (int i = 0; i < d; i++) {
			result = Integer.min(result, recorder[i][Utils
					.getHashcodeInRange(getCombinedSourceDest(source, destination) ^ randomNumbers[i], m)]);
		}
		return result;
	}

	private long getCombinedSourceDest(String src, String dst) {
		long srcInt = Utils.covertIPtoInt(src);
		long dstInt = Utils.covertIPtoInt(dst);
		srcInt <<= 32;
		long result = srcInt | dstInt;
		return result;
	}

	public static void main(String[] args) {
		CountMin c = new CountMin(4_000_000, 4);
		c.calculateAndOutputFlowSize("traffic.txt", "countmin.txt");
	}

}

class SourceDest {
	String source;
	String destination;

	public SourceDest(String source, String destination) {
		this.source = source;
		this.destination = destination;
	}
}
