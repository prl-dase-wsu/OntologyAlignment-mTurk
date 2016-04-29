package edu.wsu.MechTurk.Exe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.mturk.addon.HITDataCSVWriter;

import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitAnswerParser;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitAnswerParser.Answers;
import edu.wsu.MechTurk.Utils.HitContentFactory.QuestionID;

public class ParseResults {

	public static void main(String[] args) throws IOException {
		
//		TestType testType = TestType.MULTIPLE_CHOICE_DEFINITIONS;
//		TestType testType = TestType.MULTIPLE_CHOICE_RELATIONSHIPS;
//		TestType testType = TestType.MULTIPLE_CHOICE_VISUALS;
		TestType testType = TestType.TRUE_FALSE_DEFINITIONS;
//		TestType testType = TestType.TRUE_FALSE_RELATIONSHIPS;
//		TestType testType = TestType.TRUE_FALSE_VISUALS;
		boolean filterOutByBasicQuestion = true;
		
		Answers answers = HitAnswerParser.parseAnswers(testType, filterOutByBasicQuestion);
		Map<String, List<Boolean>> answersMap = answers.getAnswers();
		
		// mapping between questionID and question
		Map<String, QuestionID> qMap = new HashMap<String, QuestionID>();
		for (QuestionID qid : HitContentFactory.createQuestionsIDs(testType, HitContentFactory.readInMatchList())) {
			qMap.put(qid.getQuestionID(), qid);
		}
		qMap.put("BASIC_QUESTION", null);
		
		// CSV writer
		HITDataCSVWriter csvWriter = new HITDataCSVWriter("./data/" + testType.getFullTestTypeName() + ".csv", ',', false);
		csvWriter.setFieldNames(Arrays.asList("Compared entities", "Question Type", "Correct Answerws", "Incorrect Answers"));
		csvWriter.writeLine(new String[]
				{
					"Compared entities",
					"Question Type",
					"Correct Answerws", 
					"Incorrect Answers"
				}
		);
		
		int totalCorrect = 0, totalIncorrect = 0;
		for (Entry<String, List<Boolean>> entry : answersMap.entrySet()) {
			System.out.println("For question: " + entry.getKey() + " we got: ");
			int correct = 0, incorrect = 0;
			for (Boolean b : entry.getValue()) {
				if (b) {
					correct++;
					totalCorrect++;
				}
				else {
					incorrect++;
					totalIncorrect++;
				}
			}
			System.out.println("\tcorrect: " + correct);
			System.out.println("\tincorrect: " + incorrect);
			
			QuestionID qID = qMap.get(entry.getKey());
			if (qID == null) 
				continue;
			csvWriter.writeLine(new String[]
					{
						qID.getMatchPair().getOntologyNameOfFirst() + "#" + qID.getMatchPair().getEntityNameOfFirst() + "|" + qID.getMatchPair().getOntologyNameOfSecond() + "#" + qID.getMatchPair().getEntityNameOfSecond(),
						(qID.getMatchPair().isMatchCorrect()) ? "Positive" : "Negative",
						correct + "",
						incorrect + ""
					}
			
			);
			System.out.println("here");
		}
		
		System.out.println("Total correct answers: " + totalCorrect);
		System.out.println("Total incorrect answers: " + totalIncorrect);
		
		csvWriter.writeLine(new String[]
				{
					"",
					"",
					"",
					""
				}
		);
		csvWriter.writeLine(new String[]
				{
					"Total",
					"N/A",
					totalCorrect + "",
					totalIncorrect + ""
				}
		);
	}

}
