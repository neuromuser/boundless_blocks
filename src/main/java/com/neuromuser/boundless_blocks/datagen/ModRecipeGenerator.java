package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {

        super(output);
        InfiniteItem.initializeInfiniteItems();
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

        System.out.println("DEBUG: Map size is " + InfiniteItem.INFINITE_ITEMS.size());
        InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
            Item vanillaItem = item.getBaseItem();

            if (vanillaItem != net.minecraft.item.Items.AIR) {
                ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaItem, 9)
                        .input(item)
                        .criterion(hasItem(item), conditionsFromItem(item))
                        // Generates IDs like: boundless_blocks:infinite_biomesoplenty_jacaranda_planks_unpack
                        .offerTo(exporter, Registries.ITEM.getId(item).withPath(path -> path + "_unpack"));
            }
        });
    }

    // Helper method to get the ID since getRecipeIdentifier is protected/internal
    private Identifier getItemId(Item item) {
        return net.minecraft.registry.Registries.ITEM.getId(item);
    }
}