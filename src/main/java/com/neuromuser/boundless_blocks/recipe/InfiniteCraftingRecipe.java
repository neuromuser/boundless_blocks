package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.block.Block;

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {
    private final int requiredStacks;

    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
        this.requiredStacks = 9; // Default for backward compatibility
    }

    // Constructor for specific stack count
    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category, int requiredStacks) {
        super(id, category);
        this.requiredStacks = Math.max(2, Math.min(9, requiredStacks));
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        // Must have at least requiredStacks slots filled
        ItemStack firstStack = ItemStack.EMPTY;
        int filledSlots = 0;

        // Count filled slots and find first stack
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                filledSlots++;
                if (firstStack.isEmpty()) {
                    firstStack = stack;
                }
            }
        }

        // Must have exactly the required number of filled slots
        if (filledSlots != requiredStacks || firstStack.isEmpty()) {
            return false;
        }

        // First stack must be a block item
        if (!(firstStack.getItem() instanceof BlockItem)) {
            return false;
        }

        // First stack must be a full stack (64)
        if (firstStack.getCount() != 64) {
            return false;
        }

        // Get the block and check if it has an infinite version
        Block block = ((BlockItem) firstStack.getItem()).getBlock();
        if (!InfiniteItem.INFINITE_ITEMS.containsKey(block)) {
            return false;
        }

        // Check all slots
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!stack.isEmpty()) {
                // Must be the same item
                if (!ItemStack.areItemsEqual(stack, firstStack)) {
                    return false;
                }

                // Must be a full stack (64)
                if (stack.getCount() != 64) {
                    return false;
                }
            } else if (i < requiredStacks) {
                // If this slot is empty but we haven't reached requiredStacks yet, fail
                // This ensures the pattern is contiguous
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Get the block from any non-empty slot
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                return new ItemStack(InfiniteItem.INFINITE_ITEMS.get(block), 1);
            }
        }

        // Fallback (should never happen if matches() passed)
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        // All stacks are fully consumed (64 items each)
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    @Override
    public boolean fits(int width, int height) {
        // Need at least enough space for the required stacks
        // For example: 2 stacks can fit in 1x2, 4 stacks in 2x2, etc.
        return width * height >= requiredStacks;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        // Return empty as the output depends on input
        return ItemStack.EMPTY;
    }

    // Getter for configuration
    public int getRequiredStacks() {
        return requiredStacks;
    }

    // Helper to visualize the pattern
    public String getPatternDescription() {
        return String.format("%d stacks of 64", requiredStacks);
    }
}