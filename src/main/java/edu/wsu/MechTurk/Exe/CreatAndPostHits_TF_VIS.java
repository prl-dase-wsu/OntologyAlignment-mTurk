package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts true/false HITs. Each questions contains relationships
 * between two labels represented as a visual.
 */
public final class CreatAndPostHits_TF_VIS {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createTFVisualssQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInDefinitions(), ".tf.visual.question");

		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.TF_VISUALS_HITS_TO_POST_DIR), Resources.TF_VISUALS_PROPERTIES,
				"30TX348SI33V1FD244WS265FCD0588");
	}
}
