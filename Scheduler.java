import java.time.Clock;
import java.util.*;

/**
 * Manages the scheduling of processes.
 */
public class Scheduler
{
	public int virtualPage;
	public PCB randomProcess;
	public static Map<PCB, Integer> diskPagesMap = new HashMap<>();
	private int nextDiskPage = 0;
	private Map<Integer, PCB> allPCBs; // New hashmap with PCB objects
	private Map<Integer, PCB> waitingProcesses;
	private LinkedList<PCB> interactiveQueue;
	private LinkedList<PCB> realtimeQueue;
	private LinkedList<PCB> backgroundQueue;
	// private LinkedList<PCB> sleepingQueue;

	private LinkedList<PCB> sleepingProcesses;

	private Timer interruptTimer;
	private Clock clock;
	private PCB currentlyRunning;
	public Kernel kernel = OS.kernel;

	private static final int pageSize = 1024;
	private int[] pcbArray;
	private Random rand = new Random();
	private boolean[] pagesUsed;
	/**
	 * Constructs a Scheduler object.
	 */
	public Scheduler(Kernel kernel, boolean[] pagesUsed)
	{
		allPCBs = new HashMap<>();
		waitingProcesses = new HashMap<>();

        sleepingProcesses = new LinkedList<>();
		interactiveQueue = new LinkedList<>();
		realtimeQueue = new LinkedList<>();
		backgroundQueue = new LinkedList<>();
		this.kernel = kernel;

        interruptTimer = new Timer(true);
		clock = OS.getClock();
		interruptTimer.schedule(new InterruptTask(), 250, 250); // Schedule interrupt every 250ms

		this.pagesUsed = pagesUsed;
	}

	/**
	 * Creates a new process with the given priority.
	 * @param up The userland process.
	 * @param priority The priority of the process.
	 * @return The process ID.
	 * @throws Exception if an error occurs.
	 */
	public int createProcess(UserlandProcess up, OS.Priority priority) throws Exception 
	{
		PCB pcb = new PCB(up, priority);

		allPCBs.put(pcb.getPid(), pcb); // Original hashmap with PCB objects

		System.out.println("|||| SCHEDULER Creating Process: " + pcb.getClassName()+ " 	With pID: " + GetPidByName(pcb.getClassName()) + " Tied to:" + pcb + " |||||");

		if (currentlyRunning == null) 
		{
			currentlyRunning = pcb;
		}

		else 
		{
			switch (priority) 
			{
			case INTERACTIVE:
				interactiveQueue.add(pcb);
				break;
			case REALTIME:
				realtimeQueue.add(pcb);
				break;
			case BACKGROUND:
				backgroundQueue.add(pcb);
				break;
			}
		}
		return pcb.getPid(); // Return the process ID
	}

	/**
	 * Switches the currently running process.
	 * @throws Exception if an error occurs.
	 */
	public void switchProcess() throws Exception
	{
		if (!currentlyRunning.isDone() && currentlyRunning != null)
		// Determine the appropriate queue based on the priority of the currently
		// running process
		{
			//System.out.println(currentlyRunning + " " + currentlyRunning.getPriority());
			//System.out.println("|||| " + currentlyRunning.getPriority() + " ||||");
			switch (currentlyRunning.getPriority())
			{
				case INTERACTIVE:

						interactiveQueue.addLast(currentlyRunning);

					getnextProcess();
					break;

				case REALTIME:

						realtimeQueue.addLast(currentlyRunning);

					getnextProcess();
					break;

				case BACKGROUND:

						backgroundQueue.addLast(currentlyRunning);

					getnextProcess();
					break;

				default:
					throw new IllegalArgumentException("Invalid priority");
			}
		}

		if(currentlyRunning.isDone())
		{
			pcbArray = currentlyRunning.getIntArray();
			for (int i = 0; i <= 9; i++)
			{
				if(pcbArray[i] != -1)
				{
					kernel.close(pcbArray[i]);
				}
			}
			allPCBs.remove(currentlyRunning);
		}
	}

	/**
	 * Gets the next process to run.
	 */
	private void getnextProcess()
	{
		Random rand = new Random();
		int randomNumber;
		randomNumber = rand.nextInt(10) + 1;

		if(realtimeQueue.isEmpty() && interactiveQueue.isEmpty())
		{
			randomNumber = 1;
		}

		else if (realtimeQueue.isEmpty() && !interactiveQueue.isEmpty())
		{
			randomNumber = rand.nextInt(5) + 1;
		}

		else if (!realtimeQueue.isEmpty() && !backgroundQueue.isEmpty())
		{
			if(rand.nextBoolean())
			{
				randomNumber = 1;
			}
			else
			{
				randomNumber = rand.nextInt(5) + 6;
			}
		}

		else if(!realtimeQueue.isEmpty() && !interactiveQueue.isEmpty() && !backgroundQueue.isEmpty())
		{
			randomNumber = rand.nextInt(10) + 1;
		}


		if(randomNumber >= 6)
		{
			currentlyRunning = realtimeQueue.getFirst();
			realtimeQueue.removeFirst();
		}
		else if (randomNumber >= 2 && randomNumber <= 5)
		{
			currentlyRunning = interactiveQueue.getFirst();
			interactiveQueue.removeFirst();
		}
		else if(randomNumber == 1)
		{
			currentlyRunning = backgroundQueue.getFirst();
			backgroundQueue.removeFirst();

		}
	}

	/**
	 * Retrieves the currently running process.
	 * @return The currently running process.
	 */
	public PCB getCurrentlyRunning()
	{
		// TODO Auto-generated method stub
		// System.out.println(currentlyRunning);
		return currentlyRunning;
	}

	/**
	 * Puts the current process to sleep for the specified duration.
	 * @param milliseconds The duration to sleep in milliseconds.
	 * @throws Exception if an error occurs.
	 */
	public void sleepCurrentProcess(int milliseconds) throws Exception
	{
		long currentTime = clock.millis();
		long wakeUpTime = currentTime + milliseconds;

		currentlyRunning.setWakeUpTime(wakeUpTime);
		sleepingProcesses.add(currentlyRunning);
		getnextProcess();
		switchProcess();
	}

	/**
	 * Wakes up processes that have slept.
	 */
	public void wakeUpProcesses() 
	{
		long currentTime = clock.millis();

		for (PCB pcb : sleepingProcesses)
		{
			if (pcb.getWakeUpTime() <= currentTime)
			{
				// Determine the appropriate queue based on process priority
				LinkedList<PCB> targetQueue;
				switch (pcb.getPriority())
				{
				case INTERACTIVE:
					targetQueue = interactiveQueue;
					targetQueue.addLast(pcb);
					break;
				case REALTIME:
					targetQueue = realtimeQueue;
					targetQueue.addLast(pcb);
					break;
				case BACKGROUND:
					targetQueue = backgroundQueue;
					targetQueue.addLast(pcb);
					break;
				default:
					System.out.println("Process has no valid priority queue");
					break;
				}
			}
		}
		// Remove processes that have been woken up from the sleepingProcesses list
		sleepingProcesses.removeIf(pcb -> pcb.getWakeUpTime() <= currentTime);
	}

	/**
	 * Retrieves the process ID of the currently running process.
	 *
	 * @return The process ID of the currently running process, or -1 if no process is running.
	 */
	public int GetPid()
	{
		return currentlyRunning != null ? currentlyRunning.getPid() : -1;
	}

	/**
	 * Retrieves the process ID of a process by its name.
	 *
	 * @param name The name of the process to find.
	 * @return The process ID of the process with the given name, or -1 if no process with the given name is found.
	 */
	public int GetPidByName(String name)
	{
		for (Map.Entry<Integer, PCB> entry : allPCBs.entrySet())
		{
			if (entry.getValue().getClassName().equals(name))
			{
				return entry.getKey(); // Return the PID if the name matches
			}
		}
		return -1; // Return -1 if no process with the given name is found
	}

	/**
	 * Sends a kernel message to the specified receiver process.
	 * @param km The KernelMessage to be sent.
	 */
	public void sendMessage(KernelMessage km)
	{
		int receiverPid = km.getReceiverPid();

		PCB receiver = allPCBs.get(receiverPid);

		if (receiver != null)
		{
			// Process found by PID
			receiver.addMessage(km);
		}
		else
		{
			// Handle the case when the receiver PCB is not found
			System.out.println("Receiver PCB not found for PID: " + receiverPid);
		}
	}

	/**
	 * Waits for a kernel message. If a message is available, returns it; otherwise, deschedules the process.
	 * @return The received KernelMessage.
	 */
	public KernelMessage waitForMessage() throws Exception
	{
		if (currentlyRunning != null)
		{
			KernelMessage message = currentlyRunning.getNextMessage();

			if (message != null)
			{
				waitingProcesses.remove(currentlyRunning.getPid());
				currentlyRunning.setWaitingForMessage(false);
				return message;
			}

			else
			{
				// No message available, deschedule the process
				currentlyRunning.setWaitingForMessage(true);
				waitingProcesses.put(currentlyRunning.getPid(), currentlyRunning);
				getnextProcess();
				switchProcess();
				return null;
			}
		}

		else
		{
			System.out.println("Current process not found: " + currentlyRunning.getPid());
			return null;
		}
	}



	public int allocateMemory(int size) {
		int pageCount = size / pageSize;
		int startPage = -1;

		for (int i = 0; i < pageSize; i++) {
			int freePageCount = 0;

			for (int j = i; j < pageSize; j++) {
				if (!pagesUsed[j]) {
					freePageCount++;
					if (freePageCount == pageCount) {
						startPage = i;
						break;
					}
				} else {
					freePageCount = 0;
				}
			}

			if (startPage != -1) {
				for (int k = startPage; k < startPage + pageCount; k++) {
					pagesUsed[k] = true;
				}

				// Allocate VirtualtoPhysicalMapping instances for the allocated pages
				for (int k = startPage; k < startPage + pageCount; k++) {
					currentlyRunning.addMapping(k, new VirtualtoPhysicalMapping());
				}
				return startPage;
			}
		}
		return -1;
	}

	public boolean freeMemory(int pointer, int size) {
		int startPage = pointer / pageSize;
		int endPage = (pointer + size) / pageSize;

		for (int i = startPage; i <= endPage; i++) {
			if (currentlyRunning.getPageMapping(i) != null) {
				pagesUsed[i] = false;
				currentlyRunning.removeMapping(i);
			}
		}

		return true;
	}

	public void getMapping(int vpage) {
		Random rand = new Random();
		boolean randomVpage = rand.nextBoolean();

		int physicalPage = currentlyRunning.getPhysicalPageNumber(vpage);

		if (physicalPage == -1) {
			PCB victim = getRandomProcess();
			int victimVpage = getRandomValidPage(victim);

			int victimPhysicalPage = victim.getPhysicalPageNumber(victimVpage);
			int newPhysicalPage = allocatePhysicalPage();

			// If data was written to disk, load it into memory
			if (victim.getOnDiskPageNumber(victimVpage) != -1) {
				// Load data from disk
				// Code to load data from disk goes here
			} else {
				// Populate memory with 0's
				Arrays.fill(memory, newPhysicalPage * PAGE_SIZE, (newPhysicalPage + 1) * PAGE_SIZE, (byte) 0);
			}

			// Update TLB and mappings
			currentlyRunning.updateTLB(vpage, newPhysicalPage, randomVpage);
			currentlyRunning.addMapping(vpage, new VirtualtoPhysicalMapping(newPhysicalPage, -1)); // assuming -1 indicates no disk page
			victim.removeMapping(victimVpage);
			victim.setPhysicalPageNumber(victimVpage, -1); // Clear victim's physical page
		} else {
			currentlyRunning.updateTLB(vpage, physicalPage, randomVpage);
		}
	}

	private PCB getRandomProcess() {
		Random rand = new Random();
		LinkedList<PCB> processes = new LinkedList<>(allPCBs.values());

		while (!processes.isEmpty()) {
			PCB process = processes.remove(rand.nextInt(processes.size()));
			if (getRandomValidPage(process) != -1) {
				return process;
			}
		}

		throw new RuntimeException("No process with physical memory found");
	}

	private int getRandomValidPage(PCB process) {
		Random rand = new Random();
		LinkedList<Integer> validPages = new LinkedList<>();

		for (int i = 0; i < VIRTUAL_PAGE_COUNT; i++) {
			if (process.getPhysicalPageNumber(i) != -1) {
				validPages.add(i);
			}
		}

		if (!validPages.isEmpty()) {
			return validPages.get(rand.nextInt(validPages.size()));
		} else {
			return -1;
		}
	}

	private int allocatePhysicalPage() {
		for (int i = 0; i < VIRTUAL_PAGE_COUNT; i++) {
			if (!pagesUsed[i]) {
				pagesUsed[i] = true;
				return i;
			}
		}
		throw new RuntimeException("No available physical pages");
	}

	/**
	 * Handles interrupt tasks.
	 */
	private class InterruptTask extends TimerTask 
	{

		@Override
		public void run()
		{
			// OS.debug("Scheduler.InterputTask.run()");
			// Call requestStop() on the currently running process
			if (currentlyRunning != null)
			{
				currentlyRunning.requestStop();
			}
			wakeUpProcesses();
		}
	}
}