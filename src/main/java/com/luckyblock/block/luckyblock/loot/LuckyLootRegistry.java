package com.luckyblock.block.luckyblock.loot;

import net.minecraft.item.Items;

import java.util.List;
import java.util.Random;

// TODO: Consider moving this to a data-driven JSON later

public class LuckyLootRegistry {

    public LuckyLootRegistry() {}

    /* ---------------- Helper-Functions ---------------- */

    public LuckyLootItem pickItem(List<LuckyLootItem> items, Random random) {

        int total = 0;

        for (LuckyLootItem entry : items) {
            total += entry.getWeight();
        }

        int roll = random.nextInt(total);
        int cumulative = 0;

        for (LuckyLootItem entry : items) {
            cumulative += entry.getWeight();

            if (roll < cumulative) return entry;
        }

        // Never happens
        return items.getFirst();
    }

    /* ---------------- Loot-Lists ---------------- */

    public final List<LuckyLootItem> UNLUCKY = List.of(

            // Bad / cursed food
            new LuckyLootItem(Items.ROTTEN_FLESH, 2, 8, 12),
            new LuckyLootItem(Items.POISONOUS_POTATO, 1, 4, 10),
            new LuckyLootItem(Items.SPIDER_EYE, 1, 3, 8),
            new LuckyLootItem(Items.PUFFERFISH, 1, 2, 8),
            new LuckyLootItem(Items.SUSPICIOUS_STEW, 1, 1, 6),
            new LuckyLootItem(Items.CHORUS_FRUIT, 1, 3, 5),

            // Useless / random potions
            new LuckyLootItem(Items.POTION, 1, 5, 14),

            // Junk / nothing special
            new LuckyLootItem(Items.DEAD_BUSH, 1, 4, 8),
            new LuckyLootItem(Items.BOWL, 2, 6, 8),
            new LuckyLootItem(Items.STONE_BUTTON, 1, 2, 6),
            new LuckyLootItem(Items.STRING, 1, 4, 6),

            // Annoying spawn eggs
            new LuckyLootItem(Items.ZOMBIE_SPAWN_EGG, 1, 2, 6),
            new LuckyLootItem(Items.SKELETON_SPAWN_EGG, 1, 2, 6),
            new LuckyLootItem(Items.SPIDER_SPAWN_EGG, 1, 2, 5),
            new LuckyLootItem(Items.CREEPER_SPAWN_EGG, 1, 1, 3),
            new LuckyLootItem(Items.SILVERFISH_SPAWN_EGG, 1, 3, 4),

            // Weak / wooden / “why?” tools
            new LuckyLootItem(Items.WOODEN_PICKAXE, 1, 1, 8),
            new LuckyLootItem(Items.WOODEN_AXE, 1, 1, 8),
            new LuckyLootItem(Items.WOODEN_SHOVEL, 1, 1, 8),
            new LuckyLootItem(Items.WOODEN_SWORD, 1, 1, 8),
            new LuckyLootItem(Items.WOODEN_HOE, 1, 1, 8),
            new LuckyLootItem(Items.STONE_HOE, 1, 1, 8),
            new LuckyLootItem(Items.IRON_HOE, 1, 1, 6),
            new LuckyLootItem(Items.DIAMOND_HOE, 1, 1, 4),
            new LuckyLootItem(Items.NETHERITE_HOE, 1, 1, 2)
    );

    public final List<LuckyLootItem> COMMON = List.of(

            // Food and farming
            new LuckyLootItem(Items.BREAD, 1, 5, 10),
            new LuckyLootItem(Items.APPLE, 1, 4, 9),
            new LuckyLootItem(Items.CARROT, 2, 6, 8),
            new LuckyLootItem(Items.POTATO, 2, 6, 8),
            new LuckyLootItem(Items.BAKED_POTATO, 2, 5, 6),
            new LuckyLootItem(Items.BEETROOT, 2, 6, 6),
            new LuckyLootItem(Items.MELON_SLICE, 3, 8, 8),
            new LuckyLootItem(Items.PUMPKIN_PIE, 1, 2, 5),
            new LuckyLootItem(Items.PUMPKIN, 1, 3, 4),
            new LuckyLootItem(Items.COOKIE, 3, 12, 6),
            new LuckyLootItem(Items.HONEY_BOTTLE, 1, 2, 3),
            new LuckyLootItem(Items.SWEET_BERRIES, 3, 10, 6),
            new LuckyLootItem(Items.MUSHROOM_STEW, 1, 2, 4),
            new LuckyLootItem(Items.WHEAT_SEEDS, 4, 12, 6),
            new LuckyLootItem(Items.BEETROOT_SEEDS, 2, 8, 5),
            new LuckyLootItem(Items.MELON_SEEDS, 2, 6, 5),
            new LuckyLootItem(Items.PUMPKIN_SEEDS, 2, 6, 5),
            new LuckyLootItem(Items.SUGAR_CANE, 2, 6, 5),

            // Wood and nature
            new LuckyLootItem(Items.OAK_LOG, 4, 8, 12),
            new LuckyLootItem(Items.SPRUCE_LOG, 4, 8, 12),
            new LuckyLootItem(Items.BIRCH_LOG, 4, 8, 12),
            new LuckyLootItem(Items.JUNGLE_LOG, 4, 8, 10),
            new LuckyLootItem(Items.ACACIA_LOG, 4, 8, 10),
            new LuckyLootItem(Items.DARK_OAK_LOG, 4, 8, 10),
            new LuckyLootItem(Items.MANGROVE_LOG, 4, 8, 10),
            new LuckyLootItem(Items.CHERRY_LOG, 4, 6, 10),
            new LuckyLootItem(Items.BAMBOO, 4, 16, 6),
            new LuckyLootItem(Items.STICK, 4, 12, 12),
            new LuckyLootItem(Items.OAK_SAPLING, 1, 3, 6),
            new LuckyLootItem(Items.BIRCH_SAPLING, 1, 3, 6),
            new LuckyLootItem(Items.SPRUCE_SAPLING, 1, 3, 5),
            new LuckyLootItem(Items.SUNFLOWER, 1, 2, 3),

            // Basic building blocks
            new LuckyLootItem(Items.STONE,           6, 20, 9),
            new LuckyLootItem(Items.COBBLESTONE,     6, 20, 10),
            new LuckyLootItem(Items.MOSSY_COBBLESTONE, 3, 10, 5),
            new LuckyLootItem(Items.GRANITE,         4, 16, 5),
            new LuckyLootItem(Items.DIORITE,         4, 16, 5),
            new LuckyLootItem(Items.ANDESITE,        4, 16, 5),
            new LuckyLootItem(Items.POLISHED_ANDESITE, 3, 12, 4),
            new LuckyLootItem(Items.POLISHED_DIORITE,  3, 12, 4),
            new LuckyLootItem(Items.POLISHED_GRANITE,  3, 12, 4),
            new LuckyLootItem(Items.STONE_BRICKS,      4, 16, 5),
            new LuckyLootItem(Items.DEEPSLATE,     4, 14, 4),
            new LuckyLootItem(Items.CALCITE,      3, 10, 3),
            new LuckyLootItem(Items.TUFF,         3, 10, 3),
            new LuckyLootItem(Items.DIRT,         6, 20, 8),
            new LuckyLootItem(Items.SAND,         6, 20, 8),
            new LuckyLootItem(Items.GRAVEL,       6, 20, 8),
            new LuckyLootItem(Items.GLASS,        3, 10, 5),
            new LuckyLootItem(Items.WHITE_WOOL,   3, 10, 5),
            new LuckyLootItem(Items.LADDER,       3, 8, 5),
            new LuckyLootItem(Items.SCAFFOLDING,  3, 8, 4),

            // Utility / building
            new LuckyLootItem(Items.CRAFTING_TABLE, 1, 1, 6),
            new LuckyLootItem(Items.FURNACE, 1, 1, 5),
            new LuckyLootItem(Items.CHEST, 1, 2, 5),
            new LuckyLootItem(Items.BARREL, 1, 2, 5),
            new LuckyLootItem(Items.CAMPFIRE, 1, 1, 4),
            new LuckyLootItem(Items.BLAST_FURNACE, 1, 1, 2),
            new LuckyLootItem(Items.SMOKER, 1, 1, 3),
            new LuckyLootItem(Items.CAULDRON, 1, 1, 2),
            new LuckyLootItem(Items.STONECUTTER, 1, 1, 3),
            new LuckyLootItem(Items.GRINDSTONE, 1, 1, 3),
            new LuckyLootItem(Items.SMITHING_TABLE, 1, 1, 2),
            new LuckyLootItem(Items.CARTOGRAPHY_TABLE, 1, 1, 2),
            new LuckyLootItem(Items.FLETCHING_TABLE, 1, 1, 2),
            new LuckyLootItem(Items.TORCH, 8, 32, 10),
            new LuckyLootItem(Items.LANTERN, 1, 2, 5),
            new LuckyLootItem(Items.PAINTING, 1, 2, 2),

            // Ores and materials
            new LuckyLootItem(Items.COAL, 3, 8, 10),
            new LuckyLootItem(Items.IRON_NUGGET, 2, 8, 8),
            new LuckyLootItem(Items.IRON_INGOT, 1, 3, 6),
            new LuckyLootItem(Items.COPPER_INGOT, 2, 6, 6),
            new LuckyLootItem(Items.REDSTONE, 4, 16, 8),
            new LuckyLootItem(Items.LAPIS_LAZULI, 2, 6, 6),
            new LuckyLootItem(Items.QUARTZ, 2, 6, 5),
            new LuckyLootItem(Items.FLINT, 1, 3, 6),
            new LuckyLootItem(Items.AMETHYST_SHARD, 1, 4, 4),
            new LuckyLootItem(Items.OBSIDIAN, 2, 6, 4),
            new LuckyLootItem(Items.BONE, 2, 8, 6),
            new LuckyLootItem(Items.FEATHER, 2, 6, 5),
            new LuckyLootItem(Items.LEATHER, 1, 4, 5),
            new LuckyLootItem(Items.STRING, 2, 8, 5),
            new LuckyLootItem(Items.SLIME_BALL, 1, 3, 3),

            // Tools and weapons (basic)
            new LuckyLootItem(Items.STONE_PICKAXE, 1, 1, 12),
            new LuckyLootItem(Items.STONE_AXE, 1, 1, 10),
            new LuckyLootItem(Items.STONE_SHOVEL, 1, 1, 10),
            new LuckyLootItem(Items.STONE_SWORD, 1, 1, 10),
            new LuckyLootItem(Items.IRON_PICKAXE, 1, 1, 6),
            new LuckyLootItem(Items.IRON_AXE, 1, 1, 4),
            new LuckyLootItem(Items.IRON_SHOVEL, 1, 1, 4),
            new LuckyLootItem(Items.IRON_SWORD, 1, 1, 4),
            new LuckyLootItem(Items.SHIELD, 1, 1, 3),
            new LuckyLootItem(Items.BOW, 1, 1, 4),
            new LuckyLootItem(Items.ARROW, 8, 32, 6),
            new LuckyLootItem(Items.FISHING_ROD, 1, 1, 3),
            new LuckyLootItem(Items.FLINT_AND_STEEL, 1, 1, 4),
            new LuckyLootItem(Items.SHEARS, 1, 1, 3),
            new LuckyLootItem(Items.BUCKET, 1, 1, 4),
            new LuckyLootItem(Items.WATER_BUCKET, 1, 1, 2),
            new LuckyLootItem(Items.OAK_BOAT, 1, 1, 3),

            // Redstone and automation
            new LuckyLootItem(Items.REDSTONE_TORCH, 4, 8, 4),
            new LuckyLootItem(Items.REPEATER, 1, 3, 3),
            new LuckyLootItem(Items.PISTON, 1, 3, 3),
            new LuckyLootItem(Items.OBSERVER, 1, 2, 2),
            new LuckyLootItem(Items.HOPPER, 1, 1, 2),
            new LuckyLootItem(Items.DROPPER, 1, 1, 2),
            new LuckyLootItem(Items.DISPENSER, 1, 1, 2),
            new LuckyLootItem(Items.LEVER, 2, 6, 5),
            new LuckyLootItem(Items.STONE_PRESSURE_PLATE, 1, 3, 4),

            // Brewing
            new LuckyLootItem(Items.GLASS_BOTTLE, 3, 9, 5),
            new LuckyLootItem(Items.NETHER_WART, 2, 6, 4),
            new LuckyLootItem(Items.SUGAR, 2, 6, 4),
            new LuckyLootItem(Items.GLOWSTONE_DUST, 1, 4, 3),
            new LuckyLootItem(Items.GUNPOWDER, 1, 4, 3),

            // Useful / peaceful spawn eggs
            new LuckyLootItem(Items.COW_SPAWN_EGG, 1, 2, 5),
            new LuckyLootItem(Items.SHEEP_SPAWN_EGG, 1, 2, 5),
            new LuckyLootItem(Items.CHICKEN_SPAWN_EGG, 1, 3, 5),
            new LuckyLootItem(Items.PIG_SPAWN_EGG, 1, 2, 5),
            new LuckyLootItem(Items.BEE_SPAWN_EGG, 1, 2, 3),
            new LuckyLootItem(Items.HORSE_SPAWN_EGG, 1, 1, 3),
            new LuckyLootItem(Items.CAT_SPAWN_EGG, 1, 1, 3),
            new LuckyLootItem(Items.WOLF_SPAWN_EGG, 1, 2, 3),
            new LuckyLootItem(Items.FOX_SPAWN_EGG, 1, 1, 2),
            new LuckyLootItem(Items.AXOLOTL_SPAWN_EGG, 1, 1, 2)
    );


    public final List<LuckyLootItem> RARE = List.of(

            // Valuable resources
            new LuckyLootItem(Items.DIAMOND, 1, 4, 10),
            new LuckyLootItem(Items.ENDER_PEARL, 1, 3, 8),
            new LuckyLootItem(Items.NETHERITE_SCRAP, 1, 2, 3),

            // Enchanting and XP
            new LuckyLootItem(Items.ENCHANTING_TABLE, 1, 1, 6),
            new LuckyLootItem(Items.BOOKSHELF, 2, 8, 6),
            new LuckyLootItem(Items.EXPERIENCE_BOTTLE, 4, 16, 4),
            new LuckyLootItem(Items.GOLDEN_APPLE, 1, 2, 4),

            // Diamond gear
            new LuckyLootItem(Items.DIAMOND_PICKAXE, 1, 1, 5),
            new LuckyLootItem(Items.DIAMOND_AXE, 1, 1, 5),
            new LuckyLootItem(Items.DIAMOND_SHOVEL, 1, 1, 4),
            new LuckyLootItem(Items.DIAMOND_SWORD, 1, 1, 5),

            // Diamond armor
            new LuckyLootItem(Items.DIAMOND_HELMET, 1, 1, 4),
            new LuckyLootItem(Items.DIAMOND_CHESTPLATE, 1, 1, 3),
            new LuckyLootItem(Items.DIAMOND_LEGGINGS, 1, 1, 3),
            new LuckyLootItem(Items.DIAMOND_BOOTS, 1, 1, 4),

            // Netherite tools (rare but not legendary)
            new LuckyLootItem(Items.NETHERITE_PICKAXE, 1, 1, 2),
            new LuckyLootItem(Items.NETHERITE_AXE, 1, 1, 2),
            new LuckyLootItem(Items.NETHERITE_SHOVEL, 1, 1, 2),

            // Utility / late game
            new LuckyLootItem(Items.ENDER_EYE, 1, 2, 4),
            new LuckyLootItem(Items.SHULKER_BOX, 1, 2, 3),

            // Combat
            new LuckyLootItem(Items.CROSSBOW, 1, 1, 3),
            new LuckyLootItem(Items.ARROW, 32, 64, 5),
            new LuckyLootItem(Items.SPECTRAL_ARROW, 16, 32, 4),
            new LuckyLootItem(Items.TIPPED_ARROW, 8, 16, 3),
            new LuckyLootItem(Items.TRIDENT, 1, 1, 2),

            // Utility / village related spawn eggs
            new LuckyLootItem(Items.VILLAGER_SPAWN_EGG, 1, 1, 2),
            new LuckyLootItem(Items.IRON_GOLEM_SPAWN_EGG, 1, 1, 3)
    );

    public final List<LuckyLootItem> LEGENDARY = List.of(

            // Beacon jackpot
            new LuckyLootItem(Items.BEACON, 1, 1, 5),
            new LuckyLootItem(Items.DIAMOND_BLOCK, 3, 12, 5),
            new LuckyLootItem(Items.EMERALD_BLOCK, 4, 15, 5),
            new LuckyLootItem(Items.GOLD_BLOCK, 6, 20, 5),
            new LuckyLootItem(Items.IRON_BLOCK, 8, 26, 5),

            // High-value gear
            new LuckyLootItem(Items.NETHERITE_INGOT, 1, 3, 4),
            new LuckyLootItem(Items.NETHERITE_SWORD, 1, 1, 3),
            new LuckyLootItem(Items.NETHERITE_HELMET, 1, 1, 3),
            new LuckyLootItem(Items.NETHERITE_CHESTPLATE, 1, 1, 3),
            new LuckyLootItem(Items.NETHERITE_LEGGINGS, 1, 1, 3),
            new LuckyLootItem(Items.NETHERITE_BOOTS, 1, 1, 3),

            // Ultra rare utility
            new LuckyLootItem(Items.TOTEM_OF_UNDYING, 1, 1, 2),
            new LuckyLootItem(Items.ENCHANTED_GOLDEN_APPLE, 1, 1, 1),
            new LuckyLootItem(Items.ELYTRA, 1, 1, 2),

            // Firework supply
            new LuckyLootItem(Items.FIREWORK_ROCKET, 24, 46, 4),

            // Spawn eggs for PVP
            new LuckyLootItem(Items.CREEPER_SPAWN_EGG, 4, 18, 3),
            new LuckyLootItem(Items.WARDEN_SPAWN_EGG, 1, 6, 2)
    );
}
