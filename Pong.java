public class Pong extends UserlandProcess {
    private int responseWhat;

    public Pong() {
        super("PONG");
    }

    @Override
    public void main()
    {

            while (true)
            {
                KernelMessage message = OS.WaitForMessage();

                if (message != null)
                {
                    // Store the received "what" value
                    responseWhat = message.getMessageType();
                    System.out.println("PONG: from: " + message.getSenderPid() +
                            " to: " + message.getReceiverPid() +
                            " what: " + responseWhat);

                    // Respond to Ping with an incremented "what" value
                    int nextWhat = responseWhat + 1;
                    KernelMessage response = new KernelMessage(OS.GetPid(), message.getSenderPid(), nextWhat, "null".getBytes());
                    OS.SendMessage(new KernelMessage(response));

                    // Sleep for a while before receiving the next messaged
                    OS.Sleep(50);
                }
            }
    }
}
