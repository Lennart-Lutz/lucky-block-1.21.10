package com.luckyblock.block.luckyblock.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public interface LuckyEvent {
    void run(ServerWorld world, BlockPos pos, PlayerEntity player, Random random);
}
