import java.util.Scanner;

/**
 * Validates user input data.
 */
public class MarathonValidator {
	private Scanner sc;

	public MarathonValidator(Scanner sc){
		this.sc = sc;
	}

	// Gets and validates user's string
	public String getRequiredString(String prompt) {
		String answer = null;
		sc.nextLine();
		while (true) {
			System.out.print(prompt + " ");
			answer = sc.nextLine();	
			if (answer.isEmpty()) {
				System.out.println("Error! This entry is required. Try again.");
				continue;
			} else {
				return answer;
			}
		}		
	}
	
	// Gets the data (integer number) from the user
	public int getInt(String prompt) {
		boolean done = false;
		int intNum = 0;
		while(!done) {
			System.out.print(prompt + " ");
			if (sc.hasNextInt()) {
				intNum = sc.nextInt();
				done = true;
			} else {
				System.out.println("Error! Invalid integer value. Try again.");
				sc.nextLine();
				continue;
			}			
		}
		return intNum;
	}
	
	// Gets and validates the data (integer number) from the user
	public int getIntWithinRange(String prompt, int min, int max) {
		boolean done = false;
		int intNum = 0;
		while(!done) {
			intNum = getInt(prompt);
			if (intNum < min) {
				System.out.println("Error! Number must be greater than " + (min - 1));
			} else if (intNum > max) {
				System.out.println("Error! Number must be less than " + (max + 1));
			} else {
				done = true;
			}				
		}	
		return intNum;
	}
}
