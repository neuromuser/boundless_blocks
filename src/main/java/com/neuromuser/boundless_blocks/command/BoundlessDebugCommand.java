package com.neuromuser.boundless_blocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoundlessDebugCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("boundless")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("debug")
                        .executes(BoundlessDebugCommand::debugInfo))
                .then(CommandManager.literal("check")
                        .executes(BoundlessDebugCommand::checkHeldItem))
                .then(CommandManager.literal("reinit")
                        .executes(BoundlessDebugCommand::reinitialize))
        );
    }

    private static int debugInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("=== Boundless Blocks Debug Info ==="), false);
        source.sendFeedback(() -> Text.literal("Total infinite items registered: " + InfiniteItem.INFINITE_ITEMS.size()), false);
        source.sendFeedback(() -> Text.literal("Craft stacks required: " + BoundlessConfig.getCraftStacksCount()), false);
        source.sendFeedback(() -> Text.literal("Items per stack required: " + BoundlessConfig.getItemsPerStack()), false);
        source.sendFeedback(() -> Text.literal("Allowed keywords: " + String.join(", ", BoundlessConfig.allowedKeywords)), false);

        // Count blocks per namespace
        var namespaces = new java.util.HashMap<String, Integer>();
        for (Block block : InfiniteItem.INFINITE_ITEMS.keySet()) {
            Identifier id = Registries.BLOCK.getId(block);
            namespaces.merge(id.getNamespace(), 1, Integer::sum);
        }

        source.sendFeedback(() -> Text.literal("Blocks by mod:"), false);
        namespaces.forEach((namespace, count) -> {
            source.sendFeedback(() -> Text.literal("  " + namespace + ": " + count), false);
        });

        return 1;
    }

    private static int checkHeldItem(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            source.sendError(Text.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack stack = source.getPlayer().getMainHandStack();
        if (stack.isEmpty()) {
            source.sendError(Text.literal("You must hold an item"));
            return 0;
        }

        Identifier itemId = Registries.ITEM.getId(stack.getItem());
        source.sendFeedback(() -> Text.literal("=== Held Item Check ==="), false);
        source.sendFeedback(() -> Text.literal("Item: " + itemId), false);

        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            Identifier blockId = Registries.BLOCK.getId(block);

            source.sendFeedback(() -> Text.literal("Block: " + blockId), false);

            boolean hasInfiniteItem = InfiniteItem.INFINITE_ITEMS.containsKey(block);
            source.sendFeedback(() -> Text.literal("Has infinite item: " + hasInfiniteItem), false);

            boolean isAllowed = BoundlessConfig.isBlockAllowedForCrafting(blockId);
            source.sendFeedback(() -> Text.literal("Allowed for crafting: " + isAllowed), false);

            if (isAllowed && !hasInfiniteItem) {
                source.sendError(Text.literal("WARNING: Block is allowed but has no infinite item!"));
                source.sendError(Text.literal("This means it should work but the infinite item wasn't registered."));
            }

            // Check which keyword matched
            String path = blockId.getPath();
            var matchedKeywords = BoundlessConfig.allowedKeywords.stream()
                    .filter(path::contains)
                    .toList();

            if (!matchedKeywords.isEmpty()) {
                source.sendFeedback(() -> Text.literal("Matched keywords: " + String.join(", ", matchedKeywords)), false);
            }

        } else {
            source.sendFeedback(() -> Text.literal("Item is not a block"), false);
        }

        return 1;
    }

    private static int reinitialize(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(() -> Text.literal("Re-initializing infinite items..."), true);

        int beforeSize = InfiniteItem.INFINITE_ITEMS.size();
        InfiniteItem.initializeInfiniteItems(true);
        int afterSize = InfiniteItem.INFINITE_ITEMS.size();

        source.sendFeedback(() -> Text.literal("Complete! Items registered: " + afterSize + " (was: " + beforeSize + ")"), true);

        return 1;
    }
}