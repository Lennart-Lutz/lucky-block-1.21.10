package com.luckyblock.block.luckyblock;

import java.util.Map;
import java.util.Random;

public class LuckyCategoryPicker {

    private final Map<LuckyCategory, Integer> weights;

    public LuckyCategoryPicker(Map<LuckyCategory, Integer> weights) {
        this.weights = weights;
    }

    public LuckyCategory pick(Random random) {

        int total = weights.values().stream().mapToInt(Integer::intValue).sum();
        int r = random.nextInt(total);

        int current = 0;
        for (var entry : weights.entrySet()) {
            current += entry.getValue();
            if (r < current) {
                return entry.getKey();
            }
        }

        // fallback (sollte nie passieren)
        return LuckyCategory.COMMON;
    }
}
