package com.luckyblock.block.luckyblock;

import com.luckyblock.block.ModBlocks;
import com.luckyblock.block.luckyblock.event.LuckyEvent;
import com.luckyblock.block.luckyblock.event.LuckyEventRegistry;
import com.luckyblock.block.luckyblock.loot.LuckyLootItem;
import com.luckyblock.block.luckyblock.loot.LuckyLootRegistry;
import com.luckyblock.block.luckyblock.stats.LuckyStatsManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class SuperLuckyBlock extends Block {

    public final Random random;

    private final LuckyCategoryPicker luckyCategoryPicker;
    private final LuckyLootRegistry luckyLootRegistry;
    private final LuckyEventRegistry luckyEventRegistry;

    public SuperLuckyBlock(Settings settings) {
        super(settings);

        // Probability values for the Super Lucky Block
        Map<LuckyCategory, Integer> weights = Map.of(
                LuckyCategory.VERY_UNLUCKY, 6,
                LuckyCategory.UNLUCKY, 35,
                LuckyCategory.COMMON, 23,
                LuckyCategory.LUCKY, 19,
                LuckyCategory.VERY_LUCKY, 7 // For testing
        );

        this.luckyCategoryPicker = new LuckyCategoryPicker(weights);
        this.luckyLootRegistry = new LuckyLootRegistry();
        this.luckyEventRegistry = new LuckyEventRegistry();

        this.random = new Random();
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos,
                           BlockState state, @Nullable BlockEntity blockEntity,
                           ItemStack tool) {

        // Mined with netherite hoe -> return the lucky block as drop and no lucky loot drop
        if (tool.isOf(Items.NETHERITE_HOE)) {

            if (!world.isClient()) {
                ItemStack stack = new ItemStack(ModBlocks.SUPER_LUCKY_BLOCK);
                Block.dropStack(world, pos, stack);
            }

            super.afterBreak(world, player, pos, state, blockEntity, tool);
            return;
        }

        // Drop lucky loot
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {

            LuckyCategory lcat = luckyCategoryPicker.pick(this.random);
            generateCategoryDrop(serverWorld, pos, player, lcat);

            if (player != null) {
                LuckyStatsManager.incrementFor(player, lcat);
            }

            // Place the block again
            world.setBlockState(pos, this.getDefaultState(), 3);
        }
    }

    private void generateCategoryDrop(ServerWorld world, BlockPos pos, PlayerEntity player, LuckyCategory lcat) {

        switch (lcat) {
            case LuckyCategory.VERY_UNLUCKY -> generateVeryUnluckyDrop(world, pos, player);
            case LuckyCategory.UNLUCKY -> generateUnluckyDrop(world, pos, player);
            case LuckyCategory.COMMON -> generateCommonDrop(world, pos, player);
            case LuckyCategory.LUCKY -> generateLuckyDrop(world, pos, player);
            case LuckyCategory.VERY_LUCKY -> generateVeryLuckyDrop(world, pos, player);
        }
    }

    /* ---------------- Luck Category Drops -------------------- */

    private void generateVeryUnluckyDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {

        /* Just execute a very unlucky event */
        LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.VERY_UNLUCKY_EVENTS, this.random);
        event.run(world, pos, player, this.random);
    }

    private void generateUnluckyDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {

        /* Select number of rolls of items to drop */
        int val = this.random.nextInt(100);
        int rolls = 0;

        if (val < 59) { // 60%
            rolls += 1;
        } else { // 40%
            LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.UNLUCKY_EVENTS, this.random);
            event.run(world, pos, player, this.random);
            rolls += 0; // No drop
        }

        /* Drop items */
        for (int i = 0; i < rolls; i++) {
            LuckyLootItem llItem = luckyLootRegistry.pickItem(luckyLootRegistry.UNLUCKY, this.random);
            dropStack(world, pos, llItem.createStack(this.random));
        }
    }

    private void generateCommonDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {

        /* Select number of rolls of items to drop */
        int val = this.random.nextInt(100);
        int rolls = 0;

        if (val < 84) { // 85%
            rolls += 1;
        } else if (val < 94) { // 10%
            LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.COMMON_EVENTS, this.random);
            event.run(world, pos, player, this.random);
            rolls += 0; // No drop
        } else { // 5%
            rolls += 2;
        }

        /* Drop items */
        for (int i = 0; i < rolls; i++) {
            LuckyLootItem llItem = luckyLootRegistry.pickItem(luckyLootRegistry.COMMON, this.random);
            dropStack(world, pos, llItem.createStack(this.random));
        }
    }

    private void generateLuckyDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {

        /* Select number of rolls of items to drop */
        int val = this.random.nextInt(100);
        int rolls = 0;

        if (val < 39) { // 40%
            LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.LUCKY_EVENTS, this.random);
            event.run(world, pos, player, this.random);
            rolls += 1;
        } else if (val < 79) { // 40%
            rolls += 2;
        } else { // 20%
            rolls += 3;
        }

        /* Drop items */
        for (int i = 0; i < rolls; i++) {
            LuckyLootItem llItem = luckyLootRegistry.pickItem(luckyLootRegistry.RARE, this.random);
            dropStack(world, pos, llItem.createStack(this.random));
        }
    }

    private void generateVeryLuckyDrop(ServerWorld world, BlockPos pos, PlayerEntity player) {

        /* Select number of rolls of items to drop */
        int val = this.random.nextInt(100);
        int rolls = 0;

        if (val < 39) { // 40%
            LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.LUCKY_EVENTS, this.random);
            event.run(world, pos, player, this.random);
            rolls += 1;
        } else if (val < 69) { // 30%
            rolls += 2;
        } else if (val < 89) { // 20%
            rolls += 3;
        } else if (val < 94) { // 5%
            // Play celebration sound
            world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            rolls += 5;
        } else { // 5%
            // Play celebration sound
            world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            LuckyEvent event = luckyEventRegistry.pickEvent(luckyEventRegistry.VERY_LUCKY_EVENTS, this.random);
            event.run(world, pos, player, this.random);
            rolls += 0;
        }

        /* Drop items */
        for (int i = 0; i < rolls; i++) {
            LuckyLootItem llItem = luckyLootRegistry.pickItem(luckyLootRegistry.LEGENDARY, this.random);
            dropStack(world, pos, llItem.createStack(this.random));
        }
    }

} 