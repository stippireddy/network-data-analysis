import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class Utils {
	public static int covertIPtoInt(String ip) {
		String[] ipSplit = ip.split("\\.");
		return Integer.parseInt(ipSplit[0]) * 256 * 256 * 256 + Integer.parseInt(ipSplit[1]) * 256 * 256
				+ Integer.parseInt(ipSplit[2]) * 256 + Integer.parseInt(ipSplit[3]);
	}

	public static int getHashcodeInRange(String input, int maxNum) {
		return getHashcodeInRange(covertIPtoInt(input), maxNum);
	}

	public static String[] parseInput(String input) throws ArrayIndexOutOfBoundsException {
		int left = 0, right = 0;
		while (right < input.length() && input.charAt(right) != ' ') {
			right++;
		}
		String[] result = new String[3];
		result[0] = input.substring(left, right);
		while (right < input.length() && input.charAt(right) == ' ') {
			right++;
		}
		left = right;
		while (right < input.length() && input.charAt(right) != ' ') {
			right++;
		}
		result[1] = input.substring(left, right);
		while (right < input.length() && input.charAt(right) == ' ') {
			right++;
		}
		left = right;
		while (right < input.length() && input.charAt(right) != ' ') {
			right++;
		}
		result[2] = input.substring(left, right);
		return result;
	}

	public static int getHashcodeInRange(Integer input, int maxNum) {
		int hashCode = input.hashCode() % maxNum;
		return hashCode < 0 ? hashCode + maxNum : hashCode;
	}

	public static int getHashcodeInRange(Long input, int maxNum) {
		int hashCode = input.hashCode() % maxNum;
		return hashCode < 0 ? hashCode + maxNum : hashCode;
	}

	public static long getCombinedSourceDest(String src, String dst) {
		long srcInt = Utils.covertIPtoInt(src);
		long dstInt = Utils.covertIPtoInt(dst);
		srcInt <<= 32;
		long result = srcInt | dstInt;
		return result;
	}

	public static HashMap<String, Integer> readSpreadData() {
		HashMap<String, Integer> actualValue = new HashMap<>();
		Scanner sc = null;
		try {
			sc = new Scanner(new File("traffic_spread.txt"));
			while (sc.hasNextLine()) {
				String s = sc.nextLine().trim().replaceAll("\\s+", " ");
				String[] input = s.split(" ");
				actualValue.put(input[0], Integer.parseInt(input[1]));
			}
		} catch (FileNotFoundException e) {
			System.out.println("File with this name can't be found");
		} finally {
			sc.close();
		}
		return actualValue;
	}
}
