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
        // REMOVED: InfiniteItem.initializeInfiniteItems();
        // Now initialized in BoundlessBlocksDataGenerator before providers are created
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        System.out.println("=== Recipe Generation ===");
        System.out.println("Generating recipes for " + InfiniteItem.INFINITE_ITEMS.size() + " infinite items");

        InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
            Item vanillaItem = item.getBaseItem();

            if (vanillaItem != net.minecraft.item.Items.AIR) {
                ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaItem, 9)
                        .input(item)
                        .criterion(hasItem(item), conditionsFromItem(item))
                        .offerTo(exporter, Registries.ITEM.getId(item).withPath(path -> path + "_unpack"));
            }
        });

        System.out.println("Recipe generation complete");
    }
}
