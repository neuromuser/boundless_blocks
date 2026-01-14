// BoundlessBlocksClient.java
package com.neuromuser.boundless_blocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

public class BoundlessBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This ensures client-side registration
        BoundlessBlocks.LOGGER.info("Boundless Blocks client initializing...");

        // Add tooltips to infinite items
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if (stack.getItem() instanceof com.neuromuser.boundless_blocks.item.InfiniteItem) {
                tooltip.add(Text.literal("§bInfinite - never runs out!"));
                tooltip.add(Text.literal("§7Right-click to place without consuming"));
            }
        });
    }
}