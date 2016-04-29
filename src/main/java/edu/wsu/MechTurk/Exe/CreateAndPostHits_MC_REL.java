package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts multiple choice HITs. Each questions contains relationships
 * between two labels represented as a list.
 */
public class CreateAndPostHits_MC_REL {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createMCRelationshipsQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInRelationships(), ".mc.relations.ques");
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.MC_RELATIONS_HITS_TO_POST_DIR), Resources.MC_RELATIONS_PROPERTIES,
				"33PZ5DQDSTEKK7UA63F9535MO16R9W");

	}

}
