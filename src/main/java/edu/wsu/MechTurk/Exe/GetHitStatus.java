package edu.wsu.MechTurk.Exe;

import java.io.IOException;

import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Utils.HitsUtils;

public class GetHitStatus {


	public static void main(String[] args) throws IOException {
		
		String pathToSuccessFile = "./data/TF_RELATIONS_QUESTIONS/hits.success";
		
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.printDetailsAboutHits(pathToSuccessFile);
	}
}
