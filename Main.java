/**
 * Main class to start the operating system and create initial processes.
 */
public class Main
{
    /**
     * Main method to start the operating system and create initial processes.
     */
	public static void main(String[] args)
    {
        OS.Startup(new MemoryProcess());
        OS.CreateProcess(new GoodbyeWorld(), OS.Priority.REALTIME);
        OS.CreateProcess(new MemoryReadWriteTest(), OS.Priority.BACKGROUND);
        OS.CreateProcess(new Ping(), OS.Priority.INTERACTIVE);
        OS.CreateProcess(new Pong(), OS.Priority.INTERACTIVE);
        OS.CreateProcess(new HelloWorld(), OS.Priority.INTERACTIVE);
        OS.CreateProcess(new GreetingsWorld());

    }
}
