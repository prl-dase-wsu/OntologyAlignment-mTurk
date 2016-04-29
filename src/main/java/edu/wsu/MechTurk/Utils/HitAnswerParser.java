package edu.wsu.MechTurk.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import com.amazonaws.mturk.dataschema.QuestionFormAnswers;
import com.amazonaws.mturk.dataschema.QuestionFormAnswersType;
import com.amazonaws.mturk.service.axis.RequesterService;

import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory.QuestionID;

public class HitAnswerParser {
	
	public static Answers parseAnswers(TestType testType, boolean filterByBasicQuestion) throws FileNotFoundException {
		String pathToResults = "";
		switch (testType) {
		case MULTIPLE_CHOICE_DEFINITIONS:
			pathToResults = Resources.MC_DEFINITIONS_HITS_TO_POST_DIR;
			break;
		case MULTIPLE_CHOICE_RELATIONSHIPS:
			pathToResults = Resources.MC_RELATIONS_HITS_TO_POST_DIR;
			break;
		case MULTIPLE_CHOICE_VISUALS:
			pathToResults = Resources.MC_VISUALS_HITS_TO_POST_DIR;
			break;
		case TRUE_FALSE_DEFINITIONS:
			pathToResults = Resources.TF_DEFINITIONS_HITS_TO_POST_DIR;
			break;
		case TRUE_FALSE_RELATIONSHIPS:
			pathToResults = Resources.TF_RELATIONS_HITS_TO_POST_DIR;
			break;
		case TRUE_FALSE_VISUALS:
			pathToResults = Resources.TF_VISUALS_HITS_TO_POST_DIR;
			break;
		default:
			break;
		
		}
		
		Answers answers = new Answers(testType);
		
		File dir = new File(pathToResults);
		for (File file : dir.listFiles()) {
			if (file.getName().contains("results")) {
				System.out.println("Processing file: " + file.getName());
				parseXMLFile(file, testType, answers, filterByBasicQuestion);
			}
		}
		
		return answers;
	}
	
	private static void parseXMLFile(File file, TestType testType, Answers answers, boolean filterByBasicQuestion) throws FileNotFoundException {
		ArrayList<String> xmlDocs = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		// split the xmls
		Scanner in = new Scanner(file);
		while (in.hasNext()) {
			String line = in.nextLine();
			sb.append(line);
			if (line.contains("</QuestionFormAnswers>")) {
				xmlDocs.add(sb.toString());
				sb.setLength(0);
			}
		}
		
		if (filterByBasicQuestion) {
			ArrayList<String> xmlToRemove = new ArrayList<String>();
			for (String xmlDoc : xmlDocs) {
				QuestionFormAnswers questionFormAnswers = RequesterService.parseAnswers(xmlDoc);
				questionFormAnswers.getAnswer();
				for (QuestionFormAnswersType.AnswerType answerType : ((List<QuestionFormAnswersType.AnswerType>)questionFormAnswers.getAnswer())) {
					if (answerType.getQuestionIdentifier().equals("BASIC_QUESTION")) {
						if (!answerType.getSelectionIdentifier().get(0).equals("CORRECTr")) {
							xmlToRemove.add(xmlDoc);
						}
					}
				}
			}
			xmlDocs.removeAll(xmlToRemove);
	 	}
		
		// read in the question IDs and create hash -> expected answer map and hash -> fullqID map
		Set<QuestionID> questionIDs = HitContentFactory.createQuestionsIDs(testType, HitContentFactory.readInMatchList());
		Map<String, String> qID_Answer_Map = new HashMap<String, String>();
		Map<String, String> qID_FullqID_Map = new HashMap<String, String>();
		for (QuestionID qID : questionIDs) {
			
			//filling an answerKey based on the .isMAtchCorrect(): if the isMatchCorrect is true the put the first sentence into the answer key otherwise the second sentence
			//the sentences in this answer key need to be exactly same as what we put in the HitContentFacory in the place of the options
		
			if (testType == TestType.MULTIPLE_CHOICE_DEFINITIONS || testType == TestType.MULTIPLE_CHOICE_RELATIONSHIPS || testType == TestType.MULTIPLE_CHOICE_VISUALS) {
			//if isMatchCorrect() goes true the  "equal" string will be saved in  qID_Answer_Map otherwise the "not-equal" string will be stored
				if(qID.getMatchPair().getEntityNameOfFirst().toLowerCase().equals(qID.getMatchPair().getEntityNameOfSecond().toLowerCase())){
					
				qID_Answer_Map.put(qID.getQuestionIDHashed() + "", (qID
						.getMatchPair().isMatchCorrect()) ? "\""
						+ qID.getMatchPair().getEntityNameOfFirst()
						+ "_1\" and \""
						+ qID.getMatchPair().getEntityNameOfSecond() + "_2\" mean the same thing"
						: "There is no relation between \"" + qID.getMatchPair().getEntityNameOfFirst()
								+ "_1\" and \"" + qID.getMatchPair().getEntityNameOfSecond() + "_2");
				}
				else
				{
					
					qID_Answer_Map.put(qID.getQuestionIDHashed() + "", (qID
							.getMatchPair().isMatchCorrect()) ? "\""
							+ qID.getMatchPair().getEntityNameOfFirst()
							+ "\" and \""
							+ qID.getMatchPair().getEntityNameOfSecond() + "\" mean the same thing"
							: "There is no relation between \"" + qID.getMatchPair().getEntityNameOfFirst()
									+ "\" and \"" + qID.getMatchPair().getEntityNameOfSecond());
				}
			}
			if (testType == TestType.TRUE_FALSE_DEFINITIONS || testType == TestType.TRUE_FALSE_RELATIONSHIPS || testType == TestType.TRUE_FALSE_VISUALS) {		
				if(qID.getMatchPair().getEntityNameOfFirst().toLowerCase().equals(qID.getMatchPair().getEntityNameOfSecond().toLowerCase())){
				qID_Answer_Map.put(qID.getQuestionIDHashed() + "", (qID
						.getMatchPair().isMatchCorrect()) ? "\""
						+ qID.getMatchPair().getEntityNameOfFirst().toLowerCase()
						+ "_1\" and \""
						+ qID.getMatchPair().getEntityNameOfSecond().toLowerCase() + "_2\" mean the same thing"
						: "\""
						+ qID.getMatchPair().getEntityNameOfFirst().toLowerCase()
						+ "_1\" is NOT the same as \""
						+ qID.getMatchPair().getEntityNameOfSecond().toLowerCase() + "_2\"");	
				}
				else {
					qID_Answer_Map.put(qID.getQuestionIDHashed() + "", (qID
							.getMatchPair().isMatchCorrect()) ? "\""
							+ qID.getMatchPair().getEntityNameOfFirst().toLowerCase()
							+ "\" and \""
							+ qID.getMatchPair().getEntityNameOfSecond().toLowerCase() + "\" mean the same thing"
							: "\""
							+ qID.getMatchPair().getEntityNameOfFirst().toLowerCase()
							+ "\" is NOT the same as \""
							+ qID.getMatchPair().getEntityNameOfSecond().toLowerCase() + "\"");
				}
			}
			qID_FullqID_Map.put(qID.getQuestionIDHashed() + "", qID.getQuestionID());
		}
		qID_FullqID_Map.put("BASIC_QUESTION", "BASIC_QUESTION");
		qID_Answer_Map.put("BASIC_QUESTION" , "CORRECTr");
		
		// parse each xml
		for (String xmlDoc : xmlDocs) {
			QuestionFormAnswers questionFormAnswers = RequesterService.parseAnswers(xmlDoc);
			questionFormAnswers.getAnswer();
			// in answerType we collect the users' answers
			for (QuestionFormAnswersType.AnswerType answerType : ((List<QuestionFormAnswersType.AnswerType>)questionFormAnswers.getAnswer())) {
				System.out.println("\t\nFor question: " + qID_FullqID_Map.get(answerType.getQuestionIdentifier()));
			
				System.out.println("       : " + answerType.getSelectionIdentifier().get(0) + "  with \n       : " + qID_Answer_Map.get(answerType.getQuestionIdentifier()) );
				
				if (answerType.getSelectionIdentifier().get(0).toString().toLowerCase().equals(qID_Answer_Map.get(answerType.getQuestionIdentifier()+"").toLowerCase())) {
					System.out.println("\twe got true! because the answer was : " + answerType.getSelectionIdentifier().get(0));
					answers.addAnswer(qID_FullqID_Map.get(answerType.getQuestionIdentifier()), true);
				} else {
					System.out.println("\twe got false! because the answer was : " + answerType.getSelectionIdentifier().get(0));
					answers.addAnswer(qID_FullqID_Map.get(answerType.getQuestionIdentifier()), false);
				}
			}
		}
	}
	
	public static class Answers {
		private Map<String, List<Boolean>> answers = new HashMap<String, List<Boolean>>();
		
		public Answers(TestType testType) throws FileNotFoundException {
			for (QuestionID qID : HitContentFactory.createQuestionsIDs(testType, HitContentFactory.readInMatchList())) {
				answers.put(qID.getQuestionID(), null);
			}
		}
		
		public void addAnswer(String questionID, boolean isAnswerCorrect) {
			List<Boolean> answerSet = answers.get(questionID);
			if (answerSet == null) {
				answerSet = new ArrayList<Boolean>();
			}
			
			answerSet.add(new Boolean(isAnswerCorrect));
			answers.put(questionID, answerSet);
		}
		
		public Map<String, List<Boolean>> getAnswers() {
			return this.answers;
		}
		
		public List<Boolean> getAnswersForQuestion(String questionID) {
			return this.answers.get(questionID);
		}
	}
}
