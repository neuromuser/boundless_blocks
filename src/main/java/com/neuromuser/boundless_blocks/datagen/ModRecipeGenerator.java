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
        int skippedCount = 0;
        for (Map.Entry<net.minecraft.block.Block, InfiniteItem> entry : InfiniteItem.INFINITE_ITEMS.entrySet()) {
            InfiniteItem item = entry.getValue();
            Item vanillaItem = item.getBaseItem();

            // Verify both the infinite item and vanilla item are properly registered
            if (vanillaItem != net.minecraft.item.Items.AIR) {
                Identifier infiniteItemId = Registries.ITEM.getId(item);
                Identifier vanillaItemId = Registries.ITEM.getId(vanillaItem);

                // Double-check the infinite item is actually in the registry
                if (Registries.ITEM.containsId(infiniteItemId)) {
                    try {
                        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaItem, 9)
                                .input(item)
                                .criterion(hasItem(item), conditionsFromItem(item))
                                .offerTo(exporter, infiniteItemId.withPath(path -> path + "_unpack"));
                        successCount++;
                    } catch (Exception e) {
                        System.err.println("Failed to generate recipe for " + infiniteItemId + ": " + e.getMessage());
                        skippedCount++;
                    }
                } else {
                    System.err.println("Skipping recipe for unregistered item: " + infiniteItemId);
                    skippedCount++;
                }
            } else {
                skippedCount++;
            }
        }

        System.out.println("Recipe generation complete: " + successCount + " recipes generated, " + skippedCount + " skipped");
    }}
