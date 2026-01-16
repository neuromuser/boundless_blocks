package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.config.ClientConfigCache;
import com.neuromuser.boundless_blocks.network.ConfigSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoundlessBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BoundlessBlocks.LOGGER.info("Boundless Blocks client initializing...");

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientConfigCache.updateFromServer(
                        payload.allowedKeywords(),
                        payload.blacklistedKeywords(),
                        payload.showTooltips(),
                        payload.unpacking(),
                        payload.removePicked()
                );
                BoundlessBlocks.LOGGER.info("Received config from server: {} allowed, {} blacklisted",
                        payload.allowedKeywords().size(), payload.blacklistedKeywords().size());
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConfigCache.clear();
            BoundlessBlocks.LOGGER.info("Cleared server config cache");
        });

        ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
            if (!BoundlessConfig.showCanBeInfiniteTooltips) {
                return;
            }

            if (stack.getItem() instanceof BlockItem blockItem) {
                if (!stack.contains(net.minecraft.component.DataComponentTypes.CUSTOM_NAME) ||
                        !stack.getName().getString().contains("∞")) {
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
        return ClientConfigCache.isBlockAllowedForCrafting(id);
    }
}