import java.util.Random;

/**
 * Class ThreadRunner represents runners, who are implemented as separate threads.
 * Constructor takes following parameters:
 * 		String runnersName: runner's name;
 * 		int runnersSpeed: runners speed — how many meters the runner travels in each move;
 * 		int restPercentage: likelihood in percents that on any given move 
 * 							the runner will rest instead of run.
 * Constructor automatically subscribes an instance to RunnersCommunity.
 */
public class ThreadRunner implements Runnable, IRunner{

	private Thread ct;
	private String runnersName; // runner's name
	private int runnersSpeed; // runner's speed
	private int restPercentage; // likelihood that on any given move the runner will rest instead of run
	private Random random = new Random();
	private RunnersCommunity runners = RunnersCommunity.getInstance();
	private boolean isWinner = false; // is the instance a winner
	private int position;
	
	public ThreadRunner(String runnersName, int runnersSpeed, int restPercentage) {
		this.runnersName = runnersName;
		this.runnersSpeed = runnersSpeed;
		this.restPercentage = restPercentage;
		runners.subscribe(this);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * The run method of the ThreadRunner class consists of a loop that repeats until the
	 * runner has reached 1000 meters. Each time through the loop, the thread decides
	 * whether it should run or rest based on a random number and the percentage passed to the
	 * constructor (restPercentage). If this random number indicates that the runner should run, the
	 * method adds the speed value passed to the constructor (runnersSpeed). The run method
 	 * sleeps for 100 milliseconds on each repetition of the loop.
	 */
	@Override
	public void run() {
		ct = Thread.currentThread();
		boolean chance;
		position = 0;
		
		while (!ct.isInterrupted()) {
			chance = tryChanceToRun();
			if (chance) {
				// move forward
				position += runnersSpeed;
			}
			if (position >= 1000) {
				// stop the race
				isWinner = true;
				MarathonRaceApp.reportPosition(this);				
				MarathonRaceApp.finished(this);				
				break;
			}
			// report current position if the winner's position was not reported yet
			MarathonRaceApp.reportPosition(this);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		// wait for a winner to be announced first
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		if (!isWinner) {
			System.out.println(runnersName + ": You beat me fair and square.");
		}
	}

	
	/* (non-Javadoc)
	 * @see IRunner#tryChanceToRun()
	 * Calculates a random number between 1 and 100 to determine whether a thread should run or rest.
	 * Thread rests if the number is less than or equal to the percentage of time
	 * that the thread rests (restPercentage). Otherwise, the thread runs.
	 */
	@Override
	public boolean tryChanceToRun() {
		if ((random.nextInt(100) + 1) > restPercentage) {
			return true;
		} else {
			return false;
		}		
	}

	
	/* (non-Javadoc)
	 * @see IRunner#inrerruptRace()
	 * Interrupts runner's thread.
	 */
	@Override
	public void inrerruptRace() {
		ct.interrupt();		
	}
	
	@Override
	public String getRunnersName() {
		return runnersName;
	}

	@Override
	public String getRunnersPosition() {
		return Integer.toString(position);
	}

	@Override
	public boolean isWinner() {
		return isWinner;
	}
}
