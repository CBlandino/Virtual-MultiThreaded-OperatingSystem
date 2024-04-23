import java.util.concurrent.Semaphore;

public abstract class UserlandProcess
implements Runnable
{
	private Thread thread;
	private Semaphore semaphore;
	private boolean quantumExpired;
	private long wakeUpTime;
	private boolean isDone;

	private static final int PAGE_SIZE = 1024;
	private static final int TLB_SIZE = 2;
	private static final int MEMORY_SIZE = 1024 * 1024;
	private static final byte[] memory = new byte[MEMORY_SIZE];

	private static final int VIRTUAL_PAGE_COUNT = MEMORY_SIZE / PAGE_SIZE;

	//private static final Random random = new Random();

	private static int[][] TLB = {{-1, -1}, {-1, -1}}; // TLB: [0] = Virtual, [1] = Physical

	public static String[] ProcessMemory;

	public UserlandProcess(String name)
    {
        semaphore = new Semaphore(0);
        quantumExpired = false;
        thread = new Thread(this, name);
		isDone = false;

        thread.start();

		ProcessMemory = new String[1024];
    }

	public void requestStop()
    {
        quantumExpired = true;
    }

	public abstract void main();

	public boolean isStopped()
	{
		if (semaphore.availablePermits() == 0)
		{
			return true;
		}
		else
			return false;
	}

	public boolean isDone()
	{
		return isDone;
	}

	public void stop()
	{
		//OS.debug("UserlandProcess.stop() " + this.getClass());
		//System.out.println("Stop");
		semaphore.acquireUninterruptibly();
		// bmSystem.out.println("stop");
	}

	public void start()
	{
		//OS.debug("UserlandProcess.start() " + this.getClass());
		//System.out.println("Start");
		semaphore.release();
	}

	public void run()
	{
		try
		{
			semaphore.acquire();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("RUNNING");
		//OS.debug("UserlandProcess.run() " + this.getClass());
		main();

		isDone = true;

		OS.switchProcess();
	}

	public void cooperate()
	{
		if (quantumExpired)
		{
			quantumExpired = false;
			OS.switchProcess();
		}

	}

	/**
	 * Reads a byte value from the specified memory address.
	 *
	 * @param address the memory address from which the byte value will be read
	 * @return the byte value read from the memory address
	 */
	public static byte read(int address)
	{
		int virtualPage = address / PAGE_SIZE;
		int pageOffset = address % PAGE_SIZE;

		int physicalPage = -1;

		// Check TLB for mapping
		System.out.println("Checking TLB for mapping...");
		for (int i = 0; i < TLB_SIZE; i++)
		{
			if (TLB[i][0] == virtualPage)
			{
				physicalPage = TLB[i][1];
				System.out.println("Mapping found in TLB. Physical page: " + physicalPage);
				break;
			}
		}

		// If mapping not found in TLB, perform GetMapping OS call
		if (physicalPage == -1)
		{
			System.out.println("Mapping not found in TLB. Performing GetMapping OS call...");
			OS.getMapping(virtualPage);
			// Retry reading after updating TLB
			System.out.println("Retrying read after updating TLB...");
			return read(address);
		}

		// Calculate physical address
		int physicalAddress = physicalPage * PAGE_SIZE + pageOffset;
		System.out.println("Physical address calculated: " + physicalAddress);
		// Return byte from memory
		System.out.println("Returning byte from memory at physical address " + physicalAddress);
		return memory[physicalAddress];
	}

	/**
	 * Writes a byte value to the specified memory address.
	 *
	 * @param address the memory address where the byte value will be written
	 * @param value the byte value to be written
	 */
	public static void write(int address, byte value)
	{
		int virtualPage = address / PAGE_SIZE;

		int physicalPage = -1;

		// Check TLB for mapping
		System.out.println("Checking TLB for mapping...");
		for (int i = 0; i < TLB_SIZE; i++)
		{
			System.out.println("========== COMPARING TLB["+i+"][0] ("+TLB[i][0]+") TO "+virtualPage+"==========");
			if (TLB[i][0] == virtualPage)
			{
				physicalPage = TLB[i][1];
				System.out.println("Mapping found in TLB. Physical page: " + physicalPage);
				break;
			}
		}

		// If mapping not found in TLB, perform GetMapping OS call
		if (physicalPage == -1)
		{
			System.out.println("Mapping not found in TLB. Performing GetMapping OS call...");
			OS.getMapping(virtualPage);
			// Retry writing after updating TLB
			System.out.println("Retrying write after updating TLB...");
			write(address, value);
			return;
		}
		// Calculate physical address
		int pageOffset = address % PAGE_SIZE;
		int physicalAddress = physicalPage * PAGE_SIZE + pageOffset;
		System.out.println("Physical address calculated: " + physicalAddress);

		// Write byte to memory
		System.out.println("Writing byte to memory at physical address " + physicalAddress);
		memory[physicalAddress] = value;
	}

	/**
	 * Updates the Translation Lookaside Buffer (TLB) with the mapping of a virtual page to a physical page.
	 *
	 * @param virtualPage the virtual page number to be mapped
	 * @param physicalPage the physical page number corresponding to the virtual page
	 * @param randomVpage a boolean indicating whether the virtual page was selected randomly
	 */
	public static void updateTLB(int virtualPage, int physicalPage, boolean randomVpage)
	{
		UserlandProcess.TLB[randomVpage ? 1 : 0][1] = physicalPage;
		UserlandProcess.TLB[randomVpage ? 1 : 0][0] = virtualPage;
	}

	/**
	 * Clears the Translation Lookaside Buffer (TLB) by resetting all entries to -1.
	 */
	public static void clearTLB()
	{
		for (int i = 0; i < TLB_SIZE; i++)
		{
			TLB[i][0] = -1;
			TLB[i][1] = -1;
		}
	}
}

