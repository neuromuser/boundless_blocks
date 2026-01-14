package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
        // REMOVED: InfiniteItem.initializeInfiniteItems();
        // Now initialized in BoundlessBlocksDataGenerator before providers are created
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        System.out.println("=== Model Generation ===");
        System.out.println("Generating models for " + InfiniteItem.INFINITE_ITEMS.size() + " infinite items");

        InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
            blockStateModelGenerator.registerParentedItemModel(
                    item,
                    ModelIds.getItemModelId(block.asItem())
            );
        });

        System.out.println("Model generation complete");
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Leave this empty or use it for items that aren't based on blocks
    }
}
