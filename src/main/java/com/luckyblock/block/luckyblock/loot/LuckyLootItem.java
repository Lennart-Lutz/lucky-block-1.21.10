package com.luckyblock.block.luckyblock.loot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class LuckyLootItem {

    private final Item item;
    private final int min;
    private final int max;
    private final int weight;

    public LuckyLootItem(Item item, int min, int max, int weight) {
        this.item = item;
        this.min = min;
        this.max = max;
        this.weight = weight;
    }

    public Item getItem() {
        return item;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getWeight() {
        return weight;
    }

    public ItemStack createStack(Random random) {
        int count = min + random.nextInt(max - min + 1);
        return new ItemStack(item, count);
    }
}
