package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts multiple choice HITs. Each questions contains definitions
 * of two labels.
 */
public class CreateAndPostHits_MC_DEF {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createMCDefinitionsQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInDefinitions(), ".mc.definitions.question");
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.MC_DEFINITIONS_HITS_TO_POST_DIR), Resources.MC_DEFINITIONS_PROPERTIES,
				"33JQWVLE1GO8CIKA33UWJ2EFANOWT4");

	}
}
