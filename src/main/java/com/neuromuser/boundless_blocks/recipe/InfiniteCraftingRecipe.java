package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {

    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        // Ensure items are actually registered before checking
        InfiniteItem.ensureInitialized();

        int requiredStacks = BoundlessConfig.craftStacksCount;
        int requiredCount = BoundlessConfig.itemsPerStack;

        ItemStack firstStack = ItemStack.EMPTY;
        int filledSlots = 0;

        // Single pass through inventory to collect data
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            filledSlots++;

            if (firstStack.isEmpty()) {
                firstStack = stack;

                // Fail early: must be a BlockItem
                if (!(stack.getItem() instanceof BlockItem)) return false;

                // Fail early: must meet count requirement
                if (stack.getCount() < requiredCount) return false;

                // Keyword check
                Identifier blockId = Registries.ITEM.getId(stack.getItem());
                String path = blockId.getPath();
                boolean matchesKeyword = BoundlessConfig.allowedKeywords.stream()
                        .anyMatch(path::contains);

                if (!matchesKeyword) return false;
            } else {
                // Check if this slot matches the first slot
                if (!ItemStack.areItemsEqual(stack, firstStack) || stack.getCount() < requiredCount) {
                    return false;
                }
            }
        }

        // Final check: slot count must match config
        if (filledSlots != requiredStacks) return false;

        // Check if an infinite version actually exists in our registry map
        Block block = ((BlockItem) firstStack.getItem()).getBlock();
        return InfiniteItem.INFINITE_ITEMS.containsKey(block);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Find the first non-empty stack (we already validated they are all the same in matches())
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                InfiniteItem infiniteItem = InfiniteItem.INFINITE_ITEMS.get(block);
                if (infiniteItem != null) {
                    return new ItemStack(infiniteItem, 1);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        // A 3x3 grid (9 slots) is the max standard,
        // ensure the config doesn't exceed the UI capabilities
        return (width * height) >= BoundlessConfig.craftStacksCount;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER;
    }
}