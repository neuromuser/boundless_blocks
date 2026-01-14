package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
        InfiniteItem.initializeInfiniteItems();
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
            // Use getItemModelId instead of getBlockModelId
            // This ensures it copies the "Display" settings (hand rotation)
            // from the vanilla item version.
            blockStateModelGenerator.registerParentedItemModel(item, ModelIds.getItemModelId(block.asItem()));
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Leave this empty or use it for items that aren't based on blocks
    }
}