package edu.wsu.MechTurk.Exe;

import java.io.IOException;

import edu.wsu.MechTurk.Utils.HitsUtils;

public final class DeleteHits {

	public static void main(String[] args) throws IOException {
		
		//String hitsToDelete = "./data/MC_DEFINITIONS_QUES/hits.success";
		//String hitsToDelete = "./data/MC_RELATIONS_QUES/hits.success";
		//String hitsToDelete = "./data/TF_DEFINITIONS_QUESTIONS/hits.success";
		//String hitsToDelete = "./data/TF_RELATIONS_QUESTIONS/hits.success";
		//String hitsToDelete = "./data/TF_VISUALS_QUESTIONS/hits.success";
		String hitsToDelete = "./data/MC_VISUALS_QUESTIONS/hits.success";
		//String hitsToDelete = "./data/TF_RELATIONS_QUESTIONS/hits.success";
		
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.deleteHITs(hitsToDelete);
	}
	
}
