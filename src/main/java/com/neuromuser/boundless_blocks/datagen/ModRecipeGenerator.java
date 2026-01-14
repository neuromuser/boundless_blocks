package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;
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
        generateUnpackingRecipes(exporter);
        generateSpecialRecipe(exporter);

    }
    private void generateSpecialRecipe(Consumer<RecipeJsonProvider> exporter) {
            // This creates the "boundless_blocks:infinite_crafting.json" file
            // which points to your custom serializer.
            ComplexRecipeJsonBuilder.create(BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER)
                    .offerTo(exporter, BoundlessBlocks.MOD_ID + ":infinite_crafting");


    }
    private void generateUnpackingRecipes(Consumer<RecipeJsonProvider> exporter) {
        int successCount = 0;

        for (Map.Entry<net.minecraft.block.Block, InfiniteItem> entry : InfiniteItem.INFINITE_ITEMS.entrySet()) {
            InfiniteItem infiniteItem = entry.getValue();
            Item vanillaItem = entry.getKey().asItem();

            if (vanillaItem == net.minecraft.item.Items.AIR) continue;

            // Use the item directly. ShapelessRecipeJsonBuilder will extract the ID itself.
            try {
                Identifier id = Registries.ITEM.getId(infiniteItem);

                // We use the infinite item's path + _unpack to avoid ID collisions
                String recipePath = id.getPath() + "_unpack";

                ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaItem, 9)
                        .input(infiniteItem)
                        .criterion("has_infinite_item", conditionsFromItem(infiniteItem))
                        .offerTo(exporter, new Identifier(BoundlessBlocks.MOD_ID, recipePath));

                successCount++;
            } catch (Exception e) {
                System.err.println("Recipe Gen Error: " + e.getMessage());
            }
        }
        System.out.println("Generated " + successCount + " unpacking recipes.");
    }}
