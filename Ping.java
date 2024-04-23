import java.util.Arrays;

public class Ping extends UserlandProcess
{

    int what;
    public Ping()
    {
        super("PING");
    }

    @Override
    public void main()
    {
            // Get the process ID of Pong
            int pongPid = OS.GetPidByName("Pong");

            System.out.println("I am PING, pong = " + pongPid);

            // Start sending messages to Pong
            while (true)
            {
                // Send a message to Pong with the current "what" value
                KernelMessage message = new KernelMessage(OS.GetPid(), pongPid, what, "null".getBytes());
                OS.SendMessage(new KernelMessage(message));

                System.out.println("PING: from: " + OS.GetPid() +
                                   " to: " + pongPid +
                                   " what: " + what);

                OS.Sleep(50);

                KernelMessage response = OS.WaitForMessage();

                if (response != null)
                {
                    System.out.println("PING: from: " + response.getSenderPid() +
                                       " to: " + response.getReceiverPid() +
                                       " what: " + response.getMessageType());

                    what = response.getMessageType();
                }
            }// Increment "what" for the next message
    }
}

