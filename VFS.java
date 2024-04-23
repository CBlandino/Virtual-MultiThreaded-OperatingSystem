import java.util.Arrays;

/**
 * A class representing the Virtual File System (VFS) managing multiple devices.
 */
public class VFS implements Device
{

    private Device[] devices;
    private int[] ids;

    /**
     * Constructs a VFS object with the specified size.
     * @param size The size of the VFS.
     */
    VFS(int size)
    {
        devices = new Device[size];
        ids = new int[size];
    }

    /**
     * Maps a device to the specified ID in the VFS.
     * @param id The ID to map the device to.
     * @param device The device to be mapped.
     * @param deviceID The ID of the device.
     * @throws RuntimeException if the ID is invalid.
     */
    public void mapDevice(int id, Device device, int DeviceID)
    {
        if(id < 0 || id >= devices.length)
        {
            throw new RuntimeException("INVALID VFS ID --- 'mapdevice()");
        }

        else
        {
            devices[id] = device;
            ids[id] = DeviceID;
        }

    }

    /**
     * Retrieves the device associated with the specified ID.
     * @param id The ID of the device to retrieve.
     * @return The device associated with the ID.
     * @throws RuntimeException if the ID is invalid.
     */
    public Device getDevice(int id)
    {
        if(id < 0 || id >= devices.length)
        {
            throw new RuntimeException("INVALID VFS ID --- 'getdevice()'");
        }

        return devices[id];
    }

    /**
     * Retrieves the ID of the device associated with the specified ID.
     * @param id The ID of the device to retrieve.
     * @return The ID of the device.
     * @throws RuntimeException if the ID is invalid.
     */
    public int getDeviceID(int id)
    {
        if(id < 0 || id >= devices.length)
        {
            throw new RuntimeException("INVALID VFS ID --- 'getdeviceid()'");
        }

        return ids[id];
    }

    /**
     * Opens a device based on the given seed, such as "random" or "file".
     * @param seed The seed specifying the type of device to open and any associated arguments.
     * @return The ID of the opened device, or -1 if the device could not be opened.
     */
    @Override
    public int open(String seed)
    {
        String[] parts = seed.split(" ", 2); // Split input string into device name and arguments
        String deviceName = parts[0];
        String arguments = parts.length > 1 ? parts[1] : ""; // Get arguments if available

        Device device;
        int id = -1;

        switch (deviceName)
        {
            case "random":
                device = new RandomDevice();
                System.out.println("RandDevice OPENED");
                break;
            case "file":
                device = new FakeFileSystem();
                System.out.println("FakeFile OPENED");
                break;
            // Add more cases for other device types if needed
            default:
                System.out.println("Error: Device not found.");
                return -1;
        }

        for (int i = 0; i < devices.length; i++)
        {
            if (devices[i] == null)
            {
                id = device.open(arguments);
                //System.out.println("|||"+ id);
                if (id != -1)
                {
                    devices[i] = device;
                    ids[i] = id;
                    //System.out.println("|||"+ id);
                    break;
                }
            }
        }

        if (id == -1)
        {
            System.out.println("Error: No available slots to open device.");
        }

        return id;
    }

    /**
     * Closes the device associated with the specified ID.
     * @param id The ID of the device to close.
     */
    @Override
    public void close(int id)
    {

        if (id >= 0 && id < devices.length && devices[id] != null)
        {
            System.out.println(Arrays.toString(devices));

            devices[id].close(ids[id]);
            devices[id] = null;
            ids[id] = -1;

            //System.out.println("||||||||||INSIDE IF||||||||||||||||||||| " + id);
           // System.out.println("VFS.CLOSE(): " + Arrays.toString(devices));
        }
        else
        {

            System.out.println("|||||||||||||||" + id);
            System.out.println("Error: Invalid device id or device not opened.");
        }
    }

    /**
     * Reads data from the device associated with the specified ID.
     * @param id The ID of the device to read from.
     * @param size The number of bytes to read.
     * @return A byte array containing the read data.
     */
    @Override
    public byte[] read(int id, int size)
    {
        if (id >= 0 && id < devices.length && devices[id] != null)
        {
            return devices[id].read(ids[id], size);
        }
        else
        {
            System.out.println("Error: Invalid device id or device not opened.");
            return new byte[-1];
        }
    }

    /**
     * Writes data to the device associated with the specified ID.
     * @param id The ID of the device to write to.
     * @param data The byte array containing the data to be written.
     * @return The number of bytes written.
     */
    @Override
    public int write(int id, byte[] data)
    {
        if (id >= 0 && id < devices.length && devices[id] != null)
        {
            //int byteswritten = devices[id].write(ids[id], data);
            //System.out.println("VFS BYTES WRITTEN " + byteswritten);
            //return byteswritten;
            return devices[id].write(ids[id], data);
        }
        else
        {
            System.out.println("Error: Invalid device id or device not opened.");
            return -1;
        }
    }

    /**
     * Moves the file pointer associated with the specified ID to the specified position.
     * @param id The ID of the device.
     * @param to The position to seek to.
     */
    @Override
    public void seek(int id, int to)
    {
        if (id >= 0 && id < devices.length && devices[id] != null)
        {
            devices[id].seek(ids[id], to);
        }
        else
        {
            System.out.println("Error: Invalid device id or device not opened.");
        }
    }
}
