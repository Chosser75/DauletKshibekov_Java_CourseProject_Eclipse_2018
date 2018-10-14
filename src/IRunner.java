
/**
 * Interface for runners objects. *
 */
public interface IRunner {

	/**
	 * The run method should consist of a loop that repeats until the
	 * runner has reached 1000 meters. Each time through the loop, the thread should decide
	 * whether it should run or rest based on a random number and the percentage passed to the
	 * constructor. If this random number indicates that the runner should run, the
	 * method should add the speed value passed to the constructor. The run method should
 	 * sleep for 100 milliseconds on each repetition of the loop.
	 */
	void run();
	
	/**
	 * Should Calculate a random number between 1 and 100 to determine whether a thread should run or rest.
	 * Thread should rest if the number is less than or equal to the percentage of time
	 * that the thread rests. Otherwise, the thread should run.
	 * @return boolean
	 */
	boolean tryChanceToRun();
	
	/**
	 * Should interrupt runner's thread
	 */
	void inrerruptRace();
	
	String getRunnersName();
	
	String getRunnersPosition();

	boolean isWinner();
}
