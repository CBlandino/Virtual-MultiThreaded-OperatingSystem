import java.util.ArrayList;
import java.time.Clock;

/**
 * Represents the Operating System (OS) management class.
 */
public class OS
{

	/**
	 * Enumerates the types of system calls.
	 */
	enum callType
	{
		create_Process,
		switch_Process,
		sleep,
		write,
		seek,
		open,
		read,
		close,
		getpidbyname,
		getpid,
		wait_For_Message,
		send_Message,
		get_Mapping,
		allocateMem,
		freeMem

	}

	/**
	 * Enumerates the priorities for processes.
	 */
	 public enum Priority
	    {
	        INTERACTIVE,
	        REALTIME,
	        BACKGROUND
	    }

	//public static LinkedList<KernelMessage> messageQueue = new LinkedList<>();
	public static Object returnValue;
	public static callType currentCall;
	public static ArrayList<Object> parameters = new ArrayList<>();
	public static Clock clock = Clock.systemDefaultZone();
	public static Kernel kernel = new Kernel();
	public Scheduler scheduler;

	/**
	 * Creates a new process with default priority INTERACTIVE.
	 * @param up The UserlandProcess object to be created.
	 * @return The process ID.
	 */
	public static int CreateProcess(UserlandProcess up)
	{
        parameters.clear();
        parameters.add(up);

        Priority priority = Priority.INTERACTIVE;
        parameters.add(priority);


        currentCall = callType.create_Process;
		returnValue = null;

        switchtoKernel();


		while(returnValue == null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		//testing purpose
		int createReturnValue = (int) OS.returnValue;

		//testing purpose
		PCB currentlyRunningProcess = kernel.getScheduler().getCurrentlyRunning();

		//testing purpose
		System.out.println("|||| OS Creating Process " + currentlyRunningProcess.getClassName() + " With pID: " + GetPidByName(currentlyRunningProcess.getClassName()) + " ||||");

		return (int) createReturnValue;
    }

	/**
	 * Creates a new process with specified priority.
	 * @param up The UserlandProcess object to be created.
	 * @param priority The priority of the process.
	 * @return The process ID.
	 */
	public static int CreateProcess(UserlandProcess up, Priority priority)
	{
        parameters.clear();
        parameters.add(up);
        parameters.add(priority);

        currentCall = callType.create_Process;

		returnValue = null;

        switchtoKernel();

		while(returnValue == null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		//testing purpose
		PCB currentlyRunningProcess = kernel.getScheduler().getCurrentlyRunning();
		System.out.println("|||| OS Creating Process " + currentlyRunningProcess.getClassName() + " With pID: " + GetPidByName(currentlyRunningProcess.getClassName()) + " ||||");

        return (int) returnValue;
    }

	/**
	 * Initializes the operating system by starting the initial process and idle process.
	 * @param init The initial process.
	 */
	public static void Startup(UserlandProcess init)
	{
		IdleProcess idleProcess = new IdleProcess();
		CreateProcess(idleProcess, Priority.BACKGROUND);

		CreateProcess(init);
	}

	/**
	 * Switches the execution to the kernel.
	 */
	   private static void switchtoKernel()
	    {
			PCB currentlyRunningProcess = kernel.getScheduler().getCurrentlyRunning();

	        kernel.start();

	        // If the scheduler has a currently running process, stop it
			if(currentlyRunningProcess != null)
			{
				currentlyRunningProcess.stop();
			}
	    }

	/**
	 * Switches the execution to another process.
	 */
	public static void switchProcess()
	{
		parameters.clear();

		currentCall = callType.switch_Process;

		UserlandProcess.clearTLB();

		switchtoKernel();
	}

	public static void debug(String message)
	{
		System.out.println(message);
	}

	/**
	 * Pauses the current process for a specified duration.
	 * @param milliseconds The duration to sleep in milliseconds.
	 */
	public static void Sleep(int milliseconds)
	{
        parameters.clear();
        parameters.add(milliseconds);
        currentCall = callType.sleep;
        switchtoKernel();
    }

	/**
	 * Retrieves the clock instance.
	 * @return The clock instance.
	 */
	public static Clock getClock()
	{
        return clock;
    }

	/**
	 * Opens a resource identified by the given seed.
	 *
	 * @param seed The seed used to open the resource.
	 * @return An identifier for the opened resource.
	 */
	public static int open(String seed)
	{
		parameters.clear();
		parameters.add(seed);
		currentCall = callType.open;
		returnValue = null;

		switchtoKernel();

		while(returnValue == null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return (int) returnValue;
	}

	/**
	 * Closes the resource with the specified identifier.
	 *
	 * @param id The identifier of the resource to close.
	 */
	public static void close(int id)
	{
		parameters.clear();
		parameters.add(id);

		currentCall = callType.close;

		switchtoKernel();


	}

	/**
	 * Reads data from a resource with the specified identifier.
	 *
	 * @param id   The identifier of the resource to read from.
	 * @param size The size of the data to read.
	 * @return The data read from the resource.
	 */
	public static byte[] read(int id, int size)
	{
		parameters.clear();
		parameters.add(id);
		parameters.add(size);

		currentCall = callType.read;
		returnValue = null;

		switchtoKernel();

		return (byte[]) returnValue;
	}

	/**
	 * Writes data to a resource with the specified identifier.
	 *
	 * @param id   The identifier of the resource to write to.
	 * @param data The data to write.
	 * @return The number of bytes written.
	 */
	public static int write(int id, byte[] data)
	{
		parameters.clear();
		parameters.add(id);
		parameters.add(data);

		currentCall = callType.write;
		returnValue = null;

		switchtoKernel();

		return (int) returnValue;
	}

	/**
	 * Moves the read/write pointer of a resource to the specified position.
	 *
	 * @param id  The identifier of the resource.
	 * @param to  The position to move the pointer to.
	 */
	public static void seek(int id, int to)
	{
		parameters.clear();
		parameters.add(id);
		parameters.add(to);

		currentCall = callType.seek;

		switchtoKernel();

	}

	/**
	 * Retrieves the process ID of the current process.
	 *
	 * @return The process ID.
	 */
	public static int GetPid()
	{
		parameters.clear();

		currentCall = callType.getpid;

		switchtoKernel();

		return (int) returnValue;
	}

	/**
	 * Retrieves the process ID of a process by its name.
	 *
	 * @param name The name of the process.
	 * @return The process ID.
	 */
	public static int GetPidByName(String name)
	{
		parameters.clear();
		parameters.add(name);

		currentCall = callType.getpidbyname;

		switchtoKernel();

		return (int) returnValue;
	}

	/**
	 * Sends a kernel message to the specified receiver process.
	 * @param km The KernelMessage to be sent.
	 */
	public static void SendMessage(KernelMessage km)
	{
		parameters.clear();
		parameters.add(km);

		currentCall = callType.send_Message;
		returnValue = null;

		switchtoKernel();
	}

	/**
	 * Waits for a kernel message. If a message is available, returns it; otherwise, deschedules the process.
	 * @return The received KernelMessage.
	 */
	public static KernelMessage WaitForMessage()
	{
		parameters.clear();

		currentCall = callType.wait_For_Message;
		returnValue = null;

		switchtoKernel();

		return (KernelMessage) returnValue;
	}

	public static void getMapping(int VirtualPageNumber)
	{
		parameters.clear();
		parameters.add(VirtualPageNumber);

		currentCall = callType.get_Mapping;
		returnValue = null;

		switchtoKernel();
	}

	public static int allocateMemory(int size)
	{
		if(size % 1024 != 0)
		{
			System.out.println("ALLOCATE FAILURE");
			return -1;
		}

		parameters.clear();
		parameters.add(size);

		currentCall = callType.allocateMem;
		returnValue = null;

		switchtoKernel();

		while(returnValue == null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return (int) returnValue;
	}

	public static boolean freeMemory(int pointer, int size)
	{
		if(size % 1024 != 0 && pointer % 1024 != 0)
		{
			throw new RuntimeException("FREE FAILURE");
		}

		parameters.clear();
		parameters.add(pointer);
		parameters.add(size);

		currentCall = callType.freeMem;
		returnValue = null;

		switchtoKernel();

		while(returnValue == null)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return (boolean) returnValue;
	}

}