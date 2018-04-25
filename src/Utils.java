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
}
