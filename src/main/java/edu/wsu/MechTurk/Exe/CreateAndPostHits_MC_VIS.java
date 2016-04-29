package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts multiple choice HITs. Each questions contains relationships
 * between two labels represented as a visual.
 */
public class CreateAndPostHits_MC_VIS {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createMCVisualssQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInDefinitions(), ".mc.visual.question");
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.MC_VISUALS_HITS_TO_POST_DIR), Resources.MC_VISUALS_PROPERTIES,
				"38K21J14759UBUJW6KVLV8ANXOMU1U");

	}

}
