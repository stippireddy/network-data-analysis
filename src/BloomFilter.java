import java.util.Random;

public class BloomFilter {

	private int size;
	private int numberOfHashes;
	private int[] randomNumbers;

	public BloomFilter(int size, int numberOfHashes) {
		this.size = size;
		this.numberOfHashes = numberOfHashes;
		randomNumbers = new int[numberOfHashes];
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < numberOfHashes; i++) {
			randomNumbers[i] = r.nextInt(Integer.MAX_VALUE);
		}
	}

	public void encodeElement(String element) {

	}

	public boolean isElementExists(String element) {
		return false;
	}

	public static void main(String[] args) {

	}
}
