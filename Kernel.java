/**
 * Represents the kernel of an operating system.
 * The kernel manages the execution of processes and scheduling.
 */
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class Kernel implements Runnable, Device
{

	private Thread thread;
    private Semaphore semaphore;
    private Scheduler scheduler;
    private VFS vfs;
    private int[] pcbArray;
    private FakeFileSystem fileSystem;
    public boolean[] pagesUsed;



    /**
     * Constructs a Kernel object.
     * Initializes the semaphore and scheduler, and starts the thread.
     */
    public Kernel()
    {
    	this.semaphore = new Semaphore(0);
    	//this.scheduler = new Scheduler(this, pagesUsed);
    	this.thread = new Thread(this);
        this.vfs = new VFS(10);
        this.pagesUsed = new boolean[1024];
        Arrays.fill(pagesUsed, false);

        this.scheduler = new Scheduler(this, pagesUsed);
        thread.start();

        this.fileSystem = new FakeFileSystem();

        // Open the swap file
        int swapFileId = fileSystem.open("swapfile.txt");
        if (swapFileId == -1) {
            throw new RuntimeException("Failed to open swap file.");
        }
    }

    /**
     * Starts the kernel execution.
     * Releases the semaphore to begin execution.
     */
    public void start()
    {
    	semaphore.release();
    }

    /**
     * The main execution loop of the kernel.
     * Waits for semaphore release and performs operations based on OS calls.
     */
    public void run()
    {
        while (true) 
        {
            try 
            {
                semaphore.acquire();
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            switch (OS.currentCall)
            {
                case create_Process:
                    try
                    {
                        if (OS.parameters.get(0) instanceof UserlandProcess up && OS.parameters.get(1) instanceof OS.Priority priority)
                        {
                            OS.returnValue = scheduler.createProcess(up, priority);
                            PCB currentlyRunningProcess = getScheduler().getCurrentlyRunning();

                            System.out.println("|||| KERNEL Creating Process " + currentlyRunningProcess.getClassName() + " With pID: " + GetPidByName(currentlyRunningProcess.getClassName()) + " ||||");
                        }

                        else
                        {
                            throw new IllegalArgumentException("Expected UserlandProcess and OS.Priority as parameters.");
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case switch_Process:
                    try
                    {
                        scheduler.switchProcess();
                        PCB currentlyRunningProcess = getScheduler().getCurrentlyRunning();

                        //System.out.println("|||| KERNEL Switching Process " + currentlyRunningProcess.getClassName() + " With pID: " + GetPidByName(currentlyRunningProcess.getClassName()) + " ||||");
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;

                case sleep:

                    int milliseconds = (int) OS.parameters.get(0);

                    try
                    {
                        scheduler.sleepCurrentProcess(milliseconds);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }

                    break;

                case open:

                    if (OS.parameters.get(0) instanceof String seed)
                    {
                        OS.returnValue = open(seed);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected string as parameters in kernel.run()");
                    }

                    break;

                case close:

                    if (OS.parameters.get(0) instanceof Integer)
                    {
                        int id = (Integer) OS.parameters.get(0);

                        close(id);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected int as parameters in kernel.run(close)");
                    }

                    break;

                case read:

                    if (OS.parameters.get(0) instanceof Integer && OS.parameters.get(1) instanceof Integer)
                    {
                        int readId = (Integer) OS.parameters.get(0);
                        int size = (Integer) OS.parameters.get(1);

                        OS.returnValue = read(readId, size);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected int and int as parameters in kernel.run(read).");
                    }

                    break;

                case write:

                    if (OS.parameters.get(0) instanceof Integer && OS.parameters.get(1) instanceof byte[])
                    {
                        int writeId = (Integer) OS.parameters.get(0);
                        byte[] data = (byte[]) OS.parameters.get(1);

                        OS.returnValue = write(writeId, data);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected int and byte[] as parameters in kernel.run(write).");
                    }

                    break;

                case seek:

                    if (OS.parameters.get(0) instanceof Integer && OS.parameters.get(1) instanceof Integer)
                    {
                        int seekId = (Integer) OS.parameters.get(0);
                        int to = (Integer) OS.parameters.get(1);

                        seek(seekId, to);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected int and int as parameters in kernel.run(read).");
                    }

                    break;

                case getpidbyname:
                    if (OS.parameters.get(0) instanceof String)
                    {
                        OS.returnValue = GetPidByName((String) OS.parameters.get(0));
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected String as paramaters (getpidbyname).");
                    }

                    break;

                case getpid:

                    OS.returnValue = GetPid();

                    break;

                case send_Message:

                    if (OS.parameters.get(0) instanceof KernelMessage km)
                    {
                        sendMessage(km);
                    }

                    else
                    {
                        throw new IllegalArgumentException("Expected KernelMessage as parameters in kernel.run(send_Message).");
                    }
                    break;

                case get_Mapping:

                    if (OS.parameters.get(0) instanceof Integer)
                    {
                        int Vpage = (Integer) OS.parameters.get(0); //stored into variable for debugging

                        getMapping(Vpage);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Expected Integer as parameters in kernel.run(get_Mapping).");
                    }
                    break;


                case wait_For_Message:

                    try
                    {
                        OS.returnValue = waitForMessage();
                    }

                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }

                    break;

                case freeMem:

                    if (OS.parameters.get(0) instanceof Integer && OS.parameters.get(0) instanceof Integer)
                    {
                        int pointer = (Integer) OS.parameters.get(0); //stored into variable for debugging
                        int size = (Integer) OS.parameters.get(1); //stored into variable for debugging

                        OS.returnValue = freeMemory(pointer,size);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Expected Integer as parameters in kernel.run(freeMem).");
                    }

                    break;

                case allocateMem:

                    if (OS.parameters.get(0) instanceof Integer)
                    {
                        int size = (Integer) OS.parameters.get(0); //stored into variable for debugging

                        OS.returnValue = allocateMemory(size);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Expected Integer as parameters in kernel.run(allocateMem).");
                    }

                    break;
            }
                scheduler.getCurrentlyRunning().run();
        }
    }

    /**
     * Retrieves the scheduler associated with this kernel.
     * @return The scheduler object.
     */
	 public Scheduler getScheduler()
	 {
    	return scheduler;
    }

    /**
     * Opens a file with the specified seed and associates it with the currently running process.
     * @param seed The seed used for opening the file.
     * @return The index of the file in the PCB array.
     * @throws RuntimeException if no currently running process or if no empty entry is found in the PCB array.
     */
    @Override
    public int open(String seed)
    {
        // Get the currently running process
        PCB currentlyRunning = scheduler.getCurrentlyRunning();

        if (currentlyRunning == null)
        {
            throw new RuntimeException("Kernel.Open() no currentlyRunning process");
        }

        // Find an empty (-1) entry in the integer array of PCB
        int emptyIndex = -1;

        pcbArray = currentlyRunning.getIntArray();

        for (int i = 0; i < pcbArray.length; i++)
        {

            if (pcbArray[i] == -1)
            {
                emptyIndex = i;
                break;
            }
        }

        if (emptyIndex == -1)
        {
            throw new RuntimeException("Kernel.Open() cannot find an empty entry into the array");
        }

        // Call vfs.open
        int vfsId = vfs.open(seed);

        if (vfsId == -1)
        {
            throw new RuntimeException("Kernel.Open() VFS cannot find an empty entry into the array");
        }

        // Put the vfs ID into the integer array of PCB and return the array index
        pcbArray[emptyIndex] = vfsId;
        return emptyIndex;
    }

    /**
     * Closes the file associated with the given ID.
     * @param id The ID of the file to be closed.
     */
    @Override
    public void close(int id)
    {
        PCB currentlyRunning = scheduler.getCurrentlyRunning();
        pcbArray = currentlyRunning.getIntArray();


        vfs.close(pcbArray[id]);
        pcbArray[id] = -1;

        System.out.println("KERNEL.CLOSE() PCBARRAY: " + Arrays.toString(pcbArray));

    }

    /**
     * Reads data from the file associated with the given ID.
     * @param id The ID of the file to read from.
     * @param size The number of bytes to read.
     * @return A byte array containing the read data.
     * @throws RuntimeException if the ID is invalid or no such entry exists.
     */
    @Override
    public byte[] read(int id, int size)
    {
        if(id < 0 || id >= pcbArray.length)
        {
            throw new RuntimeException("Kernel.read() -- invalid ID");
        }

        PCB currentlyRunning = scheduler.getCurrentlyRunning();

        pcbArray = currentlyRunning.getIntArray();


        int pcbAT = pcbArray[id];
        if (pcbAT == -1)
        {
            System.out.println("Kernel.read() -- no such id entry");
            return new byte[-1];
        }

        return vfs.read(pcbAT, size);
    }

    /**
     * Writes data to the file associated with the given ID.
     * @param id The ID of the file to write to.
     * @param data The byte array containing the data to be written.
     * @return The number of bytes written.
     * @throws RuntimeException if the ID is invalid, no such entry exists, or data is null.
     */
    @Override
    public int write(int id, byte[] data)
    {
        if(id < 0 || id >= pcbArray.length)
        {
            throw new RuntimeException("invalid ID");
        }

        PCB currentlyRunning = scheduler.getCurrentlyRunning();

        pcbArray = currentlyRunning.getIntArray();

        int pcbAT = pcbArray[id];
        if (pcbAT == -1)
        {
            System.out.println("Kernel.write() -- no such id entry");
            return -1;
        }

        if(data == null)
        {
            throw new RuntimeException("Kernel.write() no such data entry");
        }

        int vfsint = vfs.write(pcbAT, data);

        return vfsint;
    }

    /**
     * Moves the file pointer associated with the given ID to the specified position.
     * @param id The ID of the file.
     * @param to The position to seek to.
     * @throws RuntimeException if the ID is invalid, no such entry exists, or the position is negative.
     */
    @Override
    public void seek(int id, int to)
    {
        if(id < 0 || id >= pcbArray.length)
        {
            throw new RuntimeException("invalid ID");
        }

        PCB currentlyRunning = scheduler.getCurrentlyRunning();

        pcbArray = currentlyRunning.getIntArray();

        int pcbAT = pcbArray[id];
        if (pcbAT == -1)
        {
            System.out.println("Kernel.seek() -- no such id entry");
        }

        if(to < 0)
        {
            throw new RuntimeException("Kernel.seek() no such data entry");
        }

        vfs.seek(pcbAT, to);

    }

    /**
     * Gets the process ID of the current process.
     *
     * @return The process ID.
     */
    public int GetPid()
    {
        return scheduler.GetPid();
    }

    /**
     * Gets the process ID of the process with the given name.
     *
     * @param name The name of the process.
     * @return The process ID associated with the given name.
     */
    public int GetPidByName(String name)
    {
        return scheduler.GetPidByName(name);
    }

    /**
     * Sends a kernel message to the target process.
     *
     * @param km The KernelMessage to be sent.
     */
    private void sendMessage(KernelMessage km)
    {
        // Implement the logic to send the message to the target process in the scheduler
        scheduler.sendMessage(km);
    }

    /**
     * Waits for a kernel message. If a message is available, returns it; otherwise, deschedules the process.
     * @return The received KernelMessage.
     */
    private KernelMessage waitForMessage() throws Exception
    {
        // Implement the logic to wait for a message in the scheduler
        return scheduler.waitForMessage();
    }

    private void getMapping(int Vpage)
    {
        scheduler.getMapping(Vpage);
    }

    private boolean freeMemory(int pointer, int size)
    {
        return scheduler.freeMemory(pointer, size);
    }

    private int allocateMemory(int size)
    {
        return scheduler.allocateMemory(size);
    }

    public boolean[] getPagesUsed()
    {
        boolean[] retrievedPagesUsed = new boolean[pagesUsed.length];

        System.arraycopy(pagesUsed, 0, retrievedPagesUsed, 0, 1024);

        return retrievedPagesUsed;
    }

}