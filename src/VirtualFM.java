import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;

public class VirtualFM {
	int[] integerFM;
	int s;
	int[] randomNumbers;
	// Physical bit array of size m
	private int m;
	double phi = 0.77351;

	public VirtualFM(int m, int s) {
		this.m = m;
		this.integerFM = new int[m];
		this.s = s;
		randomNumbers = new int[s];
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < s; i++) {
			randomNumbers[i] = r.nextInt(Integer.MAX_VALUE);
		}
	}

	public void calculateAndOutputSpread(String fileIn, String fileOut) {
		LinkedHashSet<String> setOfSourceIPs = new LinkedHashSet<>();
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
				// System.out.println(Arrays.toString(input));
				setOfSourceIPs.add(input[0]);
				// Recording Online
				int randomIndexAssigned = Utils.getHashcodeInRange(input[1], s);
				// System.out.println(randomIndexAssigned);
				Integer xorRandWithSourceIP = Utils.covertIPtoInt(input[0]) ^ randomNumbers[randomIndexAssigned];
				// TODO move geometric hash to left
				integerFM[Utils.getHashcodeInRange(xorRandWithSourceIP, m)] |= getGeometricHash(input[1]);
			}
			FileWriter fw = new FileWriter(new File(fileOut));
			Iterator<String> iterator = setOfSourceIPs.iterator();
			double totalZCount = 0;
			for (int currentFMSketch : integerFM) {
				totalZCount += countLeadingOnes(currentFMSketch);
			}
			double totalSize = m;
			double totalNoise = (totalSize * Math.pow(totalZCount/totalSize, 2))/phi; 
			while (iterator.hasNext()) {
				String source = iterator.next();
				double estimate = offlineEstimation(source, totalNoise);
				fw.write(source + "\t" + estimate + "\n");
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

	private int getGeometricHash(String string) {
		int hashCode = string.hashCode();
		int zeroCount = 0;
		int check = 1;
		for (int i = 0; i < 32; i++) {
			if ((hashCode & check) == 1) {
				return zeroCount;
			}
			zeroCount++;
			check <<= 1;
		}
		return zeroCount;
	}

	private double offlineEstimation(String source, double totalNoise) {
		// TODO Wrong
		double sCardinality = 0;
		for (int rand : randomNumbers) {
			sCardinality += countLeadingOnes(Utils.getHashcodeInRange(Utils.covertIPtoInt(source) ^ rand, m));
		}
		return (double) s * ((Math.pow(totalZCount, 2) - Math.pow(sCardinality, 2)) / phi);
	}

	private int countLeadingOnes(int currentFMSketch) {
		int leadingOnesCount = 0;
		int check = 1;
		for (int i = 0; i < 32; i++) {
			if ((currentFMSketch & check) == 0) {
				return leadingOnesCount;
			}
			leadingOnesCount++;
			check <<= 1;
		}
		return leadingOnesCount;
	}

	public static void main(String[] args) throws FileNotFoundException {
		VirtualBitMap t = new VirtualBitMap(1_000_000, 128);
		t.calculateAndOutputSpread("traffic.txt", "virtualbitmap.txt");
	}

}
