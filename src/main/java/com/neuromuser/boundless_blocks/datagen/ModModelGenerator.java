package com.neuromuser.boundless_blocks.datagen;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
        // REMOVED: InfiniteItem.initializeInfiniteItems();
        // Now initialized in BoundlessBlocksDataGenerator before providers are created
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Re-verify initialization just in case
        InfiniteItem.ensureInitialized();

        InfiniteItem.INFINITE_ITEMS.forEach((block, item) -> {
            try {
                // Get the ID of the source block to link the model
                Identifier blockId = Registries.BLOCK.getId(block);

                // Only generate if it's a valid block (not air)
                if (!blockId.getPath().equals("air")) {
                    blockStateModelGenerator.registerParentedItemModel(
                            item,
                            ModelIds.getItemModelId(block.asItem())
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to generate model for: " + item.getName());
            }
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Leave this empty or use it for items that aren't based on blocks
    }
}
