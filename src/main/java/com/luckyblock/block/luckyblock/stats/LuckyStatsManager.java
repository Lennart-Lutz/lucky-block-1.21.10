package com.luckyblock.block.luckyblock.stats;

import com.luckyblock.block.luckyblock.LuckyCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LuckyStatsManager {

    private static final Map<UUID, PlayerLuckyStats> STATS = new HashMap<>();

    public static void incrementFor(PlayerEntity player, LuckyCategory category) {

        PlayerLuckyStats stats = STATS.get(player.getUuid());
        // If stats are null, the player just destroyed its first lucky block, yay
        if (stats == null) {
            stats = new PlayerLuckyStats(player);
            STATS.put(player.getUuid(), stats);
        }
        stats.incrementCategoryCount(category);
    }

    public static PlayerLuckyStats getStats(UUID playerId) {
        return STATS.get(playerId);
    }

    public static Map<UUID, PlayerLuckyStats> getAll() {
        return STATS;
    }
}

