package com.luckyblock.block;

import com.luckyblock.LuckyBlockInitializer;
import com.luckyblock.block.luckyblock.LuckyBlock;

import com.luckyblock.block.luckyblock.SuperLuckyBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static Block LUCKY_BLOCK;
    public static Block SUPER_LUCKY_BLOCK;

    /**
     * Register a block with its item using the proper 1.21.10 pattern
     */
    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings settings) {
        // Create identifier and registry keys
        Identifier identifier = Identifier.of(LuckyBlockInitializer.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, identifier);

        // Set registry key in settings
        AbstractBlock.Settings blockSettings = settings.registryKey(blockKey);

        // Create and register the block
        T block = Registry.register(Registries.BLOCK, blockKey, factory.apply(blockSettings));

        // Register block item with registry key in item settings
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        BlockItem blockItem = new BlockItem(block, itemSettings);
        Registry.register(Registries.ITEM, itemKey, blockItem);

        return block;
    }

    public static void registerModBlocks() {
        LuckyBlockInitializer.LOGGER.info("Registering Mod Blocks for " + LuckyBlockInitializer.MOD_ID);

        // Register the lucky block
        LUCKY_BLOCK = register("lucky_block", LuckyBlock::new, AbstractBlock.Settings.create().strength(0.15f).sounds(BlockSoundGroup.METAL).resistance(3600.0f));

        // Register the super lucky block
        SUPER_LUCKY_BLOCK = register("super_lucky_block", SuperLuckyBlock::new, AbstractBlock.Settings.create().strength(0.55f).sounds(BlockSoundGroup.METAL).resistance(3600.0f));

        // Add the blocks to the creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(entries -> {
            entries.add(LUCKY_BLOCK);
            entries.add(SUPER_LUCKY_BLOCK);
        });
    }
}
