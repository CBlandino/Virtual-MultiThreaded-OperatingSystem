import java.util.Random;

public class MemoryReadWriteTest extends UserlandProcess {
    private static final int MEMORY_SIZE = 100 * 1024; // 100 KB
    private static final int ITERATIONS = 20; // Number of iterations to exceed memory limit

    public MemoryReadWriteTest() {
        super("MemoryReadWriteTest");
    }

    @Override
    public void main() {
        OS.allocateMemory(MEMORY_SIZE);

        // Test reading and writing within the allocated memory
        testReadWrite();

        // Test exceeding the allocated memory
        testMemoryExceed();

        // Free memory
       // OS.freeMemory(0, MEMORY_SIZE);
        System.out.println("Memory freed.");

        // Sleep the process
        int PROCESS_SLEEPER = 3028 * 3028;
        OS.Sleep(PROCESS_SLEEPER);
    }

    private void testReadWrite() {
        byte[] value = new byte[1024];
        new Random().nextBytes(value);

        // Write a byte value to memory
        UserlandProcess.write(1024, value[0]);

        // Read the byte value from memory
        byte readValue = UserlandProcess.read(1024);

        // Check if the read value matches the written value
        if (readValue == value[0]) {
            System.out.println("Memory read/write test within allocated memory successful.");
        } else {
            System.out.println("Memory read/write test within allocated memory failed. Read value: " + readValue +
                    ", Expected value: " + value[0]);
        }
    }

    private void testMemoryExceed() {
        byte[] value = new byte[1024];
        new Random().nextBytes(value);

        // Try writing beyond the allocated memory
        try {
            for (int i = 1; i <= ITERATIONS; i++) {
                UserlandProcess.write(MEMORY_SIZE + i, value[0]);
            }
            System.out.println("Memory write test beyond allocated memory successful.");
        } catch (Exception e) {
            System.out.println("Memory write test beyond allocated memory failed: " + e.getMessage());
        }

        // Try reading beyond the allocated memory
        try {
            for (int i = 1; i <= ITERATIONS; i++) {
                byte readValue = UserlandProcess.read(MEMORY_SIZE + i);
            }
            System.out.println("Memory read test beyond allocated memory successful.");
        } catch (Exception e) {
            System.out.println("Memory read test beyond allocated memory failed: " + e.getMessage());
        }
    }
}
