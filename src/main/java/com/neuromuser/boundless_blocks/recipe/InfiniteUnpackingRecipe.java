package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class InfiniteUnpackingRecipe extends SpecialCraftingRecipe {

    public InfiniteUnpackingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput inv, World world) {
        if (!BoundlessConfig.allowUnpacking) return false;

        int items = 0;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            items++;
            if (world.isClient) {
                if (!stack.hasGlint()) return false;
                if (!stack.contains(net.minecraft.component.DataComponentTypes.CUSTOM_NAME)) return false;
                String name = stack.getName().getString();
                if (!name.startsWith("∞") || !name.endsWith("∞")) return false;
            } else {
                if (!(stack.getItem() instanceof InfiniteBlockItem)) return false;
                if (InfiniteBlockItem.getBlock(stack) == null) return false;
            }
        }

        return items == 1;
    }

    // Add overload for RecipeInputInventory
    public boolean matches(RecipeInputInventory inv, World world) {
        return matches(inv.createRecipeInput(), world);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inv, RegistryWrapper.WrapperLookup lookup) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.block.Block block = InfiniteBlockItem.getBlock(stack);
                if (block != null) return new ItemStack(block.asItem(), 9);
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