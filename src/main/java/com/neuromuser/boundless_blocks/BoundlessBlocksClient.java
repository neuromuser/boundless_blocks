package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoundlessBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BoundlessBlocks.LOGGER.info("Boundless Blocks client initializing...");

        // Add tooltips to items
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            // Tooltip for infinite items
            if (stack.getItem() instanceof InfiniteItem) {
                tooltip.add(Text.translatable("tooltip.boundless_blocks.infinite_item.line1"));
                tooltip.add(Text.translatable("tooltip.boundless_blocks.infinite_item.line2"));
            }
            // Tooltip for supported blocks that can become infinite
            else if (stack.getItem() instanceof BlockItem blockItem) {
                if (isBlockSupported(blockItem)) {
                    tooltip.add(Text.translatable("tooltip.boundless_blocks.can_be_infinite"));
                }
            }
        });
    }

    /**
     * Check if a block is supported for infinite conversion
     */
    private boolean isBlockSupported(BlockItem blockItem) {
        Identifier id = Registries.ITEM.getId(blockItem);
        String path = id.getPath();
        String namespace = id.getNamespace();

        // Skip our own mod's items
        if (namespace.equals(BoundlessBlocks.MOD_ID)) {
            return false;
        }

        // Check if namespace is allowed (minecraft or biomesoplenty by default)
        if (!namespace.equals("minecraft") && !namespace.equals("biomesoplenty")) {
            return false;
        }

        // Check if the path contains any of the allowed keywords
        return BoundlessConfig.allowedKeywords.stream()
                .anyMatch(path::contains);
    }
}