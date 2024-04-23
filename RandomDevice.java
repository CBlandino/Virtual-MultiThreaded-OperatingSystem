import java.util.Arrays;
import java.util.Random;
/**
 * A class representing a random device.
 */
public class RandomDevice implements Device
{
    private Random[] randomArray;
    RandomDevice()
    {
        randomArray = new Random[10];
    }

    /**
     * Opens a random device with an optional seed value.
     * @param s The seed value for the random device.
     * @return The ID of the opened random device.
     */
    @Override
    public int open(String s)
    {

        for (int i = 0; i < randomArray.length; i++)
        {
            if (randomArray[i] == null)
            {
                if (s != null && !s.isEmpty())
                {
                    int seedValue = Integer.parseInt(s);
                    randomArray[i] = new Random(seedValue);
                }
                else
                {
                    randomArray[i] = new Random();
                }
                System.out.println("Opened Random device with RDid: " + i);
                return i;
            }
        }
        System.out.println("No empty spot available to open Random device.");
        return -1;
    }

    /**
     * Closes the random device associated with the given ID.
     * @param RDid The ID of the random device to close.
     */
    @Override
    public void close(int RDid)
    {
        if (RDid >= 0 && RDid < randomArray.length)
        {
            randomArray[RDid] = null;
            System.out.println("Closed Random device with RDid: " + RDid);
        }
        else
        {
            System.out.println("Invalid device RDid.");
        }
    }

    /**
     * Reads random bytes from the random device associated with the given ID.
     * @param RDid The ID of the random device to read from.
     * @param size The number of bytes to read.
     * @return A byte array containing the read data.
     */
    @Override
    public byte[] read(int RDid, int size)
    {
        if (RDid >= 0 && RDid < randomArray.length && randomArray[RDid] != null)
        {
            byte[] randomBytes = new byte[size];
            randomArray[RDid].nextBytes(randomBytes);

            System.out.println("Read random bytes from Random device with RDid: " + RDid);

            return randomBytes;
        }
        else
        {
            System.out.println("Invalid device RDid or device not opened.");
            return null;
        }
    }

    /**
     * Moves the file pointer of the random device associated with the given ID.
     * @param RDid The ID of the random device.
     * @param to The position to seek to.
     */
    @Override
    public void seek(int RDid, int to)
    {
        if (RDid >= 0 && RDid < randomArray.length && randomArray[RDid] != null)
        {
            byte[] randomBytes = new byte[to];
            randomArray[RDid].nextBytes(randomBytes);
            System.out.println("Seek operation performed on Random device with RDid: " + RDid);
        }
        else
        {
            System.out.println("Invalid device RDid or device not opened.");
        }
    }

    /**
     * Writes data to the random device associated with the given ID.
     * @param RDid The ID of the random device.
     * @param data The byte array containing the data to be written.
     * @return The number of bytes written.
     */
    @Override
    public int write(int RDid, byte[] data)
    {
        // Since it doesn't make sense to write to a random device, return 0 length.
        System.out.println("Writing to a Random device is not supported.");
        return -1;
    }

//    public static void main(String[] args)
//    {
//        RandomDevice randomDevice = new RandomDevice();
//        int id = randomDevice.open("12345");
//        byte[] data = randomDevice.read(id, 20);
//        randomDevice.seek(id, 10);
//        randomDevice.close(id);
//    }
}
