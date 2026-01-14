package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {

    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int requiredStacks = BoundlessConfig.getCraftStacksCount();
        int requiredCount = BoundlessConfig.getItemsPerStack();

        ItemStack firstStack = ItemStack.EMPTY;
        Set<Integer> usedSlots = new HashSet<>();
        int validStacks = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            if (isValidStack(stack)) {
                usedSlots.add(i);
                validStacks++;

                if (firstStack.isEmpty()) {
                    firstStack = stack;
                } else {
                    if (!ItemStack.areItemsEqual(stack, firstStack)) {
                        return false;
                    }
                }
            }
        }

        if (validStacks != requiredStacks) {
            return false;
        }

        for (int slot : usedSlots) {
            if (inventory.getStack(slot).getCount() < requiredCount) {
                return false;
            }
        }

        return !firstStack.isEmpty() && firstStack.getItem() instanceof BlockItem;
    }

    private boolean isValidStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem)) return false;

        net.minecraft.block.Block block = ((BlockItem) stack.getItem()).getBlock();
        Identifier blockId = Registries.BLOCK.getId(block);

        // Apply crafting filters from config
        return BoundlessConfig.isBlockAllowedForCrafting(blockId);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        // Find the block being crafted
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                net.minecraft.block.Block block = blockItem.getBlock();
                Identifier blockId = Registries.BLOCK.getId(block);

                // Create infinite item with NBT storing the block
                BoundlessBlocks.LOGGER.info("Creating infinite block for: {}", blockId);
                return InfiniteBlockItem.createInfiniteStack(block);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return (width * height) >= BoundlessConfig.getCraftStacksCount();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER;
    }
}