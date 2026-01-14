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

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (InfiniteBlockItem.isInfiniteBlock(stack)) {
                    infiniteItemCount++;
                } else {
                    return false;
                }
            }
        }

        return infiniteItemCount == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (InfiniteBlockItem.isInfiniteBlock(stack)) {
                net.minecraft.block.Block block = InfiniteBlockItem.getStoredBlock(stack);
                if (block != null) {
                    return new ItemStack(block.asItem(), 9);
                }
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