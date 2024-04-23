import java.util.Arrays;

public class FileProcess extends UserlandProcess {

    public FileProcess() {
        super("FILE SYSTEM");
    }

    @Override
    public void main()
    {

            int fileId = OS.open("file test.txt");
            if (fileId != -1)
            {
                System.out.println("File opened successfully with id: " + fileId);
            }
            else
            {
                System.out.println("Failed to open file.");
                return;
            }

            byte[] data = "This is a test file. ".getBytes();
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

            //System.out.println("File closed.");


//            cooperate();
//
//            try
//            {
//                Thread.sleep(50); // sleep for 50 ms
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }

    }
}
