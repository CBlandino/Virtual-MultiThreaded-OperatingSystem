/**
 * Represents an idle process.
 * This process cooperates with other processes and sleeps for a short duration.
 */
public class IdleProcess extends UserlandProcess
{

	/**
	 * Constructs an IdleProcess object with the name "idle".
	 */
	public IdleProcess() 
	{
		super("idle");
		// TODO Auto-generated constructor stub
	}

	/**
	 * The main method of the idle process.
	 * It continuously cooperates with other processes and sleeps for a short duration.
	 */
	@Override
	public void main() 
	{
		while (true) 
		{
			cooperate();

			try 
			{
				Thread.sleep(50); // sleep for 50 ms
			}
			catch (InterruptedException e) 
			{
	                e.printStackTrace();
			}
		}
		
	}

}
