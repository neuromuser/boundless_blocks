package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.ClientConfigCache;
import com.neuromuser.boundless_blocks.network.ConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BoundlessBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BoundlessBlocks.LOGGER.info("Boundless Blocks client initializing...");

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.SYNC_CONFIG_PACKET_ID, (client, handler, buf, responseSender) -> {
            int allowedCount = buf.readInt();
            List<String> allowedKeywords = new ArrayList<>();
            for (int i = 0; i < allowedCount; i++) {
                allowedKeywords.add(buf.readString());
            }

            int blacklistedCount = buf.readInt();
            List<String> blacklistedKeywords = new ArrayList<>();
            for (int i = 0; i < blacklistedCount; i++) {
                blacklistedKeywords.add(buf.readString());
            }

            client.execute(() -> {
                ClientConfigCache.updateFromServer(allowedKeywords, blacklistedKeywords);
                BoundlessBlocks.LOGGER.info("Received config from server: {} allowed, {} blacklisted",
                        allowedKeywords.size(), blacklistedKeywords.size());
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientConfigCache.clear();
            BoundlessBlocks.LOGGER.info("Cleared server config cache");
        });

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
        return ClientConfigCache.isBlockAllowedForCrafting(id);
    }
}