/**
 * Represents a userland process that continuously prints "Hello World" and cooperates with other processes.
 */
public class GreetingsWorld extends UserlandProcess
{
	/**
	 * Constructs a GreetingsWorld process.
	 */
	public GreetingsWorld()
	{
		super("GreetingsWorld");
		// TODO Auto-generated constructor stub
	}

	/**
	 * The main method of the GreetingsWorld process.
	 * It continuously prints "Hello World" and cooperates with other processes.
	 */
	@Override
	public void main() 
	{
		while (true)
		{

			System.out.println("Greetings World");
			System.out.flush();
			cooperate();

			try
			{
				Thread.sleep(50); // sleep for 50 ms
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			OS.Sleep(500000000);
		}
		
	}

}
