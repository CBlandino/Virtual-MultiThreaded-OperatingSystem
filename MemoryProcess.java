import java.util.Random;
public class MemoryProcess extends UserlandProcess{
    public MemoryProcess()
    {
        super("MemoryProcess");
    }

    @Override
    public void main()
    {
        OS.allocateMemory(3072);

        byte[] value = new byte[1024];
        new Random().nextBytes(value);

        // Write a byte value to memory
        UserlandProcess.write(2048, value[27]);

        // Read a byte value from memory
        byte readValue = UserlandProcess.read(1024);

        // Free memory
        OS.freeMemory(1024, 3072);
        System.out.println("COMPLETE MEMORY PROCESS.../n" +
                "CHEACKING DISK" + Scheduler.diskPagesMap
                +"/nPROCESS SLEEPING");

        // Sleep the process
        int PROCESS_SLEEPER = 3028 * 3028;
        OS.Sleep(PROCESS_SLEEPER);
    }
}
