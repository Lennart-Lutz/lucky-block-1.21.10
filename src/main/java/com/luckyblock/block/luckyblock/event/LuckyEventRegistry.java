package com.luckyblock.block.luckyblock.event;

import com.luckyblock.block.luckyblock.loot.LuckyLootItem;
import com.luckyblock.block.luckyblock.loot.LuckyLootRegistry;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.block.Block.dropStack;

public class LuckyEventRegistry {

    public LuckyEventRegistry () {}

    /* ---------------- Helper-Functions ---------------- */

    public LuckyEvent pickEvent(List<WeightedLuckyEvent> events, Random random) {

        int total = 0;

        for (WeightedLuckyEvent entry : events) {
            total += entry.getWeight();
        }

        int roll = random.nextInt(total);
        int cumulative = 0;

        for (WeightedLuckyEvent entry : events) {
            cumulative += entry.getWeight();

            if (roll < cumulative) {
                return entry.getEvent();
            }
        }

        // Never happens
        return events.getFirst().getEvent();
    }

    /* ---------------- Event-Lists ---------------- */

    // Very unlucky events are used as a heavy penalty
    public final List<WeightedLuckyEvent> VERY_UNLUCKY_EVENTS = List.of(
            new WeightedLuckyEvent(this::spawnLowHealthWithers, 2),
            new WeightedLuckyEvent(this::spawnLowHealthEnderDragon, 1),

            new WeightedLuckyEvent(this::tntRain, 6),
            new WeightedLuckyEvent(this::randomTeleport, 12)
    );

    // Unlucky events are used as a mild penalty
    public final List<WeightedLuckyEvent> UNLUCKY_EVENTS = List.of(
            new WeightedLuckyEvent(this::spawnCreeper, 10),
            new WeightedLuckyEvent(this::spawnZombie, 12),
            new WeightedLuckyEvent(this::spawnSkeleton, 10),
            new WeightedLuckyEvent(this::spawnSpider, 10),
            new WeightedLuckyEvent(this::spawnCaveSpiderNest, 6),
            new WeightedLuckyEvent(this::spawnEnderman, 10),
            new WeightedLuckyEvent(this::spawnPiglin, 10),
            new WeightedLuckyEvent(this::spawnWitch, 10),
            new WeightedLuckyEvent(this::spawnSilverfish, 12),
            new WeightedLuckyEvent(this::spawnWitherSkeleton, 10),

            new WeightedLuckyEvent(this::spawnAnvilRain, 8),
            new WeightedLuckyEvent(this::spawnSandRain, 8),
            new WeightedLuckyEvent(this::spawnGravelRain, 8),

            new WeightedLuckyEvent(this::spawnArrowRain, 8)
    );

    // Common events are used as friendly encounters
    public final List<WeightedLuckyEvent> COMMON_EVENTS = List.of(
            new WeightedLuckyEvent(this::spawnCow, 10),
            new WeightedLuckyEvent(this::spawnMushroomCow, 4),
            new WeightedLuckyEvent(this::spawnPig, 12),
            new WeightedLuckyEvent(this::spawnSheep, 10),
            new WeightedLuckyEvent(this::spawnChicken, 10),
            new WeightedLuckyEvent(this::spawnBee, 8),
            new WeightedLuckyEvent(this::spawnWolf, 10),
            new WeightedLuckyEvent(this::spawnFox, 8),
            new WeightedLuckyEvent(this::spawnAxolotl, 8),
            new WeightedLuckyEvent(this::spawnCat, 10),
            new WeightedLuckyEvent(this::spawnHorse, 6),

            new WeightedLuckyEvent(this::createBlockTower, 4),
            new WeightedLuckyEvent(this::snowballRain, 10)
    );

    // Lucky events are used to compensate bad rolls in RARE or LEGENDARY categories
    public final List<WeightedLuckyEvent> LUCKY_EVENTS = List.of(
            new WeightedLuckyEvent(this::experienceShower, 10),

            new WeightedLuckyEvent(this::spawnVillager, 2)
    );

    // Very lucky events are used to spawn very nice loot/mobs
    public final List<WeightedLuckyEvent> VERY_LUCKY_EVENTS = List.of(
            new WeightedLuckyEvent(this::spawnHappyGhast, 4),
            new WeightedLuckyEvent(this::spawnIronGolemArmy, 4),

            new WeightedLuckyEvent(this::spawnLuckyLootFirework, 8)
    );

    /* ---------------- VERY UNLUCKY Events ---------------- */

    private void spawnLowHealthWithers(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = random.nextInt(3); // Max amount of withers to spawn

        if (count != 0) {
            // Spawn wither(s)
            double radius = 10.0;
            for (int i = 0; i < count; i++) {
                // Random offset around the block
                double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double oy = pos.getY();

                var entity = EntityType.WITHER.create(world, SpawnReason.EVENT);
                if (entity != null) {
                    entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);

                    // Reduce health
                    entity.setHealth(random.nextInt(5, 40));

                    // No block damage / explosion
                    entity.setInvulTimer(0);
                    entity.setCustomName(Text.literal("Weak Wither"));
                    entity.setCustomNameVisible(true);

                    world.spawnEntity(entity);
                }
            }

            // Play wither sound at spawn
            world.playSound(
                    null,
                    pos,
                    SoundEvents.ENTITY_WITHER_AMBIENT,
                    SoundCategory.HOSTILE,
                    0.7f,
                    0.9f + random.nextFloat() * 0.2f
            );
        } else {
            // When no wither spawns, create a slight explosion
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0.0f, false, World.ExplosionSourceType.BLOCK);
        }
    }

    private void spawnLowHealthEnderDragon(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = random.nextInt(2); // Spawn a dragon or not

        if (count != 0) {
            // Spawn dragon
            double radius = 30.0;
            for (int i = 0; i < count; i++) {
                // Random offset around the block
                double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double oy = pos.getY();

                var entity = EntityType.ENDER_DRAGON.create(world, SpawnReason.EVENT);
                if (entity != null) {
                    entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);

                    // Reduce health
                    entity.setHealth(random.nextInt(5, 100));

                    // Naming
                    entity.setCustomName(Text.literal("Weak Dragon"));
                    entity.setCustomNameVisible(true);

                    world.spawnEntity(entity);
                }
            }

            // Play ender dragon sound at spawn
            world.playSound(
                    null,
                    pos,
                    SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                    SoundCategory.HOSTILE,
                    0.7f,
                    0.9f + random.nextFloat() * 0.2f
            );
        } else {
            // When no wither spawns, create a slight explosion
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0.0f, false, World.ExplosionSourceType.BLOCK);
        }
    }


    private void tntRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 3 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            var tnt = EntityType.TNT.create(world, SpawnReason.EVENT);
            if (tnt != null) {
                double ox = pos.getX() + (random.nextDouble() - 0.5) * 4.0;
                double oz = pos.getZ() + (random.nextDouble() - 0.5) * 4.0;
                tnt.refreshPositionAndAngles(ox, pos.getY() + 6, oz, 0, 0);
                world.spawnEntity(tnt);
            }
        }
    }

    private void randomTeleport(ServerWorld world, BlockPos center, PlayerEntity player, Random random) {

        int radius = 6;
        int maxHeightOffset = 3;

        double x = center.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
        double z = center.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;

        double y = center.getY() + random.nextInt(maxHeightOffset + 1);

        player.requestTeleport(x, y, z);

        // Play teleport sound
        world.playSound(null, x, y, z,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f);
    }

    /* ---------------- UNLUCKY Events ---------------- */

    private void spawnCreeper(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(2); // Max amount of creepers to spawn
        double radius = 3.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.CREEPER.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play creeper sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_CREEPER_PRIMED,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnZombie(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(6); // Max amount of zombies to spawn
        double radius = 6.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.ZOMBIE.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                // Use vanilla spawn with equipment and options like baby
                entity.initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, null);
                world.spawnEntity(entity);
            }
        }

        // Play zombie sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_ZOMBIE_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnSkeleton(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 2 + random.nextInt(5); // Max amount of skeletons to spawn
        double radius = 4.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.SKELETON.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                entity.initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, null);
                world.spawnEntity(entity);
            }
        }

        // Play skeleton sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_SKELETON_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnSpider(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(4); // Max amount of skeletons to spawn
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.SPIDER.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play spider sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_SPIDER_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnCaveSpiderNest(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int spiderCount = 1 + random.nextInt(7); // Max amount of spiders to spawn
        int cobwebCount = 5 + random.nextInt(20); // Max amount of cobwebs to spawn randomly around the block
        double radius = 6.0;

        for (int i = 0; i < spiderCount; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.CAVE_SPIDER.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        for (int i = 0; i < cobwebCount; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY() + random.nextInt(2); // Lets the cobweb float one block high in the air

            // Place the cobweb
            BlockState state = Blocks.COBWEB.getDefaultState();
            BlockPos spawnPos = BlockPos.ofFloored(ox, oy, oz);
            world.setBlockState(spawnPos, state, 3);
        }

        // Play spider sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_SPIDER_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnEnderman(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(2); // Max amount of endermans to spawn
        double radius = 3.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.ENDERMAN.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play enderman sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_ENDERMAN_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnPiglin(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(2); // Max amount of piglins to spawn
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.PIGLIN_BRUTE.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play piglin sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_PIGLIN_BRUTE_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnWitch(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1; // Max amount of witches to spawn
        double radius = 3.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.WITCH.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play witch sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_WITCH_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnSilverfish(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(15); // Max amount of silverfish to spawn
        double radius = 3.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.SILVERFISH.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play silverfish sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_SILVERFISH_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnWitherSkeleton(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1; // Max amount of wither skeletons to spawn
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.WITHER_SKELETON.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play wither skeleton sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }


    private void spawnAnvilRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 6 + random.nextInt(7); // Spawn 6-12 anvils

        double radius = 5.0;
        double height = 8.0;

        for (int i = 0; i < amount; i++) {

            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double y = pos.getY() + height + random.nextDouble() * 2;

            FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, BlockPos.ofFloored(ox, y, oz), Blocks.ANVIL.getDefaultState());
        }
    }

    private void spawnSandRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 6 + random.nextInt(7); // Spawn 6-12 anvils

        double radius = 5.0;
        double height = 8.0;

        for (int i = 0; i < amount; i++) {

            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double y = pos.getY() + height + random.nextDouble() * 2;

            FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, BlockPos.ofFloored(ox, y, oz), Blocks.SAND.getDefaultState());
        }
    }

    private void spawnGravelRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 6 + random.nextInt(7); // Spawn 6-12 anvils

        double radius = 5.0;
        double height = 8.0;

        for (int i = 0; i < amount; i++) {

            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double y = pos.getY() + height + random.nextDouble() * 2;

            FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, BlockPos.ofFloored(ox, y, oz), Blocks.GRAVEL.getDefaultState());
        }
    }


    private void spawnArrowRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 80 + random.nextInt(41); // Spawn 80-120 arrows

        double radius = 8.0;
        double height = 15.0;

        for (int i = 0; i < amount; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY() + height + random.nextDouble() * 3;

            var arrow = EntityType.ARROW.create(world, SpawnReason.EVENT);
            if (arrow == null) continue;

            // Set arrow position (important)
            arrow.refreshPositionAndAngles(ox, oy, oz, 0, 0);

            arrow.setVelocity(
                    (random.nextDouble() - 0.5) * 0.1,
                    -1.0 - random.nextDouble() * 0.3,
                    (random.nextDouble() - 0.5) * 0.1
            );

            arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
            arrow.setCritical(random.nextFloat() < 0.15f);
            arrow.setDamage(1.0);

            // Set owner to the player to trigger hostile mobs to attack the player when they got hit by any arrow
            arrow.setOwner(player);
            world.spawnEntity(arrow);
        }

        // Play sound and show some particles
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.HOSTILE,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
        world.spawnParticles(
                ParticleTypes.CLOUD,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                30,
                3.0, 2.0, 3.0,
                0.1
        );
    }

    // TODO: Bad Potion Rain, Enclosure out of (Glass, Iron, Wood, Netherite, Ocean)

    /* ---------------- COMMON Events ---------------- */

    private void spawnCow(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 2 + random.nextInt(5); // Max amount of cows to spawn
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.COW.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play cow sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_COW_AMBIENT,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnMushroomCow(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(3); // Max amount of mushroom cows to spawn
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            // Random offset around the block
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.MOOSHROOM.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play mushroom cow sound at spawn
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_MOOSHROOM_CONVERT,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void spawnPig(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 3 + random.nextInt(5); // Spawn 3–7 pigs
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.PIG.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play pig sounds
        world.playSound(null, pos,
                SoundEvents.ENTITY_PIG_AMBIENT,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f);
    }

    private void spawnSheep(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 2 + random.nextInt(3); // Spawn 2–4 sheep
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.SHEEP.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);

                // Weighted random color (white ~50%)
                DyeColor color;
                if (random.nextFloat() < 0.5f) {
                    color = DyeColor.WHITE;
                } else {
                    DyeColor[] all = DyeColor.values();
                    // Pick any color except WHITE
                    color = all[1 + random.nextInt(all.length - 1)];
                }
                entity.setColor(color);

                world.spawnEntity(entity);
            }
        }

        // Play sheep sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_SHEEP_AMBIENT,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f);
    }

    private void spawnChicken(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 4 + random.nextInt(6); // Spawn 4–10 chickens
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.CHICKEN.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play chicken sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_CHICKEN_AMBIENT,
                SoundCategory.AMBIENT,
                0.7f,
                1.0f + random.nextFloat() * 0.2f);
    }

    private void spawnBee(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 2 + random.nextInt(3); // Spawn 2–4 bees
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY() + 1.5; // bees a bit higher

            var entity = EntityType.BEE.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play bee sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_BEE_LOOP,
                SoundCategory.AMBIENT,
                0.9f,
                0.9f + random.nextFloat() * 0.2f);
    }

    private void spawnWolf(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 1 + random.nextInt(2); // Spawn 1–2 wolves
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.WOLF.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        world.playSound(null, pos,
                SoundEvents.ENTITY_WOLF_SHAKE,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f);
    }

    private void spawnFox(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 1 + random.nextInt(3); // Spawn 1–3 foxes
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.FOX.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play fox sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_FOX_AMBIENT,
                SoundCategory.AMBIENT,
                0.6f,
                1.0f + random.nextFloat() * 0.2f);
    }

    private void spawnAxolotl(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 2 + random.nextInt(3); // Spawn 2–4 axolotls
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.AXOLOTL.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play axolotl sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_AXOLOTL_IDLE_AIR,
                SoundCategory.AMBIENT,
                0.7f,
                1.0f);
    }

    private void spawnCat(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 1 + random.nextInt(3); // Spawn 1–3 cats
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.CAT.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play cat sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_CAT_AMBIENT,
                SoundCategory.AMBIENT,
                0.6f,
                1.0f + random.nextFloat() * 0.2f);
    }

    private void spawnHorse(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {
        int count = 1 + random.nextInt(2); // Spawn 1–2 horses
        double radius = 5.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.HORSE.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play horse sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_HORSE_AMBIENT,
                SoundCategory.AMBIENT,
                0.8f,
                0.9f + random.nextFloat() * 0.2f);
    }


    private void createBlockTower(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        List<Block> TOWER_BLOCKS = List.of(
                Blocks.STONE,
                Blocks.GLASS,
                Blocks.COBBLESTONE,
                Blocks.MOSSY_COBBLESTONE,
                Blocks.ANDESITE,
                Blocks.GRANITE,
                Blocks.DIORITE,
                Blocks.OAK_PLANKS,
                Blocks.SPRUCE_PLANKS,
                Blocks.BRICKS,
                Blocks.SANDSTONE,
                Blocks.SMOOTH_STONE,
                Blocks.SPONGE,
                Blocks.REDSTONE_BLOCK,
                Blocks.LAPIS_BLOCK
        );

        // How tall the tower will be after it lands
        int height = 1 + random.nextInt(2); // From 1–2 blocks tall

        // From how high they fall
        int baseY = pos.getY() + 3; // Start 4 blocks above
        int x = pos.getX();
        int z = pos.getZ();

        for (int i = 0; i < height; i++) {

            Block block = TOWER_BLOCKS.get(random.nextInt(TOWER_BLOCKS.size()));
            BlockState state = block.getDefaultState();

            // Spawn position for this block
            BlockPos spawnPos = new BlockPos(x, baseY + i, z);

            FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, spawnPos, state);
        }

        // Play block placement sound
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_STONE_PLACE,
                SoundCategory.BLOCKS,
                0.7f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    private void snowballRain(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 10 + random.nextInt(20); // Spawn between 10 and 30 snowballs
        double radius = 5.0;
        double height = 8.0;

        for (int i = 0; i < amount; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double y = pos.getY() + height + random.nextDouble() * 2;

            // Create and position the snowball
            var snowball = EntityType.SNOWBALL.create(world, SpawnReason.EVENT);

            if (snowball != null) {
                snowball.refreshPositionAndAngles(ox, y, oz, 0, 0);
                snowball.setVelocity(
                        (random.nextDouble() - 0.5) * 0.05,
                        -0.25 - random.nextDouble() * 0.1,
                        (random.nextDouble() - 0.5) * 0.05
                );
                world.spawnEntity(snowball);
            }
        }

        // Play a snow / whoosh sound
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_SNOWBALL_THROW,
                SoundCategory.WEATHER,
                1.0f,
                0.9f + random.nextFloat() * 0.2f
        );
    }

    /* ---------------- LUCKY Events ---------------- */

    private void spawnVillager(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 1 + random.nextInt(2); // Spawn 1–2 villager
        double radius = 4.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.VILLAGER.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play villager sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_VILLAGER_AMBIENT,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f);
    }


    private void experienceShower(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int amount = 5 + random.nextInt(15); // Spawn between 5 and 20 experience orbs

        for (int i = 0; i < amount; i++) {
            ExperienceOrbEntity orb = new ExperienceOrbEntity(world,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2,
                    pos.getY() + 2 + random.nextDouble() * 3,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2,
                    random.nextInt(10) + 5);
            world.spawnEntity(orb);
        }
    }

    /* ---------------- VERY LUCKY Events ---------------- */

    private void spawnHappyGhast(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        // Spawn 1 happy ghast
        double radius = 15.0;

        double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
        double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
        double oy = pos.getY();

        var entity = EntityType.HAPPY_GHAST.create(world, SpawnReason.EVENT);
        if (entity != null) {
            entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
            entity.equipStack(EquipmentSlot.SADDLE, new ItemStack(Items.BLACK_HARNESS));
            world.spawnEntity(entity);
        }

        // Drop a harness for riding it
        ItemStack stack = new ItemStack(Items.BLACK_HARNESS, 1);
        dropStack(world, pos, stack);
    }

    private void spawnIronGolemArmy(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        int count = 3 + random.nextInt(4); // Spawn 3–6 iron golems
        double radius = 8.0;

        for (int i = 0; i < count; i++) {
            double ox = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
            double oy = pos.getY();

            var entity = EntityType.IRON_GOLEM.create(world, SpawnReason.EVENT);
            if (entity != null) {
                entity.refreshPositionAndAngles(ox, oy, oz, random.nextFloat() * 360f, 0f);
                world.spawnEntity(entity);
            }
        }

        // Play iron golem sound
        world.playSound(null, pos,
                SoundEvents.ENTITY_IRON_GOLEM_DEATH,
                SoundCategory.AMBIENT,
                0.7f,
                0.9f + random.nextFloat() * 0.2f);
    }


    private void spawnLuckyLootFirework(ServerWorld world, BlockPos pos, PlayerEntity player, Random random) {

        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);

        // Create random explosion effects
        List<FireworkExplosionComponent> explosions = new ArrayList<>();

        int explosionCount = 1 + random.nextInt(2); // 1-2 effects
        int[] palette = new int[]{0xFFCC00, 0xFFFF00, 0xFFFFFF, 0x00FFFF, 0xFF66FF};

        for (int i = 0; i < explosionCount; i++) {
            int colorCount = 2 + random.nextInt(2);
            int[] colors = new int[colorCount];
            for (int c = 0; c < colorCount; c++) {
                colors[c] = palette[random.nextInt(palette.length)];
            }

            FireworkExplosionComponent.Type type = FireworkExplosionComponent.Type.values()[random.nextInt(FireworkExplosionComponent.Type.values().length)];

            FireworkExplosionComponent explosion = new FireworkExplosionComponent(
                    type,
                    IntList.of(colors),
                    IntList.of(),
                    true,
                    true
            );

            explosions.add(explosion);
        }

        FireworksComponent fireworks = new FireworksComponent(1, explosions);
        stack.set(DataComponentTypes.FIREWORKS, fireworks);

        // Spawn the rocket
        FireworkRocketEntity rocket = new FireworkRocketEntity(world, pos.getX(), pos.getY() + 1.5, pos.getZ(), stack);
        rocket.setVelocity(0.0, 0.15 + random.nextDouble() * 0.05, 0.0);
        world.spawnEntity(rocket);

        // Spawn loot above the rocket
        int lootHeight = 30;
        LuckyLootRegistry luckyLootRegistry = new LuckyLootRegistry();

        for (int i = 0; i < 20; i++) {
            LuckyLootItem llItem = luckyLootRegistry.pickItem(luckyLootRegistry.LEGENDARY, random);
            Block.dropStack(world, pos.up(lootHeight), llItem.createStack(random));
        }
    }

    // Todo: Good Potion Rain
}
