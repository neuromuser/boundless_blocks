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

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {

    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        ItemStack first = ItemStack.EMPTY;
        int validStacks = 0;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty()) continue;

            if (!(stack.getItem() instanceof BlockItem)) return false;

            if (stack.getCount() < 64) return false;

            Identifier blockId = Registries.BLOCK.getId(((BlockItem) stack.getItem()).getBlock());
            if (!BoundlessConfig.isBlockAllowed(blockId)) return false;

            if (first.isEmpty()) {
                first = stack;
            } else if (!ItemStack.areItemsEqual(stack, first)) {
                return false;
            }

            validStacks++;
        }

        return validStacks == BoundlessConfig.craftStacksCount;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                return InfiniteBlockItem.create(blockItem.getBlock());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= BoundlessConfig.craftStacksCount;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER;
    }
}