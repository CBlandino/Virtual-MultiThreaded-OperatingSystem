import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class representing a fake file system device.
 */
class FakeFileSystem implements Device
{
    private RandomAccessFile[] fileArray;
    // Track the next page number to write out to the swap file
    private RandomAccessFile swapFile;
    private int nextPageToWrite;


    /**
     * Constructs a FakeFileSystem object with an array of RandomAccessFiles.
     */
    public FakeFileSystem()
    {
        // Initialize next page to write out
        try
        {
            swapFile = new RandomAccessFile("swapfile.bin", "rw");
            nextPageToWrite = 0;
        }
        catch (IOException e)
        {
            System.err.println("Error creating swap file: " + e.getMessage());
        }

        fileArray = new RandomAccessFile[10];
    }

    public void writeToSwapFile(byte[] data)
    {
        try
        {
            swapFile.seek(nextPageToWrite * 1024);
            swapFile.write(data);
            nextPageToWrite++;
        }
        catch (IOException e)
        {
            System.err.println("Error writing to swap file: " + e.getMessage());
        }
    }

    public byte[] readFromSwapFile(int pageNumber)
    {
        try
        {
            byte[] buffer = new byte[1024];
            swapFile.seek(pageNumber * 1024);
            int bytesRead = swapFile.read(buffer);
            return bytesRead != -1 ? buffer : null;
        }
        catch (IOException e)
        {
            System.err.println("Error reading from swap file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Opens a file with the specified filename and returns its file system identifier (FFSid).
     * @param filename The name of the file to be opened.
     * @return The file system identifier (FFSid) if successful, -1 otherwise.
     */
    @Override
    public int open(String filename)
    {
        if (filename == null || filename.isEmpty())
        {
               System.out.println("Error: Filename cannot be null or empty.");
            return -1;
        }
        try
        {
            for (int i = 0; i < fileArray.length; i++)
            {
                if (fileArray[i] == null)
                {
                    fileArray[i] = new RandomAccessFile(filename, "rw");
                    System.out.println("Opened file " + filename + " as RandomAccessFile with FFSid: " + i);
                    return i;
                }
            }
            System.out.println("No empty spot available to open file.");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error: File not found - " + e.getMessage());
        }
        return -1;
    }

    /**
     * Closes the file associated with the given file system identifier (FFSid).
     * @param FFSid The file system identifier (FFSid) of the file to be closed.
     */
    @Override
    public void close(int FFSid)
    {
        if (FFSid >= 0 && FFSid < fileArray.length && fileArray[FFSid] != null)
        {
            try
            {
                fileArray[FFSid].close();
                fileArray[FFSid] = null;
                //FFSid = -1;
                System.out.println("Closed file with id: " + FFSid);
            }
            catch (IOException e)
            {
                System.out.println("Error while closing file: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("Invalid device FFSid or device not opened.");
        }
    }

    /**
     * Reads data from the file associated with the given file system identifier (FFSid).
     * @param FFSid The file system identifier (FFSid) of the file to be read from.
     * @param size The number of bytes to read.
     * @return A byte array containing the read data.
     */
    @Override
    public byte[] read(int FFSid, int size)
    {
        if (FFSid >= 0 && FFSid < fileArray.length && fileArray[FFSid] != null)
        {
            try
            {
                byte[] buffer = new byte[size];
                int bytesRead = fileArray[FFSid].read(buffer);
                if (bytesRead == -1)
                {
                    return new byte[0]; // End of file reached
                }
                else
                {
                    return buffer;
                }
            }
            catch (IOException e)
            {
                System.out.println("Error while reading file: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("Invalid device id or device not opened.");
        }
        return new byte[-1];
    }

    /**
     * Writes data to the file associated with the given file system identifier (FFSid).
     * @param FFSid The file system identifier (FFSid) of the file to write to.
     * @param data The byte array containing the data to be written.
     * @return The number of bytes written, or -1 if an error occurs.
     */
    @Override
    public int write(int FFSid, byte[] data)
    {
        if (FFSid >= 0 && FFSid < fileArray.length && fileArray[FFSid] != null)
        {
            try
            {
                fileArray[FFSid].write(data);
                return data.length; // Return the number of bytes written
            }
            catch (IOException e)
            {
                System.out.println("Error while writing to file: " + e.getMessage());
            }
        }
        else
            {
            System.out.println("Invalid device FFSid or device not opened.");
        }
        return -1;
    }

    /**
     * Moves the file pointer associated with the given file system identifier (FFSid) to the specified position.
     * @param FFSid The file system identifier (FFSid) of the file.
     * @param to The position to seek to.
     */
    @Override
    public void seek(int FFSid, int to)
    {
        if (FFSid >= 0 && FFSid < fileArray.length && fileArray[FFSid] != null)
        {
            try
            {
                System.out.println("Seeking to position: " + to);
                fileArray[FFSid].seek(to);
            }
            catch (IOException e)
            {
                System.out.println("Error while seeking file: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("Invalid device FFSid or device not opened.");
        }
    }
}
