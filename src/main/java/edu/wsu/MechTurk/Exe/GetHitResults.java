package edu.wsu.MechTurk.Exe;

import java.io.IOException;

import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Utils.HitsUtils;

public class GetHitResults {
	
	public static void main(String[] args) throws IOException {
		
//		TestType testType = TestType.MULTIPLE_CHOICE_DEFINITIONS;
//		TestType testType = TestType.MULTIPLE_CHOICE_RELATIONSHIPS;
		TestType testType = TestType.MULTIPLE_CHOICE_VISUALS;
//		TestType testType = TestType.TRUE_FALSE_DEFINITIONS;
//		TestType testType = TestType.TRUE_FALSE_RELATIONSHIPS;
//		TestType testType = TestType.TRUE_FALSE_VISUALS;
		String pathToSuccessFile = "hits.success";
		
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.getHITResults(testType, pathToSuccessFile);
	}
}
