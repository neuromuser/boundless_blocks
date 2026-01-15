package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Recipe to unpack infinite blocks back into regular blocks
 * Infinite Block -> 9 Regular Blocks
 */
public class InfiniteUnpackingRecipe extends SpecialCraftingRecipe {

    public InfiniteUnpackingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int infiniteItemCount = 0;
        int totalItemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                totalItemCount++;

                if (world.isClient()) {
                    // On client, Polymer disguises our item as the base block item
                    // We need to check if it has our custom name format "∞ ... ∞"
                    // or if it has the enchantment glint
                    if (stack.hasGlint() && stack.hasCustomName()) {
                        String name = stack.getName().getString();
                        if (name.startsWith("∞") && name.endsWith("∞")) {
                            infiniteItemCount++;
                            continue;
                        }
                    }
                    // If not our format, it's not an infinite item
                    return false;
                } else {
                    // Server side - check actual item type
                    if (stack.getItem() instanceof InfiniteBlockItem && InfiniteBlockItem.isInfiniteBlock(stack)) {
                        infiniteItemCount++;
                    } else {
                        return false;
                    }
                }
            }
        }

        return infiniteItemCount == 1 && totalItemCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            net.minecraft.block.Block block = null;

            if (stack.getItem() instanceof InfiniteBlockItem && InfiniteBlockItem.isInfiniteBlock(stack)) {
                block = InfiniteBlockItem.getStoredBlock(stack);
            }
            else if (stack.hasNbt()) {
                net.minecraft.nbt.NbtCompound nbt = stack.getNbt();
                if (nbt != null && nbt.contains("StoredBlockId")) {
                    String blockIdStr = nbt.getString("StoredBlockId");
                    net.minecraft.util.Identifier blockId = net.minecraft.util.Identifier.tryParse(blockIdStr);
                    if (blockId != null) {
                        block = net.minecraft.registry.Registries.BLOCK.get(blockId);
                    }
                }
            }

            if (block != null) {
                // Return 9 of the base block
                return new ItemStack(block.asItem(), 9);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_UNPACKING_SERIALIZER;
    }
}