## Analysis Questions

After completing the lab, answer these questions:

1. **Fragmentation Analysis:** In the basic example, what is the external fragmentation percentage? Explain why free memory becomes fragmented.

In the basic example, the external fragmentation percentage comes from how much of the free memory is split into separate chunks instead of one big block. Free memory becomes fragmented because processes get released in different places, which leaves empty gaps between allocated blocks. Over time, those gaps make it harder to fit larger requests even if the total free memory is technically enough.

2. **First-Fit Efficiency:** Why does P4 use the block left by P1 even though it's larger than needed? What happens to the unused portion?

P4 uses the block left by P1 because the First-Fit algorithm always picks the first available free block that can hold the request. Even though that block is larger than needed, it still gets used because it appears first in memory order. The extra unused space is split off into a smaller free block that can be reused later.

3. **Allocation Patterns:** Compare workload1.txt (many small processes) with workload2.txt (few large processes). Which creates more fragmentation? Why?

workload1.txt creates more fragmentation because it has a lot of smaller processes constantly being added and removed. That creates more random empty spaces throughout memory. workload2.txt is usually cleaner since larger processes take bigger continuous blocks, so there are fewer scattered gaps left behind.

4. **Failed Allocations:** Create a scenario where an allocation fails even though the total free memory is larger than the request. Why does this happen?

An allocation can still fail even if the total free memory is bigger than the request when the free memory is separated into smaller pieces. For example, if there are two free blocks of 100 KB each, a request for 150 KB would still fail because it needs one continuous block. This happens because of external fragmentation, where memory is available but not in one usable section.

5. **Algorithm Comparison:** How might Best-Fit allocation handle the basic example differently than First-Fit? Would it reduce fragmentation?

Best-Fit would probably handle the example a little differently by choosing the smallest free block that still works instead of the first one it finds. This can help reduce wasted leftover memory in some cases. At the same time, it might create a lot of tiny leftover blocks, so fragmentation can still happen depending on the request pattern.
