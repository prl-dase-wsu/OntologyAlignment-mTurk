package edu.wsu.MechTurk;

import java.util.Collection;

public final class LabelsInXML {
	
	public enum TestType {
		TRUE_FALSE_DEFINITIONS("TF_DEF", "True-False Definitions"),
		TRUE_FALSE_VISUALS("TF_VIS", "True-False Visuals"),
		TRUE_FALSE_RELATIONSHIPS("TF_REL", "True-False Relationships"),
		MULTIPLE_CHOICE_DEFINITIONS("MC_DEF", "Multiple Choice Definitions"),
		MULTIPLE_CHOICE_VISUALS("MC_VIS", "Multiple Choice Visuals"),
		MULTIPLE_CHOICE_RELATIONSHIPS("MC_REL", "Multiple Choice Relationships");
		
		private String testType;
		private String fullTestTypeName;
		
		private TestType(String testType, String fullTestTypeName) {
			this.testType = testType;
			this.fullTestTypeName = fullTestTypeName;
		}
		
		@Override
		public String toString() {
			return this.testType;
		}
		
		public String getFullTestTypeName() {
			return this.fullTestTypeName;
		}
	}
	
	// defines max number of relations between two entities displayed in a single question
	private final static int MAX_NUMBER_OF_RELATIONS = 15;
	
	// MC: multiple choice
	// TF: true false
	
	// MC - definitions only
	public final static String MC_DEFINITIONS_QUESTIONS_ID = "QUESTION_ID";
	public final static String MC_DEFINITIONS_LABEL_A = "LABEL_A";
	public final static String MC_DEFINITIONS_LABEL_B = "LABEL_B";
	public final static String MC_DEFINITIONS_LABEL_C = "LABEL_C";
	public final static String MC_DEFINITIONS_LABEL_D = "LABEL_D";
	public final static String MC_DEFINITIONS_DESCRIPTION_LABEL_A = "DESCRIPTION_LABEL_A";
	public final static String MC_DEFINITIONS_DESCRIPTION_LABEL_B = "DESCRIPTION_LABEL_B";
	public final static String MC_DEFINITIONS_DESCRIPTION_LABEL_C = "DESCRIPTION_LABEL_C";
	public final static String MC_DEFINITIONS_DESCRIPTION_LABEL_D = "DESCRIPTION_LABEL_D";
	public final static String MC_DEFINITIONS_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String MC_DEFINITIONS_LABEL_A_EQUALS_LABEL_C = "LABEL_A_EQUALS_LABEL_C";
	public final static String MC_DEFINITIONS_LABEL_A_TO_LABEL_B = "LABEL_A_TO_LABEL_B";
	public final static String MC_DEFINITIONS_LABEL_B_TO_LABEL_A = "LABEL_B_TO_LABEL_A";
	public final static String MC_DEFINITIONS_LABEL_A_EQUALS_LABEL_D = "LABEL_A_EQUALS_LABEL_D";
	public final static String MC_DEFINITIONS_LABEL_A_EQUALS_NONEOF_LABEL_B_LABEL_C_LABEL_D = "LABEL_A_EQUALS_NONEOF_LABEL_B_LABEL_C_LABEL_D";
	public final static String MC_DEFINITIONS_LABEL_A_NO_RELATION_LABEL_B = "LABEL_A_NO_RELATION_LABEL_B";
	
	// MC - visuals only
	public final static String MC_VISUAL_QUESTIONS_ID = "QUESTION_ID";
	public final static String MC_VISUAL_LABEL_A = "LABEL_A";
	public final static String MC_VISUAL_LABEL_B = "LABEL_B";
	public final static String MC_VISUALS_LABEL_C = "LABEL_C";
	public final static String MC_VISUALS_LABEL_D = "LABEL_D";
	public final static String MC_VISUAL_IMG_DESCRIPTION_LABEL_A = "IMG_DESC_LABEL_A";
	public final static String MC_VISUAL_IMG_DESCRIPTION_LABEL_B = "IMG_DESC_LABEL_B";
	public final static String MC_VISUAL_IMG_DESCRIPTION_LABEL_C = "IMG_DESC_LABEL_C";
	public final static String MC_VISUAL_IMG_DESCRIPTION_LABEL_D = "IMG_DESC_LABEL_D";
	public final static String MC_VISUAL_IMG_LINK_LABEL_A_AND_B = "IMG_LINK_LABEL_A_AND_B";
	public final static String MC_VISUAL_IMG_LINK_LABEL_B = "IMG_LINK_LABEL_B";
	public final static String MC_VISUAL_IMG_LINK_LABEL_C = "IMG_LINK_LABEL_C";
	public final static String MC_VISUAL_IMG_LINK_LABEL_D = "IMG_LINK_LABEL_D";
	public final static String MC_VISUAL_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String MC_VISUAL_LABEL_A_To_LABEL_B = "LABEL_A_To_LABEL_B";
	public final static String MC_VISUAL_LABEL_B_To_LABEL_A = "LABEL_B_To_LABEL_A";
	public final static String MC_VISUAL_LABEL_A_NO_RELATION_LABEL_B = "LABEL_A_NO_RELATION_LABEL_B";
	public final static String MC_VISUAL_HEIGHT = "HEIGHT";
	public final static String MC_VISUAL_WIDTH = "WIDTH";
	
	// MC - relationships only
	public final static String MC_RELATIONSHIP_QUESTION_ID = "QUESTION_ID";
	public final static String MC_RELATIONSHIP_LABEL_A = "LABEL_A";
	public final static String MC_RELATIONSHIP_LABEL_B = "LABEL_B";
	public final static String MC_RELATIONSHIP_LABEL_C = "LABEL_C";
	public final static String MC_RELATIONSHIP_LABEL_D = "LABEL_D";
	public final static String MC_RELATIONSHIP_LABEL_A_RELATIONSHIPTS = "LABEL_A_RELATIONSHIPS";
	public final static String MC_RELATIONSHIP_LABEL_B_RELATIONSHIPTS = "LABEL_B_RELATIONSHIPS";
	public final static String MC_RELATIONSHIP_LABEL_C_RELATIONSHIPTS = "LABEL_C_RELATIONSHIPS";
	public final static String MC_RELATIONSHIP_LABEL_D_RELATIONSHIPTS = "LABEL_D_RELATIONSHIPS";

	/**
	 * Takes relations in form of java collection (each string is meant to
	 * represent one relations) and converts it to appropriate HTML format.
	 * 
	 * @param relationships
	 * @return
	 */
	public static String mc_relationship_createHTMLContent(Collection<String> relationships) {
		if (relationships == null) {
			return "<li>Relations not available...</li>";
		}
		if (relationships.size() == 0) {
			return "<li>Relations not available...</li>";
		}
		
		int relationsCounter = 0;
		StringBuilder builder = new StringBuilder();
		for (String s : relationships) {
			builder.append("<li>" + s + "</li>\n");
			relationsCounter++;
			if (relationsCounter >= MAX_NUMBER_OF_RELATIONS) {
				return builder.toString();
			}
		}
		
		return builder.toString();
	}
	public final static String MC_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String MC_RELATIONSHIP_LABEL_A_TO_LABEL_B = "LABEL_A_TO_LABEL_B";
	public final static String MC_RELATIONSHIP_LABEL_B_TO_LABEL_A = "LABEL_B_TO_LABEL_A";
	public final static String MC_RELATIONSHIP_LABEL_A_NO_RELATION_LABEL_B = "LABEL_A_NO_RELATION_LABEL_B";	
	public final static String MC_RELATIONSHIP_LABEL_A_EQUALS_LABEL_C = "LABEL_A_EQUALS_LABEL_C";
	public final static String MC_RELATIONSHIP_LABEL_A_EQUALS_LABEL_D = "LABEL_A_EQUALS_LABEL_D";
	public final static String MC_RELATIONSHIP_LABEL_A_EQUALS_NONEOF_LABEL_B_LABEL_C_LABEL_D = "LABEL_A_EQUALS_NONEOF_LABEL_B_LABEL_C_LABEL_D";
	
	// TF - definitions only
	public final static String TF_DEFINITIONS_QUESTIONS_ID = "QUESTION_ID";
	public final static String TF_DEFINITIONS_LABEL_A = "LABEL_A";
	public final static String TF_DEFINITIONS_LABEL_B = "LABEL_B";
	public final static String TF_DEFINITIONS_DESCRIPTION_LABEL_A = "DESCRIPTION_LABEL_A";
	public final static String TF_DEFINITIONS_DESCRIPTION_LABEL_B = "DESCRIPTION_LABEL_B";
	public final static String TF_DEFINITIONS_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String TF_DEFINITIONS_LABEL_A_NOT_EQUAL_LABEL_B= "LABEL_A_NOT_EQUAL_LABEL_B";
	
	// TF - relationships only
	public final static String TF_RELATIONSHIP_QUESTION_ID = "QUESTION_ID";
	public final static String TF_RELATIONSHIP_LABEL_A = "LABEL_A";
	public final static String TF_RELATIONSHIP_LABEL_B = "LABEL_B";
	public final static String TF_RELATIONSHIP_LABEL_A_RELATIONSHIPTS = "LABEL_A_RELATIONSHIPS";
	public final static String TF_RELATIONSHIP_LABEL_B_RELATIONSHIPTS = "LABEL_B_RELATIONSHIPS";
	
	/**
	 * Takes relations in form of java collection (each string is meant to
	 * represent one relations) and converts it to appropriate HTML format.
	 * 
	 * @param relationships
	 * @return
	 */
	public static String tf_relationship_createHTMLContent(Collection<String> relationships) {
		if (relationships == null) {
			return "<li>Relations not available...</li>";
		}
		if (relationships.size() == 0) {
			return "<li>Relations not available...</li>";
		}
		
		int relationsCounter = 0;
		StringBuilder builder = new StringBuilder();
		for (String s : relationships) {
			builder.append("<li>" + s + "</li>");
			if (relationsCounter >= MAX_NUMBER_OF_RELATIONS) {
				return builder.toString();
			}
		}
		
		return builder.toString();
	}
	public final static String TF_RELATIONSHIP_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String TF_RELATIONSHIP_LABEL_A_NOT_EQUAL_LABEL_B = "LABEL_A_NOT_EQUAL_LABEL_B";
	
	// TF - visuals only 
	public final static String TF_VISUALS_QUESTIONS_ID = "QUESTION_ID";
	public final static String TF_VISUALS_LABEL_A = "LABEL_A";
	public final static String TF_VISUALS_LABEL_B = "LABEL_B";
	public final static String TF_VISUAL_IMG_DESCRIPTION_LABEL_A = "IMG_DESC_LABEL_A";
	public final static String TF_VISUAL_IMG_DESCRIPTION_LABEL_B = "IMG_DESC_LABEL_B";
	public final static String TF_VISUAL_IMG_LINK_LABEL_A_AND_B = "IMG_LINK_LABEL_A_AND_B";
	public final static String TF_VISUAL_LABEL_A_EQUALS_LABEL_B = "LABEL_A_EQUALS_LABEL_B";
	public final static String TF_VISUAL_LABEL_A_NOT_EQUAL_LABEL_B = "LABEL_A_NOT_EQUAL_LABEL_B";
	public final static String TF_VISUAL_HEIGHT = "HEIGHT";
	public final static String TF_VISUAL_WIDTH = "WIDTH";
	
	private LabelsInXML() {
		
	}
}
