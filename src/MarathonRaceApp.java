import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Daulet Kshibekov
 * 
 * Application that simulates a marathon race between interesting groups of participants, 
 * the instances of which are loaded either from .txt, .xml or .db file depending on a user's choice.
 */
public class MarathonRaceApp {
	
	// is there a winner
	private static boolean hasWinner;
	private static boolean raceFinished;

	/**
	 * Main method that asks user to choose the source file marathon participants would be downloaded from,
	 * and starts a race.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// loads a data from source and returns a list of created threads
		RunnersLoader loader = new RunnersLoader();
		MarathonValidator validator = new MarathonValidator(new Scanner(System.in));
		// stores user's choice
		int choice = 0;
		// start a loop
		while (choice != 5) {
			// holds a list of runners
			ArrayList<Thread> runners = new ArrayList<>();
			hasWinner = false;
			raceFinished = false;
			greetUser();
			// get user's choice
			choice = validator.getIntWithinRange("Enter your choice:", 1, 5);
			// handle user's choice
			runners = handleChoice(loader, runners, validator, choice);
			if (choice != 5) waitEnter();
		}	
	}

	private static void waitEnter() {
		System.out.println("Press Enter to continue...");
		try{
			System.in.read();
		}
		catch(Exception e){}
	}

	/**
	 * Handles user's choice. Gets file name, loads a data from it 
	 * and either runs the race or quits the application.
	 * 
	 * @param RunnersLoader loader
	 * @param ArrayList<Thread> runners
	 * @param MarathonValidator validator
	 * @param int choice
	 * @return ArrayList<Thread>
	 * @throws InterruptedException
	 */
	private static ArrayList<Thread> handleChoice(RunnersLoader loader, ArrayList<Thread> runners,
			MarathonValidator validator, int choice) throws InterruptedException {
		System.out.println();
		String fileName;
		switch (choice) {
			case 1:
				/* FinalDbData.db 
				   If file does not exist, it will be created 
				   and filled with default data automatically. */
				fileName = validator.getRequiredString("Enter SQLite database file name:");
				runners = loader.loadDb("src/database/" + fileName);
				break;
			case 2:
				// FinalXMLData.xml
				fileName = validator.getRequiredString("Enter XML file name:");
				runners = loader.loadXml("src/database/" + fileName);
				break;
			case 3:
				// FinalTextData.txt
				fileName = validator.getRequiredString("Enter TXT file name:");
				runners = loader.loadTxt("src/database/" + fileName);
				break;
			case 4:
				runners = loader.loadDefaults();
				break;
			case 5:
				System.out.println("Thank you for using my Marathon Race Program");
				break;
		}
		if (choice != 5) {
			startRace(runners);
		}
		System.out.println();
		return runners;
	}

	/**
	 * Prints greetings to user and prints list of choices.
	 */
	private static void greetUser() {
		System.out.println("Welcome to the Marathon Race Runner Program!");
		System.out.println();
		System.out.println("Select your data source:");
		System.out.println();
		System.out.println("1. Derby database");
		System.out.println("2. XML file");
		System.out.println("3. Text file");
		System.out.println("4. Default two runners");
		System.out.println("5. Exit");
		System.out.println();
	}
	
	/**
	 * Starts runners' threads and joins them to the main thread.
	 * @param runners
	 * @throws InterruptedException
	 */
	private static void startRace(ArrayList<Thread> runners) throws InterruptedException {
		if (runners != null && !runners.isEmpty()) {
			System.out.println();
			System.out.println("Get set...Go!");
			for (Thread runner: runners) {
				runner.start();
			}
			for (Thread runner: runners) {
				runner.join();
			}
		}
	}	
	
	/**
	 * Registers a winner, declares the end of a race and lets secondary winners 
	 * with the almost the same result to concede the race.
	 * 
	 * @param ThreadRunner winner
	 */
	public static synchronized void finished(ThreadRunner winner) {
		// interrupt all runners' threads
		RunnersCommunity.getInstance().interruptAll();
		// for those who finished the race at almost the same time as winner
		if (hasWinner) {
			System.out.println(winner.getRunnersName() + ": You beat me fair and square.");
		} else {
			hasWinner = true;
			System.out.println();
			System.out.println("The race is over! The " + winner.getRunnersName() + " is the winner.");
			System.out.println();
		}
	}
		
	/**
	 * Reports a runner's current position.
	 * Prevents other runners reporting positions after the winner's position is reported.
	 * It means that the winner's position (1000) must be the last reported.
	 * @param ThreadRunner runner
	 */
	public static synchronized void reportPosition(ThreadRunner runner) {
		if (runner.isWinner() && !raceFinished) {
			raceFinished = true;
			System.out.println(runner.getRunnersName() + " : " + runner.getRunnersPosition());
			System.out.println(runner.getRunnersName() + " : I finished!");
		}		
		if (!raceFinished) {
			System.out.println(runner.getRunnersName() + " : " + runner.getRunnersPosition());
		}		
	}
}
