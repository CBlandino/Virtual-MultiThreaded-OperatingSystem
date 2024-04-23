import java.util.Arrays;

/**
 * Represents a message exchanged within the kernel.
 */
public class KernelMessage
{
    private int senderPid;
    private int receiverPid;
    private int messageType;
    private byte[] data;

    /**
     * Constructs a new KernelMessage with the specified attributes.
     *
     * @param senderPid    The process ID of the sender.
     * @param receiverPid  The process ID of the receiver.
     * @param messageType  The type of message.
     * @param data         The data associated with the message.
     */
    public KernelMessage(int senderPid, int receiverPid, int messageType, byte[] data)
    {
        this.senderPid = senderPid;
        this.receiverPid = receiverPid;
        this.messageType = messageType;
        this.data = data;
    }

    /**
     * Copy constructor to create a new KernelMessage from an existing one.
     *
     * @param other The KernelMessage to copy.
     */
    public KernelMessage(KernelMessage other)
    {
        this.senderPid = other.senderPid;
        this.receiverPid = other.receiverPid;
        this.messageType = other.messageType;
        this.data = other.data.clone(); // Clone the byte array to avoid sharing the reference
    }

    /**
     * Gets the process ID of the sender.
     *
     * @return The sender's process ID.
     */
    public int getSenderPid()
    {
        return senderPid;
    }

    /**
     * Gets the process ID of the receiver.
     *
     * @return The receiver's process ID.
     */
    public int getReceiverPid()
    {
        return receiverPid;
    }

    /**
     * Gets the type of message.
     *
     * @return The message type.
     */
    public int getMessageType()
    {
        return messageType;
    }

    /**
     * Gets the data associated with the message.
     *
     * @return The message data.
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Returns a string representation of the KernelMessage.
     *
     * @return A string containing sender PID, receiver PID, message type, and data.
     */
    @Override
    public String toString()
    {
        return String.format("KernelMessage [SenderPID: %d, ReceiverPID: %d, MessageType: %d, Data: %s]",
                senderPid, receiverPid, messageType, Arrays.toString(data));
    }
}
