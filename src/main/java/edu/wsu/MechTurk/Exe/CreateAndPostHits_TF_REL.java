package edu.wsu.MechTurk.Exe;

import java.io.File;

import edu.wsu.MechTurk.Resources;
import edu.wsu.MechTurk.Utils.HitContentFactory;
import edu.wsu.MechTurk.Utils.HitsUtils;

/**
 * Creates and posts true/false HITs. Each questions contains relationships
 * between two labels represented as a list.
 */
public class CreateAndPostHits_TF_REL {

	public static void main(String[] args) throws Exception {

		HitContentFactory.createTFRelationshipsQuestions(10, 10, HitContentFactory.readInMatchList(),
				HitContentFactory.readInRelationships(), ".tf.relations.question");
		HitsUtils hitsUtils = HitsUtils.getHitsUtilsInstance();
		hitsUtils.postHits(new File(Resources.TF_RELATIONS_HITS_TO_POST_DIR), Resources.TF_RELATIONS_PROPERTIES,
				"3G1QR9I4MKM6T6F30NPI02EK9JYOJF");
	}
}
