package com.luckyblock.block.luckyblock.stats;

import net.minecraft.text.Text;

public class LuckyCategoryLeader {

    private Text playerName;
    private int maxCategoryCount; // Current category max count (how many hits of this category the player has)

    public LuckyCategoryLeader (Text playerName, int maxCategoryCount)
    {
        this.playerName = playerName;
        this.maxCategoryCount = maxCategoryCount;
    }

    public Text getPlayerName() {
        return this.playerName;
    }

    public int getMaxCategoryCount() {
        return this.maxCategoryCount;
    }

    public void update(Text playerName, int maxCategoryCount) {
        this.playerName = playerName;
        this.maxCategoryCount = maxCategoryCount;
    }
}
