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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class InfiniteCraftingRecipe extends SpecialCraftingRecipe {

    public InfiniteCraftingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        // Always pull the latest values from config
        int requiredStacks = BoundlessConfig.craftStacksCount;
        int requiredCount = BoundlessConfig.itemsPerStack;

        ItemStack firstStack = ItemStack.EMPTY;
        int filledSlots = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                filledSlots++;
                if (firstStack.isEmpty()) {
                    firstStack = stack;
                }
            }
        }

        // 1. Check if the amount of stacks matches config
        if (filledSlots != requiredStacks || firstStack.isEmpty()) {
            return false;
        }

        // 2. Check if the item is a block
        if (!(firstStack.getItem() instanceof BlockItem)) {
            return false;
        }

        // 3. Check if the stack size matches config (e.g., 64)
        if (firstStack.getCount() != requiredCount) {
            return false;
        }

        // 4. Keyword Filter Check
        Identifier blockId = Registries.ITEM.getId(firstStack.getItem());
        String path = blockId.getPath();

        // Use your keyword list from config
        boolean matchesKeyword = BoundlessConfig.allowedKeywords.stream()
                .anyMatch(path::contains);

        if (!matchesKeyword) {
            return false;
        }

        // 5. Check if an infinite version exists
        Block block = ((BlockItem) firstStack.getItem()).getBlock();
        if (!InfiniteItem.INFINITE_ITEMS.containsKey(block)) {
            return false;
        }

        // 6. Validate all other slots match the first one
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (!ItemStack.areItemsEqual(stack, firstStack) || stack.getCount() != requiredCount) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                return new ItemStack(InfiniteItem.INFINITE_ITEMS.get(block), 1);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        // Dynamically check against the config value
        return width * height >= BoundlessConfig.craftStacksCount;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        return DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY;
    }
}