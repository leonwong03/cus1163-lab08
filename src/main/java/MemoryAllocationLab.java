import java.io.*;
import java.util.*;

public class MemoryAllocationLab {

    static class MemoryBlock {

        int start;
        int size;
        String processName;  // null if free

        public MemoryBlock(int start, int size, String processName) {

            this.start = start;
            this.size = size;
            this.processName = processName;

        }

        public boolean isFree() {

            return processName == null;

        }

        public int getEnd() {

            return start + size - 1;

        }

    }

    static int totalMemory;
    static ArrayList<MemoryBlock> memory;
    static int successfulAllocations = 0;
    static int failedAllocations = 0;

    public static void processRequests(String filename) {

        memory = new ArrayList<>();

        successfulAllocations = 0;
        failedAllocations = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line = br.readLine();

            if (line == null || line.trim().isEmpty()) {

                System.out.println("Error: input file is empty.");
                return;

            }

            // first line = total memory size
            totalMemory = Integer.parseInt(line.trim());

            // start with one large free block
            memory.add(new MemoryBlock(0, totalMemory, null));

            // read remaining lines
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // skip blank lines
                if (line.isEmpty()) {

                    continue;

                }

                String[] parts = line.split("\\s+");

                if (parts[0].equalsIgnoreCase("REQUEST")) {

                    String processName = parts[1];
                    int size = Integer.parseInt(parts[2]);
                    allocate(processName, size);

                } else if (parts[0].equalsIgnoreCase("RELEASE")) {

                    String processName = parts[1];
                    deallocate(processName);

                } else {

                    System.out.println("Unknown command: " + line);

                }

            }

        } catch (FileNotFoundException e) {

            System.out.println("Error: file not found -> " + filename);

        } catch (IOException e) {

            System.out.println("Error reading file: " + e.getMessage());

        } catch (NumberFormatException e) {

            System.out.println("Error: invalid number format in input file.");

        }

    }

    private static void allocate(String processName, int size) {

        for (int i = 0; i < memory.size(); i++) {

                MemoryBlock block = memory.get(i);

                if (block.isFree() && block.size >= size) {

                    // exact fit
                    if (block.size == size) {

                        block.processName = processName;

                    } else {

                        // split the block
                        MemoryBlock remaining =
                                new MemoryBlock(block.start + size, block.size - size, null);

                        block.size = size;
                        block.processName = processName;

                        memory.add(i + 1, remaining);

                    }

                    successfulAllocations++;
                    System.out.println("Allocated " + size + " KB to " + processName);
                    return;

                }

            }

            failedAllocations++;
            System.out.println("Allocation failed for " + processName + " (" + size + " KB)");

        }

    private static void deallocate(String processName) {

        for (int i = 0; i < memory.size(); i++) {

            MemoryBlock block = memory.get(i);

            if (!block.isFree() && block.processName.equals(processName)) {

                block.processName = null;
                System.out.println("Released memory for " + processName);

                mergeFreeBlocks();
                return;
            }

        }

        System.out.println("Release failed: process " + processName + " not found");
    }

    private static void mergeFreeBlocks() {

        int i = 0;

        while (i < memory.size() - 1) {

            MemoryBlock current = memory.get(i);
            MemoryBlock next = memory.get(i + 1);

            if (current.isFree() && next.isFree()) {

                current.size += next.size;
                memory.remove(i + 1);

            } else {

                i++;

            }

        }

    }

    public static void displayStatistics() {

        System.out.println("\n========================================");
        System.out.println("Final Memory State");
        System.out.println("========================================");

        int blockNum = 1;

        for (MemoryBlock block : memory) {

            String status = block.isFree() ? "FREE" : block.processName;
            String allocated = block.isFree() ? "" : " - ALLOCATED";
            System.out.printf("Block %d: [%d-%d]%s%s (%d KB)%s\n",
                    blockNum++,
                    block.start,
                    block.getEnd(),
                    " ".repeat(Math.max(1, 10 - String.valueOf(block.getEnd()).length())),
                    status,
                    block.size,
                    allocated);
                    
        }

        System.out.println("\n========================================");
        System.out.println("Memory Statistics");
        System.out.println("========================================");

        int allocatedMem = 0;
        int freeMem = 0;
        int numProcesses = 0;
        int numFreeBlocks = 0;
        int largestFree = 0;

        for (MemoryBlock block : memory) {

            if (block.isFree()) {

                freeMem += block.size;
                numFreeBlocks++;
                largestFree = Math.max(largestFree, block.size);

            } else {

                allocatedMem += block.size;
                numProcesses++;

            }

        }

        double allocatedPercent = (allocatedMem * 100.0) / totalMemory;
        double freePercent = (freeMem * 100.0) / totalMemory;
        double fragmentation = freeMem > 0 ?
                ((freeMem - largestFree) * 100.0) / freeMem : 0;

        System.out.printf("Total Memory:           %d KB\n", totalMemory);
        System.out.printf("Allocated Memory:       %d KB (%.2f%%)\n", allocatedMem, allocatedPercent);
        System.out.printf("Free Memory:            %d KB (%.2f%%)\n", freeMem, freePercent);
        System.out.printf("Number of Processes:    %d\n", numProcesses);
        System.out.printf("Number of Free Blocks:  %d\n", numFreeBlocks);
        System.out.printf("Largest Free Block:     %d KB\n", largestFree);
        System.out.printf("External Fragmentation: %.2f%%\n", fragmentation);

        System.out.println("\nSuccessful Allocations: " + successfulAllocations);
        System.out.println("Failed Allocations:     " + failedAllocations);
        System.out.println("========================================");
    }

    /**
     * Main method (FULLY PROVIDED)
     */
    public static void main(String[] args) {

        if (args.length < 1) {

            System.out.println("Usage: java MemoryAllocationLab <input_file>");
            System.out.println("Example: java MemoryAllocationLab memory_requests.txt");
            return;

        }

        System.out.println("========================================");
        System.out.println("Memory Allocation Simulator (First-Fit)");
        System.out.println("========================================\n");
        System.out.println("Reading from: " + args[0]);

        processRequests(args[0]);
        displayStatistics();

    }

}