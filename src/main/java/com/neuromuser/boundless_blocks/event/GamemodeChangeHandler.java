package com.neuromuser.boundless_blocks.event;

import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamemodeChangeHandler {

    private static final Map<UUID, Map<Integer, String>> PLAYER_INFINITE_BLOCKS = new HashMap<>();

    public static void beforeGamemodeChange(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            return;
        }

        Map<Integer, String> infiniteBlocks = new HashMap<>();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (stack.getItem() instanceof InfiniteBlockItem) {
                Block block = InfiniteBlockItem.getStoredBlock(stack);
                if (block != null) {
                    infiniteBlocks.put(i, Registries.BLOCK.getId(block).toString());
                }
            }
        }

        if (!infiniteBlocks.isEmpty()) {
            PLAYER_INFINITE_BLOCKS.put(player.getUuid(), infiniteBlocks);
        }
    }

    public static void afterGamemodeChange(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            return;
        }

        Map<Integer, String> infiniteBlocks = PLAYER_INFINITE_BLOCKS.get(player.getUuid());
        if (infiniteBlocks == null) {
            return;
        }

        for (Map.Entry<Integer, String> entry : infiniteBlocks.entrySet()) {
            int slot = entry.getKey();
            String blockIdStr = entry.getValue();

            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.getItem() instanceof InfiniteBlockItem) {
                Identifier blockId = Identifier.tryParse(blockIdStr);
                if (blockId != null) {
                    Block block = Registries.BLOCK.get(blockId);
                    InfiniteBlockItem.setStoredBlock(stack, block);
                }
            }
        }

        PLAYER_INFINITE_BLOCKS.remove(player.getUuid());
    }
}