package com.luckyblock;

import com.luckyblock.block.luckyblock.LuckyCategory;
import com.luckyblock.block.luckyblock.stats.LuckyCategoryLeader;
import com.luckyblock.block.luckyblock.stats.LuckyStatsManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.EnumMap;
import java.util.Map;

public class LuckyCommandsInitializer {

    /**
     * Used to register commands for the lucky block mod using the
     * Command Manager and Command Callback.
     */
    public static void registerCommands() {

        LuckyBlockInitializer.LOGGER.info("Registering Mod Commands for " + LuckyBlockInitializer.MOD_ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal(LuckyBlockInitializer.MOD_ID)
                            .then(CommandManager.literal("stats")
                                    .executes(ctx -> showSelfStats(ctx.getSource()))
                                    .then(CommandManager.argument("target", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String target = StringArgumentType.getString(ctx, "target");
                                                return showStats(ctx.getSource(), target);
                                            })
                                    )
                            )
            );
        });
    }

    /* ---------------- Command Functions ---------------- */

    private static int showSelfStats(ServerCommandSource source) {
        ServerPlayerEntity player;

        try {
            player = source.getPlayer();
        } catch (Exception e) {
            source.sendError(Text.literal("This command can only be run by a player."));
            return 0;
        }

        return printStats(source, player.getUuid(), player.getName().getString());
    }

    private static int showStats(ServerCommandSource source, String target) {

        if (target.equalsIgnoreCase("all")) {
            return printAllStats(source);
        }

        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(target);
        if (targetPlayer == null) {
            source.sendError(Text.literal("Player not found: " + target));
            return 0;
        }

        return printStats(source, targetPlayer.getUuid(), targetPlayer.getName().getString());
    }

    private static int printStats(ServerCommandSource source, java.util.UUID uuid, String playerName) {

        var stats = LuckyStatsManager.getStats(uuid);

        if (stats == null) {
            source.sendFeedback(() -> Text.literal("No Lucky Block stats for " + playerName), false);
            return 1;
        }

        StringBuilder sb = new StringBuilder("=== Lucky Block Stats for " + playerName + " ===\n");
        for (LuckyCategory cat : LuckyCategory.values()) {
            sb.append(" - ").append(cat.name()).append(": ").append(stats.getCategoryCount(cat)).append("\n");
        }

        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        return 1;
    }

    private static int printAllStats(ServerCommandSource source) {
        var all = LuckyStatsManager.getAll();

        if (all.isEmpty()) { // Stat/Player map empty
            source.sendFeedback(() -> Text.literal("No Lucky Block stats recorded yet."), false);
            return 1;
        }

        StringBuilder sb = new StringBuilder("=== Overall Lucky Block Stats ===\n");

        int total = 0; // Total lucky block breaks over all players in the LuckyStatsManager
        Map<LuckyCategory, LuckyCategoryLeader> leaders = new EnumMap<>(LuckyCategory.class);

        // Iterate over all monitored players and do: Total accumulation, Maximum category count
        for (var entry : all.entrySet()) {

            var stats = entry.getValue();
            Text playerName = stats.getPlayerName();

            sb.append("\n").append(playerName.getString()).append(":\n");
            for (LuckyCategory lcat : LuckyCategory.values()) {

                int luckyCategoryCount = stats.getCategoryCount(lcat); // Save locally

                sb.append(" - ").append(lcat.name()).append(": ").append(luckyCategoryCount).append("\n");
                total += luckyCategoryCount;

                // Check if the stats for the player exceeds that of the global leader
                LuckyCategoryLeader leader = leaders.get(lcat);
                if (leader == null) { // If there is no leader for this category, add the first occurrence in the list
                    LuckyCategoryLeader newLeader = new LuckyCategoryLeader(playerName, luckyCategoryCount);
                    leaders.put(lcat, newLeader);
                } else { // Check if the current count exceeds the leaders count
                    if (luckyCategoryCount > leader.getMaxCategoryCount()) {
                        // Update new leader of that category
                        leader.update(playerName, luckyCategoryCount);
                        leaders.put(lcat, leader);
                    }
                }
            }
        }

        // Evaluate winner of each category
        sb.append("\n=== Winners ===\n\n");

        for (LuckyCategory lcat : LuckyCategory.values()) {

            LuckyCategoryLeader leader = leaders.get(lcat);
            sb.append(" - ").append(lcat.name()).append(": ").append(leader.getPlayerName().getString()).append(" with "). append(leader.getMaxCategoryCount()).append("\n");
        }

        sb.append("\nTotal Lucky Blocks opened: ").append(total);

        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        return 1;
    }

}
