package edu.wsu.MechTurk.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class FileReader {

	public static void main(String[] args) throws IOException {

		ArrayList<String[]> results = new ArrayList<String[]>();

		FileInputStream fstream = new FileInputStream("./src/result1id" + ".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String line;
		int counter = 0;
		String[] newWorker = new String[3];
		boolean reliableWorker = true;
		while ((line = br.readLine()) != null) {

			System.out.println(line);

			if (line.contains("WorkerID:")) {

				String[] parts = line.split(" ");
				newWorker[0] = parts[1];
			}

			else if (line.contains("NOT ")) {
				counter++;
			} else if (line.contains("NOT_CORRECT")) {
				reliableWorker = false;
				counter--;
			} else if (line.contains("</QuestionFormAnswers>")) {

				newWorker[1] = String.valueOf(counter);
				newWorker[2] = String.valueOf(reliableWorker);
				counter = 0;
				reliableWorker = true;
				results.add(newWorker);
				newWorker = new String[3];
			}

		}

		for (int i = 0; i < results.size(); i++) {
			System.out.println(results.get(i)[0]);
		}

		System.out.println("-------------------------------------------------------");
		int c = 0;
		HashSet<String> IDs = new HashSet<String>();

		for (int j = 0; j < results.size(); j++) {
			if ((Integer.parseInt(results.get(j)[1]) <= 2) && !(results.get(j)[2].contains("false"))) {
				System.out.println(results.get(j)[0] + "   " + results.get(j)[1] + "   " + results.get(j)[2]);
				IDs.add(results.get(j)[0]);
				c++;
			}
		}
		System.out.println(results.size());
		System.out.println(c);
		System.out.println(IDs.size());

	}

}
