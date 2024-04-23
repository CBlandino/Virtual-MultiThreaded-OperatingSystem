import java.util.Arrays;

/**
 * Represents a userland process that continuously prints "Goodbye World" and cooperates with other processes.
 */
public class GoodbyeWorld extends UserlandProcess
{

	/**
	 * Constructs a GoodbyeWorld process.
	 */
	public GoodbyeWorld()
	{
		super("Goodbye World");
		// TODO Auto-generated constructor stub
	}

	/**
	 * The main method of the GoodbyeWorld process.
	 * It continuously prints "Goodbye World" and cooperates with other processes.
	 */
	@Override
	public void main()
	{

		for(int i = 0; i < 0 ; i++)
		{
			int fileId = OS.open("file goodbyetest.txt");
			if (fileId != -1)
			{
				System.out.println("File opened successfully with id: " + fileId);
			}
			else
			{
				System.out.println("Failed to open file.");
				return;
			}

			byte[] data = "This is a goodbye test file. ".getBytes();
			int bytesWritten = OS.write(fileId, data);
			//System.out.println(bytesWritten);
			if (bytesWritten > 0)
			{
				System.out.println("Data written successfully to file.");
			}
			else
			{
				System.out.println("Failed to write data to file.");
			}

			OS.seek(fileId, 5);

			// Read from the file
			int bufferSize = 20;
			byte[] buffer = OS.read(fileId, bufferSize);
			//System.out.println("Data read from file: " + new String(buffer));
			if (buffer.length > 0)
			{
				System.out.println("Data read from file: " + Arrays.toString(buffer) + new String(buffer));
			}
			else
			{
				System.out.println("Failed to read data from file.");
			}

			//



			OS.close(fileId);

			System.out.println("File closed.");
		}


		while (true)
		{
			System.out.println("Goodbye World");
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
		}

	}

}
