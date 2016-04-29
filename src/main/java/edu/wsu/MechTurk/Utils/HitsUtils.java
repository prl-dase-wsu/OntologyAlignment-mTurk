package edu.wsu.MechTurk.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import com.amazonaws.mturk.addon.HITDataCSVReader;
import com.amazonaws.mturk.addon.HITDataInput;
import com.amazonaws.mturk.addon.HITProperties;
import com.amazonaws.mturk.addon.HITQuestion;
import com.amazonaws.mturk.addon.QAPValidator;
import com.amazonaws.mturk.requester.Assignment;
import com.amazonaws.mturk.requester.AssignmentStatus;
import com.amazonaws.mturk.requester.Comparator;
import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.requester.QualificationRequirement;
import com.amazonaws.mturk.requester.QualificationType;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.service.exception.ServiceException;
import com.amazonaws.mturk.util.PropertiesClientConfig;

import edu.wsu.MechTurk.LabelsInXML.TestType;
import edu.wsu.MechTurk.Resources;

public class HitsUtils {

	private static HitsUtils singleton = null;
	private RequesterService service = null;

	public void postTestHit() {
		HIT hit = null;
		try {
			QualificationType[] qTypes = this.service.getAllQualificationTypes();

			System.out.println("size : " + qTypes.length);
			QualificationType qty = qTypes[1];
			for (QualificationType qt : qTypes) {
				System.out.println(qt.getName() + ":");
				QualificationType qtyx = qt;
				System.out.println(qtyx.getQualificationTypeId() + "\n");
			}

			String s = new String("33JQWVLE1GO8CIKA33UWJ2EFANOWT4");

			QualificationRequirement[] reqArr = new QualificationRequirement[1];

			reqArr[0] = new QualificationRequirement();

			reqArr[0].setQualificationTypeId(s.trim().toString());
			reqArr[0].setComparator(Comparator.EqualTo);
			reqArr[0].setIntegerValue(new int[] { 100 });
			reqArr[0].setRequiredToPreview(false);

			String question = RequesterService.getBasicFreeTextQuestion("What is the purpose of life?");

			hit = service.createHIT(null, "Test hit", "Please ignore this HIT", "color", question, 0.01, 120l, 604800l,
					300l, 1, "test annotation", reqArr, null);

			System.out.println("The test hit was created. Hit ID:  " + hit.getHITId());
			System.out.println("HIT location:");
			System.out.println(service.getWebsiteURL() + "/mturk/preview?groupId=" + hit.getHITTypeId());
			HitPostStatusWriter.getSuccessHitPostStatusWriter("./").addHit(hit);
		} catch (ServiceException e) {
			HitPostStatusWriter.getFailureHitPostStatusWriter("./").addHit(hit);
			System.err.println(e.getLocalizedMessage());
		}

	}

	public HIT[] getMyHits() {
		return service.searchAllHITs();
	}

	public void printDetailsAboutHits(String pathToSuccessFile) throws IOException {
		HITDataInput dataInput = new HITDataCSVReader(pathToSuccessFile);

		HIT[] hits = new HIT[dataInput.getNumRows() - 1];
		for (int i = 1; i < dataInput.getNumRows(); i++) {
			hits[i - 1] = this.service.getHIT(dataInput.getRowValues(i)[0]);
		}

		printDetailsAboutHits(hits);
	}

	public void sendMessage(String subject, String messageText, String[] workerId) {
		try {
			this.service.notifyWorkers(subject, messageText, workerId);
			System.out.println("Successfully sent!");
		} catch (Exception e) {
			System.err.println("cannot be sent!");
		}
	}

	public void printDetailsAboutHits(HIT[] hits) {
		for (int i = 0; i < hits.length; i++) {
			System.out.println("Hit " + hits[i].getHITId() + ":");
			System.out.println("\t" + "Description: " + hits[i].getDescription());
			System.out.println("\t" + "Hit Group ID: " + hits[i].getHITGroupId());
			System.out.println("\t" + "Assignments available: " + hits[i].getNumberOfAssignmentsAvailable());
			System.out.println("\t" + "Status: " + hits[i].getHITStatus());
			System.out.println("\t" + "Auto-aprove time: " + hits[i].getAutoApprovalDelayInSeconds());
			SimpleDateFormat f = new SimpleDateFormat();
			System.out.println("\t" + "HIT life time: " + f.format(hits[i].getExpiration().getTime()));

		}
	}

	public void FpostHits(File dirName, String hitPropFile) throws Exception {
		HitPostStatusWriter success = HitPostStatusWriter.getSuccessHitPostStatusWriter(dirName.getPath());
		HitPostStatusWriter failure = HitPostStatusWriter.getFailureHitPostStatusWriter(dirName.getPath());

		int count = 1;
		for (File file : dirName.listFiles()) {
			System.out.println("Processing file " + count++ + " out of " + dirName.listFiles().length);
			if (file.isDirectory() || file.getName().contains("results") || file.getName().contains("failure")
					|| file.getName().contains("success")) {

				continue;
			}

			HITProperties props = new HITProperties(hitPropFile);
			HITQuestion question = new HITQuestion(file.getPath());

			QAPValidator.validate(question.getQuestion());

			HIT hit = null;
			try {
				hit = this.service.createHIT(null, props.getTitle(), props.getDescription(), props.getKeywords(),
						question.getQuestion(), new Double(props.getRewardAmount()),
						new Long(props.getAssignmentDuration()), new Long(props.getAutoApprovalDelay()),
						new Long(props.getLifetime()), new Integer(props.getMaxAssignments()), props.getAnnotation(),
						null, null);

				success.addHit(hit);
				System.out.println("here1 posthits");

			} catch (Exception e) {
				if (hit != null)
					failure.addHit(hit);
				else {
					failure.addHIT();
				}
			}
		}
	}

	public void postHits(File dirName, String hitPropFile, String qualID) throws Exception {
		HitPostStatusWriter success = HitPostStatusWriter.getSuccessHitPostStatusWriter(dirName.getPath());
		HitPostStatusWriter failure = HitPostStatusWriter.getFailureHitPostStatusWriter(dirName.getPath());

		int count = 1;
		for (File file : dirName.listFiles()) {
			if (file.isDirectory() || file.getName().contains("results") || file.getName().contains("failure")
					|| file.getName().contains("success")) {

				continue;
			}

			System.out.println("Processing file " + count++ + " out of " + dirName.listFiles().length);

			HITProperties props = new HITProperties(hitPropFile);
			HITQuestion question = new HITQuestion(file.getPath());
			QAPValidator.validate(question.getQuestion());

			// information about qualificationRequirement, it needs to be an
			// array because there might be more than one qualification required
			QualificationRequirement[] qualReq = new QualificationRequirement[1];
			qualReq[0] = new QualificationRequirement();
			qualReq[0].setQualificationTypeId(qualID);
			qualReq[0].setComparator(Comparator.EqualTo);
			qualReq[0].setIntegerValue(new int[] { 100 });

			qualReq[0].setRequiredToPreview(false);

			System.out.println("qual ID:  " + qualReq[0]);

			HIT hit = null;
			try {
				hit = this.service.createHIT(null, props.getTitle(), props.getDescription(), props.getKeywords(),
						question.getQuestion(), new Double(props.getRewardAmount()),
						new Long(props.getAssignmentDuration()), new Long(props.getAutoApprovalDelay()),
						new Long(props.getLifetime()), new Integer(props.getMaxAssignments()), props.getAnnotation(),
						qualReq, null);

				success.addHit(hit);
				System.out.println("printing in success file....created:   " + hit.getHITId());
				System.out.println("HIT location:");
				System.out.println(service.getWebsiteURL() + "/mturk/preview?groupId=" + hit.getHITTypeId());

			} catch (Exception e) {
				if (hit != null)
					failure.addHit(hit);
				else {
					failure.addHIT();
				}
			}
		}
	}

	public void postTestHit(int numOfQuestionsInHit, String hitName) throws Exception {
		Scanner inOverview = new Scanner(new File("./conf/testQuestion/myTestHits.overview"));
		Scanner inQuestion = new Scanner(new File("./conf/testQuestion/myTestHits.question"));
		PrintWriter output = new PrintWriter(new File("./data/TEST_HITS/" + hitName));

		while (inOverview.hasNext()) {
			output.println(inOverview.nextLine());
		}
		inOverview.close();

		for (int i = 0; i < numOfQuestionsInHit; i++) {
			while (inQuestion.hasNext()) {
				String line = inQuestion.nextLine();
				output.println(line);
			}
			inQuestion = new Scanner(new File("./conf/testQuestion/myTestHits.question"));
			;
		}
		inQuestion.close();

		output.println("</QuestionForm>");
		output.close();

		HITProperties props = new HITProperties("./conf/testQuestion/myTestHits.properties");
		HITQuestion question = new HITQuestion("./data/TEST_HITS/" + hitName);
		QAPValidator.validate(question.getQuestion());

		HitPostStatusWriter success = HitPostStatusWriter.getSuccessHitPostStatusWriter("./data/TEST_HITS");
		HitPostStatusWriter failure = HitPostStatusWriter.getFailureHitPostStatusWriter("./data/TEST_HITS");

		HIT hit = null;
		try {
			hit = this.service.createHIT(null, props.getTitle(), props.getDescription(), props.getKeywords(),
					question.getQuestion(), new Double(props.getRewardAmount()),
					new Long(props.getAssignmentDuration()), new Long(props.getAutoApprovalDelay()),
					new Long(props.getLifetime()), new Integer(props.getMaxAssignments()), props.getAnnotation(),
					props.getQualificationRequirements(), null);

			success.addHit(hit);
		} catch (Exception e) {
			if (hit != null)
				failure.addHit(hit);
			else {
				failure.addHIT();
			}
		}
	}

	public void getHITResultsForTestHITToFile() throws IOException {
		HITDataInput success = new HITDataCSVReader("./data/TEST_HITS/hits.success");

		for (int i = 1; i < success.getNumRows(); i++) {
			Assignment[] ass = this.service.getAllSubmittedAssignmentsForHIT(success.getRowValues(i)[0]);
			PrintWriter out = new PrintWriter("./data/TEST_HITS/hit" + i + ".results");
			for (Assignment a : ass) {
				String answerXML = a.getAnswer(); 
				out.print(answerXML);
			}
			out.close();
		}
	}

	public void getHITResults(TestType testType, String successFileName) throws IOException {
		String pathToSuccessFile = "";
		String outputFile = "";
		switch (testType) {
		case MULTIPLE_CHOICE_DEFINITIONS:
			pathToSuccessFile += Resources.MC_DEFINITIONS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.MC_DEFINITIONS_HITS_TO_POST_DIR + "/" + "results";
			break;
		case MULTIPLE_CHOICE_RELATIONSHIPS:
			pathToSuccessFile += Resources.MC_RELATIONS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.MC_RELATIONS_HITS_TO_POST_DIR + "/" + "results";
			break;
		case MULTIPLE_CHOICE_VISUALS:
			pathToSuccessFile += Resources.MC_VISUALS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.MC_VISUALS_HITS_TO_POST_DIR + "/" + "results";
			break;
		case TRUE_FALSE_DEFINITIONS:
			pathToSuccessFile += Resources.TF_DEFINITIONS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.TF_DEFINITIONS_HITS_TO_POST_DIR + "/" + "results";
			break;
		case TRUE_FALSE_RELATIONSHIPS:
			pathToSuccessFile += Resources.TF_RELATIONS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.TF_RELATIONS_HITS_TO_POST_DIR + "/" + "results";
			break;
		case TRUE_FALSE_VISUALS:
			pathToSuccessFile += Resources.TF_VISUALS_HITS_TO_POST_DIR + "/" + successFileName;
			outputFile += Resources.TF_VISUALS_HITS_TO_POST_DIR + "/" + "results";
			break;
		default:
			return;
		}

		HITDataInput success = new HITDataCSVReader(pathToSuccessFile);

		for (int i = 1; i < success.getNumRows(); i++) {
			Assignment[] ass = this.service.getAllAssignmentsForHIT(success.getRowValues(i)[0]);

			PrintWriter out = new PrintWriter(outputFile + i + "id.xml");
			for (Assignment a : ass) {
				String answerXML = a.getAnswer();
				String workerID = a.getWorkerId();
				try {
					this.service.approveAssignment(a.getAssignmentId(), "Thank you!");
				} catch (Exception e) {
					; // do I care?
				}
				out.println("WorkerID: " + workerID);
				out.print(answerXML);
			}
			out.close();
		}
	}

	public void deleteTestHIT() throws IOException {
		final HITDataInput success = new HITDataCSVReader("./data/TEST_HITS/hits.success");

		ArrayList<String> HITids = new ArrayList<String>();
		for (int i = 1; i < success.getNumRows(); i++) {
			HITids.add(success.getRowValues(i)[0]);
		}
		this.service.deleteHITs(HITids.toArray(new String[HITids.size()]), true, true, null);
	}

	public void deleteHIT(String hitID) {
		this.service.deleteHITs(new String[] { hitID }, true, true, null);
	}

	public void deleteHIT(String[] hitIDs) {
		this.service.deleteHITs(hitIDs, true, true, null);
	}

	public void deleteHITs(String pathToSucessFile) throws IOException {
		HITDataInput success = new HITDataCSVReader(pathToSucessFile);

		ArrayList<String> HITids = new ArrayList<String>();
		for (int i = 1; i < success.getNumRows(); i++) {
			HITids.add(success.getRowValues(i)[0]);
		}
		this.service.deleteHITs(HITids.toArray(new String[HITids.size()]), true, true, null);
	}

	public static HitsUtils getHitsUtilsInstance() {
		synchronized (HitsUtils.class) {
			if (singleton == null) {
				singleton = new HitsUtils();
			}
		}
		return singleton;
	}

	static {
		// create folders for outputs/logs
		File dir = new File("./data");
		if (!dir.exists())
			dir.mkdir();
		dir = new File("./data/TEST_HITS");
		if (!dir.exists())
			dir.mkdir();
	}

	private HitsUtils() {
		// connect to Amazon MTurk services
		this.service = new RequesterService(new PropertiesClientConfig("src/main/config/mturk.properties"));
	}

	private static class HitPostStatusWriter {
		private String fileName;
		private PrintWriter writer;
		private static HitPostStatusWriter successWriter;// = new
															// HitPostStatusWriter("hits.input.success");
		private static HitPostStatusWriter failureWriter;// = new
															// HitPostStatusWriter("hits.input.failure");

		private HitPostStatusWriter(String fileName) {
			try {
				this.fileName = fileName;
				this.writer = new PrintWriter(new FileWriter(fileName));
				this.writer.println("\"hitid\"\t\"hittypeid\"");
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static HitPostStatusWriter getSuccessHitPostStatusWriter(String pathToSuccessFile) {
			successWriter = new HitPostStatusWriter(pathToSuccessFile + "/hits.success");
			return successWriter;
		}

		public static HitPostStatusWriter getFailureHitPostStatusWriter(String pathToFailureFile) {
			failureWriter = new HitPostStatusWriter(pathToFailureFile + "/hits.failure");
			return failureWriter;
		}

		public void addHit(HIT hit) {
			if (hit == null) {
				return;
			}
			try {
				this.writer = new PrintWriter(new FileWriter(this.fileName, true));
				this.writer.println("\"" + hit.getHITId() + "\"\t\"" + hit.getHITTypeId() + "\"");
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void addHIT() {
			try {
				this.writer = new PrintWriter(new FileWriter(this.fileName, true));
				this.writer.println("\"" + "unknown_HIT_id" + "\"\t\"" + "unknown_HIT_type_id" + "\"");
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void addHits(HIT[] hits) {
			try {
				this.writer = new PrintWriter(new FileWriter(this.fileName, true));
				for (int i = 0; i < hits.length; i++) {
					this.writer.println("\"" + hits[i].getHITId() + "\"\t\"" + hits[i].getHITTypeId() + "\"");
				}
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
