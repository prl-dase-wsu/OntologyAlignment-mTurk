package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts true/false HITs. Each questions contains definitions
 * of two labels.
 */
public class CreateAndPostHits_TF_DEF {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createTFDefinitionsQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInDefinitions(), ".tf.definitions.question");
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.TF_DEFINITIONS_HITS_TO_POST_DIR), Resources.TF_DEFINITIONS_PROPERTIES,
				"3GNL8ZDCG6MAYVVXI54IO1ZKW49OI3");
	}

}
