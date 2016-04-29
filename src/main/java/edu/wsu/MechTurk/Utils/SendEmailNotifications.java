package edu.wsu.MechTurk.Utils;

public class SendEmailNotifications {
	public static void main(String[] args) {

		String message = "Hi \n Thanks for your great performance on matching labels from different databases."
				+ "You are now qualified to participate in future tasks of this type which have \"MC_Visual\" qualification Requirement. \n"
				+ "Keywords for tasks: 	synonym detection, ontology alignment, multiple choice, visual";

		String[] workerIDs = {};

		HitsUtils.getHitsUtilsInstance().sendMessage("MC_Visual qualification", message, workerIDs);

	}
}
