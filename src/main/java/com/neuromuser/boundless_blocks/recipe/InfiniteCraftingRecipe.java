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
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.recipe.input.CraftingRecipeInput;

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {

    public InfiniteCraftingRecipe(CraftingRecipeCategory category) {
        super(category);
    }


    @Override
    public boolean matches(CraftingRecipeInput inv, World world) {
        ItemStack first = ItemStack.EMPTY;
        int validStacks = 0;

        for (int i = 0; i < inv.getSize(); i++) {  // size() → getSize()
            ItemStack stack = inv.getStackInSlot(i);  // getStack() → getStackInSlot()
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
    public ItemStack craft(CraftingRecipeInput inv, RegistryWrapper.WrapperLookup lookup) {
        for (int i = 0; i < inv.getSize(); i++) {  // size() → getSize()
            ItemStack stack = inv.getStackInSlot(i);  // getStack() → getStackInSlot()
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