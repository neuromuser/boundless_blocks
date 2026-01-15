package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public class ModRecipeGenerator extends FabricRecipeProvider {
    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        System.out.println("=== Generating Dynamic Recipe Serializers ===");

        // Register the crafting serializer (N stacks -> 1 infinite)
        ComplexRecipeJsonBuilder.create(BoundlessBlocks.INFINITE_CRAFTING_SERIALIZER)
                .offerTo(exporter, BoundlessBlocks.MOD_ID + ":infinite_crafting");

        // Register the unpacking serializer (1 infinite -> 9 regular)
        ComplexRecipeJsonBuilder.create(BoundlessBlocks.INFINITE_UNPACKING_SERIALIZER)
                .offerTo(exporter, BoundlessBlocks.MOD_ID + ":infinite_unpacking");

        System.out.println("Generated 2 dynamic recipe serializers");
    }
}