package com.neuromuser.boundless_blocks.event;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class ItemPickupHandler {

    public static void onItemPickup(PlayerEntity player, ItemEntity itemEntity, ItemStack pickedStack) {
        if (!BoundlessConfig.removePickedBlocks || player.getWorld().isClient()) {
            return;
        }

        if (!(pickedStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block pickedBlock = blockItem.getBlock();
        Identifier pickedId = Registries.BLOCK.getId(pickedBlock);

        if (hasInfiniteVersion(player, pickedId)) {
            itemEntity.setStack(ItemStack.EMPTY);
            itemEntity.discard();
        }
    }

    private static boolean hasInfiniteVersion(PlayerEntity player, Identifier targetBlockId) {
        ItemStack[] inventory = player.getInventory().main.toArray(new ItemStack[0]);

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof InfiniteBlockItem) {
                Block storedBlock = InfiniteBlockItem.getStoredBlock(stack);
                if (storedBlock != null && Registries.BLOCK.getId(storedBlock).equals(targetBlockId)) {
                    return true;
                }
            }
        }
        return false;
    }
}