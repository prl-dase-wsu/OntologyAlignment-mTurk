package edu.wsu.MechTurk.Utils;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import edu.wsu.MechTurk.LabelsInXML;
import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Resources;

public class HitContentFactory {

	public static ArrayList<String> ontologyNames = new ArrayList<String>();

	static {
		// create folders for outputs/logs
		File dir = new File("./data");
		if (!dir.exists())
			dir.mkdir();
		dir = new File("./data/TEST_HITS");
		if (!dir.exists())
			dir.mkdir();
		dir = new File("./data/tests");
		if (!dir.exists())
			dir.mkdir();
	}

	/**
	 * MatchPair contains two entities in formatted in this way:<br>
	 * ontologyName#entityName
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ArrayList<MatchPair> readInMatchList() throws FileNotFoundException {
		Scanner in = new Scanner(new File(Resources.MATCH_LIST));
		ArrayList<MatchPair> pairs = new ArrayList<HitContentFactory.MatchPair>();
		int matchCount = 0; // first 50 are true, last 50 are false

		while (in.hasNext()) {
			String[] line = in.nextLine().split("\\|");

			String thing1 = line[0].substring(line[0].indexOf("//") + 2, line[0].length());
			String thing2 = line[1].substring(line[1].indexOf("//") + 2, line[1].length());

			pairs.add(new MatchPair(thing1, thing2, (matchCount++ < 50) ? true : false));

		}

		in.close();

		return pairs;
	}

	/**
	 * Map has the <key, value> pairs formatted as:<br>
	 * key - ontologyName#entityName<br>
	 * value - definitions
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Map<String, String> readInDefinitions() throws FileNotFoundException {
		Scanner in = new Scanner(new File(Resources.LIST_OF_DEFINITIONS));
		Map<String, String> definitions = new HashMap<String, String>();

		while (in.hasNext()) {
			String line = in.nextLine();
			String matchName = line.split("\\s+")[0].substring(line.split("\\s+")[0].indexOf("//") + 2);
			String definition = null;
			try {
				definition = line.split("\\s+", 3)[2];
			} catch (Exception e) {
				definition = "Definition not available.";
			}

			if (!definitions.containsKey(matchName))
				definitions.put(matchName, definition);
		}

		in.close();

		return definitions;
	}

	/**
	 * Map has the <key, value> pairs formatted as:<br>
	 * key - ontologyName#entityName<br>
	 * value - list of relationships
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Map<String, ArrayList<String>> readInRelationships() throws FileNotFoundException {
		Scanner in = new Scanner(new File(Resources.LIST_OF_RELATIONSHIPS));
		Map<String, ArrayList<String>> relationships = new HashMap<String, ArrayList<String>>();

		String currentEntity = null;
		while (in.hasNext()) {
			String line = in.nextLine();
			if (line.startsWith("<")) {
				currentEntity = line.substring(line.indexOf("//") + 2, line.length() - 1);
				if (!relationships.containsKey(currentEntity))
					relationships.put(currentEntity, new ArrayList<String>());
			} else {
				relationships.get(currentEntity).add(line);
			}
		}

		in.close();

		return relationships;
	}

	public static Set<QuestionID> createQuestionsIDs(TestType testType, Collection<MatchPair> matchList) {
		Set<QuestionID> ret = new HashSet<QuestionID>();

		Iterator<MatchPair> iterator = matchList.iterator();
		for (int i = 0; i < matchList.size(); i++) {
			ret.add(new QuestionID(testType, i, iterator.next()));
		}

		return ret;
	}

	public static class QuestionID {
		private TestType testType;
		private int questionNo;
		private MatchPair pair;

		public QuestionID(TestType testType, int questionNo, MatchPair pair) {
			this.testType = testType;
			this.questionNo = questionNo;
			this.pair = pair;
		}

		public MatchPair getMatchPair() {
			return this.pair;
		}

		public String getQuestionID() {
			return this.testType.toString() + "_" + this.questionNo + "_" + this.pair.thing1 + "_" + this.pair.thing2
					+ "_" + (this.pair.isMatchCorrect ? "Y" : "N");
		}

		public int getQuestionIDHashed() {
			return (this.testType.toString() + "_" + this.questionNo + "_" + this.pair.thing1 + "_" + this.pair.thing2
					+ "_" + (this.pair.isMatchCorrect ? "Y" : "N")).hashCode();
		}
	};

	public static class MatchPair {
		private final String thing1;
		private final String thing2;
		private final boolean isMatchCorrect;

		public MatchPair(String thing1, String thing2, boolean isMatchCorrect) {
			this.thing1 = thing1;
			this.thing2 = thing2;
			this.isMatchCorrect = isMatchCorrect;
		}

		public MatchPair(MatchPair other) {
			this.thing1 = other.getFirst();
			this.thing2 = other.getSecond();
			this.isMatchCorrect = other.isMatchCorrect;
		}

		public String getFirst() {
			return this.thing1;
		}

		public String getOntologyNameOfFirst() {
			return this.thing1.split("#")[0];
		}

		public String getEntityNameOfFirst() {
			return this.thing1.split("#")[1];
		}

		public String getSecond() {
			return this.thing2;
		}

		public String getOntologyNameOfSecond() {
			return this.thing2.split("#")[0];
		}

		public String getEntityNameOfSecond() {
			return this.thing2.split("#")[1];
		}

		public boolean isMatchCorrect() {
			return this.isMatchCorrect;
		}
	}

	/**
	 * Populates HIT template(s) with parameters provided in map(s).
	 * 
	 * @param pathToTemplate
	 * @param pathToOutput
	 * @param contentToReplace
	 * @return
	 * @throws FileNotFoundException
	 */
	public static void createHitWithContent(String pathToTemplate, String pathToOverview, String pathToOutput,
			Collection<Map<String, String>> contentToReplace) throws FileNotFoundException {
		Scanner in;
		PrintWriter out = new PrintWriter(new File(pathToOutput));

		// append the output with an overview first
		Scanner inOverview = new Scanner(new File(pathToOverview));
		while (inOverview.hasNext()) {
			out.println(inOverview.nextLine());
		}
		inOverview.close();
		for (Map<String, String> map : contentToReplace) {
			in = new Scanner(new File(pathToTemplate));
			while (in.hasNext()) {
				String line = in.nextLine();
				for (String pattern : map.keySet()) {
					if (line.contains(pattern)) {
						line = line.replaceFirst("\\b" + Pattern.quote(pattern) + "\\b", map.get(pattern));
					}
				}
				out.println(line);
			}
			in.close();
		}

		Scanner inCandyQuestion = new Scanner(new File(Resources.CANDY_QUESTION));
		while (inCandyQuestion.hasNext()) {
			out.println(inCandyQuestion.nextLine());
		}
		inCandyQuestion.close();

		out.println("</QuestionForm>");
		out.close();
	}

	/**
	 * 
	 * @param numberOfHits
	 * @param numberOfQuestionsInSingleHit
	 * @param matchPairs
	 * @param definitions
	 * @param outputFileName
	 * @throws FileNotFoundException
	 */
	public static void createTFDefinitionsQuestions(int numberOfHits, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, String> definitions, String outputFileName)
					throws FileNotFoundException {
		// make sure we can create that many unique questions
		if (numberOfHits * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}

		// check if the output path exists
		File outputDir = new File(Resources.TF_DEFINITIONS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.TRUE_FALSE_DEFINITIONS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHits; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				Map<String, String> questionInfo = new HashMap<String, String>();
				// prepare info about two labels
				QuestionID question = getQuestion(setOfMatchQuestionIDs);

				questionInfo.put(LabelsInXML.TF_DEFINITIONS_QUESTIONS_ID, question.getQuestionIDHashed() + "");

				if (question.getMatchPair().getEntityNameOfFirst().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toLowerCase())) {
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst() + "_1");
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond() + "_2");
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_DESCRIPTION_LABEL_A,
							definitions.get(question.getMatchPair().getFirst()));
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_DESCRIPTION_LABEL_B,
							definitions.get(question.getMatchPair().getSecond()));
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT the same as \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\"");
				} else {
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_DESCRIPTION_LABEL_A,
							definitions.get(question.getMatchPair().getFirst()));
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_DESCRIPTION_LABEL_B,
							definitions.get(question.getMatchPair().getSecond()));
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_DEFINITIONS_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" is NOT the same as \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"");

				}

				System.out.println(question.getMatchPair().getEntityNameOfFirst() + "   "
						+ question.getMatchPair().getEntityNameOfSecond());
				labelsInfo.add(questionInfo);
			}

			System.out.println("---------------------------" + labelsInfo.size());
			createHitWithContent(Resources.TF_DEFINITIONS_QUESTION, Resources.TF_DEFINITIONS_OVERVIEW,
					Resources.TF_DEFINITIONS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	public static void createTFVisualssQuestions(int numberOfHits, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, String> definitions, String outputFileName)
					throws FileNotFoundException, MalformedURLException {
		// make sure we can create that many unique questions
		if (numberOfHits * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}
		// check if the output path exists
		File outputDir = new File(Resources.TF_VISUALS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.TRUE_FALSE_VISUALS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHits; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				Map<String, String> questionInfo = new HashMap<String, String>();
				// prepare info about two labels
				QuestionID question = getQuestion(setOfMatchQuestionIDs);
				// naming the pictures .
				System.out.println(question.getMatchPair().getEntityNameOfFirst());
				String[] partsEntOne = question.getMatchPair().getEntityNameOfFirst().toString().split(" |\\_");
				String firstEntName = Arrays.stream(partsEntOne).collect(Collectors.joining("")).toLowerCase();
				System.out.println(firstEntName + "-------------------------------------test");

				System.out.println(question.getMatchPair().getEntityNameOfSecond());
				String[] partsEntTwo = question.getMatchPair().getEntityNameOfSecond().toString().split(" |\\_");
				String secondEntName = Arrays.stream(partsEntTwo).collect(Collectors.joining("")).toLowerCase();
				System.out.println(secondEntName + "-------------------------------------test");

				String imageFilename = question.getMatchPair().getOntologyNameOfFirst().trim() + "-"
						+ question.getMatchPair().getOntologyNameOfSecond().trim() + ".rdf_" + firstEntName.trim() + "-"
						+ secondEntName.trim() + ".png";
				StringBuilder builder = new StringBuilder();
				builder.append("/mturkimages1001/");
				builder.append(imageFilename);
				String imageP = builder.toString();
				URL imagePath = new URL("https", "s3.amazonaws.com", imageP);
				System.out.println(imagePath.toString() + "                                url");

				// getting the size of the image
				Image image = new ImageIcon(imagePath).getImage();
				int height = image.getHeight(null);
				int width = image.getWidth(null);

				questionInfo.put(LabelsInXML.TF_VISUALS_QUESTIONS_ID, question.getQuestionIDHashed() + "");
				questionInfo.put(LabelsInXML.TF_VISUAL_IMG_LINK_LABEL_A_AND_B, imagePath.toString());

				if (question.getMatchPair().getEntityNameOfFirst().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toLowerCase())) {
					questionInfo.put(LabelsInXML.TF_VISUALS_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst() + "_1");
					questionInfo.put(LabelsInXML.TF_VISUALS_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond() + "_2");
					questionInfo.put(LabelsInXML.TF_VISUAL_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_VISUAL_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT the same as \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\"");
				} else {
					questionInfo.put(LabelsInXML.TF_VISUALS_LABEL_A, question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.TF_VISUALS_LABEL_B, question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.TF_VISUAL_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_VISUAL_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" is NOT the same as \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"");

				}

				if ((height > 800) || (width > 800)) {
					questionInfo.put(LabelsInXML.TF_VISUAL_HEIGHT, "1000");
					questionInfo.put(LabelsInXML.TF_VISUAL_WIDTH, "800");
				}

				else if (((500 < height) && (height <= 800)) || ((500 < width) && (width <= 800))) {
					questionInfo.put(LabelsInXML.TF_VISUAL_HEIGHT, "800");
					questionInfo.put(LabelsInXML.TF_VISUAL_WIDTH, "800");
				} else {
					questionInfo.put(LabelsInXML.TF_VISUAL_HEIGHT, "500");
					questionInfo.put(LabelsInXML.TF_VISUAL_WIDTH, "500");
				}

				labelsInfo.add(questionInfo);
			}

			createHitWithContent(Resources.TF_VISUALS_QUESTION, Resources.TF_VISUALS_OVERVIEW,
					Resources.TF_VISUALS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	public static void createMCVisualssQuestions(int numberOfHits, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, String> definitions, String outputFileName)
					throws FileNotFoundException, MalformedURLException {
		// make sure we can create that many unique questions
		if (numberOfHits * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}

		// check if the output path exists
		File outputDir = new File(Resources.MC_VISUALS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.MULTIPLE_CHOICE_VISUALS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHits; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				Map<String, String> questionInfo = new HashMap<String, String>();
				// prepare info about two labels
				QuestionID question = getQuestion(setOfMatchQuestionIDs);
				// naming the pictures
				System.out.println(question.getMatchPair().getEntityNameOfFirst());
				String[] partsEntOne = question.getMatchPair().getEntityNameOfFirst().toString().split(" |\\_");
				String firstEntName = Arrays.stream(partsEntOne).collect(Collectors.joining("")).toLowerCase();
				System.out.println(firstEntName + "-------------------------------------test");

				System.out.println(question.getMatchPair().getEntityNameOfSecond());
				String[] partsEntTwo = question.getMatchPair().getEntityNameOfSecond().toString().split(" |\\_");
				String secondEntName = Arrays.stream(partsEntTwo).collect(Collectors.joining("")).toLowerCase();
				System.out.println(secondEntName + "-------------------------------------test");

				String imageFilename = question.getMatchPair().getOntologyNameOfFirst().trim() + "-"
						+ question.getMatchPair().getOntologyNameOfSecond().trim() + ".rdf_" + firstEntName.trim() + "-"
						+ secondEntName.trim() + ".png";
				StringBuilder builder = new StringBuilder();
				builder.append("/mturkimages1001/");
				builder.append(imageFilename);
				String imageP = builder.toString();
				URL imagePath = new URL("https", "s3.amazonaws.com", imageP);
				System.out.println(imagePath.toString() + "                                url");

				// getting the size of the image
				Image image = new ImageIcon(imagePath).getImage();
				int height = image.getHeight(null);
				int width = image.getWidth(null);

				questionInfo.put(LabelsInXML.MC_VISUAL_QUESTIONS_ID, question.getQuestionIDHashed() + "");
				questionInfo.put(LabelsInXML.MC_VISUAL_IMG_LINK_LABEL_A_AND_B, imagePath.toString());

				if (question.getMatchPair().getEntityNameOfFirst().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toLowerCase())) {
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst() + "_1");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond() + "_2");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" mean the same thing.");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_To_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\"" + ".");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_B_To_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\"" + ".");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "_1\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\".");
				} else {
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A, question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_B, question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" mean the same thing.");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_To_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_B_To_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_VISUAL_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "\".");

				}

				if ((height > 800) || (width > 800)) {
					questionInfo.put(LabelsInXML.MC_VISUAL_HEIGHT, "1000");
					questionInfo.put(LabelsInXML.MC_VISUAL_WIDTH, "800");
				}

				else if (((500 < height) && (height <= 800)) || ((500 < width) && (width <= 800))) {
					questionInfo.put(LabelsInXML.MC_VISUAL_HEIGHT, "800");
					questionInfo.put(LabelsInXML.MC_VISUAL_WIDTH, "800");
				} else {
					questionInfo.put(LabelsInXML.MC_VISUAL_HEIGHT, "500");
					questionInfo.put(LabelsInXML.MC_VISUAL_WIDTH, "500");
				}

				labelsInfo.add(questionInfo);
			}

			createHitWithContent(Resources.MC_VISUALS_QUESTION, Resources.MC_VISUALS_OVERVIEW,
					Resources.MC_VISUALS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	public static void createTFRelationshipsQuestions(int numberOfHits, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, ArrayList<String>> relationships, String outputFileName)
					throws FileNotFoundException {
		// make sure we can create that many unique questions
		if (numberOfHits * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}

		// check if the output path exists
		File outputDir = new File(Resources.TF_RELATIONS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.TRUE_FALSE_RELATIONSHIPS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHits; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				// for each question in the hit
				Map<String, String> questionInfo = new HashMap<String, String>();
				// prepare info about two labels
				QuestionID question = getQuestion(setOfMatchQuestionIDs);

				questionInfo.put(LabelsInXML.TF_RELATIONSHIP_QUESTION_ID, question.getQuestionIDHashed() + "");

				if (question.getMatchPair().getEntityNameOfFirst().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toLowerCase())) {

					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst() + "_1");
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond() + "_2");
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getFirst())));
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_B_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getSecond())));
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT the same as \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_1\"");

					System.out.println(question.getMatchPair().getEntityNameOfFirst() + "   "
							+ question.getMatchPair().getEntityNameOfSecond());
					System.out.println("----------------------------");
				}

				else {
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getFirst())));
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_B_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getSecond())));
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" mean the same thing");
					questionInfo.put(LabelsInXML.TF_RELATIONSHIP_LABEL_A_NOT_EQUAL_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" is NOT the same as label \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"");

					System.out.println(question.getMatchPair().getEntityNameOfFirst() + "   "
							+ question.getMatchPair().getEntityNameOfSecond());
					System.out.println("----------------------------");
				}
				labelsInfo.add(questionInfo);
			}

			createHitWithContent(Resources.TF_RELATIONS_QUESTION, Resources.TF_RELATIONS_OVERVIEW,
					Resources.TF_RELATIONS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	/**
	 * 
	 * @param numberOfHits
	 * @param numberOfQuestionsInSingleHit
	 * @param matchPairs
	 * @param relationships
	 * @param outputFileName
	 * @throws FileNotFoundException
	 */
	public static void createMCRelationshipsQuestions(int numberOfHits, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, ArrayList<String>> relationships, String outputFileName)
					throws FileNotFoundException {
		// make sure we can create that many unique questions
		if (numberOfHits * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}

		// check if the output path exists
		File outputDir = new File(Resources.MC_RELATIONS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.MULTIPLE_CHOICE_RELATIONSHIPS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHits; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				Map<String, String> questionInfo = new HashMap<String, String>();
				QuestionID question = getQuestion(setOfMatchQuestionIDs);

				if (question.getMatchPair().getEntityNameOfFirst().toString().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toString().toLowerCase())) {
					String firstEnt = question.getMatchPair().getEntityNameOfFirst() + "_1";
					String secondEnt = question.getMatchPair().getEntityNameOfSecond() + "_2";
					// System.out.println(firstEnt + " " + secondEnt);
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_QUESTION_ID, question.getQuestionIDHashed() + "");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A, firstEnt);
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B, secondEnt);
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getFirst())));
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getSecond())));
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1" + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\""
									+ " mean the same thing.");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_TO_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\"" + ".");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B_TO_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\"" + ".");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "_1\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\".");

					labelsInfo.add(questionInfo);

				} else {
					String firstEnt = question.getMatchPair().getEntityNameOfFirst();
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_QUESTION_ID, question.getQuestionIDHashed() + "");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getFirst())));
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B_RELATIONSHIPTS, LabelsInXML
							.mc_relationship_createHTMLContent(relationships.get(question.getMatchPair().getSecond())));
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"" + " mean the same thing.");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_TO_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_B_TO_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_RELATIONSHIP_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "\".");
					labelsInfo.add(questionInfo);
				}

			}

			createHitWithContent(Resources.MC_RELATIONS_QUESTION, Resources.MC_RELATIONS_OVERVIEW,
					Resources.MC_RELATIONS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	/**
	 * 
	 * @param numberOfHITs
	 * @param numberOfQuestionsInSingleHit
	 * @param matchPairs
	 * @param definitions
	 * @param outputFileName
	 * @throws FileNotFoundException
	 */

	public static void createMCDefinitionsQuestions(int numberOfHITs, int numberOfQuestionsInSingleHit,
			ArrayList<MatchPair> matchPairs, Map<String, String> definitions, String outputFileName)
					throws FileNotFoundException {
		// make sure we can create that many unique questions
		if (numberOfHITs * numberOfQuestionsInSingleHit > matchPairs.size()) {
			System.err.println("Can't create questions with given parameters");
			return;
		}

		// check if the output path exists
		File outputDir = new File(Resources.MC_DEFINITIONS_HITS_TO_POST_DIR);
		if (!outputDir.exists())
			outputDir.mkdir();

		// clean-up the folder
		deleteFolderContents(outputDir);

		Set<QuestionID> setOfMatchQuestionIDs = createQuestionsIDs(TestType.MULTIPLE_CHOICE_DEFINITIONS, matchPairs);
		// for each hit
		for (int i = 0; i < numberOfHITs; i++) {
			ArrayList<Map<String, String>> labelsInfo = new ArrayList<Map<String, String>>();
			// for each question in the hit
			for (int j = 0; j < numberOfQuestionsInSingleHit; j++) {
				Map<String, String> questionInfo = new HashMap<String, String>();
				// prepare info about four different labels
				QuestionID question = getQuestion(setOfMatchQuestionIDs); 

				if (question.getMatchPair().getEntityNameOfFirst().toString().toLowerCase()
						.equals(question.getMatchPair().getEntityNameOfSecond().toString().toLowerCase())) {

					questionInfo.put(LabelsInXML.MC_DEFINITIONS_QUESTIONS_ID, question.getQuestionIDHashed() + "");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst() + "_1");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond() + "_2");

					questionInfo.put(LabelsInXML.MC_DEFINITIONS_DESCRIPTION_LABEL_A,
							definitions.get(question.getMatchPair().getFirst()));
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_DESCRIPTION_LABEL_B,
							definitions.get(question.getMatchPair().getSecond()));
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\""
									+ " mean the same thing.");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_TO_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "_1\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\"" + ".");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_B_TO_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "_1\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "_2\"" + ".");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "_1\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "_2\".");

					labelsInfo.add(questionInfo);

				} else {
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_QUESTIONS_ID, question.getQuestionIDHashed() + "");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A,
							question.getMatchPair().getEntityNameOfFirst());
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_B,
							question.getMatchPair().getEntityNameOfSecond());
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_DESCRIPTION_LABEL_A,
							definitions.get(question.getMatchPair().getFirst()));
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_DESCRIPTION_LABEL_B,
							definitions.get(question.getMatchPair().getSecond()));
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_EQUALS_LABEL_B,
							"\"" + question.getMatchPair().getEntityNameOfFirst() + "\" and \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"" + " mean the same thing.");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_TO_LABEL_B,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfFirst() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\" is NOT necessarily  \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_B_TO_LABEL_A,
							"Any thing that is \"" + question.getMatchPair().getEntityNameOfSecond() + "\" is also \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" ,But anything that is \""
									+ question.getMatchPair().getEntityNameOfFirst() + "\" is NOT necessarily \""
									+ question.getMatchPair().getEntityNameOfSecond() + "\"" + ".");
					questionInfo.put(LabelsInXML.MC_DEFINITIONS_LABEL_A_NO_RELATION_LABEL_B,
							"There is no relation between \"" + question.getMatchPair().getEntityNameOfFirst()
									+ "\" and \"" + question.getMatchPair().getEntityNameOfSecond() + "\".");
					labelsInfo.add(questionInfo);
				}
			}

			createHitWithContent(Resources.MC_DEFINITIONS_QUESTION, Resources.MC_DEFINITIONS_OVERVIEW,
					Resources.MC_DEFINITIONS_HITS_TO_POST_DIR + "/" + i + outputFileName, labelsInfo);
		}
	}

	private static void deleteFolderContents(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				deleteFolderContents(file);
			} else {
				file.delete();
			}
		}
	}

	private static int[] shuffleIndexes(int first, int last) {
		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = first; i <= last; i++) {
			list.add(i);
		}

		Collections.shuffle(list);

		int[] ret = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ret[i] = list.get(i);
		}

		return ret;
	}

	private static String getRandomLabel(QuestionID question, Map<String, ?> definitions) {
		String random = null;

		while (random == null || random.equals(question.getMatchPair().getFirst())
				|| random.equals(question.getMatchPair().getSecond())) {
			random = (String) definitions.keySet().toArray()[new Random().nextInt(definitions.size())];
		}

		return random;
	}

	private static String getRandomLabel(QuestionID question, Map<String, ?> definitions, String labelC) {
		String random = null;

		while (random == null || random.equals(question.getMatchPair().getFirst())
				|| random.equals(question.getMatchPair().getSecond()) || random.equals(labelC)) {
			random = (String) definitions.keySet().toArray()[new Random().nextInt(definitions.size())];
		}

		return random;
	}

	private static QuestionID getQuestion(Set<QuestionID> set) {
		if (set == null) {
			return null;
		}
		if (set.size() == 0) {
			return null;
		}
		QuestionID ret = set.iterator().next();
		set.remove(ret);
		return ret;
	}

}
