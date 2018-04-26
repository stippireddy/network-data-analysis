import java.io.File;
import java.io.FileNotFoundException;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;

public class BloomFilter {

	private int size;
	private int numberOfHashes;
	private long[] randomNumbers;
	private BitSet bFilter;

	public BloomFilter(int size, int numberOfHashes) {
		this.size = size;
		this.numberOfHashes = numberOfHashes;
		randomNumbers = new long[numberOfHashes];
		this.bFilter = new BitSet(size);
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < numberOfHashes; i++) {
			randomNumbers[i] = r.nextLong();
		}
	}

	public void encodeElement(Long element) {
		for (int i = 0; i < numberOfHashes; i++) {
			bFilter.set(Utils.getHashcodeInRange(element ^ randomNumbers[i], size));
		}
	}

	public boolean isElementExists(Long element) {
		for (int i = 0; i < numberOfHashes; i++) {
			if (!bFilter.get(Utils.getHashcodeInRange(element ^ randomNumbers[i], size))) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		BloomFilter bloomFilter = new BloomFilter(40_000_000, 23);
		LinkedHashSet<Long> actualData = new LinkedHashSet<>();
		double falsePositive = 0;
		double falseIpsTested = 0;
		Scanner sc = null;
		try {
			sc = new Scanner(new File("traffic.txt"));
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
				Long sourceDestination = Utils.getCombinedSourceDest(input[0], input[1]);
				actualData.add(sourceDestination);
				bloomFilter.encodeElement(sourceDestination);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File with this name can't be found");
		} finally {
			sc.close();
		}
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 1_000_000; i++) {
			long testElement = r.nextLong();
			if (!actualData.contains(testElement)) {
				falseIpsTested++;
				if (bloomFilter.isElementExists(testElement)) {
					falsePositive++;
				}
			}
		}
		System.out.println("No of elements encoded: " + actualData.size());
		System.out.println("No of elements tested: " + 1_000_000);
		System.out.println("Bloom Filter Size: " + 8_000_000);
		System.out.println("No of Hash Functions: " + 23);
		System.out.println("Theoretical False Positive Ratio: " + 0.0000001);
		System.out.println("Estimated False Positive Ratio: " + falsePositive / falseIpsTested);
	}

}
