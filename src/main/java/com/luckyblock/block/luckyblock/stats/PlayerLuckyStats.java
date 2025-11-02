package com.luckyblock.block.luckyblock.stats;

import com.luckyblock.block.luckyblock.LuckyCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLuckyStats {

    private final UUID playerId;
    private final Text playerName;
    private final Map<LuckyCategory, Integer> categoryCounts = new EnumMap<>(LuckyCategory.class);

    public PlayerLuckyStats(PlayerEntity player) {
        this.playerId = player.getUuid();
        this.playerName = player.getName();

        // Initialize with 0
        for (LuckyCategory cat : LuckyCategory.values()) {
            categoryCounts.put(cat, 0);
        }
    }

    public void incrementCategoryCount(LuckyCategory category) {
        categoryCounts.put(category, categoryCounts.get(category) + 1);
    }

    public int getCategoryCount(LuckyCategory category) {
        return categoryCounts.get(category);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Text getPlayerName() {
        return playerName;
    }
}
