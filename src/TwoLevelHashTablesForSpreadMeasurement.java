import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class TwoLevelHashTablesForSpreadMeasurement {

	public void calculateAndOutputSpread(String fileIn, String fileOut) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(fileIn));
			sc.nextLine();
			HashMap<String, Integer> actualValues = Utils.readSpreadData();
			LinkedHashMap<String, HashSet<String>> map = new LinkedHashMap<>();
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
				map.putIfAbsent(input[0], new HashSet<>());
				map.get(input[0]).add(input[1]);
			}
			FileWriter fw = new FileWriter(new File(fileOut));
			for (Entry<String, HashSet<String>> entry : map.entrySet()) {
				fw.write(entry.getKey() + "\t" + actualValues.get(entry.getKey()) + "\t");
				fw.write(entry.getValue().size() + "\n");
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

	public static void main(String[] args) throws FileNotFoundException {
		TwoLevelHashTablesForSpreadMeasurement t = new TwoLevelHashTablesForSpreadMeasurement();
		t.calculateAndOutputSpread("traffic.txt", "twoLevelHashTables.txt");
	}
}
