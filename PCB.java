import java.util.Arrays;
import java.util.LinkedList;

/**
 * Represents a Process Control Block (PCB) for managing processes.
 */
public class PCB {
    private static int nextpid = 1;
    private int pid;
    private String name;
    private UserlandProcess userlandProcess;
    private OS.Priority priority;
    private long wakeUpTime;
    private int consecutiveStopCount;
    private int[] intArray;
    private boolean waitingForMessage;
    private LinkedList<KernelMessage> messageQueue;
    public VirtualtoPhysicalMapping[] pageMapping;



    /**
     * Constructs a PCB for a userland process with the given priority.
     *
     * @param up       The userland process.
     * @param priority The priority of the process.
     */
    public PCB(UserlandProcess up, OS.Priority priority)
    {
        this.intArray = new int[10];
        Arrays.fill(intArray, -1);
        this.priority = priority;
        this.userlandProcess = up;
        this.pid = nextpid++;
        this.consecutiveStopCount = 0;
        this.waitingForMessage = false;
        this.messageQueue = new LinkedList<>();

        pageMapping = new VirtualtoPhysicalMapping[100];

        for (int i = 0; i < 99; i++)
        {
           pageMapping[i] = new VirtualtoPhysicalMapping();
        }
    }

    /**
     * Updates the Translation Lookaside Buffer (TLB) with the mapping of a virtual page to a physical page.
     *
     * @param virtualPage the virtual page number to be mapped
     * @param physicalPage the physical page number corresponding to the virtual page
     * @param randomVpage a boolean indicating whether the virtual page was selected randomly
     */
    public void updateTLB(int virtualPage, int physicalPage, boolean randomVpage)
    {
        UserlandProcess.updateTLB(virtualPage, physicalPage, randomVpage);
    }


    /**
     * Allocates a virtual page to a physical page.
     * @param virtualPage The virtual page number.
     * @param mapping The VirtualtoPhysicalMapping object representing the mapping.
     */
    public void addMapping(int virtualPage, VirtualtoPhysicalMapping mapping)
    {
        pageMapping[virtualPage] = mapping;
        pageMapping[virtualPage].physicalPageNumber = virtualPage;
    }

    /**
     * Frees the mapping for the given virtual page.
     * @param virtualPage The virtual page number.
     */
    public void removeMapping(int virtualPage)
    {
        pageMapping[virtualPage] = null;
    }

    public VirtualtoPhysicalMapping getPageMapping(int virtualPage)
    {
        return pageMapping[virtualPage];
    }

    /**
     * Stops the process and handles priority demotion if necessary.
     */
    public void stop() {
        userlandProcess.stop();

        while (!userlandProcess.isStopped())
        {
            try
            {
                Thread.sleep(50); // Sleep for a short while
            }

            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        consecutiveStopCount++;

        // Check if the consecutive stop count reaches 5
        if (consecutiveStopCount == 5)
        {
            switch (this.priority)
            {
                case INTERACTIVE:
                    this.priority = OS.Priority.BACKGROUND;
                    Kernel kernel = new Kernel();
                    //System.out.println("|||| PROCESS DEMOTED INTERACTIVE TO BACKGROUND ||||");

                    break;

                case REALTIME:
                    this.priority = OS.Priority.INTERACTIVE;
                   // System.out.println("|||| PROCESS DEMOTED REALTIME TO INTERACTIVE ||||");

                    break;

                case BACKGROUND:

                    break;

            }
            consecutiveStopCount = 0; // Reset count after decreasing priority
        }
    }

    /**
     * Gets the physical page number mapped to the given virtual page.
     *
     * @param virtualPage The virtual page number.
     * @return The physical page number.
     */
    public int getPhysicalPageNumber(int virtualPage)
    {
        return pageMapping[virtualPage].physicalPageNumber;
    }

    /**
     * Gets the on-disk page number mapped to the given virtual page.
     *
     * @param virtualPage The virtual page number.
     * @return The on-disk page number.
     */
    public int getOnDiskPageNumber(int virtualPage)
    {
        return pageMapping[virtualPage].onDiskPageNumber;
    }

    /**
     * Getter method for the integer array
     */
    public int[] getIntArray() {
        return intArray;
    }

    /**
     * Requests the process to stop.
     */
    public void requestStop() {
        userlandProcess.requestStop();
    }

    /**
     * Checks if the process is done.
     *
     * @return true if the process is done, false otherwise.
     */
    public boolean isDone() {
        return userlandProcess.isDone();
    }

    /**
     * Runs the process.
     */
    public void run() {
        userlandProcess.start();
    }

    /**
     * Gets the wake-up time of the process.
     *
     * @return The wake-up time.
     */
    public long getWakeUpTime() {
        return wakeUpTime;
    }

    /**
     * Sets the wake-up time of the process.
     *
     * @param wakeUpTime The wake-up time to set.
     */
    public void setWakeUpTime(long wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    /**
     * Gets the priority of the process.
     *
     * @return The priority.
     */
    public OS.Priority getPriority() {
        return this.priority;
    }

    /**
     * Gets the process ID.
     *
     * @return The process ID.
     */
    public int getPid()
    {
        // TODO Auto-generated method stub
        return this.pid;
    }

    /**
     * Returns the simple name of the class of the userland process.
     *
     * @return The simple name of the userland process class.
     */
    public String getClassName()
    {
        return userlandProcess.getClass().getSimpleName();
    }

    /**
     * Sets the flag indicating whether the process is waiting for a message.
     *
     * @param waitingForMessage true if the process is waiting for a message, false otherwise.
     */
    public void setWaitingForMessage(boolean waitingForMessage)
    {
        this.waitingForMessage = waitingForMessage;
    }

    /**
     * Checks if the process is waiting for a message.
     *
     * @return true if the process is waiting for a message, false otherwise.
     */
    public boolean isWaitingForMessage()
    {
        return waitingForMessage;
    }

    /**
     * Adds a message to the message queue.
     *
     * @param message The message to be added.
     */
    public void addMessage(KernelMessage message)
    {
        messageQueue.offer(message);
    }

    /**
     * Retrieves and removes the next message from the message queue.
     *
     * @return The next message in the queue, or null if the queue is empty.
     */
    public KernelMessage getNextMessage()
    {
        return messageQueue.poll();
    }
}