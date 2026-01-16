package com.neuromuser.boundless_blocks.recipe;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class InfiniteUnpackingRecipe extends SpecialCraftingRecipe {

    public InfiniteUnpackingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        if (!com.neuromuser.boundless_blocks.config.BoundlessConfig.allowUnpacking) {
            return false;
        }

        int infiniteItemCount = 0;
        int totalItemCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                totalItemCount++;

                if (world.isClient()) {
                    if (stack.hasGlint() && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        String name = stack.getName().getString();
                        if (name.startsWith("∞") && name.endsWith("∞")) {
                            infiniteItemCount++;
                            continue;
                        }
                    }
                    return false;
                } else {
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
    public ItemStack craft(RecipeInputInventory inventory, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            net.minecraft.block.Block block = null;

            if (stack.getItem() instanceof InfiniteBlockItem && InfiniteBlockItem.isInfiniteBlock(stack)) {
                block = InfiniteBlockItem.getStoredBlock(stack);
            }
            else {
                NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (component != null) {
                    NbtCompound nbt = component.copyNbt();
                    if (nbt.contains("StoredBlockId")) {
                        String blockIdStr = nbt.getString("StoredBlockId");
                        Identifier blockId = Identifier.tryParse(blockIdStr);
                        if (blockId != null) {
                            block = Registries.BLOCK.get(blockId);
                        }
                    }
                }
            }

            if (block != null) {
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