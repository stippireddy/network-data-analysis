import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;

public class VirtualBitMap {
	int m;
	// Virtual bit array size is s
	int s;
	// The array to hold the 's' random numbers
	int[] randomNumbers;
	// Physical bit array of size m
	BitSet bitset;

	public VirtualBitMap(int m, int s) {
		this.m = m;
		this.s = s;
		randomNumbers = new int[s];
		bitset = new BitSet(m);
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < s; i++) {
			randomNumbers[i] = r.nextInt(Integer.MAX_VALUE);
		}
	}

	public void calculateAndOutputSpread(String fileIn, String fileOut) {
		LinkedHashSet<String> setOfSourceIPs = new LinkedHashSet<>();
		HashMap<String, Integer> actualValues = Utils.readSpreadData();
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
				bitset.set(Utils.getHashcodeInRange(xorRandWithSourceIP, m));
			}
			FileWriter fw = new FileWriter(new File(fileOut));
			Iterator<String> iterator = setOfSourceIPs.iterator();
			while (iterator.hasNext()) {
				String source = iterator.next();
				int estimate = offlineEstimation(source);
				fw.write(source + "\t"  + actualValues.get(source) + "\t"+ estimate + "\n");
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

	private int offlineEstimation(String source) {
		double sCardinality = 0;
		for (int rand : randomNumbers) {
			if (bitset.get(Utils.getHashcodeInRange(Utils.covertIPtoInt(source) ^ rand, m))) {
				sCardinality++;
			}
		}
		sCardinality = s - sCardinality;
		double mCardinality = m - bitset.cardinality();
		double VS = sCardinality / (double) s;
		double VM = mCardinality / (double) m;
		return (int) (s * (Math.log(VM) - Math.log(VS)));
	}

	public static void main(String[] args) throws FileNotFoundException {
		VirtualBitMap t = new VirtualBitMap(4_000_000, 1024);
		t.calculateAndOutputSpread("traffic.txt", "virtualbitmap.txt");
	}
}
