import java.util.ArrayList;


/**
 * Class RunnersCommunity registers IRunner-child instances as subscribers
 * and interrupts their threads when the race is over.
 * Observer and singleton patterns are implemented.
 */
public class RunnersCommunity {
	private ArrayList<IRunner> runners = new ArrayList<>();
	private static RunnersCommunity instance = null;
	
	/**
	 * registers ThreadRunner instances as subscribers
	 */
	public synchronized void subscribe(IRunner runner) {
		runners.add(runner);
	}
	
	/**
	 * interrupts subscribers' threads
	 */
	public synchronized void interruptAll() {
		if (!runners.isEmpty()) {
			for (IRunner runner: runners) {
				runner.inrerruptRace();
			}
		}
	}
	
	/**
	 * @return RunnersCommunity
	 * 
	 * returns the only instance of this class
	 */
	public static synchronized RunnersCommunity getInstance() {
		if (instance == null) {
			instance = new RunnersCommunity();
		}
		return instance;		
	}
}
