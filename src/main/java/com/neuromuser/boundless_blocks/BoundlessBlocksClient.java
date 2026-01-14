package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoundlessBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (!stack.hasCustomName() || !stack.getName().getString().contains("∞")) {
                    if (isBlockSupported(blockItem)) {
                        tooltip.add(Text.translatable("tooltip.boundless_blocks.can_be_infinite"));
                    }
                }
            }
        });
    }

    private boolean isBlockSupported(BlockItem blockItem) {
        Identifier id = Registries.ITEM.getId(blockItem);
        String namespace = id.getNamespace();

        if (namespace.equals(BoundlessBlocks.MOD_ID)) {
            return false;
        }

        return BoundlessConfig.isBlockAllowedForCrafting(id);
    }
}